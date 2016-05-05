package cive.shogi.Pieces;

import java.awt.*;

/**
 * Created by yotuba on 16/05/05.
 * Piece Factory class
 */
public class PieceFactory implements Constant{
    public Piece create(int pieceId, Point place) {
        Piece piece = null;
        switch(pieceId) {
            case FU: return new Fu(place);
            case KYOSHA: return new Kyosha(place);
            case KEIMA: return new Keima(place);
            case GIN: return new Gin(place);
            case KIN: return new Kin(place);
            case KAKU: return new Kaku(place);
            case HISHA: return new Hisha(place);
            case GYOKU: return new Gyoku(place);
            case UMA: return new Uma(place);
            case RYU: return new Ryu(place);
            default: return new EmptyPiece(place);
        }
    }
    public Piece createPromoted(int pieceId, Point place) {
        if(pieceId >= Piece.FU && pieceId <= Piece.GIN) {
            Piece p = new Narikin(place);
            p.setPre_typeOfPiece(pieceId);
            return p;
        } else if(pieceId == Piece.KAKU) {
            return new Uma(place);
        } else if(pieceId == Piece.HISHA) {
            return new Ryu(place);
        } else {
            return new EmptyPiece(place);
        }
    }
}
