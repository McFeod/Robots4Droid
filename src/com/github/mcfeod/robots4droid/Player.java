package com.github.mcfeod.robots4droid;

public class Player implements GameObject {
    int mX;
    int mY;
    int mNextX;
    int mNextY;

    //статы итд
    //...

    public Player(int x, int y){
        mX = x;
        mY = y;
    }
    public GameObject collideWith(GameObject other){
        Die();
        return other;
    }
    public void crash(){
        Die();
    }
    private void Die(){

    }

    public int getNextX(){
        return mNextX;
    }
    public int getNextY(){
        return mNextY;
    }
    public int getX(){
        return mX;
    }
    public int getY(){
        return mY;
    }

    public void setFuturePos(int x, int y){
        mNextX = x;
        mNextY = y;
    }
    public void makeMove(){
        mX = mNextX;
        mY = mNextY;
    }
    // ...
}