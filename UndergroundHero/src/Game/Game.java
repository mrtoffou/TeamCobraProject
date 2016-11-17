package Game;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
//import org.apache.commons.lang3.text.WordUtils;
import java.util.concurrent.TimeUnit;

import Character.Monster;
import Character.Player;
import Item.Item;
import Item.ItemGenerator;
import LogicController.BattleLogic;
import LogicController.MusicLogic;
import LogicController.PuzzleLogic;
import Puzzle.Puzzle;
import Room.Room;
import Room.RoomFactory;

/**
 * This class is responsible for parsing user's inputs and creating new game.
 * @author John, King, Kyle, Matt
 */
public class Game {
	private List<Room> factoryList;
	private List<Item> itemList;
	private Room nextRoom, currentRoom, lockedRoom, returnRoom;
	private Player player;
	private PuzzleLogic puzzleLogic;
	private BattleLogic battleLogic;
	private MusicLogic musicLogic;
	private Scanner input;
	private Function f;
	private int bagIndex;
	private boolean gameRun;

	public Game() {
		input = new Scanner(System.in);
		factoryList = new RoomFactory().getRoomFactoryList();
		itemList = new ItemGenerator().getItemList();
		f = new Function();
		puzzleLogic = new PuzzleLogic(input);
		battleLogic = new BattleLogic(input);
		musicLogic = new MusicLogic("src/sound/menu.wav");
	}

	/**
	 * @method Loads default room and player for new game state.
	 */

	private void createNewGame() {
		gameRun = true;
		player = new Player(100, 100, 3, 3, 2);
		player.setName("HERO");
		player.pickUp(itemList.get(0));
		player.pickUp(itemList.get(2));
		player.pickUp(itemList.get(4));
		player.startingEquip(0);
		player.startingEquip(0);
		currentRoom = factoryList.get(1);

		f.printBox("######################### ROOM " + currentRoom.getName() + " #############################");
		System.out.println(currentRoom.getDescription() + "\n");
	}

	public void menuScreen() {
		displayIntro();
		
		boolean start = true;
		while (start) {
			menuMusic();
			System.out.println("");
			f.printBox("######################## MENU #########################");
			System.out.println("|                 [1] New Game                            |");
			System.out.println("|                 [2] Load Game                           |");
			System.out.println("|                 [3] Gameplay Guide                      |");
			System.out.println("|                 [4] Exit Game                           |");
			f.printBox("#######################################################");
			System.out.println("");
			System.out.print(">> ");
			String userInput = input.nextLine();

			try {
				if (userInput.equals("1")) {
					System.out.print("<< CREATING NEW GAME ");
					f.print("....", 300);
					System.out.println("\n\n");
					f.printBox("############## Type 'HELP' for COMMANDS ##############");
					System.out.println("\n");
					start = false;
					createNewGame();
					floor1Music();
					play();
				}
				else if (userInput.equals("2")) {
					gameRun = true;
					start = false;
					load();
					floor1Music();
					play();
				}
				else if (userInput.equalsIgnoreCase("3")) {
					f.delay(500);
					commandDescription();
				}
				else if (userInput.equals("4")) {
					System.out.println("Exiting game...");
					start = false;
					musicLogic.BGMStop();
					System.exit(0);
				} else {
					System.out.println("Invalid Input.");
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid Input, please try again.");
			}
		}
	}

	/**
	 * @method Main method for initiating game
	 */
	private void play() {
		String userInput;

		boolean menuScreen = true;

		do {

			while (gameRun) {
				System.out.print(">>");
				userInput = input.nextLine();
				parseCommand(userInput);
				System.out.println();
			}

			menuScreen();

		} while (menuScreen);

	}

	/**
	 * @method Responsible for checking valid exits, monsters, and puzzles in
	 *         the current room.
	 * @param direction
	 */
	private void roomLogic(String direction) {
		nextRoom = currentRoom.getNextRoom(direction);
		lockedRoom = nextRoom;

		if (nextRoom == null) {
			System.out.println("Theres no exit that way, try another direction.");
			f.delay(500);
			look();
		}

		else if (factoryList.get(nextRoom.getId()).isLocked()) {
			if (factoryList.get(currentRoom.getId()).getId() == 19) {
				System.out.println("Theres no exit that way, try another direction.");
			}			
			// Initiate puzzle when approaching a lock room
			else if (lockedRoom.getRoomPuzzle() != null) {
				puzzleLogic.initiatePuzzle(lockedRoom, player);
				if (puzzleLogic.getPuzzleSolved()) {
					checkRoomPuzzleLocks(nextRoom.getRoomPuzzle());
					f.printBox("#################### ROOM " + currentRoom.getName() + " ########################");
					System.out.println(currentRoom.getDescription() + "\n");

				}
				if (puzzleLogic.getPuzzleSolved()) {
					factoryList.get(nextRoom.getId()).getRoomPuzzle().setSolved(true);
					currentRoom = lockedRoom;
					iniMonster();

				}
			} else {
				System.out.println("\"The door is locked.\"");
				System.out.println("\"I need to find a way to unlock the door.\"");
				f.delay(500);
				look();
			}
		}

		else {
			currentRoom = nextRoom;
			f.printBox("######################### ROOM " + currentRoom.getName() + " #############################");
			System.out.println(currentRoom.getDescription() + "\n");
			iniMonster();
//			battleLogic.checkFloorMusic(currentRoom);
			iniPuzzle();
//			puzzleLogic.checkFloorMusic(currentRoom);
		}
	}

	public void iniMonster() {
		Monster monster = nextRoom.getRoomMonster();
		// check if roomMonster exists and if not dead
		if (monster != null && !factoryList.get(nextRoom.getId()).getRoomMonster().isDead()) {

			// if boss, spawnrate is 100%
			if (monster.isBoss()) {
				battleLogic.initiateBattle(player, monster);
				battleLogic.checkFloorMusic(currentRoom);
			}

			// check player dead
			if (battleLogic.getWhoseDead() == 0) {
				gameRun = false;
			}

			// check monster dead and set monster dead if boss
			if (battleLogic.getWhoseDead() == 1) {
				if (monster.isBoss())
					factoryList.get(nextRoom.getId()).getRoomMonster().setDead(true);

				if (monster.getId() == 5) {
					displayEnding();
				}
			}

			checkRoomMonsterLocks(monster);

		}

		if (monster != null) {
			//
			monster.setHp(monster.getMaxhp());

			// if common monster, set spawn rate
			if (!monster.isBoss()) {
				double chance = (Math.random() * 100);
				final double SPAWN_RATE = 25.0;

				if (chance < SPAWN_RATE) {
					battleLogic.initiateBattle(player, monster);
					battleLogic.checkFloorMusic(currentRoom);
				}
			}

			// check player dead
			if (battleLogic.getWhoseDead() == 0) {
				gameRun = false;
			}
		}
		
	}

	public void iniPuzzle() {
		Puzzle puzzle = nextRoom.getRoomPuzzle();
		if (puzzle != null && !factoryList.get(nextRoom.getId()).getRoomPuzzle().isSolved()) {
			puzzleLogic.initiatePuzzle(currentRoom, player);
			puzzleLogic.checkFloorMusic(currentRoom);

			if (puzzleLogic.getPuzzleSolved()) {
				checkRoomPuzzleLocks(nextRoom.getRoomPuzzle());
			}

			if (puzzleLogic.getPuzzleSolved()) {
				factoryList.get(nextRoom.getId()).getRoomPuzzle().setSolved(true);
			}
		}
		
	}

	/**
	 * @method Parses user's input's
	 * @param input
	 */
	private void parseCommand(String command) {

		if (validCommandInput(command)) {
			String navInput = command.toUpperCase();
			roomLogic(navInput);
		}

		switch (command.toUpperCase()) {

		case "LOOK":
			look();
			break;

		case "BAG":
			f.delay(300);
			player.openInventory();
			break;

		case "VIEW":
			f.delay(300);
			player.viewEquipment();
			break;

		case "EQUIP":
			f.delay(300);
			equip();
			break;

		case "INFO":
			f.delay(300);
			System.out.println(player.toString());
			break;

		case "USE":
			f.delay(300);
			useItem();
			break;

		case "PICK":
			pick();
			break;

		case "HELP":
			f.delay(300);
			commandDescription();
			break;
			
		case "QUIT":
			gameRun = false;
			break;

		case "SAVE":
			save();

			break;
		case "LOAD":
			load();
			break;

		default:
			break;

		}
	}

	/**
	 * @method Prompts user to use item and removes item from inventory.
	 */
	private void useItem() {
		try {
			player.openInventory();
			System.out.println("Which item do you want to use? (Choose a number)");
			System.out.print(">> ");
			bagIndex = input.nextInt();
			player.useItem(bagIndex);
			input.nextLine();
		} catch (InputMismatchException e) 
		{
			System.out.println("You have put the bag away.");
		}
	}

	/**
	 * @method Displays current room's name, description, and exits.
	 */
	private void look() {
		System.out.println();
		f.printBox("######################### ROOM " + currentRoom.getName() + " ############################");
		System.out.println(currentRoom.getDescription());
		System.out.println("[" + currentRoom.getExits() + "]");
		if (factoryList.get(currentRoom.getId()).getRoomItem() != null && factoryList.get(currentRoom.getId()).getId() != 21) {
			System.out.println("You spotted " + currentRoom.getRoomItem().getName() + " on the ground.");
		}
		if (factoryList.get(currentRoom.getId()).getRoomItem() != null && factoryList.get(currentRoom.getId()).getId() == 21) {
			System.out.println("You spotted " + currentRoom.getRoomItem().getName() + " inside of the ballistic glass.");
		}	
	}

	private void pick() {
		if (factoryList.get(currentRoom.getId()).getRoomItem() != null) 
		{
			System.out.println("You have picked " + currentRoom.getRoomItem().getName() + ".");
			player.pickUp(itemList.get(currentRoom.getRoomItem().getId()));
			factoryList.get(currentRoom.getId()).setRoomItem(null);
		}
		else {
			if(player.checkInventoryKeyItem(itemList.get(6))) {
				System.out.println("You open the case with the ballistic diamond cutter.");
				System.out.println("You have picked " + itemList.get(3) + ".");
				player.pickUp(itemList.get(3));
			}
			else {
				System.out.println("You do not have ballistic diamond cutter to cut this case.");
			}
		}
	}

	/**
	 * @method Prompts user to equip item. If so, remove equipment from
	 *         inventory to player and increase/decrease stats.
	 */
	private void equip() {
		try {
			player.openInventory();
			System.out.println("What item do you want to equip? (Choose a number)");
			System.out.print(">>");
			bagIndex = input.nextInt();
			player.equip(bagIndex);
			input.nextLine();
		} catch (InputMismatchException e) 
		{
			System.out.println("You have exited the equip menu.");
		}
	}

	private void save() {
		// use the class saveLoadData to save values in to binary file
		ResourceData data = new ResourceData();
		data.setRoomArrayNumber(currentRoom.getId());
		System.out.println(currentRoom.getId());
		data.setPlayer(player);
		data.setFactoryList(factoryList);
		try {
			ResourceData.saveGame(data, "UndergroundHero.dat");
			System.out.println("<< SAVE SUCCESSFUL");
		} catch (Exception e) {
			System.out.println("<< ERROR SAVING");
			e.printStackTrace();
		}

	}

	private void load() throws ClassCastException, NullPointerException {
		// use the class saveLoadData to load values in the binary file
		try {
			
			ResourceData data = (ResourceData) ResourceData.loadGame("UndergroundHero.dat");
			if(data != null){
			currentRoom = factoryList.get(data.getRoomArrayNumber());
			
			player = data.getPlayer();
			factoryList = data.getFactoryList();
			System.out.print("<< LOADING ");
			f.print("....\n", 300);
			System.out.println("Loading successful");
			System.out.println();
			look();
			System.out.println();
			}
			else
			{
				System.out.println("There is currently no file to load. \nA new game will be created.");
				f.print("       ", 500);
				System.out.println();
				System.out.print("Please Wait");
				f.print("       ", 500);
				System.out.println();
				System.out.println("New game created.");
				f.print("       ", 500);
				System.out.println();
				
				createNewGame();
			}

		} catch (Exception e) {
			System.out.println("Error loading, A new game will be created.");
			createNewGame();
			e.printStackTrace();
		}
	}

	/**
	 * @method checks for valid navigation command
	 * @param input
	 * @return boolean
	 */
	private boolean validCommandInput(String input) {
		if (input.equalsIgnoreCase("EAST") || input.equalsIgnoreCase("WEST") || input.equalsIgnoreCase("NORTH")
				|| input.equalsIgnoreCase("SOUTH"))
			return true;
		return false;
	}

	/**
	 * @method Opens room lock depending on monster's death.
	 * @param monster
	 */
	private void checkRoomMonsterLocks(Monster monster) {

		if (monster.isDead() == true) {

			switch (monster.getId()) {

			case 0:
				factoryList.get(10).setLocked(false);
				System.out.println("As the Pogo falls, the door to the east became visible.");
				break;
			case 1:
				factoryList.get(19).setLocked(false);
				System.out.println("Room: [" + factoryList.get(19).getName() + "] is now unlocked!");
				break;
			case 4:
				factoryList.get(41).setLocked(false);
				System.out.println("Room: [" + factoryList.get(41).getName() + "] is now unlocked!");
				break;
			default:
				break;

			}
		}
	}

	/**
	 * @method Opens room lock depending on puzzle solved.
	 * @param puzzle
	 */
	private void checkRoomPuzzleLocks(Puzzle puzzle) {

		if(puzzle.isSolved() == true) {
			
			switch(puzzle.getId()) {
				case 3: factoryList.get(14).setLocked(false);
						System.out.println("You have avoided all the bullets and lasers and reached at the end of the room.");
						System.out.println("You spotted a red button and pushed.");
						try {
							System.out.println("");
							TimeUnit.SECONDS.sleep(1);
							System.out.println("COUNT DOWN COMMENCING!");
							TimeUnit.MILLISECONDS.sleep(500);
							System.out.println("5");
							TimeUnit.MILLISECONDS.sleep(500);
							System.out.println("4");
							TimeUnit.MILLISECONDS.sleep(500);
							System.out.println("3");
							TimeUnit.MILLISECONDS.sleep(500);
							System.out.println("2");
							TimeUnit.MILLISECONDS.sleep(500);
							System.out.println("1");
							TimeUnit.MILLISECONDS.sleep(500);
							System.out.println("0");
							TimeUnit.SECONDS.sleep(1);
							System.out.println("DEFENCE SYSTEM SHUTTING DOWN!");
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					break;
				case 4: factoryList.get(18).setLocked(false); 
						System.out.println("You hear the sound of a door unlocking.");
					break;
				case 5: factoryList.get(28).setLocked(false); 
						System.out.println("You sucessfully swinged across the room.");
				try {
					TimeUnit.SECONDS.sleep(1);
					System.out.println("As you landed, you encounter a giant robot");
				} catch (InterruptedException e) 
				{
				}
					break;
				case 6: factoryList.get(29).setLocked(false); 
						System.out.println("You went to the computer and input the chip");
						System.out.println("You hear the sound of a door unlocking.");
					break;
				case 7: factoryList.get(34).setLocked(false); 
						System.out.println("You hear the sound of a door unlocking.");
					break;
				case 8: factoryList.get(41).setLocked(false); 
						System.out.println("You hear the sound of a door unlocking.");
					break;
				default:
					break;
			}
		}
	}

	/**
	 * @method displays list of commands and behavior description
	 */
	private void commandDescription()
	{
		System.out.println("\n");
		f.printBox("################### [NAVIGATION] ###################");
		System.out.println("[NORTH]\tMove North.");
		System.out.println("[EAST]\tMove East.");
		System.out.println("[SOUTH]\tMove South.");
		System.out.println("[WEST]\tMove West.");
		System.out.println("--------------------------------------------------------");
		System.out.println("[LOOK]\tDisplay room description, exits, "
				         + "\n\tand item existing in the room but it will not "
				         + "\n\tshow any hidden exits.");
		System.out.println("[PICK]\tPick up the item in the room");
		System.out.println("--------------------------------------------------------");
		System.out.println("[BAG]\tDisplay items in inventory");
		System.out.println("[EQUIP]\tEquip a weapon or armor");
		System.out.println("[INFO]\tDisplay the player current status");
		System.out.println("[USE]\tUse an item");
		System.out.println("[VIEW]\tView current equipment");
		System.out.println("--------------------------------------------------------");
		System.out.println("[SAVE]\tSave game.");
		System.out.println("[LOAD]\tLoad the latest save file.");
		System.out.println("[HELP]\tView commands");
		System.out.println();
		f.printBox("##################### [BATTLE] #####################");
		System.out.println("[1. ATTACK]           Attack enemy");
		System.out.println("[2. USE ITEM]         Use item");
		System.out.println("[3. DEFEND]           Defend from enemy attack");
		System.out.println("[4. FLEE]             Run from battle");
		System.out.println("--------------------------------------------------------");
		System.out.println("\n\n");
	}
	/**
	 * @method Displays game intro
	 */
	private void displayIntro() {
		System.out.println(
						  "O       o             o                                                      o       o      O                     \n"
						+ "o       O            O                                                      O        O      o                     \n"
						+ "O       o            o                                                      o        o      O                     \n"
						+ "o       o            o                                                      o        OoOooOOo                     \n"
						+ "o       O 'OoOo. .oOoO  .oOo. `OoOo. .oOoO `OoOo. .oOo.  O   o  'OoOo.  .oOoO        o      O  .oOo. `OoOo. .oOo. \n"
						+ "O       O  o   O o   O  OooO'  o     o   O  o     O   o  o   O   o   O  o   O        O      o  OooO'  o     O   o \n"
						+ "`o     O`  O   o O   o  O      O     O   o  O     o   O  O   o   O   o  O   o        o      o  O      O     o   O \n"
						+ " `OoooO`   o   O `OoO`  `OoO'  o     `OoOo  o     `OoO'  `OoO'o  o   O  `OoO'o       o      O  `OoO'  o     `OoO' \n"
						+ "                                         O                                                                        \n"
						+ "                                      OoO'                                                                        \n");
		System.out.println("");
		f.printBox("######################## INTRO ########################");
		System.out.println("You have infiltrated the lair of the infamous "
				+ "\nsuper villain, \"Joe-Ker\". Your only powers are your exceeding wit and skill in combat. "
				+ "\nThere are a total of 4 floors and 42 rooms filled with monsters and puzzles blocking your way. "
				+ "\nTraverse through all floors and beat \"Joe-Ker\" to win the game and SAVE THE WORLD!! "
				+ "\n\nNotice: This game has sound! Please unmute your speakers/headphones for the best experience!"
				+ "\nWe recommend resizing your command line window size to 128x64."
				+ "\n - For Windows users: Right-click the title bar and click 'Properties'"
				+ "\n - Click the 'Layout' tab and adjust 'Window Size'"
				+ "\n - You'll have to restart the game for the changes to take place");

	}

	private void displayEnding() {
		endingMusic();
		f.printBox("You have beaten the game!");
		try {
			System.out.println("");
			TimeUnit.SECONDS.sleep(1);
			f.printBox("================== CREDITS ==================");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|------------- TEAM COBRA PROJECT --------------|");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|.................. John  Lam ..................|");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|................... King Lo ...................|");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|................ Kyle  Cousins ................|");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|............ William 'Matt' Smith .............|");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("============ THANK YOU FOR PLAYING! =============");
			TimeUnit.SECONDS.sleep(1);
			System.out.println("|                                               |");
			TimeUnit.SECONDS.sleep(1);
			f.printBox("================== (C)2016 ==================");
			TimeUnit.SECONDS.sleep(21);
			
			System.exit(0);		//exits application
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		gameRun = false;
	}
	
	private void floor1Music() {
		musicLogic.BGMStop();
		musicLogic = new MusicLogic("src/sound/floor1.wav");
		musicLogic.BGMLoop();
	}
	
	private void floor2Music() {
		musicLogic.BGMStop();
		musicLogic = new MusicLogic("src/sound/floor2.wav");
		musicLogic.BGMLoop();
	}
	
	private void floor3Music() {
		musicLogic.BGMStop();
		musicLogic = new MusicLogic("src/sound/floor3.wav");
		musicLogic.BGMLoop();
	}
	
	private void floor4Music() {
		musicLogic.BGMStop();
		musicLogic = new MusicLogic("src/sound/floor4.wav");
		musicLogic.BGMLoop();
	}

	private void menuMusic() {
		musicLogic.BGMStop();
		musicLogic = new MusicLogic("src/sound/menu.wav");
		musicLogic.BGMLoop();
	}

	private void endingMusic() {
		musicLogic.BGMStop();
		musicLogic = new MusicLogic("src/sound/ending.wav");
		musicLogic.BGMPlay();
	}
	
	public void floorMusicChecker(Room currentRoom){
		if (currentRoom.getId() == 1 || currentRoom.getId() == 9){
			floor1Music();
		}
		else if (currentRoom.getId() == 10 || currentRoom.getId() == 18){
			floor2Music();
		}
		else if (currentRoom.getId() == 19 || currentRoom.getId() == 28){
			floor3Music();
		}
		else if (currentRoom.getId() == 29 || currentRoom.getId() == 41){
			floor4Music();
		}
	}
	
	public Room getCurrentRoom() {
		return this.returnRoom;
	}
	
	// private String wrapText(String longDescription){
	// String shortDesc = WordUtils.wrap(longDescription, 50);
	// return shortDesc;
	// }

}
