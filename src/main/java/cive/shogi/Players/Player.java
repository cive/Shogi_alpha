package cive.shogi.Players;

import cive.shogi.Pieces.*;

import java.awt.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by yotuba on 16/03/12.
 * Player class
 * have pieces on board and in hand.
 */
public abstract class Player implements Cloneable{
    public final static int AHEAD = 0;
    public final static int BEHIND = 1;
    static final int FU_OCHI = 0;
    static final int HISHA_KAKU_OCHI = 1;
    public Player clone() throws CloneNotSupportedException {
        Player c = (Player)super.clone();
        c.piecesInHand = new ArrayList<>(piecesInHand);
        c.piecesOnBoard = new ArrayList<>(piecesOnBoard);
        return c;
    }
    private ArrayList<Piece> piecesOnBoard = new ArrayList<>();
    private ArrayList<Piece> piecesInHand = new ArrayList<>();
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
    public Stream<Piece> getPiecesOnBoard(int type) {
        return piecesOnBoard.stream().filter(x -> x.getTypeOfPiece() == type);
    }
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
    }
    // O(n)
    public Boolean reducePieceInHandThatIs(Piece p) {
        for (Piece piece: piecesInHand) {
            if (java.util.Objects.equals(piece.getTypeOfPiece(), p.getTypeOfPiece())){
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
            if (Objects.equals(p.getTypeOfPiece(), search.getTypeOfPiece())) return true;
        }
        return false;
    }
    protected void setInitial(int rule) {
        piecesOnBoard.clear();
        piecesInHand.clear();
        if(rule == FU_OCHI) {
            setKaku();
            setHisha();
            setGin();
            setKin();
            setKeima();
            setKyosha();
            setGyoku();
        } else if (rule == HISHA_KAKU_OCHI) {
            setFu();
            setGin();
            setKin();
            setKeima();
            setKyosha();
            setGyoku();
        } else {
            setDefault();
        }
    }
    protected void setDefault() {
        setFu();
        setKaku();
        setHisha();
        setGin();
        setKin();
        setKeima();
        setKyosha();
        setGyoku();
    }
    public void update(Player player) {
        piecesInHand.clear();
        piecesOnBoard.clear();
        piecesInHand.addAll(player.getPiecesInHand());
        piecesOnBoard.addAll(player.getPiecesOnBoard());
    }
    abstract protected void setFu();
    abstract protected void setKyosha();
    abstract protected void setKeima();
    abstract protected void setGin();
    abstract protected void setKin();
    abstract protected void setKaku();
    abstract protected void setHisha();
    abstract protected void setGyoku();
}
