package com.github.mcfeod.robots4droid;
// test diff
public class EnemyRobot {
	protected int mSpeed;
	protected int mVisualId;
	protected int mXPos;
	protected int mYPos;
	
	protected void findPlace(int x, int y){
	/**
	 * Поиск свободного места на карте для респауна
	 * Записть координат в x и y
	 */
		//TODO
	}
	
	protected int checkPosition(int x, int y){
	/**
	 * Проверка на столкновения с игроком и другими роботами
	 * 
	 */
		//TODO
		return 0;
	}
	
	public EnemyRobot(int speed, int visual){
		mSpeed = speed;
		mVisualId = visual;
		findPlace(mXPos, mYPos);
		//TODO обновить экран
	}
	
	public void moveToPlayer(int x, int y){
	/**
	 * Один ход робота
	 */
		for(int i=0;i<mSpeed; ++i){
			if (x < mXPos)
				mXPos--;
			else if (x > mXPos)
				mXPos++;
			if (y < mYPos)
				mYPos--;
			else if (y > mYPos)
				mYPos++;
			//TODO switch-case by result of checkPosition(mXPos, mYPos)
			//TODO обновить экран
		}
	}

}