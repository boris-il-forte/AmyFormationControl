package formation;

import graphs.RectangularGraph;
import graphs.RectangularGraph.Direction;
import jade.core.AID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.Position;

public class SimpleRectangle extends Formation
{
	public SimpleRectangle(int cols, double d)
	{
		super();
		this.cols = cols;
		
		offsets = new HashMap<Direction, Position>();
		offsets.put(Direction.up, new Position(0, -d));
		offsets.put(Direction.down, new Position(0, +d));
		offsets.put(Direction.left, new Position(-d, 0));
		offsets.put(Direction.right, new Position(+d, 0));
	}
	

	@Override
	public void computeFormation(Set<AID> agents, AID generalId)
	{
		int rows = agents.size() / cols;
		
		if(agents.size() % cols > 0)
			rows++;
		
		RectangularGraph graph = new RectangularGraph(rows, cols);
		
		for(AID agent : agents)
		{
			graph.addAgent(agent);
		}
		
		for(int i = 0; i < agents.size(); i++)
		{
			AID id = graph.get(i);
			HashMap<AID, Position> orders = getNeighbours(graph, i);
			
			if(i == 0)
			{
				orders.put(generalId, new Position(0, 0)); //FIXME centered
				sergeants = new HashSet<AID>();
				sergeants.add(id);
			}
			
			ordersMap.put(id, orders);
		}
		
	}
	
	private HashMap<AID, Position> getNeighbours(RectangularGraph graph, int i)
	{
		HashMap<AID, Position> neighbours = new HashMap<AID, Position>();

		for (Direction dir : Direction.values())
		{
			AID id = graph.getNeighbour(i, dir);
			if (id != null)
				neighbours.put(id, offsets.get(dir));
		}
		
		return neighbours;

	}
	
	
	private Map<Direction, Position> offsets;
	private final int cols;

}
