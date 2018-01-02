package com.github.cive.shogi.Pieces;

import com.github.cive.shogi.Players.AheadPlayer;
import com.github.cive.shogi.Players.PlayerBase;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Created by yotuba on 16/05/05.
 * PieceBase Factory class test
 */
public class PieceBaseFactoryTest {
    @Test
    public void 駒を生成できるか() {
        PieceFactory pf = new PieceFactory();
        assertTrue(pf.create(PieceBase.FU,new Point(1,2)).getTypeOfPiece() == PieceBase.FU);
    }
    @Test
    public void 駒を二つ生成() {
        PieceFactory pf = new PieceFactory();
        PlayerBase ap = new AheadPlayer();
        PieceBase p1 = pf.create(PieceBase.FU, new Point(5, 4));
        PieceBase p2 = pf.create(PieceBase.GIN, new Point(3, 4));
        ap.addPiecesOnBoard(pf.create(PieceBase.FU, new Point(5, 4)));
        ap.addPiecesOnBoard(pf.create(PieceBase.GIN, new Point(3, 4)));
        assertTrue(ap.getPieceOnBoardAt(new Point(5, 4)).getTypeOfPiece() == PieceBase.FU);
        assertTrue(ap.getPieceOnBoardAt(new Point(3, 4)).getTypeOfPiece() == PieceBase.GIN);
    }
}
