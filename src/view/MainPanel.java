package view;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: NoePodesta
 * Date: 10/09/12
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class MainPanel extends JPanel{

    public MainPanel(){
        buildUi();
    }

    private void buildUi() {
        setLayout(new MigLayout("fillx, gap 2, ins 5"));
        setVisible(true);
        JLabel label = createLabel("Face Detection");
        Font newLabelFont=new Font(label.getFont().getName(),Font.BOLD,label.getFont().getSize());
        label.setFont(newLabelFont);
        add(label, "align center, wrap");
        add(createLabel("Here goes the webCam"), "wrap");
        JButton button = new JButton("Click");
        add(button, "align right");
    }


    
    private JLabel createLabel(String text){
        return new JLabel(text, SwingConstants.CENTER);
    }


}


