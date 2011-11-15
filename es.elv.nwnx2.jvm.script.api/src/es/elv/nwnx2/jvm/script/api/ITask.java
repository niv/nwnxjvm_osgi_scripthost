package es.elv.nwnx2.jvm.script.api;

/**
 * A Task, which Creatures can execute.
 */
public interface ITask {
	/**
	 * Will return true if this task was already completed.
	 * Note that you cannot actually mark a task as completed yourself;
	 * the task-specific implementation will do that. If you want
	 * to abort the currently-running task, use cancel().
	 */
	boolean isComplete();
	
	/**
	 * Returns true if this task is cancelled/aborted.
	 */
	boolean isCancelled();
	
	/**
	 * Returns true if this task is inactive - either completed, or aborted.
	 * Dead tasks cannot be restarted.
	 */
	boolean isDead();
	
	/**
	 * Cancel this task. This will remove it from the current task queue,
	 * and notify all callbacks/listeners (if any).
	 */
	void cancel();
	
	/**
	 * Gets the number of ms this task was/is active.
	 */
	long getRuntime();
}