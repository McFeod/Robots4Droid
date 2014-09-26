package com.github.mcfeod.board_objects;

import com.github.mcfeod.robots4droid.GamePlay;

public class Junk implements GameObject, Playable {
    int mX;
    int mY;
    public Junk(int x, int y){
        mX = x;
        mY = y;
    }
    public GameObject collideWith(GameObject other){
        other.crash();
        return this;
    }
    public void crash(){}
    public int getX(){
        return mX;
    }
    public int getY(){
        return mY;
    }
    public void makeMove(){
        GamePlay.sBoard.add(mX, mY, this);
    }

    // пакетные методы
    void moveCurrentTo(int x, int y) {
        mX = x;
        mY = y;
        GamePlay.sBoard.addCurrent(x, y, this);
    }
}
