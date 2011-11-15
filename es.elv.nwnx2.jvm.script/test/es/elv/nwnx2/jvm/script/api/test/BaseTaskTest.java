package es.elv.nwnx2.jvm.script.api.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import es.elv.nwnx2.jvm.script.api.ICreature;
import es.elv.nwnx2.jvm.script.api.impl.NCreature;
import es.elv.nwnx2.jvm.script.api.impl.NTaskManager;
import es.elv.nwnx2.jvm.script.api.tasks.BaseTask;

public class BaseTaskTest {
	private NCreature creature;
	private NTaskManager ntm;
	private TestTask task;
	
	public static class TestTask extends BaseTask {
		public int tickCount = 0;
		
		@Override
		public void setup(ICreature host, Object... parameters) {
		}

		@Override
		public void tick() {
			tickCount++;
			if (tickCount >= 3)
				complete();
		}
	}
	
	@Before
	public void setup() {
		creature = new NCreature(0);
		ntm = new NTaskManager(creature);
		task = new TestTask();
		ntm.add(task);
	}
	
	@Test
	public void testTick() {
		assertEquals(task.tickCount, 0);
		ntm.tick();
		assertEquals(task.tickCount, 1);
		ntm.tick();
		assertEquals(task.tickCount, 2);
	}
	

	@Test
	public void testCompletion() {
		ntm.tick();
		ntm.tick();
		assertFalse(task.isComplete());
		ntm.tick();
		assertTrue(task.isComplete());
	}
	
	@Test
	public void testCancelable() {
		ntm.tick();
		ntm.tick();
		task.cancel();
		assertTrue(task.isDead());
		assertTrue(task.isCancelled());
		assertFalse(task.isComplete());
		ntm.tick();
	}
	
	@Test
	public void testTasksInSequence() {
		BaseTask task2 = new TestTask();
		ntm.add(task2);
		
		assertEquals(ntm.getTaskCount(), 2);
		
		ntm.tick(); ntm.tick(); ntm.tick();
		assertTrue(task.isComplete());
		assertEquals(ntm.getTaskCount(), 1);
		ntm.tick(); ntm.tick(); ntm.tick();
		assertEquals(ntm.getTaskCount(), 0);
		ntm.tick(); 
		assertTrue(task2.isComplete());
	}
}
