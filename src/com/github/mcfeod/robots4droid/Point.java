package com.github.mcfeod.robots4droid;

public class Point {
	public int x, y; //��� ��������� � pivat'��, ��� �� ���
	
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Point(Point old){
		this.x = old.x;
		this.y = old.y;
	}
		
	//�������� �� �������������� �������� ����
	public boolean isOnBoard(int sizeX, int sizeY){
		if ((x<0)||(y<0)||(x>=sizeX)||(y>=sizeY))
			return false;
		return true;		
	}
	
	//�������� �� �������������� ������� �������
	public boolean isVisible(Point topLeft, Point bottomRight){
		if ( (this.x<topLeft.x)||(this.y>topLeft.y)
			||(this.x>bottomRight.x)||(this.y<bottomRight.y) )	
			return false;
		return true;
	}

}