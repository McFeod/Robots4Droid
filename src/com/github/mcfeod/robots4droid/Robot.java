package com.github.mcfeod.robots4droid;

public class Robot implements GameObject {
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
        //исключить из списка роботов, уменьшить счётчик
    }
    //TODO договориться о коротких названиях
    public int getXPos(){
        return mXPos;
    }
    public int getYPos(){
        return mYPos;
    }

    //hopefully, World.isPlaceValid() can be omitted
    public void moveToPlayer(int playerX, int playerY){
        if (playerX < mXPos)
            mXPos--;
        else if (playerX > mXPos)
            mXPos++;
        if (playerY < mYPos)
            mYPos--;
        else if (playerY > mYPos)
            mYPos++;
    }
    //...
}
