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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 * User: NoePodesta
 * Date: 10/09/12
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class MainPanel extends JPanel {

    final JComboBox comboBox = new JComboBox();
    final JFrame frame = new JFrame();
    final private String[] args;


    public MainPanel(String[] args) {
        this.args = args;
        buildUi();
    }

    private void buildUi() {
        setLayout(new MigLayout("fillx, ins 5, wrap 2"));
        setVisible(true);
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

        add(label, "align center, wrap");
        add(createLabel("Webcam: "));
        webCamPanel.setSize(500, 500);
        webCamPanel.setLayout(new MigLayout("fill, ins 5, wrap 2"));
        add(webCamPanel);

        JButton snapshotButton = new JButton("Snapshot");
        add(snapshotButton, "align right");

        snapshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new SnapshotDialog(VideoFrameFactory.clone(lastFrameRef.get())).setVisible(true);
            }
        });

    }

    private final static class WebcamComboModel extends AbstractListModel implements ComboBoxModel {
        private final static long serialVersionUID = -8627944517955777531L;

        // fields
        private final java.util.List<IWebcam> devices;
        // state
        private Object selected = null;

        /**
         * Constructor.
         *
         * @param devices
         */
        public WebcamComboModel(java.util.List<IWebcam> devices) {
            this.devices = devices;
        }

        // MutableComboBoxModel implementation

        public void setSelectedItem(final Object item) {
            this.selected = item;
        }

        public Object getSelectedItem() {
            return selected;
        }

        public Object getElementAt(int index) {
            return devices.get(index);
        }

        public int getSize() {
            return devices.size();
        }

    }


    private JLabel createLabel(String text) {
        return new JLabel(text, SwingConstants.CENTER);
    }
}


