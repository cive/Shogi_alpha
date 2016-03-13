package cive.shogi.Pieces;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Gyoku extends Piece {

    public Gyoku(Point p) {
        super(p);
    }

    @Override
    public String getName() {
        return "玉";
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        Set<Point> set = new HashSet<Point>();
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                if(i == 0 && j == 0) continue;
                set.add(new Point(i, j));
            }
        }
        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.GYOKU;
    }
}
