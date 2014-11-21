package cive.shogi.Pieces;

import cive.shogi.GameBoard;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class KyoshaOfPiece extends Piece{
    @Override
    public String getName() {
        return "香";
    }

    @Override
    public Set<Point> getRuleOfPiece() {
        Set<Point> set = new HashSet<Point>();
        if(this.isBlack()) {
            for(int i = 1; i < 10; i++) {
                set.add(new Point(0, -i));
            }
        } else {
            for(int i = 1; i < 10; i++) {
                set.add(new Point(0, i));
            }
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
        set.removeAll(getSetToNeedToRemove(board, true, point));
        set.removeAll(getSetToNeedToRemove(board, false, point));
        return set;
    };
    private Set<Point> getSetToNeedToRemove(GameBoard board, boolean direction, Point mine) {
        Set<Point> set_for_remove = new HashSet<Point>();
        boolean removeFlag = false;
        for(int i = direction ?     1  :    -1;
            direction ? i < 9 : i > -9;)
        {
            Point other = new Point(mine.x, mine.y+i);
            Piece that = board.getPieceOf(other);
            if(GameBoard.isInGrid(other)){
                /**
                 * なぜ下記のようなプログラムになったかというと，
                 * 飛車が初めておけなくなる場所を
                 * 見つけなくてはならなかったから．
                 */
                boolean canMove;
                if(this.isBlack()) {
                    canMove = that.getTypeOfPiece() == 0 || (this.isBlack() && that.isWhite());
                } else {
                    canMove = that.getTypeOfPiece() == 0 || (this.isWhite() && that.isBlack());
                }
                if(canMove && !removeFlag) {
                    //nop
                } else {
                    set_for_remove.add(other);
                }
            } else {
                set_for_remove.add(other);
            }
            if(this.isBlack() && that.isWhite() || this.isBlack() && that.isBlack()) {
                removeFlag = true;
            } else if (this.isWhite() && that.isBlack() || this.isWhite() && that.isWhite()) {
                removeFlag = true;
            }
            if(direction) {
                i++;
            } else {
                i--;
            }
        }
        return set_for_remove;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.KYOSHA;
    }
}