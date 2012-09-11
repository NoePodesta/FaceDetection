package view;

import com.smaxe.uv.media.VideoFrameFactory;
import com.smaxe.uv.media.core.VideoFrame;
import com.smaxe.uv.media.swing.JVideoScreen;
import com.smaxe.uv.na.WebcamFactory;
import com.smaxe.uv.na.webcam.IWebcam;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 * User: NoePodesta
 * Date: 10/09/12
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class MainPanel extends JPanel {

    final JFrame frame = new JFrame();
    final private String[] args;


    public MainPanel(String[] args) {
        this.args = args;
        buildUi();
    }

    private void buildUi() {
        setLayout(new MigLayout("fillx, ins 5, wrap 2"));
        JLabel label = createLabel("Face Detection");
        Font newLabelFont = new Font("Calibri", Font.BOLD, 25);
        label.setForeground(Color.getHSBColor(106, 172, 252));
        label.setFont(newLabelFont);

        final JPanel webCamPanel = new JPanel();
        final AtomicReference<JFrame> frameRef = new AtomicReference<JFrame>();
        final AtomicReference<VideoFrame> lastFrameRef = new AtomicReference<VideoFrame>();

        final IWebcam webcam = WebcamFactory.getWebcams(frame, args.length == 0 ? "jitsi" : args[0]).get(0);
        if (webcam == null) return;

        final JVideoScreen videoScreen = new JVideoScreen();

        startWebCam(webCamPanel,webcam,videoScreen,frameRef,lastFrameRef);

        add(label, "align center, wrap");
        add(createLabel("Webcam: "));
        webCamPanel.setLayout(new MigLayout("fill, ins 5, wrap 2"));
        add(webCamPanel);

        JButton snapshotButton = new JButton("Snapshot");
        add(snapshotButton, "align right");
        final JToggleButton stopWebCam = new JToggleButton("Stop WebCam", false);
        add(stopWebCam, "align right");

        snapshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new SnapshotDialog(VideoFrameFactory.clone(lastFrameRef.get())).setVisible(true);
            }
        });

        stopWebCam.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                System.out.println(itemEvent.getStateChange());
                if(itemEvent.getStateChange()==1){
                    webcam.close();
                    stopWebCam.setText("Start WebCam");
                }
                else{
                    startWebCam(webCamPanel,webcam,videoScreen,frameRef,lastFrameRef);
                    stopWebCam.setText("Stop WebCam");
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
                        private VideoFrame lastFrame = new VideoFrame(0, 0, null);

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


