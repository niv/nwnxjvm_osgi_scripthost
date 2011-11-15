package es.elv.nwnx2.jvm.script;

import java.util.Map;
import java.util.Set;

import org.nwnx.nwnx2.jvm.NWObject;

import es.elv.nwnx2.jvm.script.api.IObject;


public interface ScriptHost {
	ScriptLanguage<?,?> findLanguage(String identifier) throws LanguageNotFoundException;

	void handleObjectEvent(final NWObject objSelf, final String eventClass, final Object... va);
	
	IObject getMappedObjectFor(int oid);
	void adviseDestroyManagedObject(int oid);
	
	void attachScript(VerifiedScript<?> script, Set<NWObject> host);
	void detachScript(VerifiedScript<?> script, Set<NWObject> host);
	void detachScriptFromAll(VerifiedScript<?> script);

	Map<VerifiedScript<?>, Set<NWObject>> getVerifiedScriptMap();
	
	Set<IObject> getMappedObjects();
}