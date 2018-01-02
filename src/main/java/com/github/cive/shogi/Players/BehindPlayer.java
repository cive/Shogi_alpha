package com.github.cive.shogi.Players;

import java.awt.*;
import com.github.cive.shogi.Pieces.PieceBase;
import com.github.cive.shogi.Pieces.PieceFactory;

/**
 * Created by yotuba on 16/03/12.
 * behind player (後手)
 */
public class BehindPlayer extends PlayerBase {
    private PieceFactory factory = new PieceFactory();
    public BehindPlayer() {
        setDefault();
    }
    public BehindPlayer(int rule) {
        setInitial(rule);
    }
    @Override
    protected void setFu() {
        for (int x = 0; x < 9; x++)
            this.addPiecesOnBoard(factory.create(PieceBase.FU, new Point(x, 2)));
    }
    @Override
    protected void setKaku() {
        this.addPiecesOnBoard(factory.create(PieceBase.KAKU, new Point(7, 1)));
    }
    @Override
    protected void setHisha() {
        this.addPiecesOnBoard(factory.create(PieceBase.HISHA, new Point(1, 1)));
    }
    @Override
    protected void setKin() {
        this.addPiecesOnBoard(factory.create(PieceBase.KIN, new Point(3, 0)));
        this.addPiecesOnBoard(factory.create(PieceBase.KIN, new Point(5, 0)));
    }
    @Override
    protected void setGin() {
        this.addPiecesOnBoard(factory.create(PieceBase.GIN, new Point(2, 0)));
        this.addPiecesOnBoard(factory.create(PieceBase.GIN, new Point(6, 0)));
    }
    @Override
    protected void setKeima() {
        this.addPiecesOnBoard(factory.create(PieceBase.KEIMA, new Point(1, 0)));
        this.addPiecesOnBoard(factory.create(PieceBase.KEIMA, new Point(7, 0)));
    }
    @Override
    protected void setKyosha() {
        this.addPiecesOnBoard(factory.create(PieceBase.KYOSHA, new Point(0, 0)));
        this.addPiecesOnBoard(factory.create(PieceBase.KYOSHA, new Point(8, 0)));
    }
    @Override
    protected void setGyoku() {
        this.addPiecesOnBoard(factory.create(PieceBase.GYOKU, new Point(4, 0)));
    }
}
