package cive.shogi;

import cive.shogi.Pieces.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by yotuba on 16/03/12.
 */
public abstract class Player {
    public final static int AHEAD = 0;
    public final static int BEHIND = 1;
    private ArrayList<Piece> piecesOnBoard = new ArrayList<>();
    private ArrayList<Piece> piecesInHand = new ArrayList<>();
    abstract void setInitial();
    public ArrayList<Piece> getPiecesOnBoard() {
        return piecesOnBoard;
    }
    public ArrayList<Piece> getPiecesInHand() {
        return piecesInHand;
    }
    public void addPiecesOnBoard(Piece p) {
        this.piecesOnBoard.add(p);
    }
    public void addPiecesInHand(Piece p) {
        this.piecesInHand.add(p);
    }
    // n <= 34
    // O(n)
    public Piece getPieceOnBoardAt(Point p) {
        for (Piece piece : piecesOnBoard) {
            if (piece.getPoint().equals(p)) return piece;
        }
        return new EmptyPiece(new Point(-1,-1));
    }
    // O(n)
    public Integer getPieceTypeOnBoardAt(Point p) {
        for (Piece piece : piecesOnBoard) {
            if (piece.getPoint().equals(p)) return piece.getTypeOfPiece();
        }
        return Piece.NONE;
    };
    // O(n)
    public Boolean reducePieceInHandThatIs(Piece p) {
        for (Piece piece: piecesInHand) {
            if (piece.getTypeOfPiece() == p.getTypeOfPiece()){
                piecesInHand.remove(piece);
                return true;
            }
        }
        return false;
    }
    // O(n)
    public Boolean reducePieceOnBoardAt(Point p) {
        for (Piece piece: piecesOnBoard) {
            if (piece.getPoint().equals(p)){
                piecesOnBoard.remove(piece);
                return true;
            }
        }
        return false;
    }
    // O(n)
    public Boolean matchTypeInHand(Piece search) {
        for (Piece p : piecesInHand) {
            if (p.getTypeOfPiece() == search.getTypeOfPiece()) return true;
        }
        return false;
    }
}
