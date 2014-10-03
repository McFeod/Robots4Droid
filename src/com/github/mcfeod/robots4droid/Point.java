package com.github.mcfeod.robots4droid;

public class Point {
	public int x, y;
	
	public Point(){
		this.x = 0;
		this.y = 0;
	}
	
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Point(Point pos){
		if (pos != null){
			this.x = pos.x;
			this.y = pos.y;
		}
	}
	
	public Point CopyPoint(){
		return new Point(x, y);
	}
		
	//проверка на принадлежность игровому полю
	public boolean isOnBoard(int sizeX, int sizeY){
		if ((x<0)||(y<0)||(x>=sizeX)||(y>=sizeY))
			return false;
		return true;		
	}
	
	public static boolean isOnBoard(int x, int y, int sizeX, int sizeY){
		if ((x<0)||(y<0)||(x>=sizeX)||(y>=sizeY))
			return false;
		return true;
	}
	/*
	//проверка на принадлежность видимой области
	public boolean isVisible(Point topLeft, Point bottomRight){
		if ( (this.x<topLeft.x)||(this.y>topLeft.y)
			||(this.x>bottomRight.x)||(this.y<bottomRight.y) )	
			return false;
		return true;
	}
*/
}