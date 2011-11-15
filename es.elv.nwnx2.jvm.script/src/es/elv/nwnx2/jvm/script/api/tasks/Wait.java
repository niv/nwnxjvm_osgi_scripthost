package es.elv.nwnx2.jvm.script.api.tasks;

import org.nwnx.nwnx2.jvm.NWScript;
import org.nwnx.nwnx2.jvm.Scheduler;
import org.nwnx.nwnx2.jvm.constants.Action;

import es.elv.nwnx2.jvm.script.api.ICreature;

/**
 * Makes the creature follow another.
 * @author elven
 *
 */
public class Wait extends TaskableAction {

	public Wait() {
		super(Action.WAIT);
	}
	
	private long waitMS;
	private long startTime;
	
	@Override
	public void setupAction(ICreature host, Object... parameters) {
		waitMS = (Long) parameters[0];
		System.out.println(host + " waits for " + waitMS);
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void doAction() {
		if (System.currentTimeMillis() >= (startTime + waitMS)) {
			complete();
			return;
		}
		
		System.out.println("doAction, wait");
		Scheduler.assign(getHostObject(), new Runnable() {
			@Override public void run() {
				NWScript.actionWait(((float) waitMS) / 1000);
			}
		});
	}

	@Override
	public void stopDoingAction() {
		System.out.println("stopDoingAction");
		NWScript.clearAllActions(false);
	}

	@Override
	protected boolean isActionPossible() {
		return !NWScript.getIsInCombat(getHostObject());
	}
}
