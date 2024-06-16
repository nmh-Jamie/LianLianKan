
/**
 * 测试代码
 */

import java.awt.*;
import java.awt.event.*;

/**
 * 测试程序
 */
class Tester {
	static void print(Object o) {
		System.out.print(o);
	}

	void test1() {
		Frame f = new Frame("test1");
		f.setLayout(null);
		f.setSize(800, 600);
		f.setLocation(200, 200);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.setVisible(true);

		Canvas c = new Canvas() {
			private static final long serialVersionUID = -7989182807318914110L;

			@Override
			public void paint(Graphics g) {
				double r = Math.random();
				g.drawLine((int) (r * 100), (int) (r * 100), (int) (r * 150), (int) (r * 150));
				r = Math.random();
				g.drawLine((int) (r * 100), (int) (r * 100), (int) (r * 150), (int) (r * 150));
				Tester.print("OK");
			}
		};
		f.add(c);
		c.setBounds(200, 200, 800, 800);

		Button b = new Button("Hello,world!");
		f.add(b);
		b.setBounds(100, 100, 200, 50);
		b.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				b.setLabel("mouseClicked");
				c.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				b.setLabel("mousePressed");

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				b.setLabel("mouseReleased");

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				b.setLabel("mouseEntered");

			}

			@Override
			public void mouseExited(MouseEvent e) {
				b.setLabel("mouseExited");

			}

		});

		MenuBar m = new MenuBar();
		f.setMenuBar(m);
		Menu m1 = new Menu("test1");
		Menu m2 = new Menu("test2");
		m.add(m1);
		m.add(m2);
		MenuItem m11 = new MenuItem("111");
		m1.add(m11);
	}
}

/**
 * 
 */
public class Test {

}
