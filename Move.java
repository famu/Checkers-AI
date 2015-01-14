public class Move {
		
		public Move(Location location, Move next) 
		{this.location = location; this.nextMove = next;}
		public Move(Location from, Location to) 
		{this(from, new Move(to, (Move)null));}
		public Move getFirstMove() 
		{return new Move(from(), to());}
		public Move getNextMove() 
		{return nextMove;}	
		public Location from() 
		{return location;}
		public Location to() 
		{return nextMove.location;}
		public boolean hasMoreMoves() 
		{return nextMove.nextMove != null;}
		public String toString() 
		{return from() + " - " + to();}
		private Location location;
		private Move nextMove;
	}
