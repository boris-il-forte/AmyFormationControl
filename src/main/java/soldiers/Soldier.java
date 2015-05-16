package soldiers;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.Random;

import utils.Position;


public class Soldier extends Agent
{
	public Soldier()
	{
		double x = 5 * random.nextDouble();
		double y = 5 * random.nextDouble();
		
		p = new Position(x, y);
	}
	
	protected void setup()
	{
		// Printout a welcome message
		System.out.println("Soldier " + getAID().getLocalName()
				+ " " + p.toString());
		sendPosition();
		addBehaviour(new MoveToBehaviour(new Position(0,0)));
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
				while(!p.equals(target))
				{
					Thread.sleep(dt);
					p.x += threshold(target.x - p.x);
					p.y += threshold(target.y - p.y);
					sendPosition();
					System.out.println(getAID().getLocalName() + " " + p.toString());
				}
			}
			catch (InterruptedException e)
			{

			}
		}
		
		private double threshold(double delta)
		{
			double v = vMax*dt/1000;
			return Math.max(Math.min(delta, v), -v);
		}
		
		private Position target;
		
		private static final long dt = 100; //ms
		private static final double vMax = 1; //m/s
		
		private static final long serialVersionUID = -4376132334575889969L;
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
	
	


	private Position p;
	
	private static Random random = new Random();
	
	private static final long serialVersionUID = 8339256092734037514L;

}
