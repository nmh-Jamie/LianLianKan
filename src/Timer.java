
/**
 * ¼ÆÊ±
 */

import java.awt.Font;
import java.awt.Label;

class Timer extends Label implements Runnable {
	private static final long serialVersionUID = 1L;
	int now;
	Thread thd;
	boolean isTime;

	public Timer(String s, int i, boolean isTime) {
		super(s, i);
		setFont(new Font("ËÎÌå", Font.PLAIN, 20));
		now = 0;
		this.isTime = isTime;
		if (isTime)
			thd = new Thread(this);
		else
			setVisible(false);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				now++;
				refresh();
			} catch (InterruptedException e) {
				break;
			}
		}

	}

	synchronized void stop() {
		if (isTime)
			thd.interrupt();
	}

	synchronized void start() {
		if (isTime && !thd.isAlive()) {
			thd = new Thread(this);
			thd.start();
		}
	}

	void refresh() {
		String h = String.format("%02d", now / 3600);
		String m = String.format("%02d", (now % 3600) / 60);
		String s = String.format("%02d", now % 60);
		setText(h + ":" + m + ":" + s);
	}
}