package dev.jonium.fun.logic;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class Ui {

    final JFrame window;
    final JPanel stack;
    JPanel game;
    JPanel gameWnd;
    final CardLayout cardLayout;
    GameLogic field;
    JLabel flag;
    JButton state;
    boolean refresh;

    public Ui() {

        cardLayout = new CardLayout();
        stack = new JPanel(cardLayout);

        initGM();

        window = new JFrame("Sweeper");

        window.add(stack);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cardLayout.show(stack, "mainMenu");
        window.setSize(300, 330);
        window.setMinimumSize(new Dimension(300, 330));
        window.pack();
        window.setVisible(true);

    }

    public void dispose() {
        window.setVisible(false);
        window.dispose();

    }

    private void sceneSwap(String scene) {
        cardLayout.show(stack, scene);
        window.revalidate();
        window.repaint();
    }

    private void initGM() {

        JPanel gameMenu = new JPanel(new GridBagLayout());
        stack.add(gameMenu, "gameMenu");

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;

        JButton startBtn1 = new JButton("Beginner");
        startBtn1.setPreferredSize(new Dimension(300, 30));
        startBtn1.addActionListener(e -> launchGame(8, 8, 10));

        JButton startBtn2 = new JButton("Intermediate");
        startBtn2.setPreferredSize(new Dimension(300, 30));
        startBtn2.addActionListener(e -> launchGame(16, 16, 40));

        JButton startBtn3 = new JButton("Expert");
        startBtn3.setPreferredSize(new Dimension(300, 30));
        startBtn3.addActionListener(e -> launchGame(16, 30, 99));

        JButton exitBtn = new JButton();
        exitBtn.setPreferredSize(new Dimension(300, 30));
        exitBtn.setText("Exit");
        exitBtn.addActionListener(e -> this.dispose());

        gameMenu.add(startBtn1, c);
        c.gridy = 1;
        gameMenu.add(startBtn2, c);
        c.gridy = 2;
        gameMenu.add(startBtn3, c);
        c.gridy = 3;
        gameMenu.add(exitBtn, c);

    }

    private void launchGame(int x, int y, int mines) {

        // JPanel for game window
        // Added to layout stack
        gameWnd = new JPanel();
        stack.add(gameWnd, "gameWnd");

        // Enable refresh
        refresh = true;

        // Set size
        window.setSize(30 * x, 30 * y + 30);

        // Layout for game window
        GridBagLayout layout = new GridBagLayout();
        gameWnd.setLayout(layout);

        // Counters for flags and mines
        flag = new JLabel("Flags: " + "0", SwingConstants.CENTER);
        JLabel mine = new JLabel("Mines: " + ((Integer) mines).toString(), SwingConstants.CENTER);

        // Layout for actual game
        game = new JPanel(new GridBagLayout());
        game.setBorder(new CompoundBorder(
                BorderFactory.createLoweredSoftBevelBorder(),
                BorderFactory.createEmptyBorder()
        ));

        // Add action listener
        game.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int btn = e.getButton();
                boolean right = false;

                if (btn == MouseEvent.BUTTON3) {
                    right = true;
                }

                try {
                    String[] coord = game.getComponentAt(x, y).getName().split(":");
                    click(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), right);
                } catch (NullPointerException noComp) {
                    assert true;
                }

            }
        });

        // Constructing the game toolbar
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 30;

        // Add mine counter
        gameWnd.add(mine, c);

        // Add title
        c.gridx = 1;
        gameWnd.add(new JLabel(" ", SwingConstants.CENTER), c);

        // Add flag counter
        c.gridx = 2;
        gameWnd.add(flag, c);

        // Attach the game layout
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        c.ipady = 480;
        c.ipadx = 480;
        c.gridwidth = 3;
        gameWnd.add(game, c);

        // Generate the plying field
        field = new GameLogic(x, y, mines);

        // Call for paint
        paintField();

        // Swap scene
        sceneSwap("gameWnd");
    }

    public void refresh() {

        if (!refresh) {
            return;
        }

        // Clear board
        game.removeAll();

        // Check win condition
        if (field.getDone() != (byte) 0) {
            String text;

            if (field.getDone() == (byte) 1) {
                text = "You win";
            } else {
                text = "You lose";
            }

            state = new JButton(text);
            state.addActionListener(e -> {
                window.setSize(300, 330);
                sceneSwap("gameMenu");

            });

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            c.fill = GridBagConstraints.BOTH;

            gameWnd.add(state, c);
            refresh = false;
        }

        // Call for paint
        paintField();

        // Count flags
        flag.setText("Flags: " + field.flags.toString());

        // Redraw
        window.revalidate();
        window.repaint();

    }

    private void paintField() {

        // Constructing the field graphics
        GridBagConstraints pos = new GridBagConstraints();
        pos.weightx = 0.5;
        pos.weighty = 0.5;
        pos.fill = GridBagConstraints.BOTH;
        pos.anchor = GridBagConstraints.CENTER;

        // Adding labels
        for (int i = 0; i < field.width; i++) {
            for (int j = 0; j < field.height; j++) {
                pos.gridx = i;
                pos.gridy = j;

                JLabel l = new JLabel("  ", SwingConstants.CENTER);

                l.setBorder(new CompoundBorder(
                        BorderFactory.createEmptyBorder(),
                        BorderFactory.createEmptyBorder()
                ));
                l.setPreferredSize(new Dimension(30, 30));
                l.setName(((Integer) i).toString() + ":" + ((Integer) j).toString());

                if (field.render[i][j] == (byte) -1) {
                    if (field.field[i][j] != (byte) 0) {
                        String text;

                        if (field.field[i][j] == (byte) 9) {
                            text = "x";
                        } else {
                            text = ((Byte) field.field[i][j]).toString();
                        }

                        l.setText(text);
                    }
                } else if (field.render[i][j] == (byte) 1) {
                    l.setBorder(new CompoundBorder(
                            BorderFactory.createRaisedSoftBevelBorder(),
                            BorderFactory.createEmptyBorder()
                    ));
                    l.setText("f");
                } else {
                    l.setBorder(new CompoundBorder(
                            BorderFactory.createRaisedSoftBevelBorder(),
                            BorderFactory.createEmptyBorder()
                    ));
                }

                game.add(l, pos);
            }
        }
    }

    public void click(int x, int y, boolean right) {
        field.click(x, y, right);
        refresh();

    }

}
