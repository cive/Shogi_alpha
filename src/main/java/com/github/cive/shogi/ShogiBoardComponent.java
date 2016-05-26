package com.github.cive.shogi;

import com.github.cive.shogi.Pieces.EmptyPiece;
import com.github.cive.shogi.Pieces.Piece;
import com.github.cive.shogi.Players.AheadPlayer;
import com.github.cive.shogi.Players.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class ShogiBoardComponent extends JComponent{
    private static final String ASSET_IMG_PATH = "/img";

    private static final Point OFFSET = new Point(151 + 26, 0 + 26);
    private static final int GRID = 50;
    private static final Point OFFSET_OF_GRID = new Point(1, 2);
    private static final Point AHEAD_OFFSET = new Point(650,300);
    private static final Point BEHIND_OFFSET = new Point(0, 0);
    private GameBoard gameBoard;
    private BufferedImage Shogi_Img;
    private BufferedImage ShogiBoard_Img;
    private Graphics2D offShogi_Img;
    private BufferedImage Pieces_Img[][] = new BufferedImage[2][14];
    private Point selected_point = new Point(-1,-1);
    private Piece selected_piece_in_hand;
    public ShogiBoardComponent() {
        gameBoard = new GameBoard();
        selected_piece_in_hand = new EmptyPiece();
        loadImages();

        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        addMouseListener(new MouseAdapter() {
           public void mouseClicked(MouseEvent e) {
               selectPieceAt(e.getPoint());
           }
        });
    }
    private Integer getTypeOfPieceInHand(Point clicked, Point offset) {
        boolean judge;
        judge = clicked.x >= offset.x && clicked.x <= (offset.x+150) &&
                clicked.y >= offset.y && clicked.y <= (offset.y+50);
        if(judge)return Piece.FU;
        for(int type = 2; type < 8; type+=2) {
            judge = clicked.x >= offset.x    && clicked.x <= (offset.x+75) &&
                    clicked.y >= offset.y+type*25 && clicked.y <= (offset.y+50+type*25);
            if(judge)return type;
            judge=clicked.x >= (offset.x+75)    && clicked.x <= (offset.x+150) &&
                    clicked.y >= offset.y+type*25 && clicked.y <= (offset.y+50+type*25);
            if(judge)return type+1;
        }
        return Piece.NONE;
    }
    public void selectPieceAt(Point clicked) {
    	// 盤上の選択
        boolean onBoard = clicked.x >= OFFSET.x && clicked.x <= (OFFSET.x+50*9) && clicked.y >= OFFSET.y && clicked.y <= (OFFSET.y+50*9);

        // 持ち駒を選択中
        boolean haveSelectedInHand = selected_piece_in_hand.getTypeOfPiece() != Piece.NONE;

        if(onBoard) {
            Point clicked_on_board = new Point((int) ((clicked.x - OFFSET.x) / 50), (int) ((clicked.y - OFFSET.y) / 50));
            // 既に盤上を選択していた時
            boolean haveSelectedOnBoard = getSelected_point().x != -1;

            if (haveSelectedInHand) {
        		// 持ち駒を置く．
        		gameBoard.placePieceInHand(selected_piece_in_hand, clicked_on_board);

        		// 選択解除
        		selected_piece_in_hand = new EmptyPiece();
            } else if (haveSelectedOnBoard) {
                if (gameBoard.canPlaceInside(getSelected_point(), clicked_on_board)) {
	            // なり駒するかの判定
		            if(gameBoard.getAttacker().getPieceOnBoardAt(getSelected_point()).canPromote(clicked_on_board, gameBoard.isAheadsTurn())) {
                        System.out.println("can promote");
			            Object[] options = {"はい", "いいえ", "キャンセル"};
			            int reply = JOptionPane.showOptionDialog(null, "成りますか？", "成駒", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                        if(reply == JOptionPane.YES_OPTION) {
				            gameBoard.replacePieceWithPromote(getSelected_point(), clicked_on_board);
				            setSelected_point(new Point(-1, -1));
			            } else if(reply == JOptionPane.NO_OPTION) {
				            gameBoard.replacePiece(getSelected_point(), clicked_on_board);
				            setSelected_point(new Point(-1, -1));
			            } else {
                            setSelected_point(new Point(-1, -1));
                        }
		            } else {
                        gameBoard.replacePiece(getSelected_point(), clicked_on_board);
			            setSelected_point(new Point(-1, -1));
		            }
                } else {
                    setSelected_point(clicked_on_board);
		        }
            } else {
                setSelected_point(clicked_on_board);
            }
        }  else if (!onBoard) {
            // 持ち駒の処理
        	if (haveSelectedInHand) {
        		// 選択解除
        		selected_piece_in_hand = new EmptyPiece();
        	} else {
                int type_of_selected_in_hand;
                if (gameBoard.getAttacker() instanceof AheadPlayer)
                    type_of_selected_in_hand = getTypeOfPieceInHand(clicked, AHEAD_OFFSET);
                else
                    type_of_selected_in_hand = getTypeOfPieceInHand(clicked, BEHIND_OFFSET);
                boolean couldSelect = false;
                for (Piece piece : gameBoard.getAttacker().getPiecesInHand()) {
                    if(piece.getTypeOfPiece() == type_of_selected_in_hand) {
                        couldSelect = true;
                        setSelected_piece_in_hand(piece);
                        break;
                    }
                }
                if(!couldSelect) selected_piece_in_hand = new EmptyPiece();
        	}
            setSelected_point(new Point(-1, -1));
        }
        // TODO: clear print 4 debug.
        System.out.println(selected_point);
        repaint();
    }
    public String pad(int n) {
        if (n < 10) return "0" + n;
        else return String.valueOf(n);
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // 将棋盤をoffShogi_imgにdraw.
        offShogi_Img.drawImage(ShogiBoard_Img, 0, 0, 802, 500, this);
        paintPiecesOnBoard(g2, gameBoard.getAttacker());
        paintPiecesOnBoard(g2, gameBoard.getDefender());
        // offShogi_imgにdrawする.
        paintPiecesInHand(g2, gameBoard.getAttacker());
        paintPiecesInHand(g2, gameBoard.getDefender());
        // 選択中のマス目が将棋盤のマス目であれば
        if(selected_point.x != -1 && gameBoard.getAttacker().getPieceTypeOnBoardAt(selected_point) > 0){
        	paintHighlightsOnBoard(g);
        }
        // 持ち駒であれば
        else if(gameBoard.getAttacker().matchTypeInHand(selected_piece_in_hand)){
        	paintHighlightsOfTableOfPiecesInHand(g);
        }
        g2.drawImage(Shogi_Img, 0, 0, 802, 500, this);
        g2.dispose();
    }
    private void paintPiecesOnBoard(Graphics g, Player player) {
        for(Piece p : player.getPiecesOnBoard()) {
            offShogi_Img.drawImage(
                    Pieces_Img[player instanceof AheadPlayer ? Player.AHEAD : Player.BEHIND][p.getTypeOfPiece() - 1],
                    OFFSET.x + GRID * p.getPoint().x + OFFSET_OF_GRID.x,
                    OFFSET.y + GRID * p.getPoint().y + OFFSET_OF_GRID.y,
                    45, 48, this
            );
        }
    }
    private void paintPiecesInHand(Graphics g, Player player) {
        int player_type = player instanceof AheadPlayer ? Player.AHEAD : Player.BEHIND;
        Point offset = player_type == Player.AHEAD ? AHEAD_OFFSET : BEHIND_OFFSET;
        int count_of[] = new int[7];
        for(Piece p : player.getPiecesInHand()) {
            int x = 0, y = 0;
            if(p.getTypeOfPiece() == Piece.FU) {
                count_of[Piece.FU - 1]++;
            }
            for(int i = 2; i < 8; i+=2 ) {
                if(p.getTypeOfPiece() == i) {
                    x = count_of[i-1] * 5;
                    y = 25 * i;
                    count_of[i-1]++;
                }
                if(p.getTypeOfPiece() == i+1) {
                    x = 60 + count_of[i] * 5;
                    y = 25 * i;
                    count_of[i]++;
                }
            }
            offShogi_Img.drawImage(
                    Pieces_Img[player_type][p.getTypeOfPiece() - 1],
                    offset.x + x,
                    offset.y + y,
                    45, 48, this
            );
        }
        for(int count = 1; count < count_of[Piece.FU -1]; count++) {
            int width = (count_of[Piece.FU-1] < 8)?15:5;
            offShogi_Img.drawImage(
                    Pieces_Img[player_type][Piece.FU - 1],
                    offset.x + count * width,
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
        Graphics2D g2 = (Graphics2D) g;
        Piece sel = selected_piece_in_hand;

        // TODO: 持ち駒自身のhighlightがまだ
        /*
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
        */

        // 選択した駒からおける場所へのhighlight.
        for(int x = 0; x < 9; x++) {
            if(gameBoard.selected_will_be_niFu(sel, x)){
                continue;
            }
            for(int y = 0; y < 9; y++) {
                if (gameBoard.isTherePieceAt(new Point(x, y))){
                    continue;
                }
                Point p = new Point(x, y);
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
    }

    /**
     * 将棋盤上で選択した駒，おける位置をHighlightする.
     * @param g Graphics
     */
    private void paintHighlightsOnBoard(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Piece sel = gameBoard.getPieceOf(selected_point.x, selected_point.y);
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
        Iterator itr = sel.getCapablePutPoint(gameBoard.getAttacker(), gameBoard.getDefender()).iterator();
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
            URL shogi_board_img_url = this.getClass().getResource(ASSET_IMG_PATH + "/ShogiBoard.png");
	        // TODO: delete 4 debug
            System.out.println(this.getClass().getResource(""));
	        System.out.println(this.getClass().getResource("/img/ShogiBoard.png"));
            ShogiBoard_Img = ImageIO.read(shogi_board_img_url);
            for(int i = 0; i < 14; i++) {
                String filename_of[] = new String[2];
                if(i < 10) {
                    filename_of[Player.AHEAD] = String.format("%s/pieces/B%s.png",ASSET_IMG_PATH , pad(i+1) );
                    filename_of[Player.BEHIND] = String.format("%s/pieces/W%s.png",ASSET_IMG_PATH , pad(i+1) );
                } else {
                    filename_of[Player.AHEAD] = String.format("%s/pieces/B11_%s.png",ASSET_IMG_PATH , pad(i-10+1));
                    filename_of[Player.BEHIND] = String.format("%s/pieces/W11_%s.png",ASSET_IMG_PATH,  pad(i-10+1));
                }
                //TODO: delete print 4 debug
                System.out.println(filename_of[Player.BEHIND] + " " + filename_of[Player.AHEAD]);
                Pieces_Img[Player.AHEAD][i] = ImageIO.read(getClass().getResource(filename_of[Player.AHEAD]));
                Pieces_Img[Player.BEHIND][i] = ImageIO.read(getClass().getResource(filename_of[Player.BEHIND]));
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
    public void setSelected_piece_in_hand(Piece selected_piece_in_hand) {
        this.selected_piece_in_hand = selected_piece_in_hand;
    }
    public Piece getSelected_piece_in_hand() {
        return selected_piece_in_hand;
    }
}
