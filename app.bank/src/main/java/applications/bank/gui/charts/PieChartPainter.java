package applications.bank.gui.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;

import application.base.app.gui.ColorProvider;
import application.charting.ChartPainter;

public class PieChartPainter extends ChartPainter {

	protected static PieChartPainter chartUI = new PieChartPainter();
	protected int originX, originY;
	protected int radius;

	private static double piby2 = Math.PI / 2;
	private static double twopi = Math.PI * 2;
	private static double d2r = Math.PI / 180;

	private static int xGap = 5;
	private static int inset = 40;

	@Override
	public int indexOfEntryAt(MouseEvent me) {
		int x = me.getX() - originX;
		int y = originY - me.getY();

		if (Math.sqrt(x * x + y * y) > radius) {
			return -1;
		}

		double percent = Math.atan2(Math.abs(y), Math.abs(x));

		if (x >= 0) {
			if (y <= 0) {
				percent = (piby2 - percent) + 3 * piby2;
			}
		} else {
			if (y >= 0) {
				percent = Math.PI - percent;
			} else {
				percent = Math.PI + percent;
			}
		}

		percent /= twopi;
		double t = 0.0;
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				if (t + values[i] > percent) {
					return i;
				}
				t += values[i];
			}
		}
		return -1;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Dimension size = c.getSize();
		originX = size.width / 2;
		originY = size.height / 2;

		int diameter = (originX < originY ? size.width - inset : size.height - inset);

		radius = (diameter / 2) + 1;

		int cornerX = originX - (diameter / 2);
		int cornerY = originY - (diameter / 2);

		int startAngle = 0;
		int arcAngle = 0;

		ColorProvider.startColor(0);
		for (int i = 0; i < values.length; i++) {
			arcAngle = (int) (i < values.length - 1 ? Math.round(values[i] * 360) : 360 - startAngle);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(ColorProvider.nextColor());
//			g2d.setStroke(new BasicStroke(25));
//			g2d.drawArc(cornerX + 12, cornerY + 12, diameter - 25, diameter - 25, startAngle, arcAngle);
			g2d.fillArc(cornerX, cornerY, diameter, diameter, startAngle, arcAngle);
			g.setColor(Color.black);
			g.drawOval(cornerX, cornerY, diameter, diameter);
			drawLabel(g, labels[i], startAngle + (arcAngle / 2));
			startAngle += arcAngle;
		}

	}

	public void drawLabel(Graphics g, String text, double angle) {
		g.setFont(textFont);
		g.setColor(textColor);
		double radians = angle * d2r;

		int x = (int) ((radius + xGap) * Math.cos(radians));
		int y = (int) ((radius + xGap) * Math.sin(radians));

		if (x < 0) {
			x -= SwingUtilities.computeStringWidth(g.getFontMetrics(), text);
		}
		if (y < 0) {
			y -= g.getFontMetrics().getHeight();
		}
		g.drawString(text, x + originX, originY - y);
	}

	public static ComponentUI createUI(JComponent c) {
		return chartUI;
	}

}
