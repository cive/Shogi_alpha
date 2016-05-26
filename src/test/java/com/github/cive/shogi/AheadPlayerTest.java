package com.github.cive.shogi;

import com.github.cive.shogi.Pieces.Piece;
import com.github.cive.shogi.Pieces.PieceFactory;
import com.github.cive.shogi.Players.AheadPlayer;
import com.github.cive.shogi.Players.Player;
import org.junit.Test;

import java.awt.*;
import static org.junit.Assert.*;

/**
 * Created by yotuba on 16/05/05.
 * ahead player test
 * can take place piece with piece factory class
 */
public class AheadPlayerTest {
    @Test
    public void 駒を設置できるか() {
        Player ap = new AheadPlayer();
        ap.addPiecesOnBoard((new PieceFactory().create(Piece.FU, new Point(1, 2))));
        assertTrue(ap.getPieceOnBoardAt(new Point(1,2)).getTypeOfPiece() == Piece.FU);
    }
}
