package soldiers;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import utils.Position;

public class Soldier extends Agent
{
	public Soldier()
	{
		double x = 5 * random.nextDouble();
		double y = 5 * random.nextDouble();

		p = new Position(x, y);
		target = p;
		constraints = new HashMap<AID, Position>();
		theta = new HashMap<AID, Position>();
	}

	@Override
	protected void setup()
	{
		// Send agent initial position
		sendPosition();

		// Add the behaviours
		addBehaviour(new GreetGeneralBehaviour());
		addBehaviour(new WaitForOrdersBehaviour());
		addBehaviour(new ReadNeighboursStateBehaviour());
		addBehaviour(new PublishStateBehaviour(this));
		addBehaviour(new GoInFormationBehavior(this));
		addBehaviour(new MoveToBehaviour(this));
	}

	@Override
	protected void takeDown()
	{
		// Move away the agent from the map
		p.x = -100;
		p.y = -100;
		sendPosition();

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("G", AID.ISLOCALNAME));
		msg.setContent("killed");
		send(msg);

		// Printout a dismissal message
		System.out.println("Agent " + getAID().getLocalName() + " Killed");
	}

	private class MoveToBehaviour extends TickerBehaviour
	{

		public MoveToBehaviour(Agent a)
		{
			super(a, dt);
		}

		@Override
		protected void onTick()
		{
			if (!p.equals(target))
			{
				Position delta = threshold(new Position(target.x - p.x,
						target.y - p.y));
				p.x += delta.x;
				p.y += delta.y;
				sendPosition();
			}

		}

		private Position threshold(Position delta)
		{
			double norm = Math.sqrt(delta.x * delta.x + delta.y * delta.y);
			if (norm > vMax)
			{
				double c = vMax / norm;
				Position deltaMax = new Position(c * delta.x, c * delta.y);
				return deltaMax;
			}
			else
			{
				return delta;
			}

		}

		private static final long dt = 10; // ms
		private static final double vMax = 2.0 * dt / 1000.0; // m/ms

		private static final long serialVersionUID = -4376132334575889969L;
	}

	private class GreetGeneralBehaviour extends Behaviour
	{
		@Override
		public void action()
		{
			try
			{
				Thread.sleep(500); // TODO change this??
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID("G", AID.ISLOCALNAME));
				msg.setContent("greet");
				send(msg);
			}
			catch (InterruptedException e)
			{
			}

		}

		@Override
		public boolean done()
		{
			return constraints != null;
		}

		private static final long serialVersionUID = -3849042796707872901L;
	}

	private class ReadNeighboursStateBehaviour extends CyclicBehaviour
	{
		@Override
		public void action()
		{

			MessageTemplate mt = MessageTemplate
					.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null)
			{
				try
				{
					AID sender = msg.getSender();

					if (constraints.containsKey(sender))
					{
						Position pos = (Position) msg.getContentObject();
						theta.put(sender, pos);
					}

				}
				catch (UnreadableException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				block();
			}

		}

		private static final long serialVersionUID = 5601639205068026580L;

	}

	private class WaitForOrdersBehaviour extends CyclicBehaviour
	{
		@SuppressWarnings("unchecked")
		@Override
		public void action()
		{
			try
			{
				MessageTemplate mt = MessageTemplate
						.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null)
				{
					constraints = (Map<AID, Position>) msg.getContentObject();
					theta.clear();
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

		// FIXME Debug method
		/*
		 * private void printOrders(Map<AID, Position> orders) { String
		 * orderString = getAID().getLocalName() + " recieved: ";
		 * 
		 * for (Entry<AID, Position> entry : orders.entrySet()) { String name =
		 * entry.getKey().getLocalName(); String position =
		 * entry.getValue().toString(); orderString = orderString + name + " " +
		 * position + " "; }
		 * 
		 * System.out.println(orderString); }
		 */

		private static final long serialVersionUID = -5269595491691804654L;

	}

	private class PublishStateBehaviour extends TickerBehaviour
	{

		public PublishStateBehaviour(Agent a)
		{
			super(a, 30);
		}

		@Override
		protected void onTick()
		{
			for (AID neighbour : constraints.keySet())
			{
				try
				{
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.addReceiver(neighbour);
					msg.setContentObject(p);
					send(msg);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}

		private static final long serialVersionUID = 2468710588448548066L;

	}

	private class GoInFormationBehavior extends TickerBehaviour
	{

		public GoInFormationBehavior(Agent a)
		{
			super(a, 10);
		}

		@Override
		protected void onTick()
		{
			if (constraints.size() > 0 && theta.size() == constraints.size())
			{
				Position nextPos = computeNextPosition(theta);
				if (!atConvergence(nextPos))
					target = nextPos;
			}
		}

		private Position computeNextPosition(Map<AID, Position> theta)
		{
			double alpha = 1.0 / constraints.size();

			Position deltaPos = new Position(0, 0);

			for (Entry<AID, Position> entry : theta.entrySet())
			{
				AID id = entry.getKey();
				Position pj = entry.getValue();
				Position delta = constraints.get(id);

				deltaPos.x += alpha * (pj.x - p.x - delta.x);
				deltaPos.y += alpha * (pj.y - p.y - delta.y);
			}

			Position nextPos = new Position(0, 0);
			nextPos.x = p.x + deltaPos.x;
			nextPos.y = p.y + deltaPos.y;

			return nextPos;
		}

		private boolean atConvergence(Position nextPos)
		{
			double threshold = 0.001;
			return Math.abs(p.x - nextPos.x) <= threshold
					&& Math.abs(p.y - nextPos.y) <= threshold;
		}

		private static final long serialVersionUID = 7592000075808133319L;

	}

	private void sendPosition()
	{
		try
		{
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID("map", AID.ISLOCALNAME));
			msg.setContentObject(p);
			send(msg);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private Map<AID, Position> theta;
	private Map<AID, Position> constraints;
	private Position p;
	private Position target;

	private static Random random = new Random();
	private static final long serialVersionUID = 8339256092734037514L;

}
