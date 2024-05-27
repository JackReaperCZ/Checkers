import javax.swing.*;
import java.awt.*;

/**
 * A dialog box for displaying messages related to the Checkers game.
 */
public class CheckersDialog extends JDialog {

    /**
     * Constructs a new CheckersDialog.
     *
     * @param parent        The parent Frame of the dialog
     * @param title         The title of the dialog
     * @param message       The message to be displayed in the dialog
     * @param checkersPanel The CheckersPanel associated with the dialog
     */
    public CheckersDialog(Frame parent, String title, String message, CheckersPanel checkersPanel) {
        super(parent, title, true); // Creates a modal dialog with specified title
        JPanel messagePanel = new JPanel();
        messagePanel.add(new JLabel(message)); // Add message to a panel
        add(messagePanel, BorderLayout.CENTER); // Add message panel to the center of the dialog

        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> { // Add action listener to restart button
            checkersPanel.restart(); // Restart the game
            dispose(); // Close the dialog
        });
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0)); // Add action listener to exit button
        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom of the dialog

        pack(); // Pack the components
        setLocationRelativeTo(parent); // Center the dialog relative to the parent frame
        setVisible(true); // Make the dialog visible
    }
}
