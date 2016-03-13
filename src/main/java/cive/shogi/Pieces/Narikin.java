package cive.shogi.Pieces;

import cive.shogi.Player;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Narikin extends Piece {
    int pre_type;

    public Narikin(Point p) {
        super(p);
    }

    @Override
    public String getName() {
        return "と";
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        Set<Point> set = new HashSet<Point>();
        if(player_type == Player.AHEAD) {
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
