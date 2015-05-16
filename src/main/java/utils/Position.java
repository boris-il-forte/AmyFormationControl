package utils;

import java.io.Serializable;


public class Position implements Serializable
{
	public Position(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	@Override
	public boolean equals(Object obj) 
	{
		if(obj instanceof Position)
		{
			Position newPos = (Position) obj;
			
			return newPos.x == x && newPos.y == y;
		}
		
		return false;
	};

	@Override
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}

	public double x;
	public double y;
	
	private static final long serialVersionUID = 159552650322023031L;
}
