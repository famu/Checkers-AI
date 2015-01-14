	public class IllegalMoveException extends Exception {
		

		IllegalMoveException(Move move) {this.move = move;}
		public String getMessage() {
			return "Invalid move: " + move;
		}
		public Move getMove() {return move;}
		private Move move;
	}
