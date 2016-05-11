package cive.shogi.Pieces;

import java.awt.*;

/**
 * Created by yotuba on 16/05/05.
 * Piece Factory class
 */
public class PieceFactory implements Constant{
    public Piece create(int pieceId, Point place) {
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
        if (pieceId == Piece.FU) {
            return new Tokin(place);
        } else if (pieceId == Piece.KYOSHA) {
            return new Narikyo(place);
        } else if (pieceId == Piece.KEIMA) {
            return new Narikei(place);
        } else if (pieceId == Piece.GIN) {
            return new Narigin(place);
        } else if (pieceId == Piece.KAKU) {
            return new Uma(place);
        } else if (pieceId == Piece.HISHA) {
            return new Ryu(place);
        } else {
            return new EmptyPiece(place);
        }
    }
    public Piece createDemoted(int pieceId, Point place) {
        if (pieceId == Piece.TOKIN) {
            return new Fu(place);
        } else if (pieceId == Piece.NARIKYO) {
            return new Kyosha(place);
        } else if (pieceId == Piece.NARIKEI) {
            return new Keima(place);
        } else if (pieceId == Piece.NARIGIN) {
            return new Gin(place);
        } else if (pieceId == Piece.UMA) {
            return new Kaku(place);
        } else if (pieceId == Piece.RYU) {
            return new Hisha(place);
        } else {
            return new EmptyPiece(place);
        }
    }
}
