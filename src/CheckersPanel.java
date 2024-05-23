import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CheckersPanel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 100;

    private Piece[][] board;
    private boolean isPlayerTurn = true;
    private int selectedRow = -1;
    private int selectedCol = -1;

    public CheckersPanel() {
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

    private void initializeBoard() {
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

    private void handleMouseClick(int row, int col) {
        if (isPlayerTurn) {
            if (selectedRow == -1) {
                if (board[row][col] != null && board[row][col].getType() == PieceType.RED) {
                    selectedRow = row;
                    selectedCol = col;
                }
            } else {
                if (isValidMove(selectedRow, selectedCol, row, col)) {
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

    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < 0 || toRow >= BOARD_SIZE || toCol < 0 || toCol >= BOARD_SIZE) {
            return false;
        }

        if (board[toRow][toCol] != null) {
            return false;
        }

        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        PieceType type = board[fromRow][fromCol].getType();

        // Check direction
        if (type == PieceType.RED && rowDiff <= 0) {
            return false; // Red pieces can only move downwards
        }
        if (type == PieceType.BLACK && rowDiff >= 0) {
            return false; // Black pieces can only move upwards
        }

        if (Math.abs(rowDiff) == 1 && colDiff == 1) {
            return true;
        }

        if (Math.abs(rowDiff) == 2 && colDiff == 2) {
            int captureRow = fromRow + rowDiff / 2;
            int captureCol = fromCol + (toCol - fromCol) / 2;
            if (board[captureRow][captureCol] != null && board[captureRow][captureCol].getType() != type) {
                return true;
            }
        }

        return false;
    }

    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = toRow - fromRow;
        if (Math.abs(rowDiff) == 2) {
            int captureRow = fromRow + rowDiff / 2;
            int captureCol = fromCol + (toCol - fromCol) / 2;
            board[captureRow][captureCol] = null; // Remove the captured piece
        }
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = null;
    }

    private void handleAIMove() {
        List<Move> captureMoves = getAllPossibleMoves(PieceType.BLACK, true);
        Move selectedMove = null;

        if (!captureMoves.isEmpty()) {
            selectedMove = captureMoves.get((int) (Math.random() * captureMoves.size()));
        } else {
            List<Move> simpleMoves = getAllPossibleMoves(PieceType.BLACK, false);
            if (!simpleMoves.isEmpty()) {
                selectedMove = simpleMoves.get((int) (Math.random() * simpleMoves.size()));
            }
        }

        if (selectedMove != null) {
            movePiece(selectedMove.fromRow, selectedMove.fromCol, selectedMove.toRow, selectedMove.toCol);
        }

        if (checkGameEnd()) {
            return;
        }

        isPlayerTurn = true;
    }

    private List<Move> getAllPossibleMoves(PieceType type, boolean captureOnly) {
        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != null && board[row][col].getType() == type) {
                    for (int dRow = -2; dRow <= 2; dRow++) {
                        for (int dCol = -2; dCol <= 2; dCol++) {
                            if (Math.abs(dRow) == Math.abs(dCol) && row + dRow >= 0 && row + dRow < BOARD_SIZE && col + dCol >= 0 && col + dCol < BOARD_SIZE) {
                                if (isValidMove(row, col, row + dRow, col + dCol)) {
                                    if (captureOnly && Math.abs(dRow) == 2) {
                                        moves.add(new Move(row, col, row + dRow, col + dCol));
                                    } else if (!captureOnly && Math.abs(dRow) == 1) {
                                        moves.add(new Move(row, col, row + dRow, col + dCol));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    private boolean checkGameEnd() {
        boolean redExists = false;
        boolean blackExists = false;

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
            JOptionPane.showMessageDialog(this, "Black wins!");
            return true;
        } else if (!blackExists) {
            JOptionPane.showMessageDialog(this, "Red wins!");
            return true;
        }

        if (getAllPossibleMoves(PieceType.RED, false).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Black wins! Red has no moves left.");
            return true;
        } else if (getAllPossibleMoves(PieceType.BLACK, false).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Red wins! Black has no moves left.");
            return true;
        }

        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
        drawSelection(g);
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

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
                }
            }
        }
    }

    private void drawSelection(Graphics g) {
        if (selectedRow != -1 && selectedCol != -1) {
            g.setColor(Color.YELLOW);
            g.drawRect(selectedCol * TILE_SIZE, selectedRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    class Move {
        int fromRow, fromCol, toRow, toCol;

        Move(int fromRow, int fromCol, int toRow, int toCol) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
        }
    }

    enum PieceType {
        RED, BLACK
    }

    class Piece {
        private PieceType type;

        public Piece(PieceType type) {
            this.type = type;
        }

        public PieceType getType() {
            return type;
        }
    }
}
