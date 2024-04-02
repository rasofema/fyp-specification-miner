package org.example.view;

import de.learnlib.ralib.words.PSymbolInstance;
import org.example.controller.Controller;
import org.example.helper.SupportedLearningAlgorithm;
import org.example.model.Functions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class MainPanel extends JPanel {
    private Controller app;
    private final JPanel wordsPanel;
    private final JPanel imgPanel;
    private final ArrayList<JButton> words;
    private final JComboBox<Functions.Function> functionSelection;
    private final JComboBox<SupportedLearningAlgorithm> algorithmSelection;
    private JButton button;
    private JButton counterexampleTrue;
    private JButton counterexampleFalse;
    private JLabel img;
    private ImageIcon imgIcon;
    private GridBagConstraints constraints;

    public MainPanel(Controller app) {
        super(new GridBagLayout());
        this.constraints = new GridBagConstraints();
        this.app = app;
        this.button = new JButton("Add");

        this.img = new JLabel();
        Dimension dim = new Dimension(1000, 600);
        this.imgPanel = new JPanel();
        this.imgPanel.setPreferredSize(dim);
        this.imgPanel.setMaximumSize(dim);
        this.imgPanel.setMinimumSize(dim);
        this.imgPanel.setSize(dim);
        this.imgPanel.revalidate();

        this.wordsPanel = new JPanel(new WrapLayout());
        this.words = new ArrayList<>();
        this.functionSelection = new JComboBox<>(Functions.Function.values());
        this.algorithmSelection = new JComboBox<>(SupportedLearningAlgorithm.values());
        this.counterexampleTrue = new JButton("True");
        this.counterexampleTrue.setBackground(Color.green);
        this.counterexampleTrue.setOpaque(true);
        this.counterexampleFalse = new JButton("False");
        this.counterexampleFalse.setBackground(Color.PINK);
        this.counterexampleFalse.setOpaque(true);

        setUpPanel();
        setUpListeners();
    }

    private void setUpPanel() {
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        this.imgPanel.add(this.img);
        this.add(this.imgPanel, constraints);
        this.add(this.wordsPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        this.add(this.algorithmSelection, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        this.wordsPanel.setBackground(Color.gray);
        this.add(this.wordsPanel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        this.add(this.counterexampleTrue, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;
        this.add(this.counterexampleFalse, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        this.add(this.functionSelection, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        this.add(this.button, constraints);
    }

    private void setUpListeners() {
        this.button.addActionListener(this::createWord);
        this.counterexampleTrue.addActionListener(x -> {
            app.updateFromUser(words.stream().map(JButton::getText).toList(), true);
            updateImage("out.png");
        });
        this.counterexampleFalse.addActionListener(x -> {
            app.updateFromUser(words.stream().map(JButton::getText).toList(), false);
            updateImage("out.png");
        });
        this.algorithmSelection.addItemListener(x -> {
            app.setAlgorithm((SupportedLearningAlgorithm) this.algorithmSelection.getSelectedItem());
            app.start();
        });
    }


    public void updateImage(String img) {
        if (this.imgIcon != null) {
            this.imgIcon.getImage().flush();
        }
        this.imgIcon = scaleImage(new ImageIcon(img),1000, 1000);
        this.img.setIcon(this.imgIcon);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        this.img.updateUI();
    }

    private void createWord(ActionEvent actionEvent) {
        JButton word = new JButton(Objects.requireNonNull(functionSelection.getSelectedItem()).toString());
        word.addActionListener(e1 -> {
            words.remove(word);
            wordsPanel.remove(word);
            wordsPanel.updateUI();
        });
        words.add(word);
        wordsPanel.add(word);
        wordsPanel.updateUI();
    }

//    https://stackoverflow.com/a/34189578/14335161
    private ImageIcon scaleImage(ImageIcon icon, int w, int h)
    {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();

        if(icon.getIconWidth() > w) {
            nw = w;
            nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }

        if(nh > h) {
            nh = h;
            nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }

        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH));
    }


}
