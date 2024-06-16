import java.awt.Font;
import java.awt.Label;

class Timer extends Label implements Runnable {
	private static final long serialVersionUID = 1L;
	int now;

	public Timer(String s, int i) {
		super(s, i);
		setFont(new Font("ËÎÌו", Font.PLAIN, 20));
		now = 0;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				now++;
				String h = String.format("%02d", now / 3600);
				String m = String.format("%02d", (now % 3600) / 60);
				String s = String.format("%02d", now % 60);
				setText(h + ":" + m + ":" + s);
			} catch (InterruptedException e) {
				break;
			}
		}

	}
}