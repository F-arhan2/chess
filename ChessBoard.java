import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class chessboard extends JFrame {

    JLabel[][] board = new JLabel[8][8];
    int selectedRow = -1;
    int selectedCol = -1;
    boolean whiteTurn = true;
    boolean gameOver = false;

    chessboard() {
        setLayout(new GridLayout(8, 8));
        initializeBoard();
        setSize(800, 800);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    void initializeBoard() {

        String[] pieces = {
                "♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜",
                "♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟",
                "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", "",
                "♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙",
                "♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"
        };

        int index = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                if (board[r][c] == null) {
                    board[r][c] = new JLabel("", SwingConstants.CENTER);
                    board[r][c].setFont(new Font("Serif", Font.PLAIN, 60));
                    board[r][c].setOpaque(true);

                    final int row = r, col = c;
                    board[r][c].addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            handleClick(row, col);
                        }
                    });

                    add(board[r][c]);
                }

                board[r][c].setText(pieces[index]);
                board[r][c].setBackground((r + c) % 2 == 0 ? Color.WHITE : Color.GRAY);
                index++;
            }
        }

        whiteTurn = true;
        gameOver = false;
        selectedRow = -1;
        selectedCol = -1;
    }

    boolean isPathClear(int fr, int fc, int tr, int tc) {
        int sr = Integer.signum(tr - fr);
        int sc = Integer.signum(tc - fc);
        int r = fr + sr, c = fc + sc;

        while (r != tr || c != tc) {
            if (!board[r][c].getText().equals(""))
                return false;
            r += sr;
            c += sc;
        }
        return true;
    }

    boolean isWhitePiece(String p) {
        return "♖♘♗♕♔♙".contains(p) && !p.isEmpty();
    }

    boolean isBlackPiece(String p) {
        return "♜♞♝♛♚♟".contains(p) && !p.isEmpty();
    }

    boolean isValidMove(int fr, int fc, int tr, int tc) {

        String piece = board[fr][fc].getText();
        String target = board[tr][tc].getText();

        int dr = tr - fr, dc = tc - fc;
        int absDr = Math.abs(dr), absDc = Math.abs(dc);

        switch (piece) {

            case "♙":
                if (dr == -1 && dc == 0 && target.isEmpty())
                    return true;
                if (dr == -2 && dc == 0 && fr == 6 &&
                        target.isEmpty() && board[fr - 1][tc].getText().isEmpty())
                    return true;
                if (dr == -1 && absDc == 1 && isBlackPiece(target))
                    return true;
                break;

            case "♟":
                if (dr == 1 && dc == 0 && target.isEmpty())
                    return true;
                if (dr == 2 && dc == 0 && fr == 1 &&
                        target.isEmpty() && board[fr + 1][tc].getText().isEmpty())
                    return true;
                if (dr == 1 && absDc == 1 && isWhitePiece(target))
                    return true;
                break;

            case "♖":
                if ((dr == 0 || dc == 0) &&
                        isPathClear(fr, fc, tr, tc) && !isWhitePiece(target))
                    return true;
                break;

            case "♜":
                if ((dr == 0 || dc == 0) &&
                        isPathClear(fr, fc, tr, tc) && !isBlackPiece(target))
                    return true;
                break;

            case "♗":
                if (absDr == absDc &&
                        isPathClear(fr, fc, tr, tc) && !isWhitePiece(target))
                    return true;
                break;

            case "♝":
                if (absDr == absDc &&
                        isPathClear(fr, fc, tr, tc) && !isBlackPiece(target))
                    return true;
                break;

            case "♕":
                if ((absDr == absDc || dr == 0 || dc == 0) &&
                        isPathClear(fr, fc, tr, tc) && !isWhitePiece(target))
                    return true;
                break;

            case "♛":
                if ((absDr == absDc || dr == 0 || dc == 0) &&
                        isPathClear(fr, fc, tr, tc) && !isBlackPiece(target))
                    return true;
                break;

            case "♘":
                if ((absDr == 2 && absDc == 1) || (absDr == 1 && absDc == 2))
                    return !isWhitePiece(target);
                break;

            case "♞":
                if ((absDr == 2 && absDc == 1) || (absDr == 1 && absDc == 2))
                    return !isBlackPiece(target);
                break;

            case "♔":
                if (absDr <= 1 && absDc <= 1 && !isWhitePiece(target))
                    return true;
                break;

            case "♚":
                if (absDr <= 1 && absDc <= 1 && !isBlackPiece(target))
                    return true;
                break;
        }
        return false;
    }

    boolean isCheck(boolean whiteKing) {
        int kr = -1, kc = -1;
        String king = whiteKing ? "♔" : "♚";

        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c].getText().equals(king)) {
                    kr = r;
                    kc = c;
                }

        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                String p = board[r][c].getText();

                if (whiteKing && isBlackPiece(p) && isValidMove(r, c, kr, kc))
                    return true;
                if (!whiteKing && isWhitePiece(p) && isValidMove(r, c, kr, kc))
                    return true;
            }

        return false;
    }

    boolean canEscapeCheck(boolean whitePlayer) {

        for (int fr = 0; fr < 8; fr++) {
            for (int fc = 0; fc < 8; fc++) {

                String piece = board[fr][fc].getText();

                if (whitePlayer && !isWhitePiece(piece))
                    continue;
                if (!whitePlayer && !isBlackPiece(piece))
                    continue;

                for (int tr = 0; tr < 8; tr++) {
                    for (int tc = 0; tc < 8; tc++) {

                        if (!isValidMove(fr, fc, tr, tc))
                            continue;

                        String from = board[fr][fc].getText();
                        String to = board[tr][tc].getText();

                        board[tr][tc].setText(from);
                        board[fr][fc].setText("");

                        boolean stillInCheck = isCheck(whitePlayer);

                        board[fr][fc].setText(from);
                        board[tr][tc].setText(to);

                        if (!stillInCheck)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    void promotePawn(int row, int col) {
        if (board[row][col].getText().equals("♙") && row == 0)
            board[row][col].setText("♕");
        if (board[row][col].getText().equals("♟") && row == 7)
            board[row][col].setText("♛");
    }

    void highlightCheck() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c].setBackground((r + c) % 2 == 0 ? Color.WHITE : Color.GRAY);

        if (isCheck(true))
            highlightKing("♔");
        if (isCheck(false))
            highlightKing("♚");
    }

    void highlightKing(String king) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c].getText().equals(king))
                    board[r][c].setBackground(Color.RED);
    }

    void handleClick(int row, int col) {

        if (gameOver)
            return;

        if (selectedRow == -1) {

            String clicked = board[row][col].getText();
            if (clicked.isEmpty())
                return;

            if (whiteTurn && !isWhitePiece(clicked))
                return;
            if (!whiteTurn && !isBlackPiece(clicked))
                return;

            selectedRow = row;
            selectedCol = col;
            board[row][col].setBackground(Color.YELLOW);

        } else {

            if (isValidMove(selectedRow, selectedCol, row, col)) {

                String from = board[selectedRow][selectedCol].getText();
                String to = board[row][col].getText();

                board[row][col].setText(from);
                board[selectedRow][selectedCol].setText("");

                if (isCheck(whiteTurn)) {
                    board[selectedRow][selectedCol].setText(from);
                    board[row][col].setText(to);
                } else {
                    promotePawn(row, col);
                    whiteTurn = !whiteTurn;
                }
            }

            selectedRow = selectedCol = -1;
            highlightCheck();

            boolean opponent = whiteTurn;

            if (isCheck(opponent) && !canEscapeCheck(opponent)) {
                gameOver = true;
                JOptionPane.showMessageDialog(this, "CHECKMATE!");
                initializeBoard(); // 🔁 restart
            } else if (!isCheck(opponent) && !canEscapeCheck(opponent)) {
                gameOver = true;
                JOptionPane.showMessageDialog(this, "STALEMATE!");
                initializeBoard(); // 🔁 restart
            }
        }
    }

    public static void main(String[] args) {
        new chessboard();
    }
}