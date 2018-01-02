package com.github.cive.shogi;

import com.github.cive.shogi.Pieces.PieceBase;
import com.github.cive.shogi.Pieces.PieceFactory;
import com.github.cive.shogi.Players.AheadPlayer;
import com.github.cive.shogi.Players.PlayerBase;
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
        PlayerBase ap = new AheadPlayer();
        ap.addPiecesOnBoard((new PieceFactory().create(PieceBase.FU, new Point(1, 2))));
        assertTrue(ap.getPieceOnBoardAt(new Point(1,2)).getTypeOfPiece() == PieceBase.FU);
    }
}
