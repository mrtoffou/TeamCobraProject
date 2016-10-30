package Game;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Character.Monster;
import Character.Player;
import Item.Item;
import Room.Room;

/**
 * Class name: saveLoadData.java
 *
 * This class is a class that implements Serializable interface.
 * The class stores the objects values in to binary.
 */


public class saveLoadData implements java.io.Serializable
{

	private static final long serialVersionUID = 1L;
	private Player player;
	private Monster monster;
	private List<Item> item;
	private List<Item> equipment;
	private int itemArray;
	private int equipmentArray;
	private int roomArrayNumber;
	/**
	 * @return the player
	 */
	public Player getPlayer()
	{
		return player;
	}
	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	/**
	 * @return the monster
	 */
	public Monster getMonster()
	{
		return monster;
	}
	/**
	 * @param monster the monster to set
	 */
	public void setMonster(Monster monster)
	{
		this.monster = monster;
	}
	/**
	 * @return the item
	 */
	public List<Item> getItem()
	{
		return item;
	}
	/**
	 * @param itemList the item to set
	 */
	public void setItem(List<Item> itemList)
	{
		this.item = itemList;
	}
	/**
	 * @return the equipment
	 */
	public List<Item> getEquipment()
	{
		return equipment;
	}
	/**
	 * @param equipmentList the equipment to set
	 */
	public void setEquipment(List<Item> equipmentList)
	{
		this.equipment = equipmentList;
	}
	/**
	 * @return the itemArray
	 */
	public int getItemArray()
	{
		return itemArray;
	}
	/**
	 * @param itemArray the itemArray to set
	 */
	public void setItemArray(int itemArray)
	{
		this.itemArray = itemArray;
	}
	/**
	 * @return the equipmentArray
	 */
	public int getEquipmentArray()
	{
		return equipmentArray;
	}
	/**
	 * @param equipmentArray the equipmentArray to set
	 */
	public void setEquipmentArray(int equipmentArray)
	{
		this.equipmentArray = equipmentArray;
	}
	/**
	 * @return the roomArrayNumber
	 */
	public int getRoomArrayNumber()
	{
		return roomArrayNumber;
	}
	/**
	 * @param roomArrayNumber the roomArrayNumber to set
	 */
	public void setRoomArrayNumber(int roomArrayNumber)
	{
		this.roomArrayNumber = roomArrayNumber;
	}

	//will add more once game comes near completion
	
}
	