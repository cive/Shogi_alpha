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
        assertTrue(gameBoard.getAttacker().getPieceOnBoardAt(new Point(6,6)).getCapablePutPoint(
                gameBoard.getAttacker(), gameBoard.getDefender()
        ).size() > 0);
        assertTrue(gameBoard.canPlaceInside(new Point(6,6), new Point(6,5)));
        assertFalse(gameBoard.canPlaceInside(new Point(7,1), new Point(6,2)));
        assertFalse(gameBoard.canPlaceInside(new Point(7,7), new Point(7,5)));
        assertFalse(gameBoard.canPlaceInside(new Point(1,7), new Point(3,5)));
        gameBoard.replacePiece(new Point(6,6), new Point(6,5));
        assertTrue(gameBoard.canPlaceInside(new Point(2,0), new Point(3,1)));
    }

    @Test
    public void 盤上で駒を移動する() {
        GameBoard gameBoard = new GameBoard();
        assertTrue(gameBoard.getAttacker().getPieceOnBoardAt(new Point(6,6)).getCapablePutPoint(
                gameBoard.getAttacker(), gameBoard.getDefender()
        ).size() > 0);
        assertTrue(gameBoard.canPlaceInside(new Point(6,6), new Point(6,5)));
        gameBoard.replacePiece(new Point(6,6), new Point(6,5));
        assertTrue(gameBoard.getDefender().getPieceTypeOnBoardAt(new Point(6,5)) > 0);
    }

    @Test
    public void nextTurnで手番を変える() {
        GameBoard gameBoard = new GameBoard();
        assertTrue(gameBoard.isAheadsTurn());
        gameBoard.nextTurn();
        assertFalse(gameBoard.isAheadsTurn());
    }

    @Test
    public void 成駒できるか() {
        GameBoard gameBoard = new GameBoard(0);
        assertTrue(gameBoard.getAttacker().getPieceOnBoardAt(new Point(7,7)).canPromote(new Point(7,2), gameBoard.isAheadsTurn()));
        gameBoard.replacePieceWithPromote(new Point(7,7), new Point(7,2));
        assertTrue(gameBoard.getDefender().getPieceTypeOnBoardAt(new Point(7,2)) == Piece.RYU);
    }

    @Test
    public void 駒を入手できるか() {
        GameBoard gameBoard = new GameBoard(0);
        gameBoard.replacePieceWithPromote(new Point(7,7), new Point(7,2));
        assertTrue(gameBoard.getDefender().getPiecesInHand().get(0).getTypeOfPiece() == Piece.FU);
    }
}
