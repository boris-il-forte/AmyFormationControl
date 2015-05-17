package formation;

import jade.core.AID;

import java.util.HashMap;

import utils.Position;

public class SimpleCircle extends Formation
{
	public SimpleCircle(double d)
	{
		super();
		this.d = d;
	}
	
	@Override
	public void computeFormation(java.util.Set<AID> agents, AID generalId) 
	{
		double deltaAngle = 2 * Math.PI / agents.size();
		double angle = 0;
		
		for (AID soldier : agents)
		{
			Position deltaOpt = new Position(0, 0);
			deltaOpt.x = d * Math.cos(angle);
			deltaOpt.y = d * Math.sin(angle);
			
			angle += deltaAngle;
			
			HashMap<AID, Position> orders = new HashMap<AID, Position>();
			orders.put(generalId, deltaOpt);
			
			ordersMap.put(soldier, orders);
		}
		
		sergeants = agents;
	};
		
	private final double d;

}
