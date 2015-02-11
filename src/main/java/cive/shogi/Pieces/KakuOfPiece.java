package cive.shogi.Pieces;

import cive.shogi.GameBoard;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class KakuOfPiece extends Piece {
    @Override
    public String getName() {
        return "角";
    }

    @Override
    public Set<Point> getRuleOfPiece() {
        Set<Point> set = new HashSet<Point>();
        for(int i = -9; i < 10; i++) {
            if(i == 0) continue;
            set.add(new Point(i, i));
            set.add(new Point(-i, i));
        }
        return set;
    }
    @Override
    public Set<Point> getCapableMovePiece(GameBoard board, Point point){
        Set<Point> rule_set = new HashSet<Point>();
        Set<Point> set = new HashSet<>();
        rule_set.addAll(getRuleOfPiece());
        for(Iterator i = rule_set.iterator(); i.hasNext();) {
            Point p = (Point)i.next();
            set.add(new Point(p.x+point.x,p.y+point.y));
        }
        set.removeAll(getSetToNeedToRemove(board, true, 1, point));
        set.removeAll(getSetToNeedToRemove(board, false, 1, point));
        set.removeAll(getSetToNeedToRemove(board, true, -1, point));
        set.removeAll(getSetToNeedToRemove(board, false, -1, point));
        return set;
    };

    private Set<Point> getSetToNeedToRemove(GameBoard board, boolean axis, int direction, Point mine) {
        Set<Point> set_for_remove = new HashSet<Point>();
        boolean removeFlag = false;
        for(int i = direction; -9 < i && i < 9; i += direction) {
            Point other = new Point(mine.x+(axis?i:-i), mine.y+i);
            Piece that = board.getPieceOf(other);
            if(GameBoard.isInGrid(other)){
                /**
                 * なぜ下記のようなプログラムになったかというと，
                 * 飛車が初めておけなくなる場所を
                 * 見つけなくてはならなかったから．
                 */
                if(removeFlag) {
                    set_for_remove.add(other);
                } else if(this.isEnemyFor(that)){
                    removeFlag = true;
                } else if(this.isFriendFor(that)) {
                    removeFlag = true;
                    set_for_remove.add(other);
                }
            } else {
                set_for_remove.add(other);
            }
        }
        return set_for_remove;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.KAKU;
    }
}
