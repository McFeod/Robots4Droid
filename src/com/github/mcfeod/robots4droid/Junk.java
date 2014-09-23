package com.github.mcfeod.robots4droid;

public class Junk implements GameObject {
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
    // может ли куча быть сдвинута игроком с места
    public boolean canBeEjected(Player player) {
        // может, если новое место для кучи находится в пределах доски
        return World.isPlaceValid(mX*2 - player.getX(), mY*2 - player.getY());
    }
}
