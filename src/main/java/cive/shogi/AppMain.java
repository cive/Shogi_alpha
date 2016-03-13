package cive.shogi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * http://www.coderanch.com/t/341360/GUI/java/JMenu-JToolBar#
 * http://www.ne.jp/asahi/hishidama/home/tech/java/swing/JMenuBar.html
 * TODO LIST
 * TODO: 持ち駒の処理．
 * TODO: HIGHLIGHTさせる．
 * TODO: Click処理．→
 * TODO: 成るとか，成らないとか．
 * TODO: 王手したらエフェクト．
 * TODO: 手詰まりであれば投了エフェクト．
 * TODO: Twitter連携処理．
 * TODO: and more...
 */

public class AppMain extends JFrame implements ActionListener {

    private ShogiBoardComponent pane;
    Timer timer;
    public static void main(String[] args) {
        AppMain frame = new AppMain();
        frame.setVisible(true);
    }

    public AppMain() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        setJMenuBar(initMenuBar());
        pane = new ShogiBoardComponent();
        add(pane, BorderLayout.CENTER);
        pane.setSizeOfBoard();
        pack();
        setResizable(false);
        timer = new Timer(10, this);
        timer.start();
    }

    private JMenuBar initMenuBar() {
        // File Menu
        JMenu file = new JMenu("ファイル(F)");
        file.setMnemonic(KeyEvent.VK_F);

        // View Menu
        JMenu view = new JMenu("表示(V)");
        view.setMnemonic(KeyEvent.VK_V);
        JMenuItem menuResize_window = new JMenuItem();
        menuResize_window.setText("元の大きさに戻す(R)");
        menuResize_window.setMnemonic(KeyEvent.VK_R);
        menuResize_window.addActionListener(e -> {
            pack();
        });
        view.add(menuResize_window);

        // View Menu
        JMenu tool = new JMenu("ツール(T)");
        view.setMnemonic(KeyEvent.VK_T);
        JMenuItem menuInit = new JMenuItem();
        menuInit.setText("最初から(I)");
        menuInit.setMnemonic(KeyEvent.VK_I);
        menuInit.addActionListener(e -> {
            pane.getGameBoard().initGame();
            pane.repaint();
        });
        tool.add(menuInit);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(file);
        menuBar.add(view);
        menuBar.add(tool);
        return menuBar;
    }
    public void actionPerformed(ActionEvent e){
        this.setTitle("twitter将棋(alpha ver.) : " + (pane.getGameBoard().isAheadsTurn()?"先手":"後手") + "の手番です．");
    }
}
