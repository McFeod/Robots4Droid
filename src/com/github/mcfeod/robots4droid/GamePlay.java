package com.github.mcfeod.robots4droid;
import com.github.mcfeod.board_objects.*;

public class GamePlay {
    public static Board sBoard;
    public static View sView;
    public static Player sPlayer;
    public static List sList;

    public GamePlay(int boardHeight, int boardWidth){
        sBoard = new Board(boardHeight, boardWidth);
        sPlayer = new Player();
        sView = new View(sPlayer);
        sList = new List();
    }

    public void setPlayerMove(int deltaX, int deltaY){
        sPlayer.setFuturePos(deltaX, deltaY);
    }

    //методы, которые делают ход или возвращают код, по которому ход игрока невозможен



    //взаимодействие с activity
    public boolean getObjectsFromView(){
        return sList.getNearList();
    }
    //...
}
