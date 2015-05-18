package soldiers;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import formation.Formation;
import formation.SimpleCircle;
import formation.SimpleRectangle;
import utils.Position;

public class General extends Agent
{
	public General()
	{
		p = new Position(2.5, 2.5);
		distance = 0.5;
		cols = 3;
		
		formation = new SimpleCircle(distance);
	}

	@Override
	protected void setup()
	{
		// Setup soldiers and seargeants structures
		soldiers = new HashSet<AID>();
		sergeants = new HashSet<AID>();

		// Add behaviours
		addBehaviour(new ListenBehaviour());
		addBehaviour(new InformSergeantsBehaviour(this));
	}

	@Override
	protected void takeDown()
	{
		System.out.println("General Killed");
	}

	private Formation getFormation(int n)
	{
		switch (n)
		{
		case 1:
			return new SimpleCircle(0.5);

		case 2:
			return new SimpleRectangle(3, 0.5);

		default:
			return null;

		}
	}

	private class ListenBehaviour extends CyclicBehaviour
	{

		@Override
		public void action()
		{
			ACLMessage msg = myAgent.receive();
			if (msg != null)
			{
				AID aid = msg.getSender();
				String content = msg.getContent();

				if (content.equals("killed"))
				{
					addBehaviour(new OrderBehaviour());
					soldiers.remove(aid);
				}
				else if (content.equals("greet"))
				{
					soldiers.add(aid);
					addBehaviour(new OrderBehaviour());
				}
				else if (content.equals("move"))
				{
					double x = Double.parseDouble(msg
							.getUserDefinedParameter("x"));
					double y = Double.parseDouble(msg
							.getUserDefinedParameter("y"));

					p = new Position(x, y);
				}
				else if (content.equals("formation"))
				{
					int n = Integer.parseInt(msg.getUserDefinedParameter("n"));
					
					formation = getFormation(n);
					addBehaviour(new OrderBehaviour());

				}
				else if (content.equals("distance"))
				{
					if (msg.getUserDefinedParameter("s").equals("+"))
					{
						distance += 0.1;
					}
					else if (msg.getUserDefinedParameter("s").equals("-"))
					{
						distance -= 0.1;
					}
					
					formation = new SimpleCircle(distance); //FIXME
					
					addBehaviour(new OrderBehaviour());

				}

			}
			else
			{
				block();
			}
		}

		private static final long serialVersionUID = -5948835369946779500L;
	}

	private class OrderBehaviour extends OneShotBehaviour
	{
		@Override
		public void action()
		{
			formation.computeFormation(soldiers, getAID());

			for (AID soldier : soldiers)
			{
				// get orders
				HashMap<AID, Position> orders = formation.getOrder(soldier);

				// send order
				sendOrder(soldier, orders);
			}

			sergeants = formation.getSeargeants();
		}

		private void sendOrder(AID id, HashMap<AID, Position> orders)
		{
			try
			{
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(id);
				msg.setContentObject(orders);
				send(msg);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}

		private static final long serialVersionUID = -7766888612949047856L;

	}

	private class InformSergeantsBehaviour extends TickerBehaviour
	{

		public InformSergeantsBehaviour(Agent a)
		{
			super(a, 100);
		}

		@Override
		protected void onTick()
		{
			for (AID sergeant : sergeants)
			{
				try
				{
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.addReceiver(sergeant);
					msg.setContentObject(p);
					send(msg);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

			}

		}

		private static final long serialVersionUID = 529207599738831087L;
	}

	Set<AID> soldiers;
	Set<AID> sergeants;

	double distance;
	int cols;
	Formation formation;

	private Position p;

	private static final long serialVersionUID = -3512892737673929107L;
}
