package view;

import com.smaxe.uv.media.VideoFrameFactory;
import com.smaxe.uv.media.core.VideoFrame;
import com.smaxe.uv.media.swing.JVideoScreen;
import com.smaxe.uv.na.WebcamFactory;
import com.smaxe.uv.na.webcam.IWebcam;
import controller.FaceDetection;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 * User: NoePodesta
 * Date: 10/09/12
 * Time: 10:59
 */
public class MainPanel extends JPanel {

    final JFrame frame = new JFrame();
    final private String[] args;
    VideoFrame backgroundFrame;
    int[] backgroundRGB;


    public MainPanel(String[] args) {
        this.args = args;
        buildUi();
    }

    private void buildUi() {
        
        
        setLayout(new MigLayout("fillx, ins 5, wrap 3"));
        JLabel label = createLabel("Face Detection");
        Font newLabelFont = new Font("Calibri", Font.BOLD, 25);
        label.setForeground(Color.getHSBColor(106, 172, 252));
        label.setFont(newLabelFont);

        final JPanel webCamPanel = new JPanel();
        final AtomicReference<JFrame> frameRef = new AtomicReference<JFrame>();
        final AtomicReference<VideoFrame> lastFrameRef = new AtomicReference<VideoFrame>();

        final IWebcam webcam = WebcamFactory.getWebcams(this, args.length == 0 ? "jitsi" : args[0]).get(0);
        if (webcam == null) return;

        final JVideoScreen videoScreen = new JVideoScreen();

        startWebCam(webCamPanel,webcam,videoScreen,frameRef,lastFrameRef);

        add(label, "align center, wrap, span 3");
        webCamPanel.setLayout(new MigLayout());
        webCamPanel.setVisible(true);
        webCamPanel.setSize(400,300);
        add(webCamPanel, "align center, wrap, span 3");

        JButton backgroundSnapshot = new JButton("Background snapshot");
        backgroundSnapshot.setBackground(Color.DARK_GRAY);
        add(backgroundSnapshot, "align center");

        JButton snapshot =  new JButton("Snapshot");
        snapshot.setBackground(Color.DARK_GRAY);
        add(snapshot, "align center");

        final JToggleButton toggleButtonWebCam = new JToggleButton("Stop WebCam", false);
        toggleButtonWebCam.setBackground(Color.DARK_GRAY);
        add(toggleButtonWebCam, "align center");
        
        final JLabel label1 = new JLabel("1) You need to took first the background image");



        backgroundSnapshot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                backgroundFrame = VideoFrameFactory.clone(lastFrameRef.get());
                backgroundRGB = backgroundFrame.rgb;
                File file = new File("images/imageBackGround.jpg");
                try {
                    VideoFrameFactory.saveAsJpg(file, backgroundFrame);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        snapshot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                VideoFrame imageFrame = VideoFrameFactory.clone(lastFrameRef.get());
                int[] imageRGB = imageFrame.rgb;
                File file = new File("images/image.jpg");
                try {
                    VideoFrameFactory.saveAsJpg(file, imageFrame);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int[] finalImageRGB = new int[backgroundRGB.length];
                
                for(int i=0; i< backgroundRGB.length; i++ ){
                     finalImageRGB[i] = backgroundRGB[i]-imageRGB[i];
                    
                }
                VideoFrame finalImage = new VideoFrame(400,300,finalImageRGB);
                new SnapshotDialog(finalImage).setVisible(true);
                new SnapshotDialog(imageFrame).setVisible(true);
            }
        });

        toggleButtonWebCam.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == 1) {
                    webcam.stopCapture();
                    webcam.close();
                    toggleButtonWebCam.setText("Start WebCam");
                } else {
                    startWebCam(webCamPanel, webcam, videoScreen, frameRef, lastFrameRef);
                    toggleButtonWebCam.setText("Stop WebCam");
                }
            }
        });

    }


    private JLabel createLabel(String text) {
        return new JLabel(text, SwingConstants.CENTER);
    }
    
    private void startWebCam(final JPanel webCamPanel, final IWebcam webcam, final JVideoScreen videoScreen, final AtomicReference<JFrame> frameRef, final AtomicReference<VideoFrame> lastFrameRef){
        new Thread(new Runnable() {
            public void run() {
                try {
                    webcam.open(new IWebcam.FrameFormat(400, 300), new IWebcam.IListener() {
                        private VideoFrame lastFrame = new VideoFrame(400, 300, null);

                        public void onVideoFrame(final VideoFrame frame) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    videoScreen.setFrame(frame);

                                    if (lastFrame.width != frame.width || lastFrame.height != frame.height) {
                                        final JFrame frame = frameRef.get();

                                        if (frame != null) frame.pack();
                                    }

                                    lastFrame = frame;

                                    lastFrameRef.set(lastFrame);
                                }
                            });
                        }
                    });

                    webcam.startCapture();

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            webCamPanel.add(videoScreen);
                        }
                    });
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            ex.getMessage(), ex.getMessage(), JOptionPane.WARNING_MESSAGE);
                }
            }
        }).start();
       
    }
}


