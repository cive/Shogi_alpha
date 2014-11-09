Shogi_alpha
===========

#どんなプログラム?#

将棋で遊ぶためのプログラム(alpha版)です(未完成)．次にやる実装は，持ち駒をハイライトさせて，かつ置けるようにする事です．その後にでも，二歩の実装や，成る成らないの実装を行いたいと考えています．最終的には，twitterで投稿したハッシュタグ付きのツイートを読み込んで，駒を動かす位置などを取得して，動かせる...ような大人数型のゲームを考えています．

##使い方##

とりあえず，将棋盤のコマを移動させるだけのプログラムは書きましたので，それだけで遊べるという方は，ぜひどうぞ(ぇ


 - mkdir out
 - cd src
 - javac -d ../out cive/shogi/Main.java
 - cd ../
 - mkdir out/resources
 - cp -r src/resources out/resources
 - cd out
 - java cive.shogi.Main
 - バッチファイル書いてない()

### Mavenで管理するようにしました
 - Maven version 3.2.1
 - mvn install
 - mvn compile
 - java -cp target/classes cive.shogi.Main
 - 楽ちん。

###とりあえずテスト書けよといわれた気がする...###

###お借りした素材を置いている方のリンク###
 - [無料素材倶楽部 さま][1]
 - [100g98円 さま][2]

###プログラム内部の説明 ###
### Main.java ###
 - TODOLISTがここに書かれています．進捗出したいです．
 - その他，特に説明は不要かと思います．

### GameBoardComponent.java ###
 - コンストラクタで，宣言しているのを見ていただければわかると思いますが，マウスがクリックされたときの動作として，selectPieceAt(Point p)が実行されます．
 - paintHogeHoge系は，描画系のメソッドです．いくつかに分割してあります．

### GameBoard.java ###
 - isBlack()がtrueの場合，先攻のターンです．

### Pieces package ###
 - Piecesパッケージには，将棋の駒のクラスがそれぞれ入っています．

## 最後に
作ってる人が中間報告書で忙しいので代わりに作ってください（ぇ


[1]: http://sozai.7gates.net/docs/japanese-chess/
[2]: http://www.pixiv.net/member_illust.php?mode=medium&illust_id=25263895
