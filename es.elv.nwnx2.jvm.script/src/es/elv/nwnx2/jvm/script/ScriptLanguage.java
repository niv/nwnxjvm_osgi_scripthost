package es.elv.nwnx2.jvm.script;

import es.elv.nwnx2.jvm.script.api.IObject;



/**
 * A script host is a interpreter which will manage
 * scripts written in a specific language.
 */
public interface ScriptLanguage<EH, SCRIPT extends VerifiedScript<EH>> {
	/**
	 * Returns a script identifier, which is a unique
	 * string naming this language.
	 */
	String getIdentifier();	
	
	/**
	 * Verifies & optionally compiles the given source.
	 */
	SCRIPT verifyScript(
			ScriptHost sh,
			String source,
			boolean restricted
		) throws Exception;
	
	/**
	 * Executes the given script contained in the given SCRIPT context.
	 * 
	 * Do not call this directly, use the methods provided by a ScriptHost.
	 */
	Object executeScript(
			ScriptHost sh,
			IObject hostObject,
			SCRIPT script
		) throws Exception;

	/**
	 * Execute the script handler for the given event class.
	 * 
	 * Do not call this directly, use the methods provided by a ScriptHost.
	 */
	Object excuteEventHandler(
			ScriptHost sh,
			IObject hostObject,
			SCRIPT script,
			EventHandler<EH> handler,
			Object... va
		) throws Exception;
	

	// void onScriptAttached(SCRIPT script, Set<IObject> hostObject);
	// void onScriptDetached(SCRIPT script, Set<IObject> hostObject);
}