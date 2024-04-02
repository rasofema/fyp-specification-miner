package org.example.view;

import org.example.controller.Controller;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
    private Controller app;
    private MainPanel panel;

    public MainFrame(Controller app) {
        super();
        this.app = app;
        this.panel = new MainPanel(this.app);
        setupFrame();
    }

    private void setupFrame() {
        this.setContentPane(panel);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setTitle("SpecificationMiner");
        this.setVisible(true);
    }

    public void updateImage(String img) {
        this.panel.updateImage(img);
    }
}
