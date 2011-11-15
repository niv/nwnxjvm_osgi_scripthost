package es.elv.nwnx2.jvm.script.api.impl;

import java.lang.reflect.Array;

import org.nwnx.nwnx2.jvm.NWObject;
import org.nwnx.nwnx2.jvm.NWScript;
import org.nwnx.nwnx2.jvm.NWVector;
import org.nwnx.nwnx2.jvm.Scheduler;
import org.nwnx.nwnx2.jvm.constants.ObjectType;
import org.nwnx.nwnx2.jvm.constants.Shape;
import org.nwnx.nwnx2.jvm.constants.Talkvolume;

import es.elv.nwnx2.jvm.script.api.ICreature;
import es.elv.nwnx2.jvm.script.api.IItem;
import es.elv.nwnx2.jvm.script.api.ILocation;
import es.elv.nwnx2.jvm.script.api.IObject;
import es.elv.nwnx2.jvm.script.api.ITask;
import es.elv.nwnx2.jvm.script.api.tasks.Follow;
import es.elv.nwnx2.jvm.script.api.tasks.TaskManager;


public class NCreature extends NObject implements ICreature {
	final private TaskManager taskManager;

	public NCreature(int oid) {
		super(oid);
		taskManager = new NTaskManager(this);
	}
	
	private void talk(final String message, final int tv) {
		final NWObject wrapped = getWrapped();
		Scheduler.delay(wrapped, 100, new Runnable() {
			@Override
			public void run() {				
				NWScript.speakString(message, tv);
			}			
		});
	}

	public void say(final String message) {
		checkAccess();
		talk(message, Talkvolume.TALK);
	}

	@Override
	public void whisper(String message) {
		checkAccess();
		talk(message, Talkvolume.WHISPER);
	}
	
	@Override
	public void clear() {
		checkAccess();
		
		getTaskManager().clear();
		
		Scheduler.assign(getWrapped(), new Runnable() {
			@Override public void run() {
				NWScript.clearAllActions(false);
			}
			
		});
	}

	@Override
	public int currentTasks() {
		checkAccess();
		
		return getTaskManager().getTaskCount();
	}

	@Override
	public ITask taskFollow(ICreature other, float followDistance) {
		checkAccess();
		
		Follow follow = new Follow();
		follow.setup(this, other, followDistance);
		getTaskManager().add(follow);
		return follow;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	@Override
	public boolean isPlayer() {
		return NWScript.getIsPC(getWrapped());
	}

	@Override
	public ITask[] taskList() {
		return null;
	}

	@Override
	public boolean sees(ICreature other) {
		return NWScript.getObjectSeen(((NObject) other).getWrapped(), getWrapped());
	}
	
	private <T extends IObject> T[] getNearObjects(Class<T> classOf,
			int typeMask, float distance) {

		if (distance < 1)
			distance = 1;
		if (distance > 30)
			distance = 30;
		
		NWObject[] objectsInShape = NWScript.getObjectsInShape(
				Shape.SPHERE, distance, NWScript.getLocation(getWrapped()),
				true, typeMask, new NWVector(0, 0, 0));
		
		@SuppressWarnings("unchecked")
		T[] ret = (T[]) Array.newInstance(classOf, objectsInShape.length);
		for (int i = 0; i < ret.length; i++)
			ret[i] = resolve(classOf, objectsInShape[i]);
		return ret;
	}

	@Override
	public ICreature[] getPerceivedCreatures(float distance) {
		return getNearObjects(ICreature.class, ObjectType.CREATURE, distance);
	}

	@Override
	public IItem[] getVisibleItems(float distance) {
		return getNearObjects(IItem.class, ObjectType.ITEM, distance);
	}

	@Override
	public ITask taskEquipItem(IItem item) {
		return null;
	}

	@Override
	public ITask taskUnequipItem(IItem item) {
		return null;
	}

	@Override
	public ITask taskPickupItem(IItem item) {
		return null;
	}

	@Override
	public IItem[] getInventoryItems() {
		return null;
	}

	@Override
	public ILocation getLocation() {
		return resolve(NWScript.getLocation(getWrapped()));
	}
}
