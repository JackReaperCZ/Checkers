import javax.swing.*;
import java.awt.*;

public class Game {
    private Controller controller;

    public Game() {
        JFrame jFrame = new JFrame("Checkers");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(815,838);
        jFrame.setMaximumSize(d);
        jFrame.setMinimumSize(d);
        jFrame.setSize(d);
        jFrame.setResizable(false);

        PlayFieldPanel pfp = new PlayFieldPanel(jFrame);
        this.controller = new Controller(pfp);
        jFrame.addMouseListener(controller);

        jFrame.setVisible(true);
        jFrame.add(pfp);
        pfp.paintComponent(jFrame.getGraphics());
    }

    public static void main(String[] args) {
        new Game();
    }
}
