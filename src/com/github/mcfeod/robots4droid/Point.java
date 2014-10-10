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

}