package cive.shogi;

import cive.shogi.Pieces.EmptyPiece;
import cive.shogi.Pieces.Piece;
import cive.shogi.Players.Player;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_MULTIPLYPeer;
import org.w3c.dom.ranges.RangeException;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by yotuba on 16/05/06.
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
    ArrayList<BoardSurface> list = null;
    ArrayList<String> infoList = null;
    public Kifu() {
        list = new ArrayList<>();
        infoList = new ArrayList<>();
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
        if (num <= 0) return false;
        return (count > 0 && count >= num);
    }
    public Boolean hasNext(int num) {
        if (num <= 0) return false;
        return count + num <= list.size();
    }
    public void undo(int num) {
        if (num <= 0) return;
        if (count > 0 && count >= num) count -= num;
    }
    public void redo(int num) {
        if (num <= 0) return;
        if (count + num <= list.size()) count += num;
    }
    public ArrayList<BoardSurface> getList() {
        return list;
    }
    public void update(BoardSurface bs, Player attacker, Player defender, int num) throws RangeException {
        if (num > list.size() + 1 || num < 0) {
            throw new RangeException(RangeException.BAD_BOUNDARYPOINTS_ERR, "範囲外の値です");
        } else if (list.size() > num) {
            while (list.size() > num) {
                list.remove(num);
            }
        }
        list.add(bs);
        try {
            infoList.add(getInfo(bs, list.get(list.size() - 1), attacker.clone(), defender.clone()));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        count = num;
    }
    public void update(BoardSurface bs, Player attacker, Player defender) {
        try {
            update(bs, attacker.clone(), defender.clone(), count+1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
    public String getInfo(BoardSurface now, BoardSurface prev, Player attacker, Player defender) {
        Piece cSrc = null, cDst = null;
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
            Stream<Piece> gins = attacker.getPiecesOnBoard(src_type)
                    .filter(x -> x.getCapablePutPoint(attacker, defender) == dst.getPoint())
                    .filter(x -> !x.getPoint().equals(src.getPoint()));
            boolean there_were_gin_on_col = gins.anyMatch(g -> g.getPoint().y - src.getPoint().y == 0);
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
            if (gins.count() == 0) {
                str += "";
            } else if (top) {
                if (there_were_gin_on_col) str += "直";
                else str += "上";
            } else if (right_top) {
                boolean there_were_gin_in_front_of =
                        gins.anyMatch(g -> (g.getPoint().y - src.getPoint().y) * trans == 2);
                if (!there_were_gin_on_col) str += "上";
                else if (there_were_gin_in_front_of) str += "左上";
                else str += "左";
            } else if (left_top) {
                boolean there_were_gin_in_front_of =
                        gins.anyMatch(g -> (g.getPoint().y - src.getPoint().y) * trans == 2);
                if (!there_were_gin_on_col) str += "上";
                else if (there_were_gin_in_front_of) str += "右上";
                else str += "右";
            } else if (right_bottom) {
                boolean there_were_gin_behind =
                        gins.anyMatch(g -> (g.getPoint().y - src.getPoint().y) * trans == -2);
                if (!there_were_gin_on_col) str += "引";
                else if (there_were_gin_behind) str += "左引";
                else str += "左";
            } else if (left_bottom) {
                boolean there_were_gin_behind =
                        gins.anyMatch(g -> (g.getPoint().y - src.getPoint().y) * trans == -2);
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
