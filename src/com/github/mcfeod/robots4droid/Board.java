package com.github.mcfeod.robots4droid;

import java.util.Random;

public class Board{

	public static final byte EMPTY = 0;
	public static final byte JUNK = 1;
	public static final byte ROBOT = 2;
	public static final byte FASTROBOT = 3;

	private int mWidth, mHeight;
	private byte mBoard[][];
	Random rand;

	public Board(int lengthX, int lengthY){
		mWidth = lengthX;
		mHeight = lengthY;
		mBoard = new byte[mWidth][mHeight];
		rand = new Random(System.currentTimeMillis());
		Clear();
	}

	public void Clear(){
		for (int i=0; i<mWidth; i++)
			for (int j=0; j<mHeight; j++)
				mBoard[i][j] = EMPTY;
	}

	/*Перемещение робота. При столкновении образуется куча*/
	public void MoveObject(int oldX, int oldY, int newX, int newY){
		if ((!isOnBoard(oldX, oldY)) || (!isOnBoard(newX, newY)))
			return;
		if (mBoard[oldX][oldY] != EMPTY){
			if (mBoard[newX][newY] != EMPTY)
				mBoard[newX][newY] = JUNK;
			else
				mBoard[newX][newY] = mBoard[oldX][oldY];
			mBoard[oldX][oldY] = EMPTY;
		}
	}

	/*Перемещение робота. При столкновении образуется куча*/
	public void MoveObject(Point oldPos, Point newPos){
		if ((oldPos == null) || (newPos == null))
			return;
		if ((!isOnBoard(oldPos)) || (!isOnBoard(newPos)))
			return;
		if (mBoard[oldPos.x][oldPos.y] != EMPTY){
			if (mBoard[newPos.x][newPos.y] != EMPTY)
				mBoard[newPos.x][newPos.y] = JUNK;
			else
				mBoard[newPos.x][newPos.y] = mBoard[oldPos.x][oldPos.y];
			mBoard[oldPos.x][oldPos.y] = EMPTY;
		}
	}

	/*Возвращает тип объекта, находящегося в координатах (x, y)*/
	public byte GetKind(int x, int y){
		if (isOnBoard(x, y))
			return mBoard[x][y];
		return EMPTY;
	}

	/*Изменяет тип объекта, находящегося в координатах (x, y), на kind*/
	public void SetKind(int x, int y, byte kind){
		if (isOnBoard(x, y))
			mBoard[x][y] = kind;
	}

	/*Изменяет тип объекта, находящегося в координатах (pos.x, pos.y), на kind*/
	public void SetKind(Point pos, byte kind){
		if (pos != null)
			if (isOnBoard(pos))
				mBoard[pos.x][pos.y] = kind;
	}

	/*Ищет и сохраняет в freePos координаты случайной пустой клетки*/
	public boolean RandomFindFreePos(Point freePos){
		if (freePos == null)
			return false;		
		int x, y;
		for (int i=0; i<mWidth*mHeight; i++){
			x = rand.nextInt(mWidth);
			y = rand.nextInt(mHeight);
			if (mBoard[x][y] == EMPTY){
				freePos.x = x;
				freePos.y = y;
				return true;
			}
		}
		for (int i=0; i<mWidth; i++)
			for (int j=0; j<mHeight; j++)
				if (mBoard[i][j] == EMPTY){
					freePos.x = i;
					freePos.y = j;
					return true;
				}
		return false;
	}

	/*Возвращает количество объектов с типом kind*/
	public int GetObjectCount(byte kind){
		int count=0;
		for (int i=0; i<mWidth; i++)
			for (int j=0; j<mHeight; j++)
				if (mBoard[i][j] == kind)
					count ++;
		return count;
	}

	/*Проверяет, принадлежит ли точка p полю*/
	public boolean isOnBoard(Point p){
		if (p != null)
			if ((p.x>=0) && (p.y>=0) && (p.x<mWidth) && (p.y<mHeight))
				return true;
		return false;
	}

	/*Проверяет, принадлежит ли точка с координатами (x, y) полю*/
	public boolean isOnBoard(int x, int y){
		if ((x>=0) && (y>=0) && (x<mWidth) && (y<mHeight))
			return true;
		return false;
	}

	/*Возвращает true, если в точке с координатами (x, y) находится любой робот*/
	public boolean isEnemy(int x, int y){
		if (isOnBoard(x, y))
			if ((mBoard[x][y] == ROBOT) || (mBoard[x][y] == FASTROBOT))
				return true;
		return false;
    }

	/*Возвращает true, если в точке с координатами (x, y) находится простой робот*/
	public boolean isRobot(int x, int y){
		if (isOnBoard(x, y))
			if (mBoard[x][y] == ROBOT)
				return true;
		return false;
    }

	/*Возвращает true, если в точке с координатами (x, y) находится быстрый робот*/
	public boolean isFastRobot(int x, int y){
		if (isOnBoard(x, y))
			if (mBoard[x][y] == FASTROBOT)
				return true;
		return false;
    }

	/*Возвращает true, если точке с координатами (x, y) пустая*/
	public boolean isEmpty(int x, int y){
		if (isOnBoard(x, y))
			if (mBoard[x][y] == EMPTY)
				return true;
		return false;
    }

	/*Возвращает true, если в точке с координатами (x, y) находится мусор*/
	public boolean isJunk(int x, int y){
		if (isOnBoard(x, y))
			if (mBoard[x][y] == JUNK)
				return true;
		return false;
    }

}