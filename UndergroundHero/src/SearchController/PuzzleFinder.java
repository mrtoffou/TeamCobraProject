package SearchController;

import java.util.ArrayList;

import Generator.PuzzleGenerator;
import Puzzle.Puzzle;
/**
 * This class is responsible for searching puzzle objects by name.
 * @author John
 *
 */
public class PuzzleFinder {
	
	private ArrayList<Puzzle> puzzleList;
	
	public PuzzleFinder(){
		puzzleList = new PuzzleGenerator().getPuzzleList();
	}
	
	/**
	 * @method Searches for Puzzle by name
	 * @param name
	 * @return Puzzle
	 */	
	public Puzzle puzzle(String name) {
		Puzzle puz;
		Puzzle puzzle = null;
		
		for(int i = 0; i < puzzleList.size(); i++){
			
			puz = puzzleList.get(i);
			String puzzleName = puz.getName();
			
			if(puzzleName.equalsIgnoreCase(name)){
				puzzle = puz;
				break;
			}	
		}
		
		return puzzle;
	}

}
