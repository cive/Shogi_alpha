package cive.shogi.Pieces;

import cive.shogi.Players.Player;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Uma extends Kaku{
    public Uma(Point p) {
        super(p);
    }

    @Override
    public String getName() {
        return "馬";
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        // dammy
        Set<Point> set = new HashSet<>();
        PieceFactory factory = new PieceFactory();
        Piece kaku = factory.create(Piece.KAKU, this.getPoint());
        Piece gyoku = factory.create(Piece.GYOKU, this.getPoint());
        set.addAll(kaku.getRuleOfPiece(player_type));
        set.addAll(gyoku.getRuleOfPiece(player_type));
        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.UMA;
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
