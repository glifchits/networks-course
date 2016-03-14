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

	public Point(String f) throws PointException{
		String[] part = f.split(" ");
		if (part.length != 3){
			throw new PointException("Missing parameter on point: "+ f);
		}
		String[] colors = part[2].split(":");
		this.x = Integer.parseInt(part[0]);
		this.y = Integer.parseInt(part[1]);
		this.color = new Color( Integer.parseInt(colors[0]),
				  				Integer.parseInt(colors[1]),
				  				Integer.parseInt(colors[2]));
	}

	public int hashCode(){
		int hash;
		hash = this.x *1000 + y;
		System.out.println("Hashing: " + hash);
		return hash;
	}

	public String toString(){
		return this.x + " " + this.y;
	}

	public String format(){
		return (this.x + " " + this.y + " " + this.color.getRed()+":"+
				this.color.getGreen()+":"+ this.color.getBlue());
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
