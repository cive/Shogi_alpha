package com.github.cive.shogi;

import com.github.cive.shogi.Pieces.EmptyPieceBase;
import com.github.cive.shogi.Pieces.PieceBase;
import com.github.cive.shogi.Players.AheadPlayer;
import com.github.cive.shogi.Players.PlayerBase;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import twitter4j.*;

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
    private PieceBase selected_piece_Base_in_hand;
    private ShogiBoardController controller;
    public ShogiBoardComponent() {
        controller = new ShogiBoardController();
        selected_piece_Base_in_hand = new EmptyPieceBase();
        loadImages();

        // Twitter Listener
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        FilterQuery filter = new FilterQuery();
        filter.track(new String[] {"#twitter_shogi"});
        twitterStream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                controller.placeFromTweet(status.getText());
                System.out.println(status.getText());
                repaint();
            }
            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
            @Override
            public void onTrackLimitationNotice(int i) {}
            @Override
            public void onScrubGeo(long l, long l1) {}
            @Override
            public void onStallWarning(StallWarning stallWarning) {}
            @Override
            public void onException(Exception e) {}
        });
        twitterStream.filter(filter);

        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mouseEvent(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseEvent(e);
            }
        });
    }

    private void mouseEvent(MouseEvent e) {
        if (controller.mode() == ShogiBoardController.BATTLE_MODE) {
            controller.selectPieceAt(e.getPoint());
        } else if (controller.mode() == ShogiBoardController.VIEW_MODE) {

        }
        repaint();
    }


    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        controller.setGameBoard(gameBoard);
    }
    private String pad(int n) {
        if (n < 10) return "0" + n;
        else return String.valueOf(n);
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        selected_point = controller.getSelected_point();
        try {
            selected_piece_Base_in_hand = controller.getSelected_piece_Base_in_hand();
        } catch (CloneNotSupportedException e ) {
            e.printStackTrace();
        }
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
        else if(gameBoard.getAttacker().matchTypeInHand(selected_piece_Base_in_hand)){
        	paintHighlightsOfTableOfPiecesInHand(g);
        }
        g2.drawImage(Shogi_Img, 0, 0, 802, 500, this);
        g2.dispose();
    }
    private void paintPiecesOnBoard(Graphics g, PlayerBase player) {
        for(PieceBase p : player.getPiecesOnBoard()) {
            offShogi_Img.drawImage(
                    Pieces_Img[player instanceof AheadPlayer ? PlayerBase.AHEAD : PlayerBase.BEHIND][p.getTypeOfPiece() - 1],
                    OFFSET.x + GRID * p.getPoint().x + OFFSET_OF_GRID.x,
                    OFFSET.y + GRID * p.getPoint().y + OFFSET_OF_GRID.y,
                    45, 48, this
            );
        }
    }
    private void paintPiecesInHand(Graphics g, PlayerBase player) {
        int player_type = player instanceof AheadPlayer ? PlayerBase.AHEAD : PlayerBase.BEHIND;
        Point offset = player_type == PlayerBase.AHEAD ? AHEAD_OFFSET : BEHIND_OFFSET;
        int count_of[] = new int[7];
        for(PieceBase p : player.getPiecesInHand()) {
            int x = 0, y = 0;
            if(p.getTypeOfPiece() == PieceBase.FU) {
                count_of[PieceBase.FU - 1]++;
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
        for(int count = 1; count < count_of[PieceBase.FU -1]; count++) {
            int width = (count_of[PieceBase.FU-1] < 8)?15:5;
            offShogi_Img.drawImage(
                    Pieces_Img[player_type][PieceBase.FU - 1],
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
        PieceBase sel = selected_piece_Base_in_hand;

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
                Point dest = new Point(x, y);
                if (gameBoard.isTherePieceAt(dest) || !gameBoard.wouldMoveNextLater(sel, dest)){
                    continue;
                }
                Rectangle capable_rect = new Rectangle(
                        OFFSET.x + GRID * dest.x,
                        OFFSET.y + GRID * dest.y,
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
        PieceBase sel = gameBoard.getPieceOf(selected_point.x, selected_point.y);
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
                    filename_of[PlayerBase.AHEAD] = String.format("%s/pieces/B%s.png",ASSET_IMG_PATH , pad(i+1) );
                    filename_of[PlayerBase.BEHIND] = String.format("%s/pieces/W%s.png",ASSET_IMG_PATH , pad(i+1) );
                } else {
                    filename_of[PlayerBase.AHEAD] = String.format("%s/pieces/B11_%s.png",ASSET_IMG_PATH , pad(i-10+1));
                    filename_of[PlayerBase.BEHIND] = String.format("%s/pieces/W11_%s.png",ASSET_IMG_PATH,  pad(i-10+1));
                }
                //TODO: delete print 4 debug
                System.out.println(filename_of[PlayerBase.BEHIND] + " " + filename_of[PlayerBase.AHEAD]);
                Pieces_Img[PlayerBase.AHEAD][i] = ImageIO.read(getClass().getResource(filename_of[PlayerBase.AHEAD]));
                Pieces_Img[PlayerBase.BEHIND][i] = ImageIO.read(getClass().getResource(filename_of[PlayerBase.BEHIND]));
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
}
