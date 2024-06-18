
/**
 * ·½¿éÁ¬Ïß
 */

import java.awt.Color;
import java.awt.Label;
import java.util.ArrayList;

import javax.swing.JPanel;

class Draw extends JPanel {
	private static final long serialVersionUID = 1L;
	ArrayList<Segment> ss;
	Label[] ls;
	int x0, y0;
	timeToClear t;

	Draw(int x0, int y0, int ma) {
		setOpaque(false);
		this.x0 = x0;
		this.y0 = y0;
		ss = new ArrayList<>();
		ls = new Label[ma];
		for (int i = 0; i < ls.length; ++i) {
			ls[i] = new Label("");
			ls[i].setBackground(Color.BLACK);
			add(ls[i]);
			ls[i].setVisible(false);
		}
	}

	synchronized void paint() {
		int i = 0;
		for (Segment s : ss) {
			Label l = ls[i++];
			if (s.x1 == s.x2)
				l.setSize(4, 44);
			else
				l.setSize(44, 4);
			l.setLocation(Math.min(s.x1, s.x2), Math.min(s.y1, s.y2));
			l.setVisible(true);
		}
	}

	synchronized void add(int x1, int y1, int x2, int y2) {
		ss.add(new Segment(x0 + 40 * x1, y0 - 40 * y1, x0 + 40 * x2, y0 - 40 * y2));
	}

	synchronized void clear() {
		ss.clear();
		for (Label l : ls)
			l.setVisible(false);
	}

	void draw(Node n) {
		if (n == null)
			return;
		if (t != null && t.isAlive())
			t.interrupt();
		synchronized (this) {
			while (n.pre != null) {
				int x11 = n.x - 2;
				int y11 = n.y - 2;
				n = n.pre;
				int x2 = n.x - 2;
				int y2 = n.y - 2;
				add(x11, y11, x2, y2);
			}
			paint();
			t = new timeToClear(this);
			t.start();
		}
	}
}

class Segment {
	int x1, y1, x2, y2;

	Segment(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}

class timeToClear extends Thread {
	Draw d;

	timeToClear(Draw d) {
		this.d = d;
	}

	@Override
	public void run() {
		synchronized (d) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			d.clear();
		}
	}
}
