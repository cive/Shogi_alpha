package cive.shogi.Pieces;

import cive.shogi.Player;

import java.awt.*;
import java.lang.management.PlatformLoggingMXBean;
import java.util.HashSet;
import java.util.Set;

public class Keima extends Piece {
    public Keima(Point p) {
        super(p);
    }

    @Override
    public String getName() {
        return "桂";
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        Set<Point> set = new HashSet<Point>();
        if(player_type == Player.AHEAD) {
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
