import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class DataStorage implements Serializable {
	private static final long serialVersionUID = 1L;
	int W, H, N;
	ArrayList<ArrayList<Integer>> board; // 0为通路，1为墙，2及以上为方块
	ArrayList<ArrayList<Integer>> board0;
	int havekilled;
	ArrayList<Integer> kills;
	ArrayList<Boolean> together;
	boolean isTime, isFake;
	int duration;
	String date;

	public DataStorage(GameData g, int duration, boolean isTime, boolean isFake) {
		this.duration = duration;
		date = new Date().toString();
		this.isTime = isTime;
		this.isFake = isFake;
		W = g.W;
		H = g.H;
		N = g.N;
		board = new ArrayList<>();
		for (int i = 0; i < g.board.length; ++i) {
			board.add(new ArrayList<>());
			for (int j = 0; j < g.board[0].length; ++j) {
				board.get(i).add(g.board[i][j]);
			}
		}
		board0 = new ArrayList<>();
		for (int i = 0; i < g.board0.length; ++i) {
			board0.add(new ArrayList<>());
			for (int j = 0; j < g.board0[0].length; ++j) {
				board0.get(i).add(g.board0[i][j]);
			}
		}
		havekilled = g.havekilled;
		kills = new ArrayList<>();
		for (int i = 0; i < g.kills.length; ++i)
			kills.add(g.kills[i]);
		together = new ArrayList<>();
		for (int i = 0; i < g.together.length; ++i)
			together.add(g.together[i]);
	}

}
