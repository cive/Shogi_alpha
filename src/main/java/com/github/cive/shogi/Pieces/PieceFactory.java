package com.github.cive.shogi.Pieces;

import java.awt.*;

/**
 * Created by yotuba on 16/05/05.
 * PieceBase Factory class
 */
public class PieceFactory implements Constant {
    public PieceBase create(int pieceId, Point place) {
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
            default: return new EmptyPieceBase(place);
        }
    }
    public PieceBase createPromoted(int pieceId, Point place) {
        if (pieceId == FU) {
            return new Tokin(place);
        } else if (pieceId == KYOSHA) {
            return new Narikyo(place);
        } else if (pieceId == KEIMA) {
            return new Narikei(place);
        } else if (pieceId == GIN) {
            return new Narigin(place);
        } else if (pieceId == KAKU) {
            return new Uma(place);
        } else if (pieceId == HISHA) {
            return new Ryu(place);
        } else {
            return new EmptyPieceBase(place);
        }
    }
    public PieceBase createDemoted(int pieceId, Point place) {
        if (pieceId == TOKIN) {
            return new Fu(place);
        } else if (pieceId == NARIKYO) {
            return new Kyosha(place);
        } else if (pieceId == NARIKEI) {
            return new Keima(place);
        } else if (pieceId == NARIGIN) {
            return new Gin(place);
        } else if (pieceId == UMA) {
            return new Kaku(place);
        } else if (pieceId == RYU) {
            return new Hisha(place);
        } else {
            return new EmptyPieceBase(place);
        }
    }
}
