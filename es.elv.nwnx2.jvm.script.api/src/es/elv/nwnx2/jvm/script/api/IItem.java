package es.elv.nwnx2.jvm.script.api;

/**
 * An item in the game.
 */
public interface IItem extends IObject {
	/**
	 * Returns the current owner of this item.
	 * May return null.
	 */
	ICreature getOwner();
}