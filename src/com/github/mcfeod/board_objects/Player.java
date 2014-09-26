package com.github.mcfeod.board_objects;
import com.github.mcfeod.robots4droid.*;

public class Player implements GameObject {
    private int mX;
    private int mY;
    private int mNextX;
    private int mNextY;

    public static int MOVE_MADE = 0;
    public static int ROBOT_FORWARD = 1;
    public static int UNPUSHABLE_JUNK = 2;
    public static int BOARD_BORDER = 3;

    private static int PUSH_SUCCEED = 0;
    private static int PUSH_REJECTED = 1;

    //статы итд
    //...

    public Player(){
        spawn();
    }
    public GameObject collideWith(GameObject other){
        Die();
        return other;
    }
    public void crash(){
        Die();
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
    public void setFuturePos(int deltaX, int deltaY) {
        mNextX+=deltaX;
        mNextY+=deltaY;
    }

    // пытается сделать ход в выбранную клетку, возвращает: удалось ли сделать ход
    public int makeMove(){
        if(!GamePlay.sBoard.exists(mNextX, mNextY)){
            return BOARD_BORDER;
        }
        GameObject inner = GamePlay.sBoard.getCurrent(mNextX, mNextY);
        if(inner instanceof Junk){
            if(push((Junk)inner) == PUSH_SUCCEED){
                setToSupposed();
                return 0;
            }else{
                return UNPUSHABLE_JUNK;
            }
        }else if(inner instanceof Robot){
            return ROBOT_FORWARD;
        }else{ // Empty
            setToSupposed();
            return 0;
        }
    }
    public void spawn(){
        //это заглушка
        mNextX = 0;
        mNextY = 0;
        setToSupposed();
    }
    // -----------------------------
    // приватные методы
    // ходит туда, куда и планировал
    private void setToSupposed(){
        mX = mNextX;
        mY = mNextY;
        GamePlay.sBoard.set(mX, mY, this);
    }
    // толкает от себя кучу, возвращает: удалось ли толкание
    private int push(Junk target){
        int pushPlaceX = target.getX()*2-mX;
        int pushPlaceY = target.getY()*2-mY;
        if(!GamePlay.sBoard.exists(pushPlaceX, pushPlaceY)){
            return PUSH_REJECTED;
        }
        GameObject inner = GamePlay.sBoard.getCurrent(pushPlaceX, pushPlaceY);
        if(inner instanceof Junk){
            return PUSH_REJECTED;
        }else{
            target.moveCurrentTo(pushPlaceX, pushPlaceY);
            return PUSH_SUCCEED;
        }
    }
    private void Die(){

    }
}