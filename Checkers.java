import java.util.Timer;

public class Checkers{
	private static GameState state = GameState.setBoard();
	private static AI ai = new AI();
	
	public Checkers() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		long t = System.currentTimeMillis();
		state = new GameState(state.convertArray(args[1]));
		boolean flipBoard = false;
		if (args[0].equals("1")){
			flipBoard = true;
		}
		if (flipBoard)
			state.flipBoard();
		try {
			Move mv = ai.getBestMove(state, 6);
			System.out.println(getFromTo(mv, flipBoard));
		}
		catch (NoMovesLeftException ex) {
			System.out.println(ex.getMessage());
		}
	}

	private static String getFromTo(Move m, boolean flipBoard){
		Location from = m.from();
		Location to = m.to();
		int f = from.row * 4;
		f += (from.col)/2;
		String jumps = " ";
		boolean flag = false;
		while (m.hasMoreMoves()){
			flag = true;
			jumps += GameState.getJump(m) + " ";
			m = m.getNextMove();
		}
		if (flag)
			jumps += GameState.getJump(m) + " ";
		to = m.to();
		int t = to.row * 4;
		t += (to.col)/2;
		if (flipBoard){
			f = 31-f;
			t = 31-t;
		}
		return String.format("%02d", f) + " " + String.format("%02d", t) + jumps;
	}

};
