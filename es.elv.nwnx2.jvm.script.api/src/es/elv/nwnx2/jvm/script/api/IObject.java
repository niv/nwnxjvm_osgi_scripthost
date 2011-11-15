package es.elv.nwnx2.jvm.script.api;


/**
 * The base game object, from which others inherit functionality
 * common to all objects.
 */
public interface IObject extends IPersistency {
	/**
	 * Write something to the script log.
	 */
	public void log(Object... message);
	
	/**
	 * Returns the internal object id. Useful for debugging.
	 * Will change between server restarts.
	 */
	int getObjectId();
	
	/**
	 * Returns true if this Object is valid and still mapped
	 * for this ScriptHost. Accessing any object that isn't mapped
	 * & valid will result in a NoAccessException being thrown.
	 */
	boolean isValid();
	
	/**
	 * Destroy this object, unmap it from the Script Host, and
	 * remove all running script handlers. This is non-reversible.
	 */
	void destroy();
	
	/**
	 * Gets the name of this Object.
	 */
	String getName();
	
	/**
	 * Sets the visible name of this object. 
	 */
	// void setName(String name);
	
	/**
	 * Send a custom IPC message to another object.
	 * The target object will receive the "script.ipc" event.
	 */
	void ipc(IObject target, Object message);
	
	/**
	 * Will return true if the currently-running script has
	 * full access to this object. Accessing any object that
	 * the running script has no access to will result in a
	 * NoAccessException being thrown.
	 */
	boolean mayAccess();
	
	/**
	 * Will return the distance to other, in fraction of meters.
	 * Will return for zero if the given object is not in perception
	 * range.
	 */
	float distanceTo(IObject other);
}