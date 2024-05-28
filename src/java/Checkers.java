import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The main class for the Checkers game application.
 */
public class Checkers {
    /**
     * Constructs a new Checkers game.
     */
    public Checkers() {
        // Set up the main frame
        JFrame frame = new JFrame("Checkers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(815, 845);
        frame.setResizable(false);

        // Create the Checkers panel
        CheckersPanel checkersPanel = new CheckersPanel();
        frame.add(checkersPanel);

        // Create game menu
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenu aboutMenu = new JMenu("About");
        JMenuItem restartItem = new JMenuItem("Restart");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem checkersItem = new JMenuItem("Checkers");
        // Add action listener to about menu item
        restartItem.addActionListener(_ -> checkersPanel.restart());

        exitItem.addActionListener(_ -> System.exit(0));

        checkersItem.addActionListener(_ -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Checkers"));
                } catch (IOException | URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        gameMenu.add(restartItem);
        gameMenu.add(exitItem);

        aboutMenu.add(checkersItem);

        menuBar.add(gameMenu);
        menuBar.add(aboutMenu);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }

    /**
     * The main method to start the Checkers game application.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Set up the FlatLaf Mac Dark theme
        FlatMacDarkLaf.setup();
        // Create a new instance of Checkers
        new Checkers();
    }
}