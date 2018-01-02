package com.github.cive.shogi.Pieces;

import com.github.cive.shogi.Players.PlayerBase;

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
    public String getName(Boolean in_English) {
        if (in_English) {
            return "UM";
        } else {
            return getName();
        }
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        // dammy
        Set<Point> set = new HashSet<>();
        PieceFactory factory = new PieceFactory();
        PieceBase kaku = factory.create(PieceBase.KAKU, this.getPoint());
        PieceBase gyoku = factory.create(PieceBase.GYOKU, this.getPoint());
        set.addAll(kaku.getRuleOfPiece(player_type));
        set.addAll(gyoku.getRuleOfPiece(player_type));
        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return PieceBase.UMA;
    }
    @Override
    public Set<Point> getCapablePutPoint(PlayerBase attacker, PlayerBase defender) {
        Set<Point> set = new HashSet<>();
        set.addAll(getSetToNeedToAdd(attacker, defender, true, 1, this.getPoint()));
        set.addAll(getSetToNeedToAdd(attacker, defender, false, 1, this.getPoint()));
        set.addAll(getSetToNeedToAdd(attacker, defender, true, -1, this.getPoint()));
        set.addAll(getSetToNeedToAdd(attacker, defender, false, -1, this.getPoint()));
        set.addAll((new Gyoku(this.getPoint()).getCapablePutPoint(attacker, defender)));
        return set;
    }
}
