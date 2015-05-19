package formation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jade.core.AID;

public class AgentFilter
{
	public AgentFilter(Set<AID> agents)
	{
		categorized = new ArrayList<Set<AID>>();
		for(int i = 0; i < categories.size() + 1; i++)
		{
			categorized.add(new HashSet<AID>());
		}
		
		
		for(AID id : agents)
		{
			String localName = id.getLocalName();
			char category = localName.charAt(0);
			
			Integer index = categories.get(category);
			
			if(index == null)
				index = 0;
			
			Set<AID> set = categorized.get(index);
			
			set.add(id);
		}
	}
	
	public Set<AID> getCategory(int index)
	{
		return categorized.get(index);
	}
	
	private static Map<Character, Integer> createCategories()
	{
		Map<Character, Integer> categories = new HashMap<Character, Integer>();
		categories.put('I', 1);
		categories.put('A', 2);
		categories.put('C', 3);
		return categories;
	}
	
	public static final int getCateroriesN()
	{
		return categories.size() + 1;
	}
	
	
	private static final Map<Character, Integer> categories = createCategories();
	private List<Set<AID>> categorized;
}
