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
        setLayout(new BorderLayout());
        setResizable(false);
        setTitle("Photo snapshot");


        JVideoScreen videoScreen = new JVideoScreen();

        videoScreen.setFrame(videoFrame);

        add(videoScreen, BorderLayout.CENTER);
        pack();
    }
}