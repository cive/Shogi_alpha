/**
 * GameBoard.java
 * 将棋盤の盤上の処理はここで行う
 */

package com.github.cive.shogi;

import com.github.cive.shogi.Pieces.EmptyPiece;
import com.github.cive.shogi.Pieces.Piece;
import com.github.cive.shogi.Pieces.PieceFactory;
import com.github.cive.shogi.Players.AheadPlayer;
import com.github.cive.shogi.Players.BehindPlayer;
import com.github.cive.shogi.Players.Player;

import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

public class GameBoard {
    private Kifu kifu;
    private Player attacker;
    private Player defender;
    private Player playerA;
    private Player playerB;
    public GameBoard() {
        initGame();
        //printBoard();
    }
    public GameBoard(int rule) {
        initGame(rule);
        //printBoard();
    }
    public Boolean isAheadsTurn() {
        return attacker instanceof AheadPlayer;
    }
    public void initGame() {
        playerA = new AheadPlayer();
        playerB = new BehindPlayer();
        kifu = new Kifu();
        kifu.setInitialPlayers(playerA, playerB);
        setTurn(true);
    }
    public void initGame(int rule) {
        playerA = new AheadPlayer(rule);
        playerB = new BehindPlayer();
        kifu = new Kifu();
        kifu.setInitialPlayers(playerA, playerB);
        setTurn(true);
    }
    public Piece getPieceOf(Point p) {
        if(GameBoard.isInGrid(p))return attacker.getPieceOnBoardAt(p);
        else return new EmptyPiece(new Point(-1,-1));
    }
    public Piece getPieceOf(int x, int y) {
        return this.getPieceOf(new Point(x, y));
    }
    public static boolean isInGrid(Point point) {
        return point.x >= 0 && point.x < 9
                && point.y >= 0 && point.y < 9;
    }
    public void setTurn(boolean aheadsTurn) {
        if(aheadsTurn) {
            this.attacker = playerA;
            this.defender = playerB;
        } else {
            this.attacker = playerB;
            this.defender = playerA;
        }
    }
    public void nextTurn() {
        System.out.println("attOnBoa: " + attacker.getPiecesOnBoard());
        System.out.println("attInHan: " + attacker.getPiecesInHand());
        System.out.println("defOnBoa: " + defender.getPiecesOnBoard());
        System.out.println("defInHan: " + defender.getPiecesInHand());
        if(this.attacker instanceof BehindPlayer) {
            this.attacker = playerA;
            this.defender = playerB;
        } else {
            this.attacker = playerB;
            this.defender = playerA;
        }
    }
    public Player getAttacker() {
        return attacker;
    }
    public Player getDefender() {
        return defender;
    }
    public boolean canPlaceInside(Point src, Point dst) {
        Piece p = attacker.getPieceOnBoardAt(src);
        if (p.getTypeOfPiece() == Piece.NONE) return false;
        Set<Point> s = p.getCapablePutPoint(attacker, defender);
        return s.size() != 0 && s.contains(dst);
    }
    public boolean isTherePieceAt(Point p) {
        return attacker.getPieceTypeOnBoardAt(p) + defender.getPieceTypeOnBoardAt(p) > 0;
    }
    public void placePieceInHand(Piece piece, Point dst, Boolean opt){
    	// 持ち駒を置けるなら置いて，交代
    	if(wouldMoveNextLater(piece,dst) && !selected_will_be_niFu(piece, dst.x) && !isTherePieceAt(dst)){

            // 棋譜の登録
            if (opt) {
                Piece src_piece = new PieceFactory().create(Piece.NONE, new Point(-1, -1));
                Piece dst_piece = new PieceFactory().create(piece.getTypeOfPiece(), dst);
                dst_piece.setPoint(dst);
                kifu.update(new MovementOfPiece(src_piece, dst_piece), attacker, defender);
            }

            attacker.reducePieceInHandThatIs(piece);
    		// 持ち駒を置く
            piece.setPoint(dst);
            attacker.addPiecesOnBoard(piece);

            if (opt) kifu.updateHash(getHash());

    		this.nextTurn();
    	}
    }

    public void placePieceInHand(Piece piece, Point dst) {
        placePieceInHand(piece, dst, true);
    }

    public void replacePiece(Point src, Point dst, Boolean opt) {
        Piece src_piece;
        Piece dst_piece;
        try {
            src_piece = attacker.getPieceOnBoardAt(src).clone();

            // 棋譜の登録
            if (opt) {
                Piece dst_piece_for_kifu = src_piece.clone();
                dst_piece_for_kifu.setPoint(dst);
                kifu.update(new MovementOfPiece(src_piece, dst_piece_for_kifu), attacker, defender);
            }

            attacker.reducePieceOnBoardAt(src);
            src_piece.setPoint(dst);
            if(defender.getPieceTypeOnBoardAt(dst) > 0) {
                dst_piece = defender.getPieceOnBoardAt(dst).clone();
                defender.reducePieceOnBoardAt(dst);
                dst_piece.setPoint(new Point(-1,-1));
                if(dst_piece.getTypeOfPiece() > Piece.GYOKU)
                    attacker.addPiecesInHand(dst_piece.getDemotePiece());
                else
                    attacker.addPiecesInHand(dst_piece);
            }
            attacker.addPiecesOnBoard(src_piece);

            if (opt) kifu.updateHash(getHash());

            this.nextTurn();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void replacePiece(Point src, Point dst) {
        replacePiece(src, dst, true);
    }

    /**
     *
     * @param src 移動元
     * @param dst 移動先
     * @param opt 棋譜に登録するなら{@code true}
     */
    public void replacePieceWithPromote(Point src, Point dst, Boolean opt) {
        Piece src_piece;
        Piece dst_piece;
        try {
            src_piece = attacker.getPieceOnBoardAt(src).clone();

            // 棋譜の登録
            if (opt) {
                Piece dst_piece_for_kifu = src_piece.clone();
                dst_piece_for_kifu.setPoint(dst);
                kifu.update(new MovementOfPiece(src_piece, dst_piece_for_kifu.getPromotePiece()), attacker, defender);
            }

            attacker.reducePieceOnBoardAt(src);
            src_piece.setPoint(dst);
            if(defender.getPieceTypeOnBoardAt(dst) > 0) {
                dst_piece = defender.getPieceOnBoardAt(dst).clone();
                defender.reducePieceOnBoardAt(dst);
                dst_piece.setPoint(new Point(-1,-1));
                if(dst_piece.getTypeOfPiece() > Piece.GYOKU)
                    attacker.addPiecesInHand(dst_piece.getDemotePiece());
                else
                    attacker.addPiecesInHand(dst_piece);
            }
            attacker.addPiecesOnBoard(src_piece.getPromotePiece());

            if (opt) kifu.updateHash(getHash());

            this.nextTurn();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void replacePieceWithPromote(Point src, Point dst) {
        replacePieceWithPromote(src, dst, true);
    }

    public void replaceAt(int num) {
        try {
            attacker.update(kifu.getIniAttacker());
            defender.update(kifu.getIniDefender());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        ArrayList<MovementOfPiece> list = kifu.getMovementOfPieceList();
        if (num > list.size()) return;
        try {
            for (int i = 0; i < num; i++) {
                MovementOfPiece bs = list.get(i);
                if (!bs.getSrc().isOnBoard()) {
                    placePieceInHand(bs.getDst(), bs.getDst().getPoint(), false);
                } else if (!Objects.equals(bs.getDst().getTypeOfPiece(), bs.getSrc().getTypeOfPiece())) {
                    replacePieceWithPromote(bs.getSrc().getPoint(), bs.getDst().getPoint(), false);
                } else {
                    replacePiece(bs.getSrc().getPoint(), bs.getDst().getPoint(), false);
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        kifu.undo(list.size() - num);
    }

    // 与えられた位置が含まれる列に，既に歩があればtrue
    public boolean selected_will_be_niFu(Piece selected_piece, int x){
    	boolean ret = false;
    	// 置こうとしている駒が歩であるか
    	if(selected_piece.getTypeOfPiece() == Piece.FU){
    		for(int y = 0; y < 9; y++){
        		Piece piece = attacker.getPieceOnBoardAt(new Point(x, y));
        		// 自分の駒でかつそれが歩であれば
        		if(piece.getTypeOfPiece() == Piece.FU){
        			ret = true;
        			break;
        		}
    		}
    	}
    	return ret;
    }
    // 与えられた位置が動かせる位置ならtrue
    public Boolean wouldMoveNextLater(Piece selected_piece, Point dst) {
        Piece p = null;
        try {
            p = selected_piece.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if(p == null) return false;
        p.setPoint(dst);
        return p.canMoveLater(attacker);
    }

    public void printBoard() {
        for(int y = 0; y < 9; y++) {
            for(int x = 0; x < 9; x++) {
                Player player;
                if (attacker.getPieceTypeOnBoardAt(new Point(x, y)) > 0) player = attacker;
                else player = defender;
                String str = "";
                Boolean existPiece = player.getPieceTypeOnBoardAt(new Point(x,y)) != Piece.NONE;
                if (player instanceof AheadPlayer && existPiece) str += "+";
                else if (player instanceof BehindPlayer && existPiece) str += "-";
                if (!existPiece) str += "_";
                System.out.print(str+player.getPieceOnBoardAt(new Point(x,y)).getName());
            }
            System.out.println();
        }
    }
    public ArrayList<MovementOfPiece> getKifuList() {
        return kifu.getMovementOfPieceList();
    }
    private String getHash() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(this.getBoardSurface().getBytes());
        } else {
            System.err.println("ハッシュ値が取得できません");
            throw new NullPointerException();
        }
        byte[] data = md.digest();
        return encodeHexString(data);
    }
    public String getBoardSurface() {
        String ret = "";
        for(int y = 0; y < 9; y++) {
            ret += "P" + (y+1);
            for(int x = 0; x < 9; x++) {
                Player player;
                if (attacker.getPieceTypeOnBoardAt(new Point(x, y)) > 0) player = attacker;
                else player = defender;
                Boolean existPiece = player.getPieceTypeOnBoardAt(new Point(x,y)) != Piece.NONE;
                if (player instanceof AheadPlayer && existPiece) ret += "+";
                else if (player instanceof BehindPlayer && existPiece) ret += "-";
                if (!existPiece) ret += " * ";
                else ret += player.getPieceOnBoardAt(new Point(x,y)).getName(true);
            }
            if (y != 8) ret += "\n";
        }
        return ret;
    }


}
