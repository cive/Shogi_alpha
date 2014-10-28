package cive.shogi.Pieces;

import cive.shogi.GameBoard;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class UmaOfPiece extends Piece{
    @Override
    public String getName() {
        return "馬";
    }

    @Override
    public Set<Point> getRuleOfPiece() {
        Set<Point> set = new HashSet<Point>();
        Piece kaku = new KakuOfPiece();
        Piece gyoku = new GinOfPiece();
        set.addAll(kaku.getRuleOfPiece());
        set.addAll(gyoku.getRuleOfPiece());
        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.UMA;
    }
    @Override
    public Integer getPre_typeOfPiece() {
        return Piece.KAKU;
    }
    @Override
    public Set<Point> getCapableMovePiece(GameBoard board, Point point) {
        Set<Point> set = new HashSet<Point>();
        Piece kaku = new KakuOfPiece();
        set.addAll(kaku.getCapableMovePiece(board, point));
        Point remain_point[] = {new Point(-1,0),new Point(0,1),new Point(1,0),new Point(0,-1)};
        set.removeAll(getSetToNeedToRemove(board,remain_point,point));
        return set;
    }
    public Set<Point> getSetToNeedToRemove(GameBoard board, Point[] remain_point, Point point) {
        Set<Point> set_for_remove = new HashSet<>();
        for(int i = 0; i < 4; i++) {
            Point other = new Point(remain_point[i].x+point.x, remain_point[i].y+point.y);
            Piece that = board.getPieceOf(other);
            boolean canMove = that.getTypeOfPiece() == 0
                    || this.isBlack() != that.isBlack();
            if(board.canPut(other) || canMove) {
                //nop
            } else {
                set_for_remove.add(remain_point[i]);
            }
        }
        return set_for_remove;
    }
}
