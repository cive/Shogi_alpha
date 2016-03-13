package cive.shogi.Pieces;

import cive.shogi.GameBoard;
import cive.shogi.Player;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Kaku extends Piece {
    public Kaku(Point p) {
        super(p);
    }

    @Override
    public String getName() {
        return "角";
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        // dammy
        Set<Point> set = new HashSet<Point>();
        for(int i = -8; i < 9; i++) {
            if(i == 0) continue;
            set.add(new Point(i, i));
            set.add(new Point(-i, i));
        }
        return set;
    }
    @Override
    public Set<Point> getCapablePutPoint(Player attacker, Player defender){
        Set<Point> set = new HashSet<>();
        set.addAll(getSetToNeedToAdd(attacker, defender, true, 1, this.getPoint()));
        set.addAll(getSetToNeedToAdd(attacker, defender, false, 1, this.getPoint()));
        set.addAll(getSetToNeedToAdd(attacker, defender, true, -1, this.getPoint()));
        set.addAll(getSetToNeedToAdd(attacker, defender, false, -1, this.getPoint()));
        return set;
    };

    public Set<Point> getSetToNeedToAdd(Player attacker, Player defender, boolean axis, int ini, Point selected) {
         Set<Point> set_for_add = new HashSet<>();
        for(int i = ini; Math.abs(i) < 9; i += ini) {
            Point target = new Point(selected.x+(axis?i:-i), selected.y+i);
            if(GameBoard.isInGrid(target)) {
                if (attacker.getPieceTypeOnBoardAt(target) > 0) {
                    break;
                }
                set_for_add.add(target);
                if (defender.getPieceTypeOnBoardAt(target) > 0) {
                    break;
                }
            }
        }
        return set_for_add;
    }
   @Override
    public Integer getTypeOfPiece() {
        return Piece.KAKU;
    }
}
