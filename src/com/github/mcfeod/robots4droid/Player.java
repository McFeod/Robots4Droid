package com.github.mcfeod.robots4droid;

public class Player{
	private Point mPos; //координаты
	private long mScore;
	private int mEnergy;
	public boolean isAlive; 
	public boolean isWinner;
	public boolean areSuicidesForbidden;

	public Player(){
		mPos = new Point();
		this.reset();
	}

	public Player(int x, int y, int energy, long score, boolean alive, boolean winner){
		mPos = new Point(x,y);
		mEnergy = energy;
		mScore = score;
		isAlive = alive;
		isWinner = winner;
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

	public long getScore() {
		return mScore;
	}

	public void chScore(long diff){
		mScore += diff;
	}

	public int getEnergy() {
		return mEnergy;
	}

	public void chEnergy(int diff){
		mEnergy += diff;
	}

	public void reset(){
		mEnergy = 0;
		mScore = 0;
		isWinner = false;
		isAlive = true;
	}
}
