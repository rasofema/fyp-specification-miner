package org.example.view;

import org.example.controller.Controller;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
    private Controller app;

    public MainFrame(Controller app) {
        super();
        this.app = app;
        setupFrame();
    }

    private void setupFrame() {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setTitle("SpecificationMiner");
        this.setVisible(true);
    }
}
