package org.example.view;

import de.learnlib.ralib.words.PSymbolInstance;
import org.example.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class MainPanel extends JPanel {
    private Controller app;
    private final JPanel wordsPanel;
    private final ArrayList<JButton> words;
    private final JComboBox comboBox;
    private JButton button;
    private JButton counterexampleTrue;
    private JButton counterexampleFalse;
    private JLabel img;
    private GridBagConstraints constraints;

    public MainPanel(Controller app) {
        super(new GridBagLayout());
        this.constraints = new GridBagConstraints();
        this.app = app;
        this.button = new JButton("Add");
        this.img = new JLabel(new ImageIcon("out.png"));
        this.wordsPanel = new JPanel(new WrapLayout());
        this.words = new ArrayList<>();
        this.comboBox = new JComboBox<>(new Vector<>(app.getAlphabet().stream().map(PSymbolInstance::toString).toList()));
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
        this.add(this.img, constraints);

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
        this.add(this.comboBox, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        this.add(this.button, constraints);
    }

    private void setUpListeners() {
        this.button.addActionListener(this::createWord);
        this.counterexampleTrue.addActionListener(x -> {
            updateImage(null);
            app.start();
            updateImage("out.png");
        });
        this.counterexampleFalse.addActionListener(x -> {
            updateImage(null);
            app.start();
            updateImage("out.png");
        });
    }

    private void updateImage(String file) {
        if (file == null) {
            this.img.setIcon(null);
        }
        else {
            this.img.setIcon(new ImageIcon(file));
        }
        this.updateUI();
    }

    private void createWord(ActionEvent actionEvent) {
        JButton word = new JButton(Objects.requireNonNull(comboBox.getSelectedItem()).toString());
        word.addActionListener(e1 -> {
            words.remove(word);
            wordsPanel.remove(word);
            wordsPanel.updateUI();
        });
        words.add(word);
        wordsPanel.add(word);
        wordsPanel.updateUI();
    }

}
