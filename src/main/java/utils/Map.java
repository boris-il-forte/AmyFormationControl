package utils;

import jade.core.AID;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;

import environment.MapAgent;

public class Map extends JFrame
{
	private MapAgent mapAgent;

	public Map(MapAgent a)
	{
		super(a.getLocalName());

		mapAgent = a;

		// Make the agent terminate when the user closes
		// the GUI using the button on the upper right corner
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				mapAgent.doDelete();
			}
		});

		setContentPane(new DrawingSurface());
		setSize(500, 500);
		setResizable(false);
	}

	public void showGui()
	{
		super.setVisible(true);
	}

	private class DrawingSurface extends JPanel
	{
		public DrawingSurface()
		{
			setSize(500, 500);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			java.util.Map<AID, Position> positions = mapAgent.getPositions();

			for (Entry<AID, Position> position : positions.entrySet())
			{
				String name = position.getKey().getLocalName();
				Position p = position.getValue();

				int x = (int) (scale * p.x);
				int y = (int) (scale * p.y);
				int r = 20;

				g.drawOval(x - r / 2, y - r / 2, r, r);
				printSimpleString(name, x, y, g);
			}

		}

		private void printSimpleString(String s, int x, int y, Graphics g)
		{
			int stringLen = (int) g.getFontMetrics().getStringBounds(s, g)
					.getWidth();
			int stringHeight = (int) g.getFontMetrics().getStringBounds(s, g)
					.getHeight();

			g.drawString(s, x - stringLen / 2, y + stringHeight / 2);
		}

		private static final int scale = 100;

		private static final long serialVersionUID = 3692117294885935974L;
	}

	private static final long serialVersionUID = 2465455386306850386L;
}