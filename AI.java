import java.util.ArrayList;
import java.util.Random;

public class AI {
	private Random random = new Random();
	public AI() {
		// TODO Auto-generated constructor stub
	}
	
	
	private void getSimpleMoves(GameState state, int r, int c,ArrayList<Move> moves) {
		Location from = new Location(r, c);
		Move move;
		//Test four basic moves
		move = new Move(from, new Location(from.row - 1, from.col + 1));
		if (state.isLegal(move)) moves.add(move);
		move = new Move(from, new Location(from.row - 1, from.col - 1));
		if (state.isLegal(move)) moves.add(move);
		move = new Move(from, new Location(from.row + 1, from.col + 1));
		if (state.isLegal(move)) moves.add(move);
		move = new Move(from, new Location(from.row + 1, from.col - 1));
		if (state.isLegal(move)) moves.add(move);
	}

	public void getJumpMoves(GameState state, int r, int c, ArrayList<Move> moves) {
		ArrayList<Move> doubleJumps = new ArrayList<Move>();
		Location from = new Location(r, c);
		Move move;
		move = new Move(from, new Location(from.row - 2,
				           from.col + 2));
		if (state.isLegal(move)) {
			moves.add(move);
			getJumpMoves(new GameState(state, move), move.to().row,
					               move.to().col, doubleJumps);
			for (int i = 0; i < doubleJumps.size(); i++)
				moves.add(new Move(from, doubleJumps.get(i)));
		}
		move = new Move(from, new Location(from.row - 2,
				from.col - 2));
		if (state.isLegal(move)) {
			moves.add(move);
			getJumpMoves(new GameState(state, move), move.to().row, 
					move.to().col, doubleJumps);
			for (int i = 0; i < doubleJumps.size(); i++)
				moves.add(new Move(from, doubleJumps.get(i)));
		}
		move = new Move(from, new Location(from.row + 2,
				from.col + 2));
		if (state.isLegal(move)) {
			moves.add(move);
			getJumpMoves(new GameState(state, move), move.to().row,
					move.to().col, doubleJumps);
			for (int i = 0; i < doubleJumps.size(); i++)
				moves.add(new Move(from, doubleJumps.get(i)));
		}
		move = new Move(from, new Location(from.row + 2, 
				from.col - 2));
		if (state.isLegal(move)) {
			moves.add(move);
			getJumpMoves(new GameState(state, move), move.to().row, 
					move.to().col, doubleJumps);
			for (int i = 0; i < doubleJumps.size(); i++)
				moves.add(new Move(from, doubleJumps.get(i)));
		}
	}
	public ArrayList<Move> getPotentialCaptureMoves(GameState state){
		ArrayList<Move> jumpMoves = new ArrayList<Move>();
		for (int row= 0; row < 8; row++)
			for (int col = 0; col < 8; col++) {
				if (state.getPlayer(row, col) != 1) continue;
				getJumpMoves(state, row, col, jumpMoves);
			}
		if (jumpMoves.size() > 0)
			return jumpMoves;
		else
			return null;
	}
	public Move getBestMove (GameState state, int Level) throws NoMovesLeftException {
		//Generating valid moves
		ArrayList<Move> jumpMoves = new ArrayList<Move>();
		ArrayList<Move> moves = new ArrayList<Move>();
		for (int row= 0; row < 8; row++)
			for (int col = 0; col < 8; col++) {
				if (state.getPlayer(row, col) != 1) continue;
				getSimpleMoves(state, row, col, moves);
				getJumpMoves(state, row, col, jumpMoves);
				moves.addAll(jumpMoves);
			}
		if (jumpMoves.size() <= 0){
			//scoring the moves.
			ArrayList<Move> bestMoves = new ArrayList<Move>();
			int bScore = -9999999; //almost negative infinity.
			for (int i = 0; i < moves.size(); i++)
				try {
					GameState nextMove = new GameState(state, moves.get(i));
					int score;
					if (Level == 0) score = calculateScore(nextMove);
					else {
						nextMove.flipBoard();
						try {
							Move min = getBestMove (nextMove, Level - 1);
							nextMove.applyMove(min);
						} catch (NoMovesLeftException e) {
//							continue;
						}
						nextMove.flipBoard();
						score = calculateScore(nextMove);
					}

					if (score == bScore) {
						bestMoves.add(moves.get(i));
					}
					else if (score > bScore) {
						bestMoves.clear();
						bestMoves.add(moves.get(i));
						bScore = score;
					}
				} catch (IllegalMoveException e) {continue;}
			if (bestMoves.isEmpty()) throw new NoMovesLeftException();

			//chosing best move at random
			return bestMoves.get(random.nextInt(bestMoves.size()));
		}
		//assigning scores to moves.
		ArrayList<Move> bestMoves = new ArrayList<Move>();
		int bscore = -9999999; //almost negative infinity
		for (int i = 0; i < jumpMoves.size(); i++)
			try {
				GameState nextMove = new GameState(state, jumpMoves.get(i));
				int score, prevscore;
				prevscore= calculateScore(nextMove);
				if (Level == 0) score = calculateScore(nextMove);
				else {
					nextMove.flipBoard();
					try {
						Move min = getBestMove (nextMove, Level - 1);
						nextMove.applyMove(min);
					} catch (NoMovesLeftException e) {
//						continue;
					}
					nextMove.flipBoard();
					score = calculateScore(nextMove);
				}

				score += prevscore;
				if (score == bscore) {
					bestMoves.add(jumpMoves.get(i));
				}
				else if (score > bscore) {
					bestMoves.clear();
					bestMoves.add(jumpMoves.get(i));
					bscore = score;
				}
			} catch (IllegalMoveException e) {continue;}
		if (bestMoves.isEmpty()) throw new NoMovesLeftException();
		//best move is chosen at random.
		return bestMoves.get(random.nextInt(bestMoves.size()));
	}
	public int calculateScore (GameState state) {
		int r = 0, R = 0, b = 0, B = 0;
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
				if (state.get(row, col) == 'r') r++;
				else if (state.get(row, col) == 'R') R++;
				else if (state.get(row, col) == 'b') b++;
				else if (state.get(row, col) == 'B') B++;
		if (r + R == 0) return 999999;
		else if (b + B == 0) return -999999;
		else return (b + 2 * B) - (r + 2 * R);
	}
	
};
