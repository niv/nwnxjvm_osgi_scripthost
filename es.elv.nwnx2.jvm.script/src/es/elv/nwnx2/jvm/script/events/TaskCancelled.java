package es.elv.nwnx2.jvm.script.events;

import org.nwnx.nwnx2.jvm.NWObject;

import es.elv.nwnx2.jvm.hierarchy.events.CoreEvent;
import es.elv.nwnx2.jvm.script.api.tasks.BaseTask;

public class TaskCancelled extends CoreEvent {

	public final NWObject creature;
	public final BaseTask task;

	public TaskCancelled(NWObject gameObject, BaseTask task) {
		this.creature = gameObject;
		this.task = task;
	}

}
