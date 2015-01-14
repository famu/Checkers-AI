import java.lang.Thread.State;

/*
 * legends:
 * '.' Black squares
 * '_' White square with no pieces
 * 'r' pawn of the red pieces (r)
 * 'R' king of the red pieces (R)
 * 'b' pawn of the black pieces
 * 'B' king of the black pieces
 */

public class GameState {


	private char[][] board = new char[8][8];

	public GameState(GameState prev, Move m) {
		this(prev.toString());
		try {
			applyMove(m);
		} catch (IllegalMoveException e) {}
	}
	public GameState() {
		this("._._._._"
			+ "_._._._."
			+ "._._._._"
			+ "_._._._."
			+ "._._._._"
			+ "_._._._."
			+ "._._._._"
			+ "_._._._.");
	}
	public static int getJump(Move m){
		Location jumped = null;
		Location to = m.to();
		Location from = m.from();
		//calculating absolute distances
		int dr = to.row - from.row;
		int adr = (dr > 0)? dr : -dr;
		int dc = to.col - from.col;
		int adc = (dc > 0)? dc : -dc;
		//in case of jump
		if (adr > 1) {
			//emptying the jumped square
			jumped = new Location(from.row + (dr / adr), from.col + (dc / adc));
		}
		int j = jumped.row * 4;
		j += (jumped.col)/2;
		return j;
	}
	public boolean isLegal(Move m) {
		//move means origin to destination square
		if (m.getNextMove() == null) return false;
		Location from = m.from();
		Location to = m.to();
		//the moves outside the board are not valid
		if (!isOnBoard(to.row, to.col)
				|| !isOnBoard(from.row, from.col)) return false;
		//moving to an invalid square or to pre-equiped square
		if (get(from.row, from.col) == '_'
				|| get(from.row, from.col) == '.'
				|| get(to.row, to.col) != '_') return false;
		//calculating absolute distances
		int d_r = to.row - from.row;
		int a_d_r = (d_r > 0)? d_r : -d_r;
		int d_c = to.col - from.col;
		int a_d_c = (d_c > 0)? d_c : -d_c;
		//diagonal moves are valid
		if (a_d_r != a_d_c || a_d_r < 1 || a_d_r > 2) return false;
		//pawn of player1 is moving diagonal too but one square up
		if (d_r > 0 && get(from.row, from.col) == 'b') return false;
		//pawn of player2 is moving diagonal too but one square up
		if (d_r < 0 && get(from.row, from.col) == 'r') return false;
		//in case of jumps
		if (a_d_r == 2) {
			Location jumped = new Location(from.row + (d_r / a_d_r), from.col + (d_c / a_d_c));
			int fromPlayer = getPlayer(from.row, from.col);
			int jumpedPlayer = getPlayer(jumped.row, jumped.col);
			//jump over same color piece and white square isn't valid.
			if (jumpedPlayer == 0 || jumpedPlayer == fromPlayer) return false;
		}
		if (m.hasMoreMoves()) return new GameState(this, m.getFirstMove()).isLegal(m.getNextMove());
		//finally the move is valid
		return true;
	}
	
	public GameState(String state){
		char[] charArray = state.toCharArray();
		int index = 0;
		for (int r = 0; r < 8; r++)
			for(int c = 0; c < 8; c++) {
				while (Character.isWhitespace(charArray[index])) index++;
				setPiece(r, c, charArray[index++]);
			}
	}

	public Location applyMove(Move m) throws IllegalMoveException {
		if (!isLegal(m)) throw new IllegalMoveException(m);
		Location jumped = null;
		Location to = m.to();
		Location from = m.from();
		//calculating absolute distances
		int d_r = to.row - from.row;
		int a_d_r = (d_r > 0)? d_r : -d_r;
		int d_c = to.col - from.col;
		int a_d_c = (d_c > 0)? d_c : -d_c;
		boolean King = false;
		//in case of jumps
		if (a_d_r > 1) {
			//emptying the square of jumped piece
			jumped = new Location(from.row + (d_r / a_d_r), from.col + (d_c / a_d_c));
			setPiece(jumped.row, jumped.col, '_');
		}
		//applying move
		if (to.row == 0 && get(from.row, from.col) == 'b') {
			setPiece(to.row, to.col, 'B');
			King = true;
			jumped = null;
		}
		else if (to.row == 7 && get(from.row, from.col) == 'r') {
			setPiece(to.row, to.col, 'R');
			King = true;
		}
		else setPiece(to.row, to.col, get(from.row, from.col));

		setPiece(from.row, from.col, '_');

		if (!King)
			if (m.hasMoreMoves()) applyMove(m.getNextMove());
		return jumped;
	}
	
	public String isWinner(){
		for (int row = 0; row < 8; row++)
			for(int col = 0; col < 8; col++) {
				if (board[row][col] == 'b' || board[row][col] == 'B')
					return "";
			}
		return "AI";
	}
	public String convertArray(String state){
		String newState = "";
		char[] array = this.toString().toCharArray();
		char[] charArray = state.toCharArray();
		for (int i = 0, j = 0; i < 72; ) {
			if (array[i] == '.'){
				newState += ".";
				i++;
				continue;
			}else if (Character.isWhitespace(array[i])){
				i++;
				continue;
			}
			switch (charArray[j++]) {
				case '0':
					newState += "_";
					i++;
					break;
				case '1':
					newState += "r";
					i++;
					break;
				case '2':
					newState += "b";
					i++;
					break;
				case '3':
					newState += "R";
					i++;
					break;
				case '4':
					newState += "B";
					i++;
					break;
				default:
					break;
			}
		}
		return newState;
	}
	public void rotateBoard() {
		char[][] rotated = new char [8][8];
		for (int r = 0; r < 8; r++)
			for (int c = 0; c < 8; c++)
				rotated[c][7 - r] = board[r][c];
		board = rotated;
	}
	public static GameState setBoard() {
		return new GameState(
				  ".r.r.r.r"
				+ "r.r.r.r."
				+ ".r.r.r.r"
				+ "_._._._."
				+ "._._._._"
				+ "b.b.b.b."
				+ ".b.b.b.b"
				+ "b.b.b.b.");}
	public void flipBoard() {
		rotateBoard();
		rotateBoard();
		for (int r = 0; r < 8; r++)
			for (int c = 0; c < 8; c++)
				if (board[r][c] == 'r') board[r][c] = 'b';
				else if (board[r][c] == 'R') board[r][c] = 'B';
				else if (board[r][c] == 'b') board[r][c] = 'r';
				else if (board[r][c] == 'B') board[r][c] = 'R';
	}
	public boolean isOnBoard(int row, int col) {
		 
		if(row >= 0 && row < 8 && col >= 0 && col < 8)
			return true;
		return false;
		}
	public char get(int row, int col) {return board[row][col];}
	public void setPiece(int row, int col, char piece) {
		if ( piece == '_'||piece == 'r' || piece == 'R' || piece == 'b' || piece == 'B')
			board[row][col] = piece;
		else
			board[row][col] = '.';
	}
	public int getPlayer(int row, int col) {
		char piece = get(row, col);
		// player 1 for black and player 2 for red. null otherwise
		if (piece == 'b' || piece == 'B') return 1;
		else if (piece == 'r' || piece == 'R') return 2;
		else return 0;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++)
				sb.append(board[r][c]);
			sb.append('\n');
		}
		return sb.toString();
	}
}
