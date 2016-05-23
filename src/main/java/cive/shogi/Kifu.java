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
 * undo, redoを使って手番(count)を変更
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
    int count;
    Players initialPlayers;
    ArrayList<MovementOfPiece> movementOfPieceArrayList = null;
    ArrayList<String> kifuList = null;
    public Kifu() {
        movementOfPieceArrayList = new ArrayList<>();
        kifuList = new ArrayList<>();
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
        return num > 0 && (count > 0 && count >= num);
    }
    public Boolean hasNext(int num) {
        return num > 0 && count + num <= movementOfPieceArrayList.size();
    }
    public void undo(int num) {
        if (num <= 0) return;
        if (count > 0 && count >= num) count -= num;
    }
    public void redo(int num) {
        if (num <= 0) return;
        if (count + num <= movementOfPieceArrayList.size()) count += num;
    }

    /**
     * 駒の移動を記録したデータリストを返す
     * @return 駒の移動を記録したリスト
     */
    public ArrayList<MovementOfPiece> getMovementOfPieceArrayList() {
        return movementOfPieceArrayList;
    }

    /**
     * {@param num}番目からの状態にリストをアップデートする
     * ゲーム上で戻るなどして、途中からはじめるときに使用する
     * @param mp 駒の移動
     * @param attacker 駒を移動する側
     * @param defender 防御側
     * @param num 何手目をアップデートするか
     * @throws RangeException {@param num}が不正
     */
    public void update(MovementOfPiece mp, Player attacker, Player defender, int num) throws RangeException {
        if (num > movementOfPieceArrayList.size() + 1 || num < 0) {
            throw new RangeException(RangeException.BAD_BOUNDARYPOINTS_ERR, "範囲外の値です");
        } else if (movementOfPieceArrayList.size() > num) {
            while (movementOfPieceArrayList.size() > num) {
                movementOfPieceArrayList.remove(num);
            }
        }
        movementOfPieceArrayList.add(mp);
        try {
            kifuList.add(getKifu(
                    mp
                    , movementOfPieceArrayList.get(movementOfPieceArrayList.size() - 1)
                    , attacker.clone()
                    , defender.clone()
            ));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        count = num;
    }
    public void update(MovementOfPiece bs, Player attacker, Player defender) {
        try {
            update(bs, attacker.clone(), defender.clone(), count+1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
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
        return count % 2 == 0;
    }
    public Integer getCount() {
        return count;
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
}
