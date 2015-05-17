package graphs;

import jade.core.AID;

import java.util.ArrayList;
import java.util.List;

public class RectangularGraph
{
	public RectangularGraph(int rows, int cols)
	{
		this.cols = cols;

		graph = new ArrayList<List<AID>>();

		for (int i = 0; i < rows; i++)
		{
			graph.add(new ArrayList<AID>());
		}

		currentRow = 0;
	}

	public void addAgent(AID id)
	{
		graph.get(currentRow).add(id);

		if (graph.get(currentRow).size() == cols)
			currentRow++;
	}

	public AID get(int i)
	{
		int row = i / cols;
		int col = i % cols;

		return graph.get(row).get(col);
	}

	public enum Direction
	{
		up, down, left, right
	}

	public AID getNeighbour(int i, Direction dir)
	{		
		int row = i / cols;
		int col = i % cols;

		switch (dir)
		{

		case up:
			row--;
			break;

		case down:
			row++;
			break;

		case left:
			col--;
			break;

		case right:
			col++;
			break;

		}

		return get(row, col);

	}

	private AID get(int row, int col)
	{
		try
		{
			return graph.get(row).get(col);
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}

	}

	private int currentRow;
	private int cols;
	private List<List<AID>> graph;
}
