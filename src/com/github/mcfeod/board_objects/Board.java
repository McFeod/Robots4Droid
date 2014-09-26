package com.github.mcfeod.board_objects;

public class Board {
    private int mWidth;
    private int mHeight;
    private GameObject[][] mCurrent;
    private GameObject[][] mFuture;

    public Board(int height, int width){
        mHeight = height;
        mWidth = width;
        mCurrent = new GameObject[mHeight][mWidth];
        mFuture = new GameObject[mHeight][mWidth];
        clearBoard(mCurrent);
        clearBoard(mFuture);
    }
    public boolean exists(int x, int y){
        return (x>0)&&(x<mHeight)&&(y>0)&&(y<mWidth);
    }
    public void refresh(){
        //вытесняет mFuture в mCurrent, очищает mFuture
    }
    public int getWidth(){
        return mWidth;
    }
    public int getHeight(){
        return mHeight;
    }

    //пакетные методы, доступные объектам интерфейса GameObject
    GameObject get(int x, int y){
        return mFuture[x][y];
    }
    GameObject getCurrent(int x, int y){
        return mCurrent[x][y];
    }

    //во избежание путаницы с координатами эти методы объекты могут применять только на себя
    void set(int x, int y, GameObject fill){
        mFuture[x][y] = fill;
    }
    void setCurrent(int x, int y, GameObject fill){
        mCurrent[x][y] = fill;
    }
    void add(int x, int y, GameObject addict){
        mFuture[x][y] = mFuture[x][y].collideWith(addict);
    }
    void addCurrent(int x, int y, GameObject addict){
        mCurrent[x][y] = mCurrent[x][y].collideWith(addict);
    }

    //приватные методы
    private void clearBoard(GameObject[][] arr){
        //заполняет всё поле ссылками на Empty. Должна быть встроенная функция.
    }
}
