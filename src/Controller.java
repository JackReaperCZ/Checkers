import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller extends MouseAdapter {
    private PlayFieldPanel playFieldPanel;

    public Controller(PlayFieldPanel playFieldPanel) {
        this.playFieldPanel = playFieldPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        playFieldPanel.clicked(e.getX(),e.getY());
    }
}