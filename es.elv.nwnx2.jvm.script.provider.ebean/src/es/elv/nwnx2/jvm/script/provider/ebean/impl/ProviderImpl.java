package es.elv.nwnx2.jvm.script.provider.ebean.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.nwnx.nwnx2.jvm.NWObject;

import com.avaje.ebean.SqlUpdate;

import es.elv.nwnx2.jvm.events.game.CreatureHeartbeat;
import es.elv.nwnx2.jvm.events.game.ItemActivated;
import es.elv.nwnx2.jvm.hierarchy.bundles.Every;
import es.elv.nwnx2.jvm.hierarchy.bundles.Feature;
import es.elv.nwnx2.jvm.hierarchy.events.Handler;
import es.elv.nwnx2.jvm.script.ScriptHost;
import es.elv.nwnx2.jvm.script.ScriptHostListener;
import es.elv.nwnx2.jvm.script.ScriptLanguage;
import es.elv.nwnx2.jvm.script.VerifiedScript;
import es.elv.nwnx2.jvm.script.api.IObject;
import es.elv.nwnx2.jvm.script.provider.AggregateView;
import es.elv.nwnx2.jvm.script.provider.LogEntry;
import es.elv.nwnx2.jvm.script.provider.LogEntry.Type;
import es.elv.nwnx2.jvm.script.provider.ebean.impl.ScriptManager.MappedScript;
import es.elv.osgi.base.Autolocate;
import es.elv.osgi.persistence.ebean.PersistenceService;
import es.elv.osgi.persistence.ebean.PersistentModelClassService;

public class ProviderImpl extends Feature implements PersistentModelClassService, ScriptHostListener {
	@Autolocate
	private PersistenceService ps;
	
	@Override
	public Class<?>[] getAnnotatedModels() {
		return new Class<?>[] {
				LogEntry.class, AggregateView.class
		};
	}
	
	@Autolocate
	private ScriptHost sh;
	
	final private ScriptManager manager = new ScriptManager();

	private void rehash() throws Exception {
		List<MappedScript> scriptsToDetach = new LinkedList<MappedScript>();
		
		List<Integer> activeVersionIds = new LinkedList<Integer>();
		for (AggregateView view : ps.get().find(AggregateView.class).findList()) {
			activeVersionIds.add(view.version);
			
			MappedScript mapped = manager.getByVersionId(view.version);
			
			@SuppressWarnings("rawtypes")
			ScriptLanguage lang = sh.findLanguage(view.language);
			
			if (mapped == null) {
				log.debug("New script version appeared: " + view.version);
				@SuppressWarnings("rawtypes")
				VerifiedScript verifyScript = lang.verifyScript(sh, view.source, view.restricted);
				verifyScript.setMetaData("scriptVersionId", view.version);
				verifyScript.setMetaData("scriptScriptId", view.script);
				
				mapped = new MappedScript(verifyScript, view.script, view.version, view.language, view.restricted);
			}
			
			manager.set(mapped);
		}
		
		for (Entry<Integer, MappedScript> e : manager.getScriptsByVersionId().entrySet()) {
			if (activeVersionIds.contains(e.getKey()))
				continue;
			
			log.debug("Script version disappeared: " + e.getKey());
			scriptsToDetach.add(e.getValue());
		}
		
		
		for (MappedScript detach : scriptsToDetach) {
			log.debug("Detaching " + detach);
			sh.detachScriptFromAll(detach.vscript);
			manager.deleteByScriptId(detach.script);
		}
	}
	
	@Override protected void unload() throws Exception {
		for (Entry<Integer, MappedScript> e : manager.getScriptsByScriptId().entrySet()) {
			sh.detachScriptFromAll(e.getValue().vscript);
		}
		
		super.unload();
	}
	
	@Every(interval=30 * 1000) void periodicRehash() {
		try {
			rehash();
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	@Handler void itemActivate(ItemActivated e) {
		int scriptId = e.item.getInt("vscriptid");
		if (0 == scriptId)
			return;
		
		MappedScript s = manager.getByScriptId(scriptId);
		if (s == null)
			return;
		
		Set<NWObject> set = new HashSet<NWObject>();
		set.add(e.item);
		sh.attachScript(s.vscript, set);
	}
	
	@Handler void creatureHB(CreatureHeartbeat e) {
		int scriptId = e.creature.getInt("vscriptid");
		if (0 == scriptId)
			return;
		
		MappedScript s = manager.getByScriptId(scriptId);
		if (s == null)
			return;
		
		Set<NWObject> set = new HashSet<NWObject>();
		set.add(e.creature);
		sh.attachScript(s.vscript, set);
	}
	
	
	private void scriptLog(LogEntry.Type type, int scriptVersion, IObject host, String context, String message) {
		LogEntry entry = new LogEntry();
		entry.oid = host.getObjectId();
		entry.oname =  host.getName(); // NWScript.getName(host, false);
		entry.type = type;
		entry.context = context;
		entry.message = message;
		entry.version = scriptVersion;
		ps.get().save(entry);
	}

	@Override
	public void onManagedObjectCreated(int oid, IObject resolved, VerifiedScript<?> associatedScript) { }

	@Override
	public void onManagedObjectDestroyed(int oid) { }

	@Override
	public void onManagedObjectTick(int oid, IObject resolved) { }

	@Override
	public void onScriptAttached(VerifiedScript<?> script, Set<IObject> host) {
		log.debug("Script " + script + " attached to " + host);
	}

	@Override
	public void onScriptDetached(VerifiedScript<?> script, Set<IObject> host) {
		log.debug("Script " + script + " detached from " + host);
	}

	@Override
	public void onScriptError(VerifiedScript<?> script, IObject hostObject, Exception e) {
		log.error("Script " + script + " generated error on " + hostObject, e);
		
		final Integer versionId = (Integer) script.getMetaData("scriptVersionId");
		if (versionId == null || versionId == 0)
			return;

		manager.deleteByVersionId(versionId);
		
		String s = "UPDATE sh.versions set verified = false where id = :id";
		SqlUpdate update = ps.get().createSqlUpdate(s);
		update.setParameter("id", versionId);
		ps.get().execute(update);

		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		scriptLog(Type.ERROR, versionId, hostObject, sw.toString(), e.toString());
	}

}