package com.github.mcfeod.robots4droid;
import com.github.mcfeod.board_objects.*;

public class List {
    private playableList mNearList;
    //private playableList mSpeedNearList;
    private playableList mFarList;
    //private playableList mSpeedFarList;

    public List(){
        mNearList        = new playableList();
        mFarList         = new playableList();
        //mSpeedNearList = new playableList();
        //mSpeedFarList  = new playableList();
    }
    // sorry for my english
    public Iterable<Playable> getNearList(){
        return mNearList;
    }
    public Iterable<Playable> getFarList(){
        return mFarList;
    }

    public void makeMoveNear(){
        //дублирование кода с методом makeMoveFar
        if(mNearList.isEmpty()) return;
        for(Playable obj : mNearList){
            obj.makeMove();
            if(!GamePlay.sView.isIn(obj)){
                mNearList.remove(obj);
                mFarList.add(obj);
            }
        }
    }
    public void makeMoveFar(){
        //дублирование кода с методом makeMoveNear
        if(mFarList.isEmpty()) return;
        for(Playable obj : mFarList){
            obj.makeMove();
            if(!GamePlay.sView.isIn(obj)){
                mFarList.remove(obj);
                mFarList.add(obj);
            }
        }
    }
    public void makeMoveAllOver(){
        makeMoveNear();
        makeMoveFar();
    }
    public void addRobot(int speed, int visual, int weight){
        Robot rob = new Robot(speed, visual, weight);
        if(GamePlay.sView.isIn(rob)){
            mNearList.add(rob);
        }else{
            mFarList.add(rob);
        }
    }
    public void addJunk(){
        Junk junk = new Junk();
        if(GamePlay.sView.isIn(junk)){
            mNearList.add(junk);
        }else{
            mFarList.add(junk);
        }
    }
    public void deletePlayable(Playable target){
        if(GamePlay.sView.isIn(target)){
            mNearList.remove(target);
        }else{
            mFarList.remove(target);
        }
    }
}
