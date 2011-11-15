package es.elv.nwnx2.jvm.script.api.tasks;

import org.nwnx.nwnx2.jvm.NWObject;
import org.nwnx.nwnx2.jvm.NWScript;

import es.elv.nwnx2.jvm.script.api.ICreature;

/**
 */
public abstract class TaskableAction extends BaseTask {
	private final int taskAction;

	protected TaskableAction(int taskAction) {
		super();
		this.taskAction = taskAction;
	}
	
	private NWObject host;
	
	protected NWObject getHostObject() {
		return host;
	}
	
	@Override
	final public void setup(ICreature host, Object... parameters) {
		this.host = new NWObject(host.getObjectId());
		setupAction(host, parameters);
		doAction();
	}

	@Override
	final public void tick() {
		if (!getHostObject().valid()) {
			System.out.println("host invalid, stopping ticking");
			cancel();
			return;
		}

		int currentAction = NWScript.getCurrentAction(getHostObject());
		if (currentAction != taskAction && isActionPossible()) {
			System.out.println("current action != taskAction, resetting");
			doAction();
		}
	}
	
	@Override
	final public void cancel() {
		super.cancel();
		System.out.println("Cancel");
		stopDoingAction();
	}
	
	protected abstract boolean isActionPossible();
	protected abstract void setupAction(ICreature host, Object... parameters);
	protected abstract void doAction();
	protected abstract void stopDoingAction();
}