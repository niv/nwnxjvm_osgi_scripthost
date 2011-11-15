package es.elv.nwnx2.jvm.script.impl;

import org.nwnx.nwnx2.jvm.NWObject;
import org.nwnx.nwnx2.jvm.Scheduler;

import es.elv.nwnx2.jvm.hierarchy.bundles.HierarchyService;
import es.elv.nwnx2.jvm.hierarchy.events.CoreEvent;
import es.elv.nwnx2.jvm.script.APIHost;
import es.elv.nwnx2.jvm.script.StorageProvider;
import es.elv.nwnx2.jvm.script.VerifiedScript;
import es.elv.nwnx2.jvm.script.api.IObject;
import es.elv.nwnx2.jvm.script.api.StorageException;
import es.elv.nwnx2.jvm.script.api.impl.NCreature;
import es.elv.nwnx2.jvm.script.api.impl.NObject;
import es.elv.nwnx2.jvm.script.api.tasks.BaseTask;
import es.elv.nwnx2.jvm.script.events.IPC;
import es.elv.nwnx2.jvm.script.events.TaskCancelled;
import es.elv.nwnx2.jvm.script.events.TaskCompleted;
import es.elv.nwnx2.jvm.script.events.TaskStarted;
import es.elv.osgi.base.Autolocate;
import es.elv.osgi.base.SCRHelper;

public class API extends SCRHelper implements APIHost {
	private static API hackyhack;
	
	@Autolocate
	private HierarchyService hs;

	public static API instance() {
		return hackyhack;
	}

	@Override
	protected void load() throws Exception {
		API.hackyhack = this;
	}
	
	@Override
	protected void unload() throws Exception {
		API.hackyhack = null;
	}
	
	public StorageProvider getStorageProvider() throws StorageException {
		StorageProvider s = locateServiceFor(StorageProvider.class);
		if (null == s)
			throw new StorageException("No storage service available.");
		return s;
	}
	
	private IObject self;
	private ScriptHostImpl sh;
	private VerifiedScript<?> script;
	private boolean restricted;
	
	public void setObjectSelf(IObject self) {
		this.self = self;
	}
	public IObject getObjectSelf() {
		return self;
	}
	public ScriptHostImpl getScriptHost() {
		return sh;
	}
	public void setScriptHost(ScriptHostImpl sh) {
		this.sh = sh;
	}
	
	public void setExecutingScript(VerifiedScript<?> script) {
		this.script = script;
	}
	public VerifiedScript<?> getExecutingScript() {
		return script;
	}
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}
	public boolean isRestricted() {
		return restricted;
	}
	
	private void sendSafeEvent(NWObject host, final CoreEvent e) {
		Scheduler.delay(host, 100L, new Runnable() {
			@Override
			public void run() {
				hs.sendEvent(e);
			}
		});
	}

	public void ipc(NObject from, NObject to, Object message) {
		sendSafeEvent(from.getWrapped(), new IPC(from.getWrapped(), to.getWrapped(), message));
	}

	public void onTaskManagerTaskStarted(NCreature creature, BaseTask task) {
		sendSafeEvent(creature.getWrapped(), new TaskStarted(creature.getWrapped(), task));
	}

	public void onTaskManagerTaskCompleted(NCreature creature, BaseTask task) {
		sendSafeEvent(creature.getWrapped(), new TaskCompleted(creature.getWrapped(), task));
	}

	public void onTaskManagerTaskCancelled(NCreature creature, BaseTask task) {
		sendSafeEvent(creature.getWrapped(), new TaskCancelled(creature.getWrapped(), task));
	}
}