import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CheckersPanel class represents the GUI panel for the checkers game.
 * It handles the game logic and rendering.
 */
public class CheckersPanel extends JPanel {
    private static final int BOARD_SIZE = 8; // Size of the board
    private static final int TILE_SIZE = 100; // Size of each tile
    private BufferedImage crown = null; // Image for indicating a queen piece
    private final Color darkBoardColor = new Color(85, 136, 34); // Dark color for the board tiles
    private final Color lightBoardColor = new Color(255, 238, 187); // Light color for the board tiles

    private Piece[][] board; // 2D array representing the game board
    private boolean isPlayerTurn = true; // Indicates whether it's the player's turn
    private int selectedRow = -1; // Row of the selected piece
    private int selectedCol = -1; // Column of the selected piece

    /**
     * Constructs a new CheckersPanel.
     * Initializes the game board and sets up mouse listener for interaction.
     */
    public CheckersPanel() {
        try {
            this.crown = ImageIO.read(new File("res/crown.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = e.getY() / TILE_SIZE;
                int col = e.getX() / TILE_SIZE;
                handleMouseClick(row, col);
                repaint();
            }
        });
    }

    /**
     * Initializes the game board with pieces in their starting positions.
     */
    private void initializeBoard() {
        // Clear the board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
            }
        }
        // Set up pieces for both players
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 != 0) {
                    if (row < 3) {
                        board[row][col] = new Piece(PieceType.RED);
                    } else if (row > 4) {
                        board[row][col] = new Piece(PieceType.BLACK);
                    }
                }
            }
        }
    }

    /**
     * Handles mouse click events on the game board.
     *
     * @param row Row index of the clicked tile
     * @param col Column index of the clicked tile
     */
    private void handleMouseClick(int row, int col) {
        if (isPlayerTurn) {
            // If no piece is selected
            if (selectedRow == -1) {
                // Select the piece if it belongs to the player
                if (board[row][col] != null && board[row][col].getType() == PieceType.RED) {
                    selectedRow = row;
                    selectedCol = col;
                }
            } else {
                // If a piece is already selected, try to move it to the clicked tile
                if (isValidMove(selectedRow, selectedCol, row, col)) {
                    if (board[selectedRow][selectedCol].isQueen()) {
                        if (Move.hasPassedThroughEnemy(selectedRow, selectedCol, row, col, board)) {
                            int[] dir = Move.translateMoveDirection(selectedRow, selectedCol, row, col);
                            board[row - dir[0]][col - dir[1]] = null;
                        }
                    }
                    movePiece(selectedRow, selectedCol, row, col);
                    if (checkGameEnd()) {
                        return;
                    }
                    isPlayerTurn = false;
                    handleAIMove();
                }
                selectedRow = -1;
                selectedCol = -1;
            }
        }
    }

    /**
     * Checks if a move is valid.
     *
     * @param fromRow Starting row of the piece
     * @param fromCol Starting column of the piece
     * @param toRow   Destination row of the piece
     * @param toCol   Destination column of the piece
     * @return True if the move is valid, otherwise false
     */
    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
// Check if the destination is within the board bounds
        if (toRow < 0 || toRow >= BOARD_SIZE || toCol < 0 || toCol >= BOARD_SIZE) {
            return false;
        }
        // Check if the destination tile is empty
        if (board[toRow][toCol] != null) {
            return false;
        }
        // Calculate row and column differences
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;
        // Get the type and queen status of the moving piece
        PieceType type = board[fromRow][fromCol].getType();
        boolean isQueen = board[fromRow][fromCol].isQueen();

        // Regular piece movement
        if (!isQueen) {
            // Check direction and distance
            if (type == PieceType.RED && rowDiff <= 0) {
                return false; // White pieces can only move downwards
            }
            if (type == PieceType.BLACK && rowDiff >= 0) {
                return false; // Black pieces can only move upwards
            }

            if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1) {
                return true; // Single step movement
            }

            if (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 2) {
                int captureRow = fromRow + rowDiff / 2;
                int captureCol = fromCol + colDiff / 2;
                return board[captureRow][captureCol] != null && board[captureRow][captureCol].getType() != type;
            }
        } else { // Queen piece movement
            int[] direction = Move.translateMoveDirection(fromRow, fromCol, toRow, toCol);

            int newRow = fromRow + direction[0];
            int newCol = fromCol + direction[1];
            boolean hasCaptured = false;

            while (newRow != toRow || newCol != toCol) {
                if (board[newRow][newCol] != null) {
                    if (board[newRow][newCol].getType() == type || hasCaptured) {
                        return false;
                    }
                    hasCaptured = true;
                }
                newRow += direction[0];
                newCol += direction[1];
            }

            return true;
        }

        return false;
    }

    /**
     * Moves a piece on the board from the starting position to the destination.
     *
     * @param fromRow Starting row of the piece
     * @param fromCol Starting column of the piece
     * @param toRow   Destination row of the piece
     * @param toCol   Destination column of the piece
     */
    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        // Handle capturing move
        int rowDiff = toRow - fromRow;
        if (Math.abs(rowDiff) == 2) {
            int captureRow = fromRow + rowDiff / 2;
            int captureCol = fromCol + (toCol - fromCol) / 2;
            board[captureRow][captureCol] = null;
        }
        // Move the piece to the destination
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = null;

        // Promote piece to queen if it reaches the opposite end
        if ((board[toRow][toCol].getType() == PieceType.RED && toRow == BOARD_SIZE - 1) ||
                (board[toRow][toCol].getType() == PieceType.BLACK && toRow == 0)) {
            board[toRow][toCol].setQueen(true);
        }
    }

    /**
     * Handles the AI's move.
     */
    private void handleAIMove() {
        // Get all possible capture moves for the AI
        List<Move> captureMoves = getAllPossibleMoves(PieceType.BLACK, true);
        Move selectedMove = null;

        if (!captureMoves.isEmpty()) {
            selectedMove = captureMoves.get((int) (Math.random() * captureMoves.size()));
        } else {
            // If no capture moves available, select a simple move
            List<Move> simpleMoves = getAllPossibleMoves(PieceType.BLACK, false);
            if (!simpleMoves.isEmpty()) {
                selectedMove = simpleMoves.get((int) (Math.random() * simpleMoves.size()));
            }
        }
        // Perform the selected move
        if (selectedMove != null) {
            if (board[selectedMove.getFromRow()][selectedMove.getFromCol()].isQueen()) {
                if (Move.hasPassedThroughEnemy(selectedMove.getFromRow(), selectedMove.getFromCol(), selectedMove.getToRow(), selectedMove.getToCol(), board)) {
                    int[] dir = Move.translateMoveDirection(selectedMove.getFromRow(), selectedMove.getFromCol(), selectedMove.getToRow(), selectedMove.getToCol());
                    board[selectedMove.getToRow() - dir[0]][selectedMove.getToCol() - dir[1]] = null;
                }
            }
            movePiece(selectedMove.getFromRow(), selectedMove.getFromCol(), selectedMove.getToRow(), selectedMove.getToCol());
        }

        if (checkGameEnd()) {
            return;
        }

        isPlayerTurn = true;
    }

    /**
     * Gets all possible moves for a given piece type.
     *
     * @param type        Type of the pieces (RED or BLACK)
     * @param captureOnly Flag to indicate if only capture moves should be considered
     * @return List of all possible moves
     */
    private List<Move> getAllPossibleMoves(PieceType type, boolean captureOnly) {
        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != null && board[row][col].getType() == type) {
                    moves.addAll(getPossibleMovesForPiece(row, col, captureOnly));
                }
            }
        }
        return moves;
    }

    /**
     * Gets all possible moves for a piece at the given position.
     *
     * @param row         Row index of the piece
     * @param col         Column index of the piece
     * @param captureOnly Flag to indicate if only capture moves should be considered
     * @return List of all possible moves for the piece
     */
    private List<Move> getPossibleMovesForPiece(int row, int col, boolean captureOnly) {
        List<Move> moves = new ArrayList<>();
        Piece piece = board[row][col];
        if (piece == null) {
            return moves;
        }

        int[][] directions;
        if (piece.isQueen()) {
            directions = new int[][]{{1, -1}, {1, 1}, {-1, -1}, {-1, 1}};
        } else if (piece.getType() == PieceType.RED) {
            directions = new int[][]{{1, -1}, {1, 1}};
        } else {
            directions = new int[][]{{-1, -1}, {-1, 1}};
        }

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            while (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE) {
                if (isValidMove(row, col, newRow, newCol)) {
                    if (!captureOnly) {
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                } else {
                    int captureRow = newRow + direction[0];
                    int captureCol = newCol + direction[1];
                    if (isValidMove(row, col, captureRow, captureCol)) {
                        moves.add(new Move(row, col, captureRow, captureCol));
                    }
                    break;
                }

                if (piece.isQueen()) {
                    newRow += direction[0];
                    newCol += direction[1];
                } else {
                    break;
                }
            }
        }

        return moves;
    }

    /**
     * Checks if the game has ended.
     *
     * @return True if the game has ended, otherwise false
     */
    private boolean checkGameEnd() {
        boolean redExists = false;
        boolean blackExists = false;

        // Check if both players still have pieces on the board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != null) {
                    if (board[row][col].getType() == PieceType.RED) {
                        redExists = true;
                    } else if (board[row][col].getType() == PieceType.BLACK) {
                        blackExists = true;
                    }
                }
            }
        }

        if (!redExists) {
            new CheckersDialog((Frame) SwingUtilities.getWindowAncestor(this), "Game Over", "Black wins!", this);
            return true;
        } else if (!blackExists) {
            new CheckersDialog((Frame) SwingUtilities.getWindowAncestor(this), "Game Over", "Red wins!", this);
            return true;
        }
        // Check if any player has no legal moves left
        if (getAllPossibleMoves(PieceType.RED, false).isEmpty()) {
            new CheckersDialog((Frame) SwingUtilities.getWindowAncestor(this), "Game Over", "Black wins! Red has no moves left.", this);
            return true;
        } else if (getAllPossibleMoves(PieceType.BLACK, false).isEmpty()) {
            new CheckersDialog((Frame) SwingUtilities.getWindowAncestor(this), "Game Over", "Red wins! Black has no moves left.", this);
            return true;
        }

        return false;
    }

    /**
     * Paints the components of the game board.
     *
     * @param g Graphics object for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
        drawSelection(g);
    }

    /**
     * Draws the tiles of the game board.
     *
     * @param g Graphics object for drawing
     */
    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(lightBoardColor);
                } else {
                    g.setColor(darkBoardColor);
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /**
     * Draws the pieces on the game board.
     *
     * @param g Graphics object for drawing
     */
    private void drawPieces(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null) {
                    if (piece.getType() == PieceType.RED) {
                        g.setColor(Color.RED);
                    } else {
                        g.setColor(Color.BLACK);
                    }
                    g.fillOval(col * TILE_SIZE + 10, row * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                    g.setColor(Color.WHITE);
                    g.drawOval(col * TILE_SIZE + 10, row * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                    if (piece.isQueen()) {
                        g.drawImage(crown, col * TILE_SIZE + 15, row * TILE_SIZE + 15, null);
                    }
                }
            }
        }
    }

    /**
     * Draws the selection highlight around the selected piece and its possible moves.
     *
     * @param g Graphics object for drawing
     */
    private void drawSelection(Graphics g) {
        if (selectedRow != -1 && selectedCol != -1) {
            g.setColor(Color.YELLOW);
            g.drawRect(selectedCol * TILE_SIZE, selectedRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            List<Move> allMoves = getPossibleMovesForPiece(selectedRow, selectedCol, false);

            g.setColor(Color.ORANGE);
            for (Move m : allMoves) {
                g.drawRect(m.getToCol() * TILE_SIZE, m.getToRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /**
     * Restarts the game by resetting the board and other game state.
     */
    public void restart() {
        initializeBoard();
        isPlayerTurn = true;
        selectedRow = -1;
        selectedCol = -1;
        repaint();
    }
}
