

package es.elv.nwnx2.jvm.script.provider.ebean.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import es.elv.nwnx2.jvm.script.VerifiedScript;

public class ScriptManager {
	public static class MappedScript {
		final VerifiedScript<?> vscript;
		final String language;
		final int script;
		final int version;
		final boolean restricted;

		boolean initialized = false;

		public MappedScript(VerifiedScript<?> vscript, int script, int version, String language, boolean restricted) {
			this.vscript = vscript;
			this.script = script;
			this.version = version;
			this.language = language;
			this.restricted = restricted;
		}
	}

	final private Map<Integer, MappedScript> scriptsByVersionId =
		Collections.synchronizedMap(new HashMap<Integer, MappedScript>());
	final private Map<Integer, MappedScript> scriptsByScriptId =
		Collections.synchronizedMap(new HashMap<Integer, MappedScript>());

	public int getVersionIdByVerifiedScript(VerifiedScript<?> script) {
		int versionId = -1;

		for (Entry<Integer, MappedScript> e : getScriptsByVersionId().entrySet()) {
			if (e.getValue().vscript.equals(script)) {
				versionId = e.getValue().version;
				break;
			}
		}

		return versionId;
	}

	public int getScriptIdByVerifiedScript(VerifiedScript<?> script) {
		int scriptId = -1;

		for (Entry<Integer, MappedScript> e : getScriptsByVersionId().entrySet()) {
			if (e.getValue().vscript.equals(script)) {
				scriptId = e.getValue().script;
				break;
			}
		}

		return scriptId;
	}


	public void deleteByScriptId(int scriptId) {
		MappedScript sv = scriptsByScriptId.remove(scriptId);
		if (null == sv) return;
		scriptsByVersionId.remove(sv.version);
	}

	public void deleteByVersionId(int versionId) {
		MappedScript sv = scriptsByVersionId.remove(versionId);
		if (null == sv) return;
		scriptsByScriptId.remove(sv.script);
	}

	public MappedScript getByVersionId(int versionId) {
		return getScriptsByVersionId().get(versionId);
	}

	public MappedScript getByScriptId(int scriptId) {
		return getScriptsByScriptId().get(scriptId);
	}

	public void set(MappedScript script) {
		getScriptsByVersionId().put(script.version, script);
		getScriptsByScriptId().put(script.script, script);
	}

	public Map<Integer, MappedScript> getScriptsByScriptId() {
		return scriptsByScriptId;
	}

	public Map<Integer, MappedScript> getScriptsByVersionId() {
		return scriptsByVersionId;
	}

	public MappedScript getByVerifiedScript(VerifiedScript<?> script) {
		return getScriptsByScriptId().get(getScriptIdByVerifiedScript(script));
	}

}
