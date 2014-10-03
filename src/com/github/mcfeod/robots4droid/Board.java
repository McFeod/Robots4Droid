package com.github.mcfeod.robots4droid;

import java.util.Random;

public class Board{
	
	private int mLengthX, mLengthY;
	public Object mBoard[][];
	
	public Board(int lengthX, int lengthY){
		mLengthX = lengthX;
		mLengthY = lengthY;
		mBoard = new Object[mLengthX][mLengthY];	
		Clear();
	}
	
	public void Clear(){
		for (int i=0; i<mLengthX; i++)
			for (int j=0; j<mLengthY; j++)
				mBoard[i][j] = null;
	}
	
	public void CreateObject(Point pos, int kind){
		if (pos != null)
			if (pos.isOnBoard(mLengthX, mLengthY))
				mBoard[pos.x][pos.y] = new Object(kind);
	}
	
	public void CreateObject(int x, int y, int kind){
		if (Point.isOnBoard(x, y, mLengthX, mLengthY))
			mBoard[x][y] = new Object(kind);
	}
	
	public void DeleteObject(Point pos){
		if (pos != null)
			if (pos.isOnBoard(mLengthX, mLengthY))
				mBoard[pos.x][pos.y] = null;
	}
	
	public void MoveObject(int oldX, int oldY, int newX, int newY){
		if (mBoard[oldX][oldY] != null){
			int powerNew = 0;
			if (mBoard[newX][newY] != null)
				powerNew = mBoard[newX][newY].GetPower();
			mBoard[newX][newY] = mBoard[oldX][oldY];
			if (powerNew > 0){
				mBoard[newX][newY].ChPower(powerNew);
				if (mBoard[newX][newY].GetPower() >= 4){
					mBoard[newX][newY].SetKind(Object.JUNK);
				}
			}
			mBoard[oldX][oldY] = null;
		}
	}
	
	public void MoveObject(Point posOld, Point posNew){
		if ((posOld.isOnBoard(mLengthX, mLengthY)) &&
		 (posNew.isOnBoard(mLengthX, mLengthY)))
			if (mBoard[posOld.x][posOld.y] != null){
				int powerNew = 0;
				if (mBoard[posNew.x][posNew.y] != null)
					powerNew = mBoard[posNew.x][posNew.y].GetPower();
				mBoard[posNew.x][posNew.y] = mBoard[posOld.x][posOld.y];
				if (powerNew > 0){
					mBoard[posNew.x][posNew.y].ChPower(powerNew);
					if (mBoard[posNew.x][posNew.y].GetPower() >= 4){
						mBoard[posNew.x][posNew.y].SetKind(Object.JUNK);
					}
				}		
				mBoard[posOld.x][posOld.y] = null;
			}
	}
	
	public Object GetObject(Point pos){
		if (pos != null)
			if (pos.isOnBoard(mLengthX, mLengthY))
				if (mBoard[pos.x][pos.y] != null)
					return mBoard[pos.x][pos.y];
				else
					return null;
			else			
				return null;
		else
			return null;
	}
	
	public Object GetObject(int x, int y){
		if (Point.isOnBoard(x, y, mLengthX, mLengthY))
			if (mBoard[x][y] != null)
				return mBoard[x][y];
			else
				return null;
		else 
			return null;
	}
	
	public void SetObject(int x, int y, Object obj){
		mBoard[x][y] = obj;
	}
	
	public void SetObject(Point pos, Object obj){
		if (pos != null)
			if (pos.isOnBoard(mLengthX, mLengthY))
				mBoard[pos.x][pos.y] = obj;
	}

	public Point RandomFindFreePos(){
		Random rand = new Random(System.currentTimeMillis());
		int x, y;
		for (int i=0; i<mLengthX*mLengthY; i++){
			x = rand.nextInt(mLengthX);
			y = rand.nextInt(mLengthY);
			if (mBoard[x][y] == null)
				return new Point(x,y);
		}
		return null;
	}
	
	public int GetObjectCount(byte kind){
		int count=0;
		for (int i=0; i<mLengthX; i++)
			for (int j=0; j<mLengthY; j++)
				if (mBoard[i][j] != null)
					if (mBoard[i][j].GetKind() == kind)
						count ++;
		return count;		
	}

}