package cive.shogi;

import cive.shogi.Pieces.*;

import java.awt.*;
import java.util.*;

public class GameBoard {
    private Piece board_Arr[][] = new Piece[9][9];
    private boolean turn = true;
    public GameBoard() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                board_Arr[i][j] = new EmptyPiece();
            }
        }
        initGame();
        // TODO: delete debug
        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 9; y++) {
                System.out.print(board_Arr[x][y].getName());
            }
            System.out.println();
        }
        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 9; y++) {
                System.out.print(board_Arr[x][y].isBlack()?"黒":"白");
            }
            System.out.println();
        }
    }
    private void initGame() {
        setTurn(true);
        board_Arr = new Piece[][]{
                {new KyoshaOfPiece(), new KeimaOfPiece(), new GinOfPiece(), new KinOfPiece(), new GyokuOfPiece(), new KinOfPiece(), new GinOfPiece(), new KeimaOfPiece(), new KyoshaOfPiece() },
                {new EmptyPiece(), new HishaOfPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new KakuOfPiece(), new EmptyPiece() },
                {new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece() },
                {new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece() },
                {new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece() },
                {new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece() },
                {new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece(), new FuOfPiece() },
                {new EmptyPiece(), new KakuOfPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new EmptyPiece(), new HishaOfPiece(), new EmptyPiece() },
                {new KyoshaOfPiece(), new KeimaOfPiece(), new GinOfPiece(), new KinOfPiece(), new GyokuOfPiece(), new KinOfPiece(), new GinOfPiece(), new KeimaOfPiece(), new KyoshaOfPiece() }
        };
        for(int i = 0; i < 9; i++) {
            board_Arr[8][i].setTurn(true);
            board_Arr[6][i].setTurn(true);
        }
        board_Arr[7][1].setTurn(true);
        board_Arr[7][7].setTurn(true);
        // isBlack && getTypeOfPiece > 0 : exist.
    }
    public void setBoard_Arr(Piece piece, Point p) {
        if(this.isInGrid(p))board_Arr[p.y][p.x] = piece;
    }
    public Piece getPieceOf(Point p) {
        if(this.isInGrid(p))return board_Arr[p.y][p.x];
        else return new EmptyPiece();
    }
    public Piece getPieceOf(int x, int y) {
        return this.getPieceOf(new Point(x, y));
    }
    public static boolean isInGrid(Point point) {
        if(point.x >= 0 && point.x < 9
                && point.y >= 0 && point.y < 9) return true;
        else return false;
    }
    public boolean isBlack() {
        return turn;
    }
    public void setTurn(boolean turn) {
        this.turn = turn;
    }
    public void nextTurn() {
        this.turn = !turn;
    }

    public void logicOfInside(Point move_before, Point move_after) {
        Piece that = this.getPieceOf(move_before.x, move_before.y);
        Iterator itr = that.getCapableMovePiece(this, move_before).iterator();
        for (; itr.hasNext(); ) {
            Point capable_move_point = (Point) itr.next();
            boolean canMove = move_after.x == capable_move_point.x && move_after.y == capable_move_point.y;
            if (canMove) {
                Piece clicked_piece = this.getPieceOf(move_after);
                if (clicked_piece.getTypeOfPiece() > 0) {
                    this.addPieceInHand(clicked_piece);
                }
                this.setBoard_Arr(that, move_after);
                this.setBoard_Arr(new EmptyPiece(), move_before);
                this.nextTurn();
            }
        }
    }
    private ArrayList<Piece> piece_inHand_of_black = new ArrayList<>();
    private ArrayList<Piece> piece_inHand_of_white = new ArrayList<>();
    public void addPieceInHand(Piece others) {
        if(others.isBlack()) {
            others.setTurn(false);
            piece_inHand_of_white.add(others);
        } else {
            others.setTurn(true);
            piece_inHand_of_black.add(others);
        }
    }
    public ArrayList<Piece> getPieces_inHand_of_black() {
        return piece_inHand_of_black;
    }
    public ArrayList<Piece> getPieces_inHand_of_white() {
        return piece_inHand_of_white;
    }
}
