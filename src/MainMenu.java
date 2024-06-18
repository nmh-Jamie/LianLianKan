
/**
 * 主菜单
 */

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.*;

/**
 * 主菜单
 */
public class MainMenu extends Frame {
	private static final long serialVersionUID = 1L;

	MainMenu() {
		super("主菜单");
		setSize(800, 600);
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
				System.exit(0);
			}
		});
		setItem();
		setVisible(true);
	}

	void setItem() {
		setLayout(null);
		setHead();
		setButton();
		setOthers();
	}

	void setHead() {
		Label l = new Label("连 连 看", Label.CENTER);
		l.setFont(new Font("宋体", Font.BOLD, 50));
		l.setSize(getWidth(), getHeight() / 10);
		l.setLocation((getWidth() - l.getWidth()) / 2, (int) (getHeight() * 0.15));
		add(l);
	}

	void setButton() {
		Button b1 = new Button("新建游戏");
		b1.setFont(new Font("宋体", Font.PLAIN, 25));
		b1.setSize(getWidth() / 3, getHeight() / 10);
		b1.setLocation((getWidth() - b1.getWidth()) / 2, (int) (getHeight() * 0.35));
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateGame();
			}
		});
		add(b1);

		Button b2 = new Button("本地存档");
		b2.setFont(new Font("宋体", Font.PLAIN, 25));
		b2.setSize(getWidth() / 3, getHeight() / 10);
		b2.setLocation((getWidth() - b2.getWidth()) / 2, (int) (getHeight() * 0.50));
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadLocal();
			}
		});
		add(b2);

		Button b3 = new Button("网络存档");
		b3.setFont(new Font("宋体", Font.PLAIN, 25));
		b3.setSize(getWidth() / 3, getHeight() / 10);
		b3.setLocation((getWidth() - b3.getWidth()) / 2, (int) (getHeight() * 0.65));
		add(b3);
	}

	void setOthers() {
		Label l = new Label("LianLianKan 2024 v1.0.0", Label.CENTER);
		l.setFont(new Font("宋体", Font.ITALIC, 20));
		l.setSize(getWidth(), getHeight() / 10);
		l.setLocation((getWidth() - l.getWidth()) / 2, (int) (getHeight() * 0.85));
		add(l);

		MenuBar m = new MenuBar();
		Menu m1 = new Menu("关于");
		setMenuBar(m);
		m.add(m1);
	}

	void loadLocal() {
		System.out.println("load...");
		FileDialog f = new FileDialog(this, "读取存档", FileDialog.LOAD);
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
				ObjectInputStream input = new ObjectInputStream(new FileInputStream(path));
				new Game((DataStorage) input.readObject());
				input.close();
			} catch (Exception e) {
				System.out.println("导入失败！");
			}

		}
	}
}
