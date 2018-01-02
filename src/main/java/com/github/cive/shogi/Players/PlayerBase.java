package com.github.cive.shogi.Players;

import com.github.cive.shogi.Pieces.EmptyPieceBase;
import com.github.cive.shogi.Pieces.PieceBase;

import java.awt.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by yotuba on 16/03/12.
 * Player class
 * have pieces on board and in hand.
 */
public abstract class PlayerBase implements Cloneable{
    public final static int AHEAD = 0;
    public final static int BEHIND = 1;
    static final int FU_OCHI = 0;
    static final int HISHA_KAKU_OCHI = 1;
    public PlayerBase clone() throws CloneNotSupportedException {
        PlayerBase c = (PlayerBase)super.clone();
        c.piecesInHand = new ArrayList<>(piecesInHand);
        c.piecesOnBoard = new ArrayList<>(piecesOnBoard);
        return c;
    }
    private ArrayList<PieceBase> piecesOnBoard = new ArrayList<>();
    private ArrayList<PieceBase> piecesInHand = new ArrayList<>();
    public ArrayList<PieceBase> getPiecesOnBoard() {
        return piecesOnBoard;
    }
    public ArrayList<PieceBase> getPiecesInHand() {
        return piecesInHand;
    }
    public void addPiecesOnBoard(PieceBase p) {
        this.piecesOnBoard.add(p);
    }
    public void addPiecesInHand(PieceBase p) {
        this.piecesInHand.add(p);
    }
    // n <= 34
    // O(n)
    public Stream<PieceBase> getPiecesOnBoard(int type) {
        return piecesOnBoard.stream().filter(x -> x.getTypeOfPiece() == type);
    }
    // O(n)
    public PieceBase getPieceOnBoardAt(Point point_for_gui) {
        for (PieceBase pieceBase : piecesOnBoard) {
            if (pieceBase.getPoint().equals(point_for_gui)) return pieceBase;
        }
        return new EmptyPieceBase(new Point(-1,-1));
    }
    // O(n)
    public Integer getPieceTypeOnBoardAt(Point point_for_gui) {
        for (PieceBase pieceBase : piecesOnBoard) {
            if (pieceBase.getPoint().equals(point_for_gui)) return pieceBase.getTypeOfPiece();
        }
        return PieceBase.NONE;
    }
    // O(n)
    public Boolean reducePieceInHandThatIs(PieceBase p) {
        for (PieceBase pieceBase : piecesInHand) {
            if (java.util.Objects.equals(pieceBase.getTypeOfPiece(), p.getTypeOfPiece())){
                piecesInHand.remove(pieceBase);
                return true;
            }
        }
        return false;
    }
    // O(n)
    public Boolean reducePieceOnBoardAt(Point point_for_gui) {
        for (PieceBase pieceBase : piecesOnBoard) {
            if (pieceBase.getPoint().equals(point_for_gui)){
                piecesOnBoard.remove(pieceBase);
                return true;
            }
        }
        return false;
    }
    // O(n)
    public Boolean matchTypeInHand(PieceBase search) {
        for (PieceBase p : piecesInHand) {
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
    public void update(PlayerBase player) {
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
