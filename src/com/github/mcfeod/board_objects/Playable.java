package com.github.mcfeod.board_objects;

public interface Playable {
    public int getX();
    public int getY();
    public void makeMove();
    //public void spawn(); переписать как абстрактный класс
}
