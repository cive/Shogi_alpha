package com.github.cive.shogi.Pieces;

import java.awt.*;
import java.util.Set;

public class EmptyPieceBase extends PieceBase {
    public EmptyPieceBase(Point p) {
        super(p);
    }
    public EmptyPieceBase() {
        super(new Point(-1,-1));
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        return null;
    }
    @Override
    public Integer getTypeOfPiece() {
        return NONE;
    }
}
