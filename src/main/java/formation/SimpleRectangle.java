package formation;

import jade.core.AID;

import java.util.HashSet;
import java.util.Set;

import utils.Position;

public class SimpleRectangle extends AbstractRectangleFormation
{
	public SimpleRectangle(int cols, double d)
	{
		super(cols, d);
	}
	

	@Override
	public void computeFormation(Set<AID> agents, AID generalId)
	{
		sergeants = new HashSet<AID>();
		createRectangle(agents, generalId, new Position(0,0));		
	}
	

}
