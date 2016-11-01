package Room;

import Puzzle.*;

public class RoomPuzzle extends Room{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6453134434802104009L;

	public RoomPuzzle(int id, String name, String description, String exits, boolean locked, Puzzle roomPuzzle) {
		super(id, name, description, exits, locked);
		this.roomPuzzle = roomPuzzle;
	}

}
