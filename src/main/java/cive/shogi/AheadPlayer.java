package cive.shogi;

import cive.shogi.Pieces.*;

import java.awt.*;

/**
 * Created by yotuba on 16/03/12.
 */
public class AheadPlayer extends Player {
    static final int FU_OCHI = 0;
    static final int HISHA_KAKU_OCHI = 1;
    public AheadPlayer() {
        this.setInitial();
    }
    // for test
    public AheadPlayer(int rule) {
        if(rule == FU_OCHI) {
            setKaku();
            setHisha();
            setGin();
            setKin();
            setKeima();
            setKyosha();
            this.addPiecesOnBoard(new Gyoku(new Point(4, 8)));
        } else if (rule == HISHA_KAKU_OCHI) {
            setFu();
            setGin();
            setKin();
            setKeima();
            setKyosha();
            this.addPiecesOnBoard(new Gyoku(new Point(4, 8)));
        } else {
            setInitial();
        }
    }
    @Override
    void setInitial(){
        setFu();
        setKaku();
        setHisha();
        setGin();
        setKin();
        setKeima();
        setKyosha();
        this.addPiecesOnBoard(new Gyoku(new Point(4, 8)));
    }
    void setFu() {
        for (int x = 0; x < 9; x++)
            this.addPiecesOnBoard(new Fu(new Point(x, 6)));
    }
    void setKaku() {
        this.addPiecesOnBoard(new Kaku(new Point(1, 7)));
    }
    void setHisha() {
        this.addPiecesOnBoard(new Hisha(new Point(7, 7)));
    }
    void setKin() {
        this.addPiecesOnBoard(new Kin(new Point(3, 8)));
        this.addPiecesOnBoard(new Kin(new Point(5, 8)));
    }
    void setGin() {
        this.addPiecesOnBoard(new Gin(new Point(2, 8)));
        this.addPiecesOnBoard(new Gin(new Point(6, 8)));
    }
    void setKeima() {
        this.addPiecesOnBoard(new Keima(new Point(1, 8)));
        this.addPiecesOnBoard(new Keima(new Point(7, 8)));
    }
    void setKyosha() {
        this.addPiecesOnBoard(new Kyosha(new Point(0, 8)));
        this.addPiecesOnBoard(new Kyosha(new Point(8, 8)));
    }
}
