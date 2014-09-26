package com.github.mcfeod.board_objects;

import com.github.mcfeod.robots4droid.GamePlay;

public class Robot implements GameObject, Playable {
    protected int mSpeed;
    protected int mVisualId;
    protected int mXPos;
    protected int mYPos;
    protected int mWeight;

    /*Здесь либо позиция во встроенном бибилотечном списке, либо Robot next, либо ещё что-то.
     * */
    public Robot(int speed, int visual, int weight, int x, int y){
        mSpeed = speed;
        mVisualId = visual;
        mXPos = x;
        mYPos = y;
        mWeight = weight;
    }
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
                return new Junk(mXPos, mYPos);
            }
        }else{
            return other.collideWith(this);
        }
    }
    public void crash(){
        GamePlay.sList.deletePlayable(this);
    }
    public int getX(){
        return mXPos;
    }
    public int getY(){
        return mYPos;
    }

    //hopefully, GamePlay.sBoard.exist() can be omitted
    public void makeMove(){
        int playerX = GamePlay.sPlayer.getX();
        int playerY = GamePlay.sPlayer.getY();
        if (playerX < mXPos)
            mXPos--;
        else if (playerX > mXPos)
            mXPos++;
        if (playerY < mYPos)
            mYPos--;
        else if (playerY > mYPos)
            mYPos++;
        GamePlay.sBoard.add(mXPos, mYPos, this);
    }
    public void spawn(){
        // заглушка
        mXPos = 0;
        mYPos = 0;
        GamePlay.sBoard.add(mXPos, mYPos, this);
    }
    //...
}
