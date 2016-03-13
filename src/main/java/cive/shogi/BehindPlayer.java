package cive.shogi;

import java.awt.*;
import cive.shogi.Pieces.*;

/**
 * Created by yotuba on 16/03/12.
 */
public class BehindPlayer extends Player {
    public BehindPlayer() {
        this.setInitial();
    }
    @Override
    void setInitial(){
        for (int x = 0; x < 9; x++)
            this.addPiecesOnBoard(new Fu(new Point(x, 2)));
        this.addPiecesOnBoard(new Kaku(new Point(7, 1)));
        this.addPiecesOnBoard(new Hisha(new Point(1, 1)));
        this.addPiecesOnBoard(new Gyoku(new Point(4, 0)));
        this.addPiecesOnBoard(new Kin(new Point(3, 0)));
        this.addPiecesOnBoard(new Kin(new Point(5, 0)));
        this.addPiecesOnBoard(new Gin(new Point(2, 0)));
        this.addPiecesOnBoard(new Gin(new Point(6, 0)));
        this.addPiecesOnBoard(new Keima(new Point(1, 0)));
        this.addPiecesOnBoard(new Keima(new Point(7, 0)));
        this.addPiecesOnBoard(new Kyosha(new Point(0, 0)));
        this.addPiecesOnBoard(new Kyosha(new Point(8, 0)));
    }
}
