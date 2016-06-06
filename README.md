Shogi_alpha [![Build Status](https://travis-ci.org/cive/Shogi_alpha.svg?branch=master)](https://travis-ci.org/cive/Shogi_alpha) [![codecov](https://codecov.io/gh/cive/Shogi_alpha/branch/master/graph/badge.svg)](https://codecov.io/gh/cive/Shogi_alpha)

===========

#どんなプログラム?#

将棋で遊ぶためのプログラム(alpha版)です(未完成)．
最終的には，twitterで投稿したハッシュタグ付きのツイートを読み込んで，駒を動かす位置などを取得して，動かせる...ような大人数型のゲームを考えています．
![sample](https://github.com/cive/Shogi_alpha/blob/master/sample.png)


##使い方##

- Debian系

```console:inst-deb
sudo apt-get update
sudo apt-get install maven
```

- RedHat系

```console:inst-red
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven
mvn --version
```

- Windows
 - http://weblabo.oscasierra.net/install-maven-32-windows/

### 実行ファイル生成と実行
 - Maven version 3.2.1

```console:maven
mvn package
java -jar ShogiAlpha-0.1-x64.jar
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
