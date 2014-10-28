package cive.shogi.Pieces;

import java.awt.*;
import java.util.Set;

public class EmptyPiece extends Piece {
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public Set<Point> getRuleOfPiece() {
        return null;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.NONE;
    }
}
