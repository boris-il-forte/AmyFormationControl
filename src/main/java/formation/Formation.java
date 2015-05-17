package formation;

import jade.core.AID;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utils.Position;

abstract public class Formation
{
	public Formation()
	{
		ordersMap = new HashMap<AID, HashMap<AID, Position>>();
	}
	
	public abstract void computeFormation(Set<AID> agents, AID generalId);
	
	public HashMap<AID, Position> getOrder(AID soldier)
	{
		return ordersMap.get(soldier);
	}
	
	public Set<AID> getSeargeants()
	{
		return sergeants;
	}
	
	
	protected Map<AID, HashMap<AID, Position>> ordersMap;
	protected Set<AID> sergeants;
}
