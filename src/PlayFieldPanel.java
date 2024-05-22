import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayFieldPanel extends JPanel {
    private JFrame mainFrame;
    private boolean whiteTurn = true;
    private final PlayField playField;
    private final Color dark = new Color(85, 136, 34);
    private final Color light = new Color(255, 238, 187);

    private Integer[] selected;

    public PlayFieldPanel(JFrame mainFrame) {
        this.playField = new PlayField();
        this.mainFrame = mainFrame;
    }

    public void clicked(int x, int y) {
        Integer[] l = translate(x, y);
        ArrayList<Integer> target;
        if (whiteTurn) {
            target = new ArrayList<>(List.of(1,3));
        } else {
            target = new ArrayList<>(List.of(2,4));
        }

        if (selected == null) {
            System.out.println(target.contains(this.playField.getField()[l[0]][l[1]]));
            System.out.println(this.playField.getField()[l[0]][l[1]]);
            if (target.contains(this.playField.getField()[l[0]][l[1]])) {
                this.selected = l;
            }
        } else {
            if (Objects.equals(selected[0], l[0]) && Objects.equals(selected[1], l[1])) {
                this.selected = null;
            } else if (target.contains(this.playField.getField()[l[0]][l[1]])) {
                this.selected = l;
            } else {
                this.selected = null;
            }
        }
        repaint();
    }

    private Integer[] translate(int mouseX, int mouseY) {
        int fieldX = mouseX / 80;
        int fieldY = mouseY / 80;

        fieldX = Math.max(0, Math.min(fieldX, 10 - 1));
        fieldY = Math.max(0, Math.min(fieldY, 10 - 1));

        return new Integer[]{fieldX, fieldY};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Integer[][] field = playField.getField();
        boolean flag = false;
        for (int i = 0; i <= 9; i++) {
            flag = !flag;
            for (int y = 0; y <= 9; y++) {
                if (flag) {
                    g.setColor(light);
                    g.fillRect(80 * i, 80 * y, 80, 80);
                    flag = false;
                } else {
                    g.setColor(dark);
                    g.fillRect(80 * i, 80 * y, 80, 80);
                    flag = true;
                }

                Integer curr = field[y][i];

                if (selected != null) {
                    if (selected[0] == i && selected[1] == y) {
                        g.setColor(new Color(255, 200, 43, 109));
                        g.fillRect(80 * i, 80 * y, 80, 80);
                    }
                }

                if (curr != -1) {
                    switch (curr) {
                        case 1 -> g.setColor(Color.WHITE);
                        case 2 -> g.setColor(new Color(196, 0, 3));
                    }
                    g.fillOval((80 * i) + 5, (80 * y) + 5, 70, 70);
                    g.setColor(Color.BLACK);
                    g.drawOval((80 * i) + 5, (80 * y) + 5, 70, 70);
                }
            }
        }
    }
}