package cive.shogi.Pieces;

import cive.shogi.GameBoard;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class Piece implements ConstantOfPiece{
    public String getName() {
        return "　";
    };
    boolean turn;
    public abstract Set<Point> getRuleOfPiece();
    public Set<Point> getCapableMovePiece(GameBoard board, Point point){
        Set<Point> set = new HashSet<Point>();
        for(Iterator i = this.getRuleOfPiece().iterator(); i.hasNext();) {
            Point rule_of_point = (Point)i.next();
            Point other = new Point(point.x+rule_of_point.x,point.y+rule_of_point.y);
            Piece that = board.getPieceOf(other);
            boolean canMove;
            if(this.isBlack()) {
                canMove = that.getTypeOfPiece() == 0 || (this.isBlack() && that.isWhite());
            } else {
                canMove = that.getTypeOfPiece() == 0 || (this.isWhite() && that.isBlack());
            }
            if(board.canPut(other) && canMove) {
                set.add(other);

            }
        }
        return set;
    };
    public abstract Integer getTypeOfPiece();
    public Integer getPre_typeOfPiece() {
        return Piece.NONE;
    };
    public void setPre_typeOfPiece(int pre_type) {
        // nop
    };
    /** isBlack() 先手か後手かの持ち駒を返すメソッド
     * 将棋では黒であれば先手であることから
     * isBlack()がtrueであれば，先手である．
     * @return 先手であればtrue.
     */
    public final boolean isBlack() {
        return getTypeOfPiece() > 0 && turn;
    };
    public final boolean isWhite() {
        return getTypeOfPiece() > 0 && !turn;
    }
    public final void setTurn(boolean turn) {
        this.turn = turn;
    };

}
