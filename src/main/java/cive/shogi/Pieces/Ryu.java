package cive.shogi.Pieces;

import cive.shogi.Player;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Ryu extends Hisha {
    public Ryu(Point p) {
        super(p);
    }

    @Override
    public String getName() {
        return "龍";
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        // dammy
        Set<Point> set = new HashSet<>();
        Piece hisha = new Hisha(this.getPoint());
        Piece gyoku = new Gyoku(this.getPoint());
        set.addAll(hisha.getRuleOfPiece(player_type));
        set.addAll(gyoku.getRuleOfPiece(player_type));

        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.RYU;
    }
    @Override
    public Set<Point> getCapablePutPoint(Player attacker, Player defender) {
        Set<Point> set = new HashSet<>();
        set.addAll(getSetToNeedToAdd(attacker, defender, true, 1, this.getPoint()));
        set.addAll(getSetToNeedToAdd(attacker, defender, false, 1, this.getPoint()));
        set.addAll(getSetToNeedToAdd(attacker, defender, true, -1, this.getPoint()));
        set.addAll(getSetToNeedToAdd(attacker, defender, false, -1, this.getPoint()));
        set.addAll((new Gyoku(this.getPoint()).getCapablePutPoint(attacker, defender)));
        return set;
    }

}
