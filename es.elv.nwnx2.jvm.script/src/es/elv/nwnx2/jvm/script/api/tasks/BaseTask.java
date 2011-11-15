package es.elv.nwnx2.jvm.script.api.tasks;

import es.elv.nwnx2.jvm.script.api.ICreature;
import es.elv.nwnx2.jvm.script.api.ITask;

/**
 * A Task to be executed by a ingame-object.
 * 
 * Will be performed until completed, or completion
 * not possible.
 */
abstract public class BaseTask implements ITask {
	final private long start;
	private boolean completed;
	private boolean cancelled;
	
	public BaseTask() {
		start = System.currentTimeMillis();
	}
	
	abstract public void setup(ICreature host, Object... parameters);
	
	abstract public void tick();
	
	@Override
	final public long getRuntime() {
		return System.currentTimeMillis() - start;
	}
	
	final protected void complete() {
		if (isDead()) return;
		completed = true;
	}
	
	@Override
	public void cancel() {
		if (isDead()) return;
		cancelled = true;
	}
	
	@Override
	final public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	final public boolean isComplete() {
		return completed;
	}
	
	@Override
	final public boolean isDead() {
		return cancelled || completed;
	}
}
