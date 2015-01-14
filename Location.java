public class Location {

		public String toString() {
		return "[" + row + ", " + col + "]";
	}
	public Location(int row, int col) 
	{
		this.row = row; this.col = col;
	}
	public int row;
	public int col;
}
