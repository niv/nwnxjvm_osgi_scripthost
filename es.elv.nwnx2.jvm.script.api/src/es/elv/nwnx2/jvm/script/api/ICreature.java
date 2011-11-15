package es.elv.nwnx2.jvm.script.api;

/**
 * A Creature in the game. This includes NPCs, all monsters, and
 * player characters, but NOT dungeon masters.
 */
public interface ICreature extends IObject {
	/**
	 * Forgets all tasks, stops all timers, and
	 * clears any associated local state.
	 */
	void clear();
	
	/**
	 * Returns the number of current task list of this Creature.
	 */
	int currentTasks();
	
	/**
	 * Return the task list of this Creature. May be empty.
	 * The active task is at the head of the list.
	 */
	ITask[] taskList();
	
	/**
	 * Return true if this Creature is a player character.
	 */
	boolean isPlayer();
	
	/**
	 * Makes this creature say the given text immediately.
	 */
	void say(String message);
	
	/**
	 * Makes this creature whisper the given text immediately.
	 */
	void whisper(String message);
	
	/**
	 * Will return true if this Creature can see the Creature other.
	 */
	boolean sees(ICreature other);
	
	/**
	 * Returns all perceived Creatures nearby (heard, seen).
	 * Will not return placeables, items, objects.
	 * 
	 * Distance: the distance in meters. Limited to 0-30.
	 */
	ICreature[] getPerceivedCreatures(float distance);
	
	/**
	 * Returns the location of this Creature. 
	 */
	ILocation getLocation();	
	
	/*
	 * Movement
	 */
	
	/**
	 * Makes this Creature follow other, with the given distance.
	 * 
	 * Will enqueue as a task.
	 */
	ITask taskFollow(ICreature other, float followDistance);
	
	/**
	 * Task this creature to walk to the given waypoint.
	 */
	// void taskWalk(IWaypoint whereTo);
	
	/**
	 * Task this creature to run to the given location.
	 */
	// void taskRun(IWaypoint whereTo);
	
	/*
	 * Items & Inventory
	 */
	
	/**
	 * Makes this Creature equip the given item. The item must be in this
	 * Creatures inventory and be script-accessible.
	 */
	ITask taskEquipItem(IItem item);
	
	/**
	 * Makes this Creature unequip the given item. The item must be in
	 * this Creatures inventory, be script-accessible, and currently equipped.
	 */
	ITask taskUnequipItem(IItem item);
	
	/**
	 * Returns all Items nearby. Will only return items this
	 * Creature can see.
	 * 
	 * Distance: the distance in meters. Limited from 1 to 30, inclusive.
	 */
	IItem[] getVisibleItems(float distance);
	
	/**
	 * Makes this Creature pick up the given item.
	 * Will only work for items currently in visible range (i.e. via getVisibleItems())
	 */
	ITask taskPickupItem(IItem item);
	
	/**
	 * Returns all (script-accessible) items currently in this Creatures inventory.
	 */
	IItem[] getInventoryItems();
	
	

}