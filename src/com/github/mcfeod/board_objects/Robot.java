package com.github.mcfeod.board_objects;

import com.github.mcfeod.robots4droid.GamePlay;

public class Robot extends Playable{
    protected int mSpeed;
    protected int mVisualId;
    protected int mWeight;

    /*Здесь либо позиция во встроенном бибилотечном списке, либо Robot next, либо ещё что-то.
     * */
    public Robot(int speed, int visual, int weight){
        mSpeed = speed;
        mVisualId = visual;
        mWeight = weight;
        spawn();
    }
    @Override
    public GameObject collideWith(GameObject other){
        //обработка столкновения между роботами, остальное на совести других классов
        if(other instanceof Robot){
            if(this.mWeight > ((Robot) other).mWeight){
                other.crash();
                return this;
            }else if(this.mWeight < ((Robot) other).mWeight){
                this.crash();
                return other;
            }else{
                this.crash();
                other.crash();
                return new Junk();
            }
        }else{
            return other.collideWith(this);
        }
    }
    @Override
    public void crash(){
        GamePlay.sList.deletePlayable(this);
    }
    @Override
    public int getX(){
        return mX;
    }
    @Override
    public int getY(){
        return mY;
    }
    @Override
    public boolean isFast(){
        return mSpeed>1;
    }

    //hopefully, GamePlay.sBoard.exist() can be omitted
    @Override
    public void makeMove(){
        int playerX = GamePlay.sPlayer.getX();
        int playerY = GamePlay.sPlayer.getY();
        if (playerX < mX)
            mX--;
        else if (playerX > mX)
            mX++;
        if (playerY < mY)
            mY--;
        else if (playerY > mY)
            mY++;
        GamePlay.sBoard.add(mX, mY, this);
    }
    //...
}
