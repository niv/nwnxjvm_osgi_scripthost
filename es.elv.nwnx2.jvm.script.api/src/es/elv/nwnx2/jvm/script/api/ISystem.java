package es.elv.nwnx2.jvm.script.api;

/**
 * The System interface.
 */
public interface ISystem {
	/**
	 * Returns the current serverside unix time, with milliseconds.
	 * Works just like System.currentTimeMillis() in Java.
	 */
	public long currentTimeMillis();
}
