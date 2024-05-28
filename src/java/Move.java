/**
 * Represents a move in a checkers game.
 */
public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;

    /**
     * Constructs a new Move object.
     *
     * @param fromRow The row index of the piece before the move
     * @param fromCol The column index of the piece before the move
     * @param toRow   The row index of the destination position after the move
     * @param toCol   The column index of the destination position after the move
     */
    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    /**
     * Translates the move direction from the starting position to the destination position.
     *
     * @param fromRow The starting row index
     * @param fromCol The starting column index
     * @param toRow   The destination row index
     * @param toCol   The destination column index
     * @return An array containing the row and column direction of the move, or null if the move is invalid
     */
    public static int[] translateMoveDirection(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        if (Math.abs(rowDiff) != Math.abs(colDiff)) {
            return null;
        }
        int rowDirection;
        if (rowDiff > 0) {
            rowDirection = 1;
        } else {
            rowDirection = -1;
        }
        int colDirection;
        if (colDiff > 0) {
            colDirection = 1;
        } else {
            colDirection = -1;
        }

        return new int[]{rowDirection, colDirection};
    }

    /**
     * Checks if the move has passed through an enemy piece.
     *
     * @param fromRow The starting row index
     * @param fromCol The starting column index
     * @param toRow   The destination row index
     * @param toCol   The destination column index
     * @param board   The current state of the game board
     * @return True if the move passes through an enemy piece, false otherwise
     */
    public static boolean hasPassedThroughEnemy(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        int rowDirection = rowDiff > 0 ? 1 : -1;
        int colDirection = colDiff > 0 ? 1 : -1;

        int newRow = fromRow + rowDirection;
        int newCol = fromCol + colDirection;

        while (newRow != toRow || newCol != toCol) {
            if (board[newRow][newCol] != null && board[newRow][newCol].getType() != board[fromRow][fromCol].getType()) {
                return true; // Enemy piece found in the path
            }
            newRow += rowDirection;
            newCol += colDirection;
        }

        return false; // No enemy piece found in the path
    }

    /**
     * Gets the starting row index of the move.
     *
     * @return The starting row index
     */
    public int getFromRow() {
        return fromRow;
    }

    /**
     * Gets the starting column index of the move.
     *
     * @return The starting column index
     */
    public int getFromCol() {
        return fromCol;
    }

    /**
     * Gets the destination row index of the move.
     *
     * @return The destination row index
     */
    public int getToRow() {
        return toRow;
    }

    /**
     * Gets the destination column index of the move.
     *
     * @return The destination column index
     */
    public int getToCol() {
        return toCol;
    }
}
