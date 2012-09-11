package view;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: NoePodesta
 * Date: 11/09/12
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends JFrame{

    JFrame frame;

    public MainFrame(){
        buildUi();
    }

    private void buildUi() {
        frame = new JFrame("Face Detection Application");
        MainPanel mainPanel = new MainPanel();
        frame.add(mainPanel);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

}
