package com.github.mcfeod.board_objects;

import com.github.mcfeod.robots4droid.GamePlay;

public class Junk extends Playable {
    int mX;
    int mY;
    public Junk(){
        spawn();
    }
    @Override
    public GameObject collideWith(GameObject other){
        other.crash();
        return this;
    }
    @Override
    public void crash(){}
    @Override
    public int getX(){
        return mX;
    }
    @Override
    public int getY(){
        return mY;
    }
    @Override
    public boolean isFast() {
        return false;
    }
    @Override
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
