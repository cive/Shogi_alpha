package com.github.cive.shogi;

import com.github.cive.shogi.Pieces.PieceBase;

/**
 * Created by yotuba on 16/05/20.
 * 盤面のデータ構造
 */
public class MovementOfPiece {
    public MovementOfPiece(PieceBase src, PieceBase dst) {
        setDst(dst);
        setSrc(src);
    }
    private PieceBase src;
    private PieceBase dst;
    public void setDst(PieceBase dst) {
        try {
            this.dst = dst.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
    public void setSrc(PieceBase src) {
        try {
            this.src = src.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
    public PieceBase getDst() throws CloneNotSupportedException{
        return dst.clone();
    }
    public PieceBase getSrc() throws CloneNotSupportedException {
        return src.clone();
    }
    @Override
    public String toString() {
        return src.toString() + "->" + dst.toString();
    }
}