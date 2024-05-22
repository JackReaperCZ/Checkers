import java.awt.*;

public class PlayField {
    private Integer[][] field;

    public PlayField() {
        field = new Integer[10][10];
        initializeBoard();
    }
    private void initializeBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if ((i < 4) && (i % 2 != j % 2)) {
                    field[i][j] = 1; // White pieces
                } else if ((i > 5) && (i % 2 != j % 2)) {
                    field[i][j] = 2; // Black pieces
                } else {
                    field[i][j] = -1; // Empty spaces
                }
            }
        }
    }

    public Integer[][] getField() {
        return field;
    }
}
