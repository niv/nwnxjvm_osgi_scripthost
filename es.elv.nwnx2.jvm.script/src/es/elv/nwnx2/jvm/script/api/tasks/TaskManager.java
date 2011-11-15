package es.elv.nwnx2.jvm.script.api.tasks;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class TaskManager {
	final private Object mutex = new Object();
	
	private Queue<BaseTask> tasks = new ConcurrentLinkedQueue<BaseTask>();
	private BaseTask current = null;
	
	abstract public void onTaskStarted(BaseTask task);
	abstract public void onTaskCompleted(BaseTask task);
	abstract public void onTaskCancelled(BaseTask task);
	
	public void add(BaseTask task) {
		if (task.isDead())
			throw new TaskDeadException();
		
		synchronized(mutex) {
			tasks.add(task);
			schedule();
		}
	}
	
	public void clear() {
		synchronized (mutex) {
			if (current != null) {
				current.cancel();
				onTaskCancelled(current);
				current = null;
			}
			for (BaseTask task : tasks)
				onTaskCancelled(task);
				
			tasks.clear();
		}
	}
	
	
	public void tick() {
		synchronized(mutex) {
			schedule();
			
			if (current != null)
				current.tick();
			
			schedule();
		}
	}
	
	private void schedule() {
		synchronized(mutex) {
			if (current == null && tasks.size() == 0)
				return;			
			
			if (current != null && current.isComplete()) {
				onTaskCompleted(current);
				current = null;
			}

			if (current != null && current.isCancelled()) {
				onTaskCancelled(current);
				current = null;
			}
			
			if (current == null && tasks.size() > 0) {
				current = tasks.poll();
				onTaskStarted(current);
			}
		}
	}

	public int getTaskCount() {
		synchronized (mutex) {
			return tasks.size() + (current != null ? 1 : 0);
		}
	}
}
