package es.elv.nwnx2.jvm.script.api.tasks;

import org.nwnx.nwnx2.jvm.NWObject;
import org.nwnx.nwnx2.jvm.NWScript;
import org.nwnx.nwnx2.jvm.Scheduler;
import org.nwnx.nwnx2.jvm.constants.Action;

import es.elv.nwnx2.jvm.script.api.ICreature;
import es.elv.nwnx2.jvm.script.api.IObject;

/**
 * Makes the creature follow another.
 * @author elven
 *
 */
public class Follow extends TaskableAction {

	public Follow() {
		super(Action.FOLLOW);
	}
	
	private NWObject toFollow;
	private float followDistance;
	
	@Override
	public void setupAction(ICreature host, Object... parameters) {
		toFollow = new NWObject(((IObject) parameters[0]).getObjectId());
		followDistance = (Float) parameters[1];
		System.out.println(host + " follows " + toFollow);
	}

	@Override
	protected void doAction() {
		System.out.println("doAction, forceFollow");
		Scheduler.assign(getHostObject(), new Runnable() {
			@Override public void run() {
				NWScript.clearAllActions(false);
				NWScript.actionForceFollowObject(toFollow, followDistance);
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
