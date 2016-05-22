package cive.shogi.Players;

import cive.shogi.Pieces.*;

import java.awt.*;

/**
 * Created by yotuba on 16/03/12.
 * ahead player (先手)
 */
public class AheadPlayer extends Player {
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
            this.addPiecesOnBoard(factory.create(Piece.FU, new Point(x, 6)));
    }
    @Override
    protected void setKaku() {
        this.addPiecesOnBoard(factory.create(Piece.KAKU, new Point(1,7)));
    }
    @Override
    protected void setHisha() {
        this.addPiecesOnBoard(factory.create(Piece.HISHA, new Point(7, 7)));
    }
    @Override
    protected void setKin() {
        this.addPiecesOnBoard(factory.create(Piece.KIN, new Point(3, 8)));
        this.addPiecesOnBoard(factory.create(Piece.KIN, new Point(5, 8)));
    }
    @Override
    protected void setGin() {
        this.addPiecesOnBoard(factory.create(Piece.GIN, new Point(2, 8)));
        this.addPiecesOnBoard(factory.create(Piece.GIN, new Point(6, 8)));
    }
    @Override
    protected void setKeima() {
        this.addPiecesOnBoard(factory.create(Piece.KEIMA, new Point(1, 8)));
        this.addPiecesOnBoard(factory.create(Piece.KEIMA, new Point(7, 8)));
    }
    @Override
    protected void setKyosha() {
        this.addPiecesOnBoard(factory.create(Piece.KYOSHA, new Point(0, 8)));
        this.addPiecesOnBoard(factory.create(Piece.KYOSHA, new Point(8, 8)));
    }
    @Override
    protected void setGyoku() {
        this.addPiecesOnBoard(factory.create(Piece.GYOKU, new Point(4, 8)));
    }
}
