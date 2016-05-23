package cive.shogi;

import cive.shogi.Pieces.Piece;
import cive.shogi.Pieces.PieceFactory;
import cive.shogi.Players.AheadPlayer;
import cive.shogi.Players.BehindPlayer;
import cive.shogi.Players.Player;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Created by yotuba on 16/05/06.
 * 棋譜クラスのテスト
 * 棋譜を登録できるかどうかなど
 */
public class KifuTest {
    @Test
    public void 棋譜の追加() {
        Kifu kifu = new Kifu();
        Player attacker = new AheadPlayer();
        Player defender = new BehindPlayer();
        assertTrue(kifu.getCount() == 0);
        PieceFactory pf = new PieceFactory();
        Point ptSrc = new Point(0, 6);
        Point ptDst = new Point(0, 5);
        Piece src = pf.create(Piece.FU, ptSrc);
        Piece dst = pf.create(Piece.FU, ptDst);
        kifu.update(new MovementOfPiece(src, dst), attacker, defender);
        assertTrue(kifu.getCount() == 1);
        ArrayList<MovementOfPiece> list = kifu.getMovementOfPieceArrayList();
        assertTrue (list.size() == 1);
        for (MovementOfPiece bs : list) {
            try {
                assertEquals(bs.getSrc().getPoint(), ptSrc);
                assertEquals(bs.getDst().getPoint(), ptDst);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void 先手後手の判定() {
        Kifu kifu = new Kifu();
        Player attacker = new AheadPlayer();
        Player defender = new BehindPlayer();
        PieceFactory pf = new PieceFactory();
        MovementOfPiece bs[] = new MovementOfPiece[2];
        Piece src1 = pf.create(Piece.FU, new Point(0, 6));
        Piece dst1 = pf.create(Piece.FU, new Point(0, 5));
        bs[0] = new MovementOfPiece(src1, dst1);
        kifu.update(bs[0], attacker, defender);
        Piece src2 = pf.create(Piece.FU, new Point(0, 5));
        Piece dst2 = pf.create(Piece.FU, new Point(0, 4));
        bs[1] = new MovementOfPiece(src2, dst2);
        kifu.update(bs[1], attacker, defender);
        ArrayList<MovementOfPiece> list = kifu.getMovementOfPieceArrayList();
        assertTrue (list.size() == 2);
        assertTrue (kifu.isBlackTurn());
        try {
            Piece src_l0 = list.get(0).getSrc();
            Piece src_l1 = list.get(1).getSrc();
            Piece dst_l0 = list.get(0).getDst();
            assertTrue (src_l0.getTypeOfPiece() == Piece.FU);
            assertTrue (src_l1.getTypeOfPiece() == Piece.FU);
            assertTrue (Objects.equals(src_l0.getTypeOfPiece(), dst_l0.getTypeOfPiece()));
            assertEquals (src_l0.getPoint(), new Point(0,6));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void 前の手に戻す() {
        Kifu kifu = new Kifu();
        Player attacker = new AheadPlayer();
        Player defender = new BehindPlayer();
        MovementOfPiece bs[] = new MovementOfPiece[2];
        assertFalse(kifu.hasAhead(-1));
        PieceFactory pf = new PieceFactory();
        Piece src1 = pf.create(Piece.FU, new Point(0, 6));
        Piece dst1 = pf.create(Piece.FU, new Point(0, 5));
        assertFalse(kifu.hasAhead(1));
        bs[0] = new MovementOfPiece(src1, dst1);
        kifu.update(bs[0], attacker, defender);
        assertTrue(kifu.hasAhead(1));
        assertFalse(kifu.hasAhead(2));
        Piece src2 = pf.create(Piece.FU, new Point(0, 5));
        Piece dst2 = pf.create(Piece.FU, new Point(0, 4));
        bs[1] = new MovementOfPiece(src2, dst2);
        kifu.update(bs[1], attacker, defender);
        assertTrue(kifu.hasAhead(1));
        assertTrue(kifu.hasAhead(2));
        if(kifu.hasAhead(1)) { /* 一つ前に手があるか */
            kifu.undo(1); /* 棋譜は保存したままカウントを減らす */
        }
        assertTrue(kifu.getCount() == 1);
        assertTrue(kifu.getMovementOfPieceArrayList().size() == 2);
        assertFalse(kifu.isBlackTurn());
    }
    @Test
    public void 元の手に戻す() {
        /**
         * 本来ならば、プレイヤーの持ち駒が変化するが、
         * テストなので省略。
         */
        Kifu kifu = new Kifu();
        Player attacker = new AheadPlayer();
        Player defender = new BehindPlayer();
        MovementOfPiece bs[] = new MovementOfPiece[2];
        PieceFactory pf = new PieceFactory();
        Piece src1 = pf.create(Piece.FU, new Point(0, 6));
        Piece dst1 = pf.create(Piece.FU, new Point(0, 5));
        assertFalse(kifu.hasAhead(1));
        bs[0] = new MovementOfPiece(src1, dst1);
        kifu.update(bs[0], attacker, defender);
        Piece src2 = pf.create(Piece.FU, new Point(0, 5));
        Piece dst2 = pf.create(Piece.FU, new Point(0, 4));
        bs[1] = new MovementOfPiece(src2, dst2);
        kifu.update(bs[1], attacker, defender);
        if(kifu.hasAhead(1)) {
            kifu.undo(1);
        }
        assertTrue(kifu.getCount() == 1);
        assertTrue(kifu.getMovementOfPieceArrayList().size() == 2);
        if(kifu.hasNext(1)) {
            kifu.redo(1);
        }
        assertTrue(kifu.getCount() == 2);
        assertTrue(kifu.getMovementOfPieceArrayList().size() == 2);
    }
    @Test
    public void 初期配置の登録() {
        Kifu kifu = new Kifu();
        Player attacker = new AheadPlayer();
        Player defender = new BehindPlayer();
        kifu.setInitialPlayers(attacker, defender);
        Point p = new Point(0, 6);
        try {
            assertEquals(attacker.getPieceTypeOnBoardAt(p), kifu.getIniAttacker().getPieceTypeOnBoardAt(p));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
