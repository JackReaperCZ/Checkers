/**
 * Represents a piece in a checkers game.
 */
public class Piece {
    private final PieceType type;
    private boolean isQueen;

    /**
     * Constructs a new Piece object with the specified type.
     *
     * @param type The type of the piece (BLACK or RED)
     */
    public Piece(PieceType type) {
        this.type = type;
        this.isQueen = false;
    }

    /**
     * Gets the type of the piece.
     *
     * @return The type of the piece (BLACK or RED)
     */
    public PieceType getType() {
        return type;
    }

    /**
     * Checks if the piece is a queen.
     *
     * @return True if the piece is a queen, false otherwise
     */
    public boolean isQueen() {
        return isQueen;
    }

    /**
     * Sets whether the piece is a queen.
     *
     * @param isQueen True to set the piece as a queen, false otherwise
     */
    public void setQueen(boolean isQueen) {
        this.isQueen = isQueen;
    }
}
