package es.elv.nwnx2.jvm.script;

import java.util.Set;

import es.elv.nwnx2.jvm.script.api.IObject;

public interface ScriptHostListener {
	void onManagedObjectCreated(int oid, IObject resolved, VerifiedScript<?> associatedScript);
	void onManagedObjectTick(int oid, IObject resolved);
	void onManagedObjectDestroyed(int oid);
	
	
	/**
	 * Called when a script generated an exception and is being disabled.
	 * Will be followed by a onScriptDetached with all objects attached
	 * to the error-throwing script.
	 */
	void onScriptError(
			VerifiedScript<?> script,
			IObject hostObject,
			Exception e
		);
	
	void onScriptAttached(VerifiedScript<?> script, Set<IObject> host);
	void onScriptDetached(VerifiedScript<?> script, Set<IObject> host);
}