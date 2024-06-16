import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

class GameBoard extends JPanel {
	private static final long serialVersionUID = 1L;
	final static Color[] colors = { null, null, Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.ORANGE,
			Color.PINK, Color.GRAY, Color.CYAN, Color.BLACK, Color.DARK_GRAY };
	Game G;
	GameData g;
	Cell[][] bs;
	Checkbox fake1;
	volatile int now = -1; // x+y*W

	GameBoard(int W, int H, int N, Game G) {
		setSize(W * 40, H * 40);
		setOpaque(false);
		g = new GameData(W, H, N);
		this.G = G;
		int L = 40;
		int x0 = 0, y0 = getHeight() - L;
		setLayout(null);
		bs = new Cell[g.W][];
		for (int i = 0; i < g.W; ++i) {
			bs[i] = new Cell[g.H];
			for (int j = 0; j < g.H; ++j) {
				bs[i][j] = new Cell(i, j, this);
				bs[i][j].setBackground(colors[g.getColor(i, j)]);
				bs[i][j].setSize(L, L);
				bs[i][j].setLocation(x0 + L * i, y0 - L * j);
				add(bs[i][j]);
			}
		}
	}

	GameBoard(DataStorage d, Game G) {
		setSize(d.W * 40, d.H * 40);
		setOpaque(false);
		g = new GameData(d);
		this.G = G;
		int L = 40;
		int x0 = 0, y0 = getHeight() - L;
		setLayout(null);
		bs = new Cell[g.W][];
		for (int i = 0; i < g.W; ++i) {
			bs[i] = new Cell[g.H];
			for (int j = 0; j < g.H; ++j) {
				bs[i][j] = new Cell(i, j, this);
				bs[i][j].setBackground(colors[g.getColor0(i, j)]);
				bs[i][j].setSize(L, L);
				bs[i][j].setLocation(x0 + L * i, y0 - L * j);
				add(bs[i][j]);
				if (g.getColor(i, j) == 0)
					bs[i][j].setVisible(false);
			}
		}
	}
}

class Cell extends Button implements ActionListener {
	private static final long serialVersionUID = 1L;
	int x, y;
	GameBoard g;

	Cell(int x, int y, GameBoard g) {
		this.x = x;
		this.y = y;
		this.g = g;
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (g.G.fake1 != null && g.G.fake1.getState()) {
			setVisible(false);
			g.g.kill(x, y);
			g.now = -1;
		} else if (g.now != -1 && x + y * g.g.W != g.now) {
			int x1 = g.now % g.g.W, y1 = g.now / g.g.W;
			Node n = g.g.kill(x1, y1, x, y);
			if (n != null) {
				// System.out.println("(" + n.x + "," + n.y + ")");
				g.G.c.draw(n);
				g.bs[x1][y1].setVisible(false);
				g.bs[x][y].setVisible(false);
				g.now = -1;
			} else {
				System.out.println("error");
				g.now = x + y * g.g.W;
				return;
			}
		} else {
			g.now = x + y * g.g.W;
			return;
		}
		g.G.playing = true;
		g.G.s.setMaximum(g.g.havekilled);
		g.G.s.setValue(g.g.havekilled);
		System.out.println("now: " + g.now);
		g.G.playing = false;

		if (g.g.isComplete()) {
			System.out.println("**** WIN ****");
			g.G.thd.interrupt();
			Dialog d = new Dialog(g.G, "你过关！");
			d.setResizable(false);
			d.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					d.dispose();
				}
			});

			File imageFile = new File("img/win.png");
			try {
				System.err.println("img");
				Image image = ImageIO.read(imageFile);
				ImageIcon imageIcon = new ImageIcon(image);
				JLabel label = new JLabel(imageIcon);
				d.add(label);
				System.err.println("img");
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
			d.pack();
			{ // 位置居中
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Dimension screenSize = toolkit.getScreenSize();
				int x = (screenSize.width - d.getWidth()) / 2;
				int y = (screenSize.height - d.getHeight()) / 2;
				d.setLocation(x, y);
			}
			d.setVisible(true);
		}
	}
}