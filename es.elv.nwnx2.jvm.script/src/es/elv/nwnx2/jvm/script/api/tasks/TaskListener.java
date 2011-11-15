package es.elv.nwnx2.jvm.script.api.tasks;

import es.elv.nwnx2.jvm.script.api.impl.NObject;

public interface TaskListener {
	void taskCompleted(NObject host, BaseTask task);
	void taskCancelled(NObject host, BaseTask task);
}
