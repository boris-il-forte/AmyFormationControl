package formation;

import jade.core.AID;

import java.util.HashSet;
import java.util.Set;

import utils.Position;

public class ComplexRectangle extends AbstractRectangleFormation
{

	public ComplexRectangle(int cols, double d)
	{
		super(cols, d);
	}

	@Override
	public void computeFormation(Set<AID> agents, AID generalId)
	{
		sergeants = new HashSet<AID>();
		
		AgentFilter filter = new AgentFilter(agents);
		
		double offset = 0.0;
		
		for(int i = 0; i < AgentFilter.getCateroriesN(); i++)
		{
			Set<AID> agentSubGroup = filter.getCategory(i);
			createRectangle(agentSubGroup, generalId, new Position(0, -offset));
			
			if(agentSubGroup.size() > 0)
			{
				int rows = agentSubGroup.size() / cols;
				if(agentSubGroup.size() % cols > 0)
					rows++;
				offset += rows * d;
			}
		}
		
	}

}
