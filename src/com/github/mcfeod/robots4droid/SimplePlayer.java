package com.github.mcfeod.robots4droid;

public class SimplePlayer{
    private Point mPos; //������ ����������
    private int mScore; 
    private int mEnergy;
    private int mLevel;
    public boolean isAlive;

	public SimplePlayer(int x, int y){
		mPos = new Point(x,y);
        mEnergy = 0;
        mScore = 0;
        isAlive = true;
    }
    
    public int getLevel() {
		return mLevel;
	}

	public void incLevel() {
		mEnergy+= mLevel*0.2+1;
		mScore+= mLevel*5;
		mLevel++;
	}
	
    public Point getPos() {
		return mPos;
	}  

	public void setPos(Point pos) {
		mPos = pos;
	}

	public int getScore() {
		return mScore;
	}
	
	public void incScore(int diff){
		mScore += diff;
	}

	public int getEnergy() {
		return mEnergy;
	}
	
	public void chEnergy(int diff){
		mEnergy += diff;
	}

}