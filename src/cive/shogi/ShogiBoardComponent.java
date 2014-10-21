package cive.shogi;

import cive.shogi.Pieces.EmptyPiece;
import cive.shogi.Pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class ShogiBoardComponent extends JComponent{
    private static final int BLACK = 0;
    private static final int WHITE = 1;
    private static final String asset_img_path = "resources/img";

    private static final Point OFFSET = new Point(151 + 26, 0 + 26);
    private static final int GRID = 50;
    private static final Point OFFSET_OF_GRID = new Point(1, 2);
    private static final Point BLACKS_OFFSET = new Point(650,300);
    private static final Point WHITES_OFFSET = new Point(0, 0);
    private GameBoard gameBoard;
    private BufferedImage Shogi_Img;
    private BufferedImage ShogiBoard_Img;
    private Graphics2D offShogi_Img;
    private BufferedImage Pieces_Img[][] = new BufferedImage[2][14];
    private Point selected_point = new Point(-1,-1);
    private Piece selected_pieces_inHand = new EmptyPiece();
    public ShogiBoardComponent() {
        gameBoard = new GameBoard();
        loadImages();

        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        addMouseListener(new MouseAdapter() {
           public void mouseClicked(MouseEvent e) {
               selectPieceAt(e.getPoint());
           }
        });
    }
    private boolean[] getJudgementsOfPieceInHand(Point p, Point offset) {
        boolean judgements[] = new boolean[7];
        judgements[Piece.FU -1] = p.x >= offset.x && p.x <= (offset.x+150) &&
                p.y >= offset.y && p.y <= (offset.y+50);
        for(int i = 2; i < 8; i+=2) {
            judgements[i-1] = p.x >= offset.x    && p.x <= (offset.x+75) &&
                    p.y >= offset.y+i*25 && p.y <= (offset.y+50+i*25);
            judgements[i]=p.x >= offset.x    && p.x <= (offset.x+150) &&
                    p.y >= offset.y+100+i*25 && p.y <= (offset.y+100+i*25);
        }
        return judgements;
    }
    private void logicOfMovingPieceOnBoard(Point selecting, Point clicked) {
        if (getSelected_point().x != -1) {
            Piece that = gameBoard.getPieceOf(getSelected_point().x, getSelected_point().y);
            // クリックした駒が，ちゃんと手番に合っているかどうか．
            // あっていれば，trueを返す．
            boolean matchAlternative = that.isBlack() && gameBoard.isBlack() || that.isWhite() && !gameBoard.isBlack();
            if (matchAlternative) {
                Iterator itr = that.getCapableMovePiece(gameBoard, getSelected_point()).iterator();
                for (; itr.hasNext(); ) {
                    Point capable_move_point = (Point) itr.next();
                    boolean canMove = clicked.x == capable_move_point.x && clicked.y == capable_move_point.y;
                    if (canMove) {
                        Piece clicked_piece = gameBoard.getPieceOf(clicked);
                        if (clicked_piece.getTypeOfPiece() > 0) {
                            gameBoard.addPieceInHand(clicked_piece);
                        }
                        gameBoard.setBoard_Arr(that, clicked);
                        gameBoard.setBoard_Arr(new EmptyPiece(), getSelected_point());
                        gameBoard.nextTurn();
                    }
                }
            }
            setSelected_point(new Point(-1, -1));
        } else {
            setSelected_point(clicked);
        }
    }
    public void selectPieceAt(Point selecting) {
        boolean onBoard = selecting.x >= OFFSET.x && selecting.x <= (OFFSET.x+50*9) && selecting.y >= OFFSET.y && selecting.y <= (OFFSET.y+50*9);
        if(onBoard) {
            Point clicked_point = new Point((int) ((selecting.x - OFFSET.x) / 50), (int) ((selecting.y - OFFSET.y) / 50));
            logicOfMovingPieceOnBoard(selecting, clicked_point);
        }  else if (!onBoard) {
            // 持ち駒の処理．
            // TODO: メソッド分割．
            boolean onWhiteBoard_of[];
            onWhiteBoard_of = getJudgementsOfPieceInHand(selecting, WHITES_OFFSET);
            boolean onBlackBoard_of[];
            onBlackBoard_of = getJudgementsOfPieceInHand(selecting, BLACKS_OFFSET);
            Set<Piece> whites_set = gameBoard.getPieces_inHand_of_white();
            Set<Piece> blacks_set = gameBoard.getPieces_inHand_of_black();
            boolean couldSelected = false;
            for(int type = 1; type < 8; type++) {
                if (onWhiteBoard_of[type-1]) {
                    couldSelected = setSelected_pieces_inHand(whites_set, type);
                    if(couldSelected)break;
                    System.out.println(selected_pieces_inHand);
                }
                if (onBlackBoard_of[type-1]) {
                    couldSelected = setSelected_pieces_inHand(blacks_set, type);
                    if(couldSelected)break;
                }
                if (!couldSelected) {
                    selected_pieces_inHand = new EmptyPiece();
                }
            }
            selected_point = new Point(-1, -1);
        }
        // TODO: clear print 4 debug.
        System.out.println(selected_point);
        repaint();
    }
    // もし，セレクトできれば，trueを返す．
    public boolean setSelected_pieces_inHand(Set<Piece> set, final int TYPE) {
        boolean couldSelected = false;
        for(Iterator i = set.iterator(); i.hasNext();) {
            Piece piece = (Piece)i.next();
            if(piece.getTypeOfPiece() == TYPE) {
                selected_pieces_inHand = piece;
                // TODO: clear print 4 debug.
                System.out.println("IN HAND, SELECTED PIECE'S TYPE IS " + TYPE);
                couldSelected = true;
                break;
            }
        }
        return couldSelected;
    }
    public String pad(int n) {
        if (n < 10) return "0" + n;
        else return String.valueOf(n);
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // 将棋盤をoffShogi_imgにdraw.
        offShogi_Img.drawImage(ShogiBoard_Img, 0, 0, 802, 500, this);
        paintPiecesOnBoard(g2);
        Set<Piece> blacks_set = gameBoard.getPieces_inHand_of_black();
        Set<Piece> whites_set = gameBoard.getPieces_inHand_of_white();
        // offShogi_imgにdrawする.
        paintPiecesInHand(g2, blacks_set, BLACKS_OFFSET);
        paintPiecesInHand(g2, whites_set, WHITES_OFFSET);
        // 選択中のマス目が将棋盤のマス目であれば
        if(selected_point.x != -1) paintHighlightsOnBoard(g);
        g2.drawImage(Shogi_Img, 0, 0, 802, 500, this);
        g2.dispose();
    }
    private void paintPiecesOnBoard(Graphics g) {
        for(int y = 0; y < 9; y++) for(int x = 0; x < 9; x++) {
            Piece that  = gameBoard.getPieceOf(new Point(x, y));
            int type = that.getTypeOfPiece();
            if(type > 0){
                 offShogi_Img.drawImage(
                        Pieces_Img[that.isBlack()?BLACK:WHITE][type-1],
                        OFFSET.x + GRID *x + OFFSET_OF_GRID.x,
                        OFFSET.y + GRID *y + OFFSET_OF_GRID.y,
                        45, 48, this
                );
            }
        } // double for
    }
    private void paintPiecesInHand(Graphics g, Set<Piece> set, Point offset) {
        int count_of[] = new int[7];
        int alternative  = 0;
        for(Iterator ite = set.iterator(); ite.hasNext();) {
            Piece piece_inHand = (Piece) ite.next();
            alternative = piece_inHand.isBlack() ? BLACK : WHITE;
            int x = 0, y = 0;
            if(piece_inHand.getTypeOfPiece() == Piece.FU) {
                count_of[Piece.FU - 1]++;
            }
            for(int i = 2; i < 8; i+=2 ) {
                if(piece_inHand.getTypeOfPiece() == i) {
                    x = count_of[i-1] * 5;
                    y = 25 * i;
                    count_of[i-1]++;
                }
                if(piece_inHand.getTypeOfPiece() == i+1) {
                    x = 60 + count_of[i] * 5;
                    y = 25 * i;
                    count_of[i]++;
                }
            }
            offShogi_Img.drawImage(
                    Pieces_Img[alternative][piece_inHand.getTypeOfPiece() - 1],
                    offset.x + x,
                    offset.y + y,
                    45, 48, this
            );
        }
        for(int i = 0; i < count_of[Piece.FU -1]; i++) {
            int width = (count_of[Piece.FU-1] < 8)?15:5;
            offShogi_Img.drawImage(
                    Pieces_Img[alternative][Piece.FU - 1],
                    offset.x + i * width,
                    offset.y,
                    45, 48, this
            );
        }
    }

    /**
     * 駒台上で選択した駒，おける位置をHighlightする.
     * @param g Graphics
     */
    private void paintHighlightsOfTableOfPiecesInHand(Graphics g) {

    }

    /**
     * 将棋盤上で選択した駒，おける位置をHighlightする.
     * @param g Graphics
     */
    private void paintHighlightsOnBoard(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Piece that  = gameBoard.getPieceOf(selected_point.x, selected_point.y);
        int type = that.getTypeOfPiece();
        if(type > 0 && ((that.isBlack()) && gameBoard.isBlack() || that.isWhite() && !gameBoard.isBlack())) {
            Rectangle rect = new Rectangle(
                    OFFSET.x + GRID * selected_point.x,
                    OFFSET.y + GRID * selected_point.y,
                    48,
                    48
            );
            // 選択した駒のhighlight.
            offShogi_Img.setColor(new Color(255, 0, 0, 90));
            offShogi_Img.setStroke(new BasicStroke(5.0f));
            offShogi_Img.draw(rect);
            // 選択した駒からおける場所へのhighlight.
            Iterator itr = that.getCapableMovePiece(gameBoard, selected_point).iterator();
            for(;itr.hasNext();) {
                Point p = (Point)itr.next();
                Rectangle capable_rect = new Rectangle(
                        OFFSET.x + GRID * p.x,
                        OFFSET.y + GRID * p.y,
                        48,
                        48
                );
                offShogi_Img.setColor(new Color(0, 255, 0, 80));
                offShogi_Img.draw(capable_rect);
            }
        }
        // 持ち駒からおける場所のhighlight.
    }
    private void loadImages() {
        Shogi_Img = new BufferedImage(802, 500, BufferedImage.TYPE_3BYTE_BGR);
        ShogiBoard_Img = new BufferedImage(802, 500, BufferedImage.TYPE_INT_ARGB);
        offShogi_Img = Shogi_Img.createGraphics();
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 14; j++) {
                Pieces_Img[i][j] = new BufferedImage(30, 32, BufferedImage.TYPE_4BYTE_ABGR);
            }
        }
        try {
            ShogiBoard_Img = ImageIO.read(new File(asset_img_path + "/ShogiBoard.png"));
            for(int i = 0; i < 14; i++) {
                String filename_of[] = new String[2];
                if(i < 10) {
                    filename_of[BLACK] = new String().format(asset_img_path + "/pieces/B%s.png", pad(i+1) );
                    filename_of[WHITE] = new String().format(asset_img_path + "/pieces/W%s.png", pad(i+1) );
                } else {
                    filename_of[BLACK] = new String().format(asset_img_path + "/pieces/B11_%s.png", pad(i-10+1));
                    filename_of[WHITE] = new String().format(asset_img_path + "/pieces/W11_%s.png", pad(i-10+1));
                }
                //TODO: delete print 4 debug
                System.out.println(filename_of[WHITE] + " " + filename_of[BLACK]);
                Pieces_Img[BLACK][i] = ImageIO.read(new File(filename_of[BLACK]));
                Pieces_Img[WHITE][i] = ImageIO.read(new File(filename_of[WHITE]));
            }
        } catch(IOException e) {
            //TODO: delete print 4 debug
            e.printStackTrace();
        }
    }

    public void setSizeOfBoard() {
        Dimension dim = new Dimension(802, 500);
        setMinimumSize(dim);
        setPreferredSize(dim);
    }
    public void setSelected_point(Point p) {
        this.selected_point = p;
    }
    public Point getSelected_point() {
        return this.selected_point;
    }
    public GameBoard getGameBoard() {
        return gameBoard;
    }
}
