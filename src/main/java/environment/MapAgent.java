package environment;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.HashMap;

import utils.Map;
import utils.Position;

public class MapAgent extends Agent
{
	public java.util.Map<AID, Position> getPositions()
	{
		return positions;
	}

	protected void setup()
	{
		// Create the hasmap for saving positions
		positions = new HashMap<AID, Position>();

		// Create and show the GUI
		map = new Map(this);
		map.showGui();
		map.repaint();

		// Add behaviours to handle the map
		addBehaviour(new UpdateMapBehaviour(this));
		addBehaviour(new ReadStateBehaviour());
	}

	@Override
	protected void takeDown()
	{
		map.dispose();
		// Printout a dismissal message
		System.out.println("Map terminating.");
	}

	private class UpdateMapBehaviour extends TickerBehaviour
	{
		public UpdateMapBehaviour(Agent a)
		{
			super(a, 5);
		}

		@Override
		protected void onTick()
		{
			map.repaint();
		}

		private static final long serialVersionUID = -1572474012070874783L;
	}

	private class ReadStateBehaviour extends CyclicBehaviour
	{
		@Override
		public void action()
		{
			try
			{
				ACLMessage msg = myAgent.receive();
				if (msg != null)
				{
					AID id = msg.getSender();
					Position p = (Position) msg.getContentObject();

					positions.put(id, p);
				}
				else
				{
					block();
				}
			}
			catch (UnreadableException e)
			{
				e.printStackTrace();
			}

		}

		private static final long serialVersionUID = -5269595491691804654L;

	}

	public void setGeneralPosition(Position p)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("G", AID.ISLOCALNAME));
		msg.setContent("move");
		msg.addUserDefinedParameter("x", "" + p.x);
		msg.addUserDefinedParameter("y", "" + p.y);
		send(msg);
	}

	private Map map;
	private java.util.Map<AID, Position> positions;
	private static final long serialVersionUID = -1684139020716145598L;

}
