package com.github.mcfeod.robots4droid;

public class Player{
    private Point mPos; //координаты
    private int mScore;
    private int mEnergy;
    public boolean isAlive; 
    public boolean areSuicidesForbidden;

	public Player(){
		mPos = new Point();
        mEnergy = 0;
        mScore = 0;
        isAlive = true;
    }
	
	public Player(int x, int y, int energy, int score, boolean alive){
		mPos = new Point(x,y);
        mEnergy = energy;
        mScore = score;
        isAlive = alive;
    }

    public Point getPos(){
		return mPos;
	}

	public void setPos(int x, int y){
		mPos.x = x;
		mPos.y = y;
	}
	
	public void setPos(Point pos){
		if (pos != null){
			mPos.x = pos.x;
			mPos.y = pos.y;
		}
	}

	public int getScore() {
		return mScore;
	}

	public void chScore(int diff){
		mScore += diff;
	}

	public int getEnergy() {
		return mEnergy;
	}

	public void chEnergy(int diff){
		mEnergy += diff;
	}

}