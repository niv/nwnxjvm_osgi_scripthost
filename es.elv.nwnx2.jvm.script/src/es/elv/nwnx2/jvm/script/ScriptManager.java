package es.elv.nwnx2.jvm.script;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * Utility class to help script providers track their own
 * mapped scripts easily.
 */
public class ScriptManager<I> {

	public class MappedScript {
		public final VerifiedScript<?> vscript;
		public final String language;
		public final boolean restricted;
		public final I mapObj;

		boolean initialized = false;

		public MappedScript(VerifiedScript<?> vscript, I key, String language, boolean restricted) {
			this.vscript = vscript;
			this.mapObj = key;
			this.language = language;
			this.restricted = restricted;
		}
	}

	public MappedScript createMappedScript(VerifiedScript<?> vscript, I key, String language, boolean restricted) {
		return new MappedScript(vscript, key, language, restricted);
	}

	final private Map<I, MappedScript> scriptsMap =
		Collections.synchronizedMap(new HashMap<I, MappedScript>());

	public I getMapObjByVerifiedScript(VerifiedScript<?> script) {
		I path = null;

		for (Entry<I, MappedScript> e : scriptsMap.entrySet()) {
			if (e.getValue().vscript.equals(script)) {
				path = e.getValue().mapObj;
				break;
			}
		}

		return path;
	}

	public MappedScript deleteByMapObj(I path) {
		return scriptsMap.remove(path);
	}

	public MappedScript getByMapObj(I path) {
		return scriptsMap.get(path);
	}

	public void set(MappedScript script) {
		scriptsMap.put(script.mapObj, script);
	}

	public MappedScript getByVerifiedScript(VerifiedScript<?> script) {
		return scriptsMap.get(getMapObjByVerifiedScript(script));
	}

	public MappedScript getByUUID(UUID uuid) {
		for (Entry<I, MappedScript> e : scriptsMap.entrySet()) {
			if (e.getValue().vscript.getUUID().equals(uuid))
				return e.getValue();
		}
		return null;
	}

}
