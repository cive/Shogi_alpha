Shogi_alpha [![Build Status](https://travis-ci.org/cive/Shogi_alpha.svg?branch=master)](https://travis-ci.org/cive/Shogi_alpha)
===========

#どんなプログラム?#

将棋で遊ぶためのプログラム(alpha版)です(未完成)．
最終的には，twitterで投稿したハッシュタグ付きのツイートを読み込んで，駒を動かす位置などを取得して，動かせる...ような大人数型のゲームを考えています．

##使い方##

とりあえず，将棋盤のコマを移動させるだけのプログラムは書きましたので，それだけで遊べるという方は，ぜひどうぞ(ぇ

```console:sample
mkdir out
cd src
javac -d ../out cive/shogi/AppMain.java
cd ../
mkdir out/resources
cp -r src/resources out/resources
cd out
java cive.shogi.AppMain
```
## Mavenで管理するようにしました
 - Maven version 3.2.1

```console:maven
mvn install
mvn compile
java -cp target/classes cive.shogi.AppMain
```

## お借りした素材を置いている方のリンク
 - [無料素材倶楽部 さま][1]
 - [100g98円 さま][2]

## プログラム内部の説明
### AppMain.java
 - TODOLISTがここに書かれています．進捗出したいです．

### GameBoardComponent.java
 - コンストラクタで，宣言しているのを見ていただければわかると思いますが，マウスがクリックされたときの動作として，selectPieceAt(Point p)が実行されます．
 - paintHogeHoge系は，描画系のメソッドです．いくつかに分割してあります．

### GameBoard.java
 - isBlack()がtrueの場合，先攻のターンです．

### Pieces package
 - Piecesパッケージには，将棋の駒のクラスがそれぞれ入っています．

### Players package
 - Ahead Playerは手前側のプレイヤー

[1]: http://sozai.7gates.net/docs/japanese-chess/
[2]: http://www.pixiv.net/member_illust.php?mode=medium&illust_id=25263895
