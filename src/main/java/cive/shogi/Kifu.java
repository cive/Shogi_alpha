package cive.shogi;

import cive.shogi.Pieces.EmptyPiece;
import cive.shogi.Pieces.Piece;
import cive.shogi.Players.Player;
import org.w3c.dom.ranges.RangeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yotuba on 16/05/06.
 * 棋譜を記録
 * プレイヤーの初期の持ち駒 と 駒の移動を記録
 * undo, redoを使って手数(movedNum)を変更
 * もし、実際の手番(movementOfPieceList.size())よりも前の手番を参照していたらそれ以降の盤面を削除し、そこから新規に記録する
 */
public class Kifu {
    private class Players {
        private Player attacker;
        private Player defender;
        public Players(Player attacker, Player defender) {
            setAttacker(attacker);
            setDefender(defender);
        }
        protected void setAttacker(Player attacker) {
            try {
                this.attacker = attacker.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        protected void setDefender(Player defender) {
            try {
                this.defender = defender.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        protected Player getAttacker() throws CloneNotSupportedException{
            return attacker.clone();
        }
        protected Player getDefender() throws CloneNotSupportedException{
            return defender.clone();
        }
    }
    private int movedNum;
    public Boolean hasSafeUpdated;
    private Players initialPlayers;
    private ArrayList<MovementOfPiece> movementOfPieceList = null;
    private ArrayList<String> kifuList = null;
    private List<String> hashList = null;
    public Kifu() {
        movementOfPieceList = new ArrayList<>();
        kifuList = new ArrayList<>();
        hashList = new ArrayList<>();
        hasSafeUpdated = true;
    }
    public void setInitialPlayers(Player attacker, Player defender) {
        initialPlayers = new Players(attacker, defender);
    }
    public Player getIniAttacker() throws CloneNotSupportedException{
        return initialPlayers.getAttacker();
    }
    public Player getIniDefender() throws CloneNotSupportedException{
        return initialPlayers.getDefender();
    }
    public Boolean hasAhead(int num) {
        return num > 0 && (movedNum > 0 && movedNum >= num);
    }
    public Boolean hasNext(int num) {
        return num > 0 && movedNum + num <= movementOfPieceList.size();
    }
    public void undo(int num) {
        if (num <= 0) return;
        if (movedNum > 0 && movedNum >= num) movedNum -= num;
    }
    public void redo(int num) {
        if (num <= 0) return;
        if (movedNum + num <= movementOfPieceList.size()) movedNum += num;
    }

    /**
     * 駒の移動を記録したデータリストを返す
     * @return 駒の移動を記録したリスト
     */
    public ArrayList<MovementOfPiece> getMovementOfPieceList() {
        return movementOfPieceList;
    }

    /**
     * undo(n)でn手前にセットすることでn手前以降のデータを削除し、n+1手目のデータをセットする
     * num = list.size() - n となる
     * 実際に外から実行できるのは update(MovementOfPiece, Player, Player)のみ
     * @param mp 駒の移動
     * @param attacker 駒を移動する側
     * @param defender 防御側
     * @param num num手目にアップデート、それ以降の棋譜は削除
     * @throws RangeException 入力値numが不正
     */
    private void update(MovementOfPiece mp, Player attacker, Player defender, int num) throws RangeException {
        if (num > movementOfPieceList.size() + 1 || num < 0) {
            throw new RangeException(RangeException.BAD_BOUNDARYPOINTS_ERR, "範囲外の値です");
        } else if (movementOfPieceList.size() > num) {
            while (movementOfPieceList.size() > num) {
                movementOfPieceList.remove(num);
                kifuList.remove(num);
                hashList.remove(num);
            }
        }
        movementOfPieceList.add(mp);
        try {
            kifuList.add(getKifu(
                    mp
                    , movementOfPieceList.get(movementOfPieceList.size() - 1)
                    , attacker.clone()
                    , defender.clone()
            ));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        movedNum = num;
    }

    /**
     * {@code undo(n)}でn手前にセットすることでn手前以降のデータを削除し、n+1手目のデータをセットする
     * {@code undo(n)}を使用していない場合は、棋譜を登録するのみ
     * 使用後は {@code updateHash}を使用すること
     * @param mp 駒の移動
     * @param attacker 駒を移動する側
     * @param defender 防御側
     */
    public void update(MovementOfPiece mp, Player attacker, Player defender) {
        if (!hasSafeUpdated) {
            System.err.println("haven't update fully");
            return;
        }
        try {
            update(mp, attacker.clone(), defender.clone(), movedNum +1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        hasSafeUpdated = false;
    }

    /**
     * 盤面を登録。千日手の判定に使う。
     * [must] {@code update(MovementOfPiece, Player, Player)}宣言後に必ず使うこと
     * @param hexStringByBoardSurface 盤面から生成したハッシュ値
     */
    public void updateHash(String hexStringByBoardSurface) {
        if (!hasSafeUpdated) {
            hashList.add(hexStringByBoardSurface);
            hasSafeUpdated = true;
        } else {
            System.err.println("haven't update fully");
        }
    }

    /**
     * 棋譜(String)を出力するメソッド
     * updateされる毎に実行される
     * @param now 現在の盤面
     * @param prev 一つ前の盤面
     * @param attacker 現在の盤面の攻撃側
     * @param defender 現在の盤面の防御側
     * @return 棋譜
     */
    private String getKifu(MovementOfPiece now, MovementOfPiece prev, Player attacker, Player defender) {
        Piece cSrc, cDst;
        try {
            cSrc = now.getSrc();
            cDst = now.getDst();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return "clone error";
        }
        final Piece src = cSrc;
        final Piece dst = cDst;
        String str = "";
        str += isBlackTurn() ? "▲" : "△";
        int trans = isBlackTurn() ? 1 : -1; /* 先手、後手座標変換用 */
        try {
            if (prev.getDst().getPoint().equals(dst.getPoint())) {
                str += "同";
            } else {
                str += (10 - dst.getPoint().x + 1) + getChineseNum(dst.getPoint().y + 1);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return "clone error";
        }
        str += dst.getName();
        int src_type = src.getTypeOfPiece();
        /**
         * おける場所が重ならない駒は
         * FU, KYOSHA or GYOKU
         * おける場所が重なる駒は
         * KEIMA, GIN, KIN(NARI*), KAKU, HISHA, UMA or RYU
         */
        if (src_type == Piece.KEIMA) {
            Piece keima = attacker.getPiecesOnBoard(src_type)
                    .filter(x -> x.getCapablePutPoint(attacker, defender) == dst.getPoint())
                    .filter(x -> !x.getPoint().equals(src.getPoint()))
                    .findFirst()
                    .orElse(new EmptyPiece());
            if (keima.getTypeOfPiece() == Piece.NONE) {
                str += "";
            } else if ((src.getPoint().x - keima.getPoint().x) * trans > 0) {
                str += "右";
            } else {
                str += "左";
            }
        }
        if (src_type == Piece.GIN) {
            List<Piece> gins = new ArrayList<>(
                    Arrays.asList(
                        attacker.getPiecesOnBoard(src_type)
                        .filter(x -> x.getCapablePutPoint(attacker, defender) == dst.getPoint())
                        .filter(x -> !x.getPoint().equals(src.getPoint()))
                        .toArray(Piece[]::new)
                    )
            );
            boolean there_were_gin_on_col = gins.stream().anyMatch(g -> g.getPoint().y - src.getPoint().y == 0);
            boolean top = dst.getPoint().x - src.getPoint().x == 0
                    && (dst.getPoint().y - src.getPoint().y) * trans > 0;
            boolean right_top = (dst.getPoint().x - src.getPoint().x) * trans > 0
                    && (dst.getPoint().y - src.getPoint().y) * trans > 0;
            boolean left_top = (dst.getPoint().x - src.getPoint().x) * trans < 0
                    && (dst.getPoint().y - src.getPoint().y) * trans > 0;
            boolean right_bottom = (dst.getPoint().x - src.getPoint().x) * trans > 0
                    && (dst.getPoint().y - src.getPoint().y) * trans < 0;
            boolean left_bottom = (dst.getPoint().x - src.getPoint().x) * trans < 0
                    && (dst.getPoint().y - src.getPoint().y) * trans < 0;
            if (gins.stream().count() == 0) {
                str += "";
            } else if (top) {
                if (there_were_gin_on_col) str += "直";
                else str += "上";
            } else if (right_top) {
                boolean there_were_gin_in_front_of =
                        gins.stream().anyMatch(g -> (g.getPoint().y - src.getPoint().y) * trans == 2);
                if (!there_were_gin_on_col) str += "上";
                else if (there_were_gin_in_front_of) str += "左上";
                else str += "左";
            } else if (left_top) {
                boolean there_were_gin_in_front_of =
                        gins.stream().anyMatch(g -> (g.getPoint().y - src.getPoint().y) * trans == 2);
                if (!there_were_gin_on_col) str += "上";
                else if (there_were_gin_in_front_of) str += "右上";
                else str += "右";
            } else if (right_bottom) {
                boolean there_were_gin_behind =
                        gins.stream().anyMatch(g -> (g.getPoint().y - src.getPoint().y) * trans == -2);
                if (!there_were_gin_on_col) str += "引";
                else if (there_were_gin_behind) str += "左引";
                else str += "左";
            } else if (left_bottom) {
                boolean there_were_gin_behind =
                        gins.stream().anyMatch(g -> (g.getPoint().y - src.getPoint().y) * trans == -2);
                if (!there_were_gin_on_col) str += "引";
                else if (there_were_gin_behind) str += "右引";
                else str += "右";
            }
            // TODO impl KIN(NARI*), KAKU, HISHA, UMA and RYU
        }
        // 成　または　不成
        // srcが盤上にあり、成駒できるとき
        if (src.canPromote(dst.getPoint(), isBlackTurn()) && src.isOnBoard()) {
            if (dst.getTypeOfPiece() == src_type) {
                str += "不成";
            } else {
                str += "成";
            }
        }
        return str;
    }
    public boolean isBlackTurn() {
        return movedNum % 2 == 0;
    }
    public Integer getMovedNum() {
        return movedNum;
    }
    private String getChineseNum(int num) {
        switch (num) {
            case 1: return "一";
            case 2: return "二";
            case 3: return "三";
            case 4: return "四";
            case 5: return "五";
            case 6: return "六";
            case 7: return "七";
            case 8: return "八";
            case 9: return "九";
            default: return "";
        }
    }
    public Boolean isSennnichite() {
        if (hashList.isEmpty()) return false;
        String hex = hashList.get(hashList.size()-1);
        return hashList.stream().filter(str -> str.equals(hex)).count() > 3;
    }
}
