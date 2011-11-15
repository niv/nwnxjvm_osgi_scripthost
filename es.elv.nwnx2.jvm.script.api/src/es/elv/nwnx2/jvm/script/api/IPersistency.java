package es.elv.nwnx2.jvm.script.api;

public interface IPersistency {
	
	/**
	 * Store the given object persistently between
	 * server restarts, keyed to this host object.
	 * You can store any valid javascript object, including
	 * arrays, but not runtime-specifics (like game object references).
	 * 
	 * The running script may be subject to storage quota,
	 * and over-quota will result in a StorageException being thrown.
	 * 
	 * If persistent storage is not available, a StorageException will
	 * be thrown.
	 */
	void set(String key, Object value) throws StorageException;
	
	/**
	 * Retrieve the given object from persistent storage.
	 * Will return null when not found.
	 * 
	 * If persistent storage is not available, a StorageException will
	 * be thrown.
	 */
	Object get(String key) throws StorageException;
}
