
/**
 * 单局游戏
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 游戏
 */
public class Game extends Frame {
	private static final long serialVersionUID = 1L;
	GameBoard g;
	int W, H, N;
	boolean isTime, isFake;
	Thread thd;
	JSlider s;
	Checkbox fake1;
	Timer t;
	Draw c;
	volatile boolean playing = false;

	Game(int W, int H, int N, boolean isTime, boolean isFake) {
		super("新游戏（开始于 " + new Date().toString() + "）");
		this.W = W;
		this.H = H;
		this.N = N;
		this.isTime = isTime;
		this.isFake = isFake;
		setSize(40 * (W + 6), 40 * (H + 5));
		setResizable(false);
		{ // 位置居中
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension screenSize = toolkit.getScreenSize();
			int x = (screenSize.width - getWidth()) / 2;
			int y = (screenSize.height - getHeight()) / 2;
			setLocation(x, y);
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (thd != null)
					thd.interrupt();
				dispose();
			}
		});
		setItem();
		setVisible(true);
	}

	void setItem() {
		setLayout(null);

		MenuBar m = new MenuBar();
		Menu m1 = new Menu("保存");
		setMenuBar(m);
		m.add(m1);
		MenuItem m11 = new MenuItem("保存到本地");
		m11.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveLocal();
			}
		});
		m1.add(m11);

		if (isTime) {
			t = new Timer("00:00:00", Label.CENTER);
			t.setSize(getWidth(), 20);
			t.setLocation(0, 60);
			add(t);
			thd = new Thread(t);
			thd.start();
		}

		g = new GameBoard(W, H, N, this);
		g.setLocation((getWidth() - g.getWidth()) / 2, 100);
		add(g);

		if (isFake) {
			fake1 = new Checkbox("强制消除");
			fake1.setFont(new Font("宋体", Font.PLAIN, 20));
			fake1.setSize(getWidth() / 3, 20);
			fake1.setLocation((getWidth() - 100) / 2, 130 + 40 * H);
			add(fake1);
		}

		s = new JSlider(0, 0);
		s.setSize(getWidth() * 8 / 10, 20);
		s.setBackground(Color.WHITE);
		s.setLocation(getWidth() / 10, 160 + 40 * H);
		add(s);

		s.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!playing) {
					if (s.getValue() < g.g.havekilled)
						backward();
					else if (s.getValue() > g.g.havekilled)
						forward();
				}
				try {
					Thread.sleep(15);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});

		c = new Draw(g.getX() + 20, g.getY() + 40 * H - 20, 2 * (W + 2) + 2 * (H + 2));
		c.setSize(getSize());
		add(c);
		System.err.println("create OK");

		setComponentZOrder(g, getComponentCount() - 2);
		setComponentZOrder(c, getComponentCount() - 1);
		revalidate();
		repaint();
	}

	void saveLocal() {
		System.out.println("save");
		FileDialog f = new FileDialog(this, "保存存档", FileDialog.SAVE);
		f.setFile("Untitled.llk");
		f.setFilenameFilter(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".llk") || new File(dir, name).isDirectory();
			}
		});
		f.setVisible(true);
		String path = f.getFile();
		if (path != null) {
			path = f.getDirectory() + path;
			System.out.println(path);
			try {
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(path));
				output.writeObject(new DataStorage(g.g, t.now, isTime, isFake));
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	synchronized void backward() {
		System.out.println("backward");
		g.g.recover();
		int k = g.g.kills[g.g.havekilled];
		boolean t = g.g.together[g.g.havekilled];
		g.bs[k % W][k / W].setVisible(true);
		if (t) {
			g.g.recover();
			k = g.g.kills[g.g.havekilled];
			g.bs[k % W][k / W].setVisible(true);
		}
		s.setValue(g.g.havekilled);
	}

	synchronized void forward() {
		System.out.println("forward");
		int k = g.g.kills[g.g.havekilled];
		boolean t = g.g.together[g.g.havekilled];
		if (t) {
			int x2 = g.g.kills[g.g.havekilled + 1] % W;
			int y2 = g.g.kills[g.g.havekilled + 1] / W;
			Node n = g.g.kill(k % W, k / W, x2, y2);
			g.G.c.draw(n);
			g.bs[k % W][k / W].setVisible(false);
			g.bs[x2][y2].setVisible(false);
		} else {
			g.g.kill(k % W, k / W);
			g.bs[k % W][k / W].setVisible(false);
		}
		s.setValue(g.g.havekilled);
	}

	Game(DataStorage d) { // 导入存档
		super("游戏存档（最后于 " + d.date + "）");
		W = d.W;
		H = d.H;
		N = d.N;
		isTime = d.isTime;
		isFake = d.isFake;
		setSize(40 * (W + 6), 40 * (H + 5));
		setResizable(false);
		{ // 位置居中
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension screenSize = toolkit.getScreenSize();
			int x = (screenSize.width - getWidth()) / 2;
			int y = (screenSize.height - getHeight()) / 2;
			setLocation(x, y);
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (thd != null)
					thd.interrupt();
				dispose();
			}
		});

		setLayout(null);

		MenuBar m = new MenuBar();
		Menu m1 = new Menu("保存");
		setMenuBar(m);
		m.add(m1);
		MenuItem m11 = new MenuItem("保存到本地");
		m11.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveLocal();
			}
		});
		m1.add(m11);

		if (isTime) {
			t = new Timer("00:00:00", Label.CENTER);
			t.now = d.duration;
			t.setSize(getWidth(), 20);
			t.setLocation(0, 60);
			add(t);
			thd = new Thread(t);
			thd.start();
		}

		g = new GameBoard(d, this);
		g.setLocation((getWidth() - g.getWidth()) / 2, 100);
		add(g);

		if (isFake) {
			fake1 = new Checkbox("强制消除");
			fake1.setFont(new Font("宋体", Font.PLAIN, 20));
			fake1.setSize(getWidth() / 3, 20);
			fake1.setLocation((getWidth() - 100) / 2, 130 + 40 * H);
			add(fake1);
		}

		s = new JSlider(0, d.havekilled);
		s.setValue(d.havekilled);
		s.setSize(getWidth() * 8 / 10, 20);
		s.setBackground(Color.WHITE);
		s.setLocation(getWidth() / 10, 160 + 40 * H);
		add(s);

		s.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!playing) {
					if (s.getValue() < g.g.havekilled)
						backward();
					else if (s.getValue() > g.g.havekilled)
						forward();
				}
				try {
					Thread.sleep(15);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});

		c = new Draw(g.getX() + 20, g.getY() + 40 * H - 20, 2 * (W + 2) + 2 * (H + 2));
		c.setSize(getSize());
		add(c);
		System.err.println("load OK");

		setComponentZOrder(g, getComponentCount() - 2);
		setComponentZOrder(c, getComponentCount() - 1);
		revalidate();
		repaint();

		setVisible(true);
	}
}
