package formation;

public class FormationManager
{
	public FormationManager()
	{
		cols = 3;
		distance = 0.5;
		formationN = 1;
	}
	
	public Formation getFormation(int formationN)
	{
		this.formationN = formationN;
		switch (formationN)
		{
		case 1:
			return new SimpleCircle(distance);

		case 2:
			return new SimpleRectangle(cols, distance);

		default:
			return null;

		}
		
	}
	
	public Formation changeDistance(boolean increment)
	{
		if(increment)
		{
			distance += 0.1;
		}
		else
		{
			distance -= 0.1;
		}
		
		distance = Math.max(distance, 0);
		
		return getFormation(formationN);
	}
	
	public Formation changeCols(boolean increment)
	{
		if(increment)
		{
			cols++;
		}
		else
		{
			cols--;
		}
		
		cols = Math.max(cols, 1);
		
		return getFormation(formationN);
	}
	
	private int cols;
	private double distance;
	private int formationN;
	
}
