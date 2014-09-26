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
    /*
     * Методы взаимодействия с activity
     * */
    public void setPlayerMove(int deltaX, int deltaY){
        sPlayer.setFuturePos(deltaX, deltaY);
    }
    public int movePlayer(){
        return sPlayer.makeMove();
    }
    public void changeNearBoard(){
        sBoard.refresh();
        sList.makeMoveNear();
    }
    //Внимание! Этот метод должен вызываться activity только после вызова changeNearBoard
    public void changeFarBoard(){
        sList.makeMoveFar();
    }
    public void changeAllBoard(){
        sBoard.refresh();
        sList.makeMoveNear();
        sList.makeMoveFar();
    }
    public Iterable<Playable> getObjectsFromView(){
        return sList.getNearList();
    }
}
