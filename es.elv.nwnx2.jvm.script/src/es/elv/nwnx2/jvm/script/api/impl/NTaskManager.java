package es.elv.nwnx2.jvm.script.api.impl;

import es.elv.nwnx2.jvm.script.api.tasks.BaseTask;
import es.elv.nwnx2.jvm.script.api.tasks.TaskManager;
import es.elv.nwnx2.jvm.script.impl.API;

public class NTaskManager extends TaskManager {
	final NCreature creature;

	public NTaskManager(NCreature creature) {
		this.creature = creature;
	}

	@Override
	public void onTaskStarted(BaseTask task) {
		API.instance().onTaskManagerTaskStarted(creature, task);
	}

	@Override
	public void onTaskCompleted(BaseTask task) {
		API.instance().onTaskManagerTaskCompleted(creature, task);
	}

	@Override
	public void onTaskCancelled(BaseTask task) {
		API.instance().onTaskManagerTaskCancelled(creature, task);
	}

}
