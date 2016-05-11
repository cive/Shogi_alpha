package cive.shogi.Pieces;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yotuba on 16/05/12.
 * Promoted Kyosha
 */
public class Narikyo extends Piece {
    public Narikyo(Point p) {
        super(p);
    }

    @Override
    public String getName() {
        return "杏";
    }

    @Override
    public Set<Point> getRuleOfPiece(int player_type) {
        Set<Point> set = new HashSet<>();
        PieceFactory factory = new PieceFactory();
        Piece kin = factory.create(Piece.KIN, this.getPoint());
        set.addAll(kin.getRuleOfPiece(player_type));
        return set;
    }
    @Override
    public Integer getTypeOfPiece() {
        return Piece.NARIKYO;
    }
}
