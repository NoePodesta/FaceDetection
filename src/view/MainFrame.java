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

    private JFrame frame;
    final private String[] args;

    public MainFrame(String[] args){
        this.args = args;
        buildUi();
    }

    private void buildUi() {
        frame = new JFrame("Face Detection Application");
        MainPanel mainPanel = new MainPanel(args);
        frame.add(mainPanel);
        frame.setSize(600, 500);
        frame.setVisible(true);

    }



}
