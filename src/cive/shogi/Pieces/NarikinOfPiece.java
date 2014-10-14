package cive.shogi.Pieces;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class NarikinOfPiece extends Piece {
    int pre_type;
    @Override
    public String getName() {
        return "成金";
    }

    @Override
    public Set<Point> getRuleOfPiece() {
        Set<Point> set = new HashSet<Point>();
        if(this.isBlack()) {
            set.add(new Point(-1, -1));
            set.add(new Point( 0, -1));
            set.add(new Point( 1, -1));
            set.add(new Point(-1,  0));
            set.add(new Point( 1,  0));
            set.add(new Point( 0,  1));
        } else {
            set.add(new Point(-1,  1));
            set.add(new Point( 0,  1));
            set.add(new Point( 1,  1));
            set.add(new Point(-1,  0));
            set.add(new Point( 1,  0));
            set.add(new Point( 0, -1));
        }
        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.NARIKIN;
    }
    @Override
    public Integer getPre_typeOfPiece() {
        return pre_type;
    }
    @Override
    public void setPre_typeOfPiece(int pre_type) {
        this.pre_type = pre_type;
    }
}
