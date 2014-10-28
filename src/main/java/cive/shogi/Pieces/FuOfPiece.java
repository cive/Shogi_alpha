package cive.shogi.Pieces;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class FuOfPiece extends Piece{
    @Override
    public String getName() {
        return "歩";
    }

    @Override
    public Set<Point> getRuleOfPiece() {
        Set<Point> set = new HashSet<Point>();
        if(this.isBlack()) {
            set.add(new Point(0, -1));
        } else {
            set.add(new Point(0, 1));
        }
        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.FU;
    }
}
