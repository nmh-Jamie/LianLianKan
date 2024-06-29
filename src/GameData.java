
/**
 * 游戏数据
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * 盘面数据
 */
public class GameData {
	int W, H, N;
	int[][] board; // 0为通路，1为墙，2及以上为方块
	int[][] board0;
	int havekilled;
	volatile int[] kills;
	volatile boolean[] together;
	final static int[] dx = { 1, 0, -1, 0 }, dy = { 0, 1, 0, -1 };

	GameData(int W, int H, int N) {
		this.W = W;
		this.H = H;
		this.N = N;
		havekilled = 0;
		kills = new int[W * H];
		together = new boolean[W * H];
		board = new int[W + 4][];
		board0 = new int[W + 4][];
		for (int i = 0; i < W + 4; ++i) {
			board[i] = new int[H + 4];
			board0[i] = new int[H + 4];
		}
		// 外墙
		for (int i = 0; i < W + 4; ++i)
			board[i][0] = board[i][H + 3] = 1;
		for (int i = 1; i < W + 3; ++i)
			board[i][1] = board[i][H + 2] = 0;
		for (int i = 0; i < H + 4; ++i)
			board[0][i] = board[W + 3][i] = 1;
		for (int i = 1; i < H + 3; ++i)
			board[1][i] = board[W + 2][i] = 0;

		{ // 随机生成盘面
			Random r = new Random();
			int[] L = new int[W * H];
			for (int i = 0; i < W * H; ++i) {
				L[i] = i % N + 2;
			}
			for (int i = W * H / (2 * N) * 2 * N, j = 0; i < W * H; ++i, ++j) {
				L[i] = j / 2 + 2;
			}
			for (int i = W * H - 1; i >= 0; --i) {
				int idx = r.nextInt(i + 1);
				board[i / H + 2][i % H + 2] = L[idx];
				L[idx] = L[i];
			}
		}
		for (int i = 0; i < W + 4; ++i) {
			for (int j = 0; j < H + 4; ++j) {
				board0[i][j] = board[i][j];
			}
		}
		showBoard();
	}

	GameData(DataStorage d) {
		W = d.W;
		H = d.H;
		N = d.N;
		havekilled = d.havekilled;
		kills = new int[W * H];
		for (int i = 0; i < kills.length; ++i)
			kills[i] = d.kills.get(i);
		together = new boolean[W * H];
		for (int i = 0; i < together.length; ++i)
			together[i] = d.together.get(i);
		board = new int[W + 4][];
		board0 = new int[W + 4][];
		for (int i = 0; i < W + 4; ++i) {
			board[i] = new int[H + 4];
			board0[i] = new int[H + 4];
			for (int j = 0; j < H + 4; ++j) {
				board[i][j] = d.board.get(i).get(j);
				board0[i][j] = d.board0.get(i).get(j);
			}
		}
		showBoard();
	}

	void show() {
		System.out.println("last: " + (W * H - havekilled));
	}

	void showBoard() {
		for (int y = H + 3; y >= 0; --y) {
			for (int x = 0; x <= W + 3; ++x) {
				System.err.printf("%4d", board[x][y]);
			}
			System.err.println();
		}
	}

	int getColor(int x, int y) {
		return board[x + 2][y + 2];
	}

	int getColor0(int x, int y) {
		return board0[x + 2][y + 2];
	}

	boolean isComplete() {
		return havekilled == W * H;
	}

	synchronized void kill(int x, int y, boolean f) {
		board[x + 2][y + 2] = 0;
		kills[havekilled] = x + y * W;
		together[havekilled] = f;
		havekilled++;
		show();
	}

	void kill(int x, int y) {
		kill(x, y, false);
	}

	synchronized Node kill(int x1, int y1, int x2, int y2) {
		if (getColor(x1, y1) != getColor(x2, y2))
			return null;
		else {
			x1 += 2;
			y1 += 2;
			x2 += 2;
			y2 += 2;

			// BFS
			boolean[][][][] vis;
			vis = new boolean[W + 4][][][];
			for (int i = 0; i < W + 4; ++i) {
				vis[i] = new boolean[H + 4][][];
				for (int j = 0; j < H + 4; ++j) {
					vis[i][j] = new boolean[4][];
					for (int k = 0; k < 4; ++k) {
						vis[i][j][k] = new boolean[3];
					}
				}
			}
			Queue<Node> q = new LinkedList<>();
			q.add(new Node(x1, y1, -1, 0));
			vis[x1][y1][0][0] = vis[x1][y1][1][0] = vis[x1][y1][2][0] = vis[x1][y1][3][0] = true;

			Node re;
			while (!q.isEmpty()) {
				Node now = q.poll();
				// System.err.println("(" + now.x + "," + now.y + ")");
				for (int i = 0; i < 4; ++i) {
					int x = now.x + dx[i], y = now.y + dy[i];
					if (board[x][y] != 0 && !(x == x2 && y == y2))
						continue;
					int t;
					if (i == now.d || now.d == -1)
						t = now.t;
					else
						t = now.t + 1;
					if (t > 2 || vis[x][y][i][t])
						continue;
					vis[x][y][i][t] = true;
					re = new Node(x, y, i, t, now);
					q.add(re);
					if (x == x2 && y == y2) {
						kill(x1 - 2, y1 - 2, true);
						kill(x2 - 2, y2 - 2, true);
						return re;
					}
				}
			}
			return null;
		}
	}

	synchronized void recover() {
		havekilled--;
		int i = kills[havekilled];
		board[i % W + 2][i / W + 2] = board0[i % W + 2][i / W + 2];
		show();
	}

	synchronized void rekill() {
		int i = kills[havekilled];
		havekilled++;
		board[i % W + 2][i / W + 2] = 0;
		show();
	}
}

class Node { // BFS结点
	int x, y; // 当前位置
	int d; // 当前方向
	int t; // 已拐次数
	Node pre;

	Node(int x, int y, int d, int t) {
		this.x = x;
		this.y = y;
		this.d = d;
		this.t = t;
	}

	Node(int x, int y, int d, int t, Node n) {
		this.x = x;
		this.y = y;
		this.d = d;
		this.t = t;
		pre = n;
	}
}
