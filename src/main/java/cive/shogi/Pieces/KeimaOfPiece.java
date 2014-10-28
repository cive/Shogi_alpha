package cive.shogi.Pieces;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class KeimaOfPiece extends Piece {
    @Override
    public String getName() {
        return "桂";
    }

    @Override
    public Set<Point> getRuleOfPiece() {
        Set<Point> set = new HashSet<Point>();
        if(this.isBlack()) {
            set.add(new Point(-1, -2));
            set.add(new Point(1, -2));
        } else {
            set.add(new Point(-1, 2));
            set.add(new Point(1, 2));
        }
        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.KEIMA;
    }
}
