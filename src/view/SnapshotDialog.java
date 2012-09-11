package view;

import com.smaxe.uv.media.VideoFrameFactory;
import com.smaxe.uv.media.core.VideoFrame;
import com.smaxe.uv.media.swing.JVideoScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: NoePodesta
 * Date: 11/09/12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */

public class SnapshotDialog extends JDialog
{
    private final static long serialVersionUID = -3136925779409522531L;

    /**
     * Constructor.
     *
     * @param videoFrame
     */
    public SnapshotDialog(final VideoFrame videoFrame)
    {
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setTitle("Photo snapshot");

        JVideoScreen videoScreen = new JVideoScreen();

        videoScreen.setFrame(videoFrame);

        videoScreen.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(final MouseEvent e)
            {
                final int clickCount = e.getClickCount();
                final Object source = e.getSource();

                switch (clickCount)
                {
                    case 1:
                    {
                        if (SwingUtilities.isRightMouseButton(e))
                        {
                            JPopupMenu popup = new JPopupMenu();

                            popup.add(new AbstractAction("Save As..")
                            {
                                private final static long serialVersionUID = 0L;

                                public void actionPerformed(ActionEvent e)
                                {
                                    JFileChooser fileChooser = new JFileChooser();

                                    fileChooser.setCurrentDirectory(new File("."));

                                    final int rv = fileChooser.showSaveDialog(getParent());

                                    if (rv != JFileChooser.APPROVE_OPTION) return;

                                    final File file = fileChooser.getSelectedFile();

                                    new Thread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            try
                                            {
                                                VideoFrameFactory.saveAsJpg(file, videoFrame);
                                            }
                                            catch (IOException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            });

                            popup.show((Component) source, e.getX(), e.getY());
                        }
                    } break;
                }
            }
        });

        this.add(videoScreen, BorderLayout.CENTER);
        this.pack();
    }
}