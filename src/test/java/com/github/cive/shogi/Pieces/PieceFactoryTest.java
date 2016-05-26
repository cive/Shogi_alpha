package com.github.cive.shogi.Pieces;

import com.github.cive.shogi.Players.AheadPlayer;
import com.github.cive.shogi.Players.Player;
import com.github.cive.shogi.Pieces.Piece;
import com.github.cive.shogi.Pieces.PieceFactory;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Created by yotuba on 16/05/05.
 * Piece Factory class test
 */
public class PieceFactoryTest {
    @Test
    public void 駒を生成できるか() {
        PieceFactory pf = new PieceFactory();
        assertTrue(pf.create(Piece.FU,new Point(1,2)).getTypeOfPiece() == Piece.FU);
    }
    @Test
    public void 駒を二つ生成() {
        PieceFactory pf = new PieceFactory();
        Player ap = new AheadPlayer();
        Piece p1 = pf.create(Piece.FU, new Point(5, 4));
        Piece p2 = pf.create(Piece.GIN, new Point(3, 4));
        ap.addPiecesOnBoard(pf.create(Piece.FU, new Point(5, 4)));
        ap.addPiecesOnBoard(pf.create(Piece.GIN, new Point(3, 4)));
        assertTrue(ap.getPieceOnBoardAt(new Point(5, 4)).getTypeOfPiece() == Piece.FU);
        assertTrue(ap.getPieceOnBoardAt(new Point(3, 4)).getTypeOfPiece() == Piece.GIN);
    }
}
