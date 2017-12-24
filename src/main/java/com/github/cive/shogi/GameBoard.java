/**
 * GameBoard.java
 * 将棋盤の盤上の処理はここで行う
 */

package com.github.cive.shogi;

import com.github.cive.shogi.Exceptions.PlayerNotDefinedGyokuException;
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
import java.util.List;
import java.util.stream.Stream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

//import jdk.internal.util.xml.impl.Pair;
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
    private void initGame(int rule) {
        playerA = new AheadPlayer(rule);
        playerB = new BehindPlayer();
        kifu = new Kifu();
        kifu.setInitialPlayers(playerA, playerB);
        setTurn(true);
    }
    public Piece getPieceOf(Point p) {
        if(GameBoard.isInGrid(p))
        {
            if( attacker.getPieceOnBoardAt(p).getTypeOfPiece() != Piece.NONE )
            {
                return attacker.getPieceOnBoardAt(p);
            }
            else if (defender.getPieceOnBoardAt(p).getTypeOfPiece() != Piece.NONE)
            {
                return defender.getPieceOnBoardAt(p);
            }
            else return new EmptyPiece(new Point(-1,-1));
        }
        else return new EmptyPiece(new Point(-1,-1));
    }
    public Piece getPieceOf(int x, int y) {
        return this.getPieceOf(new Point(x, y));
    }
    public static boolean isInGrid(Point point) {
        return point.x >= 0 && point.x < 9
                && point.y >= 0 && point.y < 9;
    }
    private void setTurn(boolean aheadsTurn) {
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
        isConclusion();
    }
    public Player getAttacker() {
        return attacker;
    }
    public Player getDefender() {
        return defender;
    }
    public Boolean canPlaceInside(Point src, Point dst) {
        Piece p = attacker.getPieceOnBoardAt(src);
        if (p.getTypeOfPiece() == Piece.NONE) return false;
        Set<Point> s = p.getCapablePutPoint(attacker, defender);
        return s.size() != 0 && s.contains(dst);
    }
    public Boolean isTherePieceAt(Point p) {
        return attacker.getPieceTypeOnBoardAt(p) + defender.getPieceTypeOnBoardAt(p) > 0;
    }
    private void placePieceInHand(Piece piece, Point dst, Boolean opt){
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

    private void replacePiece(Point src, Point dst, Boolean opt) {
        try {

            // 棋譜の登録
            if (opt) {
                Piece src_piece_for_kifu = attacker.getPieceOnBoardAt(src).clone();
                Piece dst_piece_for_kifu = attacker.getPieceOnBoardAt(src).clone();
                dst_piece_for_kifu.setPoint(dst);
                kifu.update(new MovementOfPiece(src_piece_for_kifu, dst_piece_for_kifu), attacker, defender);
            }

            replace(src, dst, attacker, defender, false);

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
    private void replacePieceWithPromote(Point src, Point dst, Boolean opt) {
        try {
            // 棋譜の登録
            if (opt) {
                Piece src_piece_for_kifu = attacker.getPieceOnBoardAt(src).clone();
                Piece dst_piece_for_kifu = attacker.getPieceOnBoardAt(src).clone();
                dst_piece_for_kifu.setPoint(dst);
                kifu.update(new MovementOfPiece(src_piece_for_kifu, dst_piece_for_kifu.getPromotePiece()), attacker, defender);
            }

            // 駒の移動
            replace(src, dst, attacker, defender, true);

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

    private void replace(Point src, Point dst, Player attacker, Player defender, Boolean willPromote) throws CloneNotSupportedException{
        Piece src_piece = attacker.getPieceOnBoardAt(src).clone();
        attacker.reducePieceOnBoardAt(src);
        src_piece.setPoint(dst);
        if(defender.getPieceTypeOnBoardAt(dst) > 0) {
            Piece dst_piece = defender.getPieceOnBoardAt(dst).clone();
            defender.reducePieceOnBoardAt(dst);
            dst_piece.setPoint(new Point(-1,-1));
            if(dst_piece.getTypeOfPiece() > Piece.GYOKU)
                attacker.addPiecesInHand(dst_piece.getDemotePiece());
            else
                attacker.addPiecesInHand(dst_piece);
        }
        if (willPromote)
            attacker.addPiecesOnBoard(src_piece.getPromotePiece());
        else
            attacker.addPiecesOnBoard(src_piece);
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
    
    // 場所を表わす文字列を座標値に変換（「5二」を表わす「52」は(4, 1)に変換）
    private Point LocationStringToPoint(String str) {
    	int x = Integer.parseInt(str.substring(0, 1));
    	int y = Integer.parseInt(str.substring(1, 2));
    	return new Point(9 - x, y - 1);
    }
    
    // 文字列で駒を移動する
    public void MoveByString(String str) {
    	// 文字列のパターンで場合分け
    	if (Pattern.compile("^[1-9]{4}\\s[#].*").matcher(str).matches()) {
    		// 移動
        	Point src = LocationStringToPoint(str.substring(0, 2));
        	Point dst = LocationStringToPoint(str.substring(2, 4));
            if (canPlaceInside(src, dst))
        		replacePiece(src, dst);
    	}else if (Pattern.compile("^[1-9]{4}\\+\\s[#].*").matcher(str).matches()) {
    		// 移動して成る
        	Point src = LocationStringToPoint(str.substring(0, 2));
        	Point dst = LocationStringToPoint(str.substring(2, 4));
            if (canPlaceInside(src, dst))
            	replacePieceWithPromote(src, dst);
    	}else if (Pattern.compile("^.[1-9]{2}\\s[#].*").matcher(str).matches()) {
    		// 持ち駒を指す
    		Map<String, Integer> nameIdPairs = new HashMap<String, Integer>() {
    			{
    				String[] keys = new String[] {"歩", "香", "桂", "銀", "金", "角", "飛"};
    				int[] values = new int[] {Piece.FU, Piece.KYOSHA, Piece.KEIMA, Piece.GIN, Piece.KIN, Piece.KAKU, Piece.HISHA};
    				for(int i = 0; i < 7; i++){
    					put(keys[i], values[i]);
    				}
    			}
    		};
    		String nameOfHand = str.substring(0, 1);	// これから置く駒
    		if(nameIdPairs.containsKey(nameOfHand)){
    			int pieceId = nameIdPairs.get(nameOfHand);
   	        	Point dst = LocationStringToPoint(str.substring(1, 3));
   				placePieceInHand(new PieceFactory().create(pieceId, new Point(-1, -1)), dst);
    		}
    	}
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

    /**
     * 敵が王手を仕掛けて来ていたら王手されていると判断する
     * @return 王手されているならば{@code true}
     */
    @org.jetbrains.annotations.NotNull
    private Boolean isMated(Player attacker, Player defender) throws PlayerNotDefinedGyokuException {
        // とりあえずの実装
        // より早く判定するには、玉の周りの駒と飛車角行を調べればよい
        Point ptGyoku = attacker.getPiecesOnBoard(Piece.GYOKU)
                .findFirst()
                .orElseThrow(PlayerNotDefinedGyokuException::new)
                .getPoint();
        Set<Point> set = new HashSet<>();
        defender.getPiecesOnBoard().stream().forEach(x -> set.addAll(x.getCapablePutPoint(defender,attacker)));
        return set.stream().anyMatch(x -> x.equals(ptGyoku));
    }

    public Boolean isMated() throws PlayerNotDefinedGyokuException {
        return isMated(this.attacker, this.defender);
    }

    /**
     * 投了判定
     * 王手されているときに判定する
     * 計算量は多め
     * @return これ以上指す手がない場合{@code true}
     */
    public Boolean isCheckmated() throws PlayerNotDefinedGyokuException {
        // 王手されている前提
        // 攻撃側の玉を動かしたとしてもisMatedだった場合、投了の可能性があるので
        // 何かはれる可能性がないか調べる
        Player cAtt = null, cDef = null;
        try {
            cAtt = attacker.clone();
            cDef = defender.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        final Player att = cAtt;
        final Player def = cDef;
        List<Piece> pieces_in_hand = new ArrayList<>();
        pieces_in_hand.addAll(attacker.getPiecesInHand());
        Set<Point> points_of_capable_moving_of_gyoku = new HashSet<>();
        Point ptGyoku;
        if (att != null && def != null) {
            ptGyoku = att.getPiecesOnBoard(Piece.GYOKU)
                    .findAny()
                    .orElseThrow(PlayerNotDefinedGyokuException::new)
                    .getPoint();
            points_of_capable_moving_of_gyoku.addAll(att.getPieceOnBoardAt(ptGyoku).getCapablePutPoint(att,def));
        } else {
            System.err.println("読み込みエラー");
            throw new NullPointerException();
        }
        // まず、玉を他の位置に移動して王手が防げるか判定する
        for (Point c : points_of_capable_moving_of_gyoku) {
            Player a = null, d = null;
            try {
                a = att.clone();
                d = def.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            if (a != null && d != null) {
                try {
                    replace(ptGyoku, c, a, d, false);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            if (!isMated(a, d)) return false;
        }

        // もし、駒を持っていて指すことで王手を防げるのなら詰みではない
        if(!def.getPiecesInHand().isEmpty()) for (Point c : points_of_capable_moving_of_gyoku) {
            Player a = null, d = null;
            try {
                a = att.clone();
                d = def.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            if (a != null && d != null) {
                for (Piece piece : pieces_in_hand) {
                    if ( !selected_will_be_niFu(piece, c.x)) {
                        piece.setPoint(c);
                        a.addPiecesOnBoard(piece);
                        if (!isMated(a, d)) return false;
                    }
                }
            }
        }
        // もし、玉の周りの駒を移動させることで王手を防げるのなら詰みではない
        for (Piece piece : att.getPiecesOnBoard()) {
            Point src = piece.getPoint();
            Set<Point> points = new HashSet<>();
            points.addAll(piece.getCapablePutPoint(att, def));
            for (Point dest : points) {
                Player a = null, d = null;
                try {
                    a = att.clone();
                    d = def.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                if (a != null && d != null) {
                    try {
                        replace(src, dest, a, d, false);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                if (!isMated(a, d)) return false;
            }
        }
        return true;
    }

    /**
     * 終局判定
     * @return 終局であれば{@code true}
     */
    public Boolean isConclusion() {
        if (kifu.isSennnichite()) return true;
        try {
            if (isMated()) {
                System.out.println("Mate");
                if (isCheckmated()) {
                    System.out.println("Checkmate");
                    return true;
                }
            }
        } catch (PlayerNotDefinedGyokuException e) {
            e.printStackTrace();
        }
        return false;
    }
}