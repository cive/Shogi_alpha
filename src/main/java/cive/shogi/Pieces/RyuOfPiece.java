package cive.shogi.Pieces;

import cive.shogi.GameBoard;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RyuOfPiece extends Piece {
    @Override
    public String getName() {
        return "龍";
    }

    @Override
    public Set<Point> getRuleOfPiece() {
        Set<Point> set = new HashSet<Point>();
        Piece hisha = new HishaOfPiece();
        Piece gyoku = new GyokuOfPiece();
        set.addAll(hisha.getRuleOfPiece());
        set.addAll(gyoku.getRuleOfPiece());

        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.RYU;
    }
    @Override
    public Integer getPre_typeOfPiece() {
        return Piece.HISHA;
    }
    @Override
    public Set<Point> getCapableMovePiece(GameBoard board, Point point) {
        Set<Point> rule_set = new HashSet<>();
        Set<Point> set = new HashSet<>();
        rule_set.addAll(getRuleOfPiece());
	for(Iterator i = rule_set.iterator(); i.hasNext();) {
            Point p = (Point)i.next();
	    set.add(new Point(p.x+point.x, p.y+point.y));
	}
        set.removeAll(getSetToRemove(board, true, 1, point));
        set.removeAll(getSetToRemove(board, true, -1, point));
	set.removeAll(getSetToRemove(board, false, 1, point));
	set.removeAll(getSetToRemove(board, false, -1, point));
        Point remain_point[] = {new Point(-1,-1),new Point(-1,1),new Point(1,-1),new Point(1,1)};
        set.removeAll(getSetToRemove(board,remain_point,point));
        return set;
    }
    private Set<Point> getSetToRemove(GameBoard board, Point[] remain_point, Point point) {
        Set<Point> set_for_remove = new HashSet<>();
        for(int i = 0; i < 4; i++) {
            Point other = new Point(remain_point[i].x+point.x, remain_point[i].y+point.y);
            Piece that = board.getPieceOf(other);
            boolean canMove = that.getTypeOfPiece() == 0
                    || this.isBlack() != that.isBlack();
            if(board.isInGrid(other) || canMove) {
                //nop
            } else {
                set_for_remove.add(remain_point[i]);
            }
        }
        return set_for_remove;
    }
    private Set<Point> getSetToRemove(GameBoard board, boolean axis, int direction, Point mine) {
        Set<Point> set_for_remove = new HashSet<Point>();
        boolean removeFlag = false;
        for(int i = direction; -9 < i && i < 9; i += direction) {
            Point other = new Point(mine.x+(axis?i:0), mine.y+(axis?0:i));
            Piece that = board.getPieceOf(other);
            if(GameBoard.isInGrid(other)){
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

}
