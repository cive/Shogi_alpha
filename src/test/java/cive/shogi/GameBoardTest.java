package cive.shogi;

import cive.shogi.Pieces.EmptyPiece;
import cive.shogi.Pieces.Piece;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class GameBoardTest {

    @Test
    public void getPieceOfで盤外を選択したときに返すのはEmptyPiece() throws Exception {
        GameBoard gameBoard = new GameBoard();
        assertTrue(gameBoard.getPieceOf(new Point(-1, 0)) instanceof EmptyPiece);
        assertTrue(gameBoard.getPieceOf(new Point(13124,5151)) instanceof EmptyPiece);
    }

    @Test
    public void isInGridが盤内かどうかを返す() throws Exception {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                assertTrue(GameBoard.isInGrid(new Point(i,j)));
            }
        }
        for(int i = -1; i > -3; i--) {
            for(int j = -1; j > -3; j--) {
                assertFalse(GameBoard.isInGrid(new Point(i,j)));
            }
        }
    }

    @Test
    public void 盤上で駒を移動できるかを判定する() {
        GameBoard gameBoard = new GameBoard();
        assertTrue(gameBoard.canPlaceInside(new Point(6,6), new Point(6,5)));
        assertFalse(gameBoard.canPlaceInside(new Point(7,1), new Point(6,2)));
    }

    @Test
    public void nextTurnで手番を変える() {
        GameBoard gameBoard = new GameBoard();
        assertTrue(gameBoard.isBlacksTurn());
        gameBoard.nextTurn();
        assertFalse(gameBoard.isBlacksTurn());
    }

}
