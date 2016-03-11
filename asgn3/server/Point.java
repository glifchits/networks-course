package a3;

import java.awt.Color;

public class Point {
	private int x;
	private int y;
	private Color color;

	public Point(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public int hashCode(){
		int hash;
		hash = this.x *1000 + y;
		return hash;
	}

	public String toString(){
		return this.x + " " + this.y;
	}
	public boolean equals(Point rhs){
		boolean same = true;
		if ((rhs.x != this.x) ||(rhs.y != this.y)){
			same = false;
		}
		return same;
	}

	public void setColor(Color color){
		this.color = color;
	}

	public Color getColor(){
		return this.color;
	}

	public int getX(){
		return this.x;
	}

	public int getY(){
		return this.y;
	}
}
