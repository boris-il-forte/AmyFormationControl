package soldiers;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
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
		constraints = new HashMap<AID, Position>();
	}

	@Override
	protected void setup()
	{
		// Send agent initial position
		sendPosition();

		// Add the waitForOrders behaviour
		addBehaviour(new GreetGeneralBehaviour());
		addBehaviour(new WaitForOrdersBehaviour());
		addBehaviour(new GoInFormationBehavior(this));
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

	private class MoveToBehaviour extends OneShotBehaviour
	{
		public MoveToBehaviour(Position target)
		{
			this.target = target;
		}

		@Override
		public void action()
		{
			try
			{
				while (!p.equals(target))
				{
					Thread.sleep(dt);
					p.x += threshold(target.x - p.x);
					p.y += threshold(target.y - p.y);
					sendPosition();
				}
			}
			catch (InterruptedException e)
			{

			}
		}

		private double threshold(double delta)
		{
			double v = vMax * dt / 1000;
			return Math.max(Math.min(delta, v), -v);
		}

		private Position target;

		private static final long dt = 100; // ms
		private static final double vMax = 1; // m/s

		private static final long serialVersionUID = -4376132334575889969L;
	}

	private class GreetGeneralBehaviour extends Behaviour
	{
		@Override
		public void action()
		{
			try
			{
				Thread.sleep(500); // TODO change this
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

					// FIXME DEBUG
					System.out.print("agent " + getAID().getLocalName()
							+ " recieved order: ");
					System.out.println(constraints.values().iterator().next()
							.toString());
				}
				else
				{
					block();
				}
			}
			catch (UnreadableException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private static final long serialVersionUID = -5269595491691804654L;

	}

	private class GoInFormationBehavior extends TickerBehaviour
	{

		public GoInFormationBehavior(Agent a)
		{
			super(a, 100);
		}

		@Override
		protected void onTick()
		{
			if (constraints.size() > 0)
			{
				sendStateToNeighbours();
				Map<AID, Position> theta = getNeighboursState();
				Position nextPos = computeNextPosition(theta);
				if (!p.equals(nextPos))
					addBehaviour(new MoveToBehaviour(nextPos));
			}
		}

		private Position computeNextPosition(Map<AID, Position> theta)
		{
			double alpha = 1.0 / constraints.size();

			Position deltaPos = new Position(0, 0);

			// FIXME debug
			System.out.println("-- Agent " + getAID().getLocalName());
			System.out.println("p " + p.toString());
			System.out.println("alpha " + alpha);

			for (Entry<AID, Position> entry : theta.entrySet())
			{
				AID id = entry.getKey();
				Position pj = entry.getValue();
				Position delta = constraints.get(id);

				deltaPos.x += alpha * (pj.x - p.x - delta.x);
				deltaPos.y += alpha * (pj.y - p.y - delta.y);

				// FIXME debug
				System.out.println("delta " + delta.toString());
				System.out.println("deltaPos " + deltaPos.toString());
			}

			Position nextPos = new Position(0, 0);
			nextPos.x = p.x + deltaPos.x;
			nextPos.y = p.y + deltaPos.y;

			// FIXME Debug
			System.out.println("nextPos " + nextPos.toString());

			return nextPos;
		}

		private Map<AID, Position> getNeighboursState()
		{
			MessageTemplate mt = MessageTemplate
					.MatchPerformative(ACLMessage.INFORM);
			Map<AID, Position> theta = new HashMap<AID, Position>();

			while (theta.size() != constraints.size())
			{
				try
				{
					ACLMessage msg = myAgent.blockingReceive(mt);
					theta.put(msg.getSender(),
							(Position) msg.getContentObject());
				}
				catch (UnreadableException e)
				{
					e.printStackTrace();
				}
			}

			return theta;
		}

		private void sendStateToNeighbours()
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

	private Map<AID, Position> constraints;
	private Position p;

	private static Random random = new Random();
	private static final long serialVersionUID = 8339256092734037514L;

}
