package com.github.cive.shogi.Players;

import com.github.cive.shogi.Pieces.PieceBase;
import com.github.cive.shogi.Pieces.PieceFactory;

import java.awt.*;

/**
 * Created by yotuba on 16/03/12.
 * ahead player (先手)
 */
public class AheadPlayer extends PlayerBase {
    private PieceFactory factory = new PieceFactory();
    // for test
    public AheadPlayer() {
        setDefault();
    }
    public AheadPlayer(int rule) {
        setInitial(rule);
    }
    @Override
    protected void setFu() {
        for (int x = 0; x < 9; x++)
            this.addPiecesOnBoard(factory.create(PieceBase.FU, new Point(x, 6)));
    }
    @Override
    protected void setKaku() {
        this.addPiecesOnBoard(factory.create(PieceBase.KAKU, new Point(1,7)));
    }
    @Override
    protected void setHisha() {
        this.addPiecesOnBoard(factory.create(PieceBase.HISHA, new Point(7, 7)));
    }
    @Override
    protected void setKin() {
        this.addPiecesOnBoard(factory.create(PieceBase.KIN, new Point(3, 8)));
        this.addPiecesOnBoard(factory.create(PieceBase.KIN, new Point(5, 8)));
    }
    @Override
    protected void setGin() {
        this.addPiecesOnBoard(factory.create(PieceBase.GIN, new Point(2, 8)));
        this.addPiecesOnBoard(factory.create(PieceBase.GIN, new Point(6, 8)));
    }
    @Override
    protected void setKeima() {
        this.addPiecesOnBoard(factory.create(PieceBase.KEIMA, new Point(1, 8)));
        this.addPiecesOnBoard(factory.create(PieceBase.KEIMA, new Point(7, 8)));
    }
    @Override
    protected void setKyosha() {
        this.addPiecesOnBoard(factory.create(PieceBase.KYOSHA, new Point(0, 8)));
        this.addPiecesOnBoard(factory.create(PieceBase.KYOSHA, new Point(8, 8)));
    }
    @Override
    protected void setGyoku() {
        this.addPiecesOnBoard(factory.create(PieceBase.GYOKU, new Point(4, 8)));
    }
}
