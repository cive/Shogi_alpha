/**
 * GameBoard.java
 * 将棋盤の盤上の処理はここで行う
 */

package cive.shogi;

import cive.shogi.Pieces.*;

import java.awt.*;
import java.util.*;

public class GameBoard {
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
        setTurn(true);
    }
    public void initGame(int rule) {
        playerA = new AheadPlayer(rule);
        playerB = new BehindPlayer();
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
    public void placePieceInHand(Piece piece, Point dst){
    	// 持ち駒を置けるなら置いて，交代
    	if(wouldMoveNextLater(piece,dst) && !selected_will_be_niFu(piece, dst.x) && !isTherePieceAt(dst)){
            attacker.reducePieceInHandThatIs(piece);
    		// 持ち駒を置く
            piece.setPoint(dst);
            attacker.addPiecesOnBoard(piece);

    		this.nextTurn();
    	}
    }

    public void replacePiece(Point src, Point dst) {
        Piece src_piece;
        Piece dst_piece;
        try {
            src_piece = attacker.getPieceOnBoardAt(src).clone();
            attacker.reducePieceOnBoardAt(src);
            src_piece.setPoint(dst);
            if(defender.getPieceTypeOnBoardAt(dst) > 0) {
                dst_piece = defender.getPieceOnBoardAt(dst).clone();
                defender.reducePieceOnBoardAt(dst);
                dst_piece.setPoint(new Point(-1,-1));
                if(dst_piece.getPre_typeOfPiece() > 0)
                    attacker.addPiecesInHand(dst_piece.getDemotePiece());
                else
                    attacker.addPiecesInHand(dst_piece);
            }
            attacker.addPiecesOnBoard(src_piece);
            this.nextTurn();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void replacePieceWithPromote(Point src, Point dst) {
        Piece src_piece;
        Piece dst_piece;
        try {
            src_piece = attacker.getPieceOnBoardAt(src).clone();
            attacker.reducePieceOnBoardAt(src);
            src_piece.setPoint(dst);
            if(defender.getPieceTypeOnBoardAt(dst) > 0) {
                dst_piece = defender.getPieceOnBoardAt(dst).clone();
                defender.reducePieceOnBoardAt(dst);
                dst_piece.setPoint(new Point(-1,-1));
                if(dst_piece.getPre_typeOfPiece() > 0)
                    attacker.addPiecesInHand(dst_piece.getDemotePiece());
                else
                    attacker.addPiecesInHand(dst_piece);
            }
            attacker.addPiecesOnBoard(src_piece.getPromotePiece());
            this.nextTurn();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
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
        return p.getCapablePutPoint(attacker, defender).size() > 0;
    }

    public void printBoard() {
        for(int y = 0; y < 9; y++) {
            for(int x = 0; x < 9; x++) {
                Player player;
                if (attacker.getPieceTypeOnBoardAt(new Point(x, y)) > 0) player = attacker;
                else player = defender;
                System.out.print(player.getPieceOnBoardAt(new Point(x,y)).getName());
            }
            System.out.println();
        }
    }
}
