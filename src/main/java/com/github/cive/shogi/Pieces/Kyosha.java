package com.github.cive.shogi.Pieces;

import com.github.cive.shogi.Players.AheadPlayer;
import com.github.cive.shogi.GameBoard;
import com.github.cive.shogi.Players.Player;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Kyosha extends Piece {
    public Kyosha(Point p) {
        super(p);
    }

    @Override
    public String getName() {
        return "香";
    }
    @Override
    public String getName(Boolean in_English) {
        if (in_English) {
            return "KY";
        } else {
            return getName();
        }
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        // dammy
        Set<Point> set = new HashSet<>();
        if(player_type == Player.AHEAD) {
            for(int i = 1; i < 10; i++) {
                set.add(new Point(0, -i));
            }
        } else {
            for(int i = 1; i < 10; i++) {
                set.add(new Point(0, i));
            }
        }
        return set;
    }
    @Override
    public Set<Point> getCapablePutPoint(Player attacker, Player defender) {
        int player_type = attacker instanceof AheadPlayer ? Player.AHEAD : Player.BEHIND;
        Set<Point> set = new HashSet<>();
        int ini = player_type == Player.AHEAD ? -1 : 1;
        for(int i = ini;  Math.abs(i) < 9;i += ini) {
            Point target = new Point(this.getPoint().x, this.getPoint().y+i);
            if(GameBoard.isInGrid(target)) {
                if (attacker.getPieceTypeOnBoardAt(target) > 0) {
                    break;
                }
                set.add(target);
                if (defender.getPieceTypeOnBoardAt(target) > 0) {
                    break;
                }
            }
        }
        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.KYOSHA;
    }
}
