
/**
 * 新建游戏
 */

import java.awt.*;
import java.awt.event.*;

/**
 * 新建游戏
 */
public class CreateGame extends Frame {
	private static final long serialVersionUID = 1L;
	SingleInput i1, i2, i3;
	Checkbox c1, c2;

	CreateGame() {
		super("新建游戏");
		setSize(600, 400);
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
				dispose();
			}
		});
		setItem();
		setVisible(true);
	}

	void setItem() {
		setLayout(null);
		setInput();
	}

	void setInput() {
		Label l1 = new Label("请设置游戏参数", Label.CENTER);
		l1.setFont(new Font("宋体", Font.BOLD, 20));
		l1.setSize(getWidth(), (int) (getHeight() * 0.1));
		l1.setLocation((getWidth() - l1.getWidth()) / 2, (int) (getHeight() * 0.1));
		add(l1);

		i1 = new SingleInput("横向方块数", "16", 100);
		i1.setSize(getWidth(), (int) (getHeight() * 0.1));
		i1.setLocation((getWidth() - i1.getWidth()) / 2, (int) (getHeight() * 0.25));
		add(i1);

		i2 = new SingleInput("纵向方块数", "10", 100);
		i2.setSize(getWidth(), (int) (getHeight() * 0.1));
		i2.setLocation((getWidth() - i2.getWidth()) / 2, (int) (getHeight() * 0.35));
		add(i2);

		i3 = new SingleInput("方块种类数", "10", 10);
		i3.setSize(getWidth(), (int) (getHeight() * 0.1));
		i3.setLocation((getWidth() - i3.getWidth()) / 2, (int) (getHeight() * 0.45));
		add(i3);

		c1 = new Checkbox("开启计时", true);
		c1.setFont(new Font("宋体", Font.ITALIC, 15));
		c1.setSize(getWidth() / 3, getHeight() / 10);
		c1.setLocation((getWidth() - c1.getWidth() / 2) / 2, (int) (getHeight() * 0.55));
		add(c1);

		c2 = new Checkbox("开启作弊", true);
		c2.setFont(new Font("宋体", Font.ITALIC, 15));
		c2.setSize(getWidth() / 3, getHeight() / 10);
		c2.setLocation((getWidth() - c2.getWidth() / 2) / 2, (int) (getHeight() * 0.65));
		add(c2);

		Button b = new Button("开始游戏");
		b.setFont(new Font("宋体", Font.PLAIN, 25));
		b.setSize(getWidth() / 3, getHeight() / 10);
		b.setLocation((getWidth() - b.getWidth()) / 2, (int) (getHeight() * 0.80));
		add(b);

		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int W = i1.getInput();
				int H = i2.getInput();
				int N = i3.getInput();
				boolean isTime = c1.getState();
				boolean isFake = c2.getState();
				if (W == -1 || H == -1 || N == -1)
					return;
				System.err.println("Game: " + W + " " + H + " " + N + " " + isTime + " " + isFake);
				new Game(W, H, N, isTime, isFake);
			}
		});
	}
}

class SingleInput extends Panel {
	private static final long serialVersionUID = 1L;
	int max;
	Label l;
	TextField t;

	SingleInput(String name, String input0, int max) {
		this.max = max;
		setLayout(new FlowLayout());
		l = new Label(name + ": ", Label.CENTER);
		l.setFont(new Font("宋体", Font.PLAIN, 15));
		add(l);
		t = new TextField(input0, 10);
		t.setFont(new Font("宋体", Font.PLAIN, 15));
		add(t);
		setVisible(true);
	}

	int getInput() {
		try {
			l.setForeground(Color.BLACK);
			int i = Integer.parseInt(t.getText());
			if (i <= 0 || i > max)
				throw new Exception("数字不符合规范");
			return i;
		} catch (Exception e) {
			l.setForeground(Color.RED);
			return -1;
		}
	}
}