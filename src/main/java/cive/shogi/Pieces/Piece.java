package cive.shogi.Pieces;

import cive.shogi.AheadPlayer;
import cive.shogi.GameBoard;
import cive.shogi.Player;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class Piece implements Constant, Cloneable{
    private Point p;
    public Piece(Point p) {
        this.p = p;
    }
    public String getName() {
        return "　";
    };
    public void setPoint(Point p) {
        this.p = p;
    }
    public Point getPoint() {
        return p;
    }
    public Piece clone() throws CloneNotSupportedException {
        return (Piece)super.clone();
    }
    public abstract Set<Point> getRuleOfPiece(int player_type);
    public Set<Point> getCapablePutPoint(Player attacker, Player defender) {
        int player_type = attacker instanceof AheadPlayer ? Player.AHEAD : Player.BEHIND;
        Set<Point> set = new HashSet<>();
        for(Iterator i = this.getRuleOfPiece(player_type).iterator(); i.hasNext();) {
            Point rule_of_point = (Point)i.next();
            Point target = new Point(this.getPoint().x+rule_of_point.x, this.getPoint().y+rule_of_point.y);
            if (attacker.getPieceTypeOnBoardAt(target) == 0 && GameBoard.isInGrid(target))set.add(target);
        }
        return set;
    }
    public abstract Integer getTypeOfPiece();
    public Integer getPre_typeOfPiece() {
        return Piece.NONE;
    };
    public void setPre_typeOfPiece(int pre_type) {
        // nop
    };
    public Boolean canPromote(Point dst, boolean isAheadsTurn) {
        //this : src
        boolean aPromote = (0 <= dst.y && dst.y <= 2) || (0 <= this.getPoint().y && this.getPoint().y <= 2);
        boolean bPromote = (6 <= dst.y && dst.y <= 8) || (6 <= this.getPoint().y && this.getPoint().y <= 8);
        int type = this.getTypeOfPiece();
        boolean canPromoteForType = (type >= Piece.FU && type <= Piece.GIN) || type == Piece.KAKU || type == Piece.HISHA;
        if(isAheadsTurn && aPromote && canPromoteForType) {
            return true;
        } else if (!isAheadsTurn && bPromote && canPromoteForType) {
            return true;
        }
        return false;
    }
    public final Piece getPromotePiece() {
	    if(getTypeOfPiece() >= Piece.FU && getTypeOfPiece() <= Piece.GIN) {
		    Piece p = new Narikin(this.p);
		    p.setPre_typeOfPiece(getTypeOfPiece());
		    return p;
	    } else if(getTypeOfPiece() == Piece.KAKU) {
		    return new Uma(this.p);
	    } else if(getTypeOfPiece() == Piece.HISHA) {
		    return new Ryu(this.p);
	    }
	    return new EmptyPiece(this.p);
    }
    public final Piece getDemotePiece() {
        switch (getPre_typeOfPiece()) {
            case Piece.FU:
                return new Fu(this.p);
            case Piece.KYOSHA:
                return new Kyosha(this.p);
            case Piece.KEIMA:
                return new Keima(this.p);
            case Piece.GIN:
                return new Gin(this.p);
            case Piece.KAKU:
                return new Kaku(this.p);
            case Piece.HISHA:
                return new Hisha(this.p);
            default:
                return new Fu(this.p);
        }
    }
    @Override
    public String toString() {
        return getName();
    }
}
