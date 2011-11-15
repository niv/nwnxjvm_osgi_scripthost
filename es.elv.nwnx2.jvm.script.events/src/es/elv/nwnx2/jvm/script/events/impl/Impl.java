package es.elv.nwnx2.jvm.script.events.impl;

import org.nwnx.nwnx2.jvm.NWObject;
import org.nwnx.nwnx2.jvm.NWScript;

import es.elv.nwnx2.jvm.events.chatlistener.CreatureChatTalkHeard;
import es.elv.nwnx2.jvm.events.game.CreatureDeath;
import es.elv.nwnx2.jvm.events.game.CreatureHears;
import es.elv.nwnx2.jvm.events.game.CreatureNoLongerHears;
import es.elv.nwnx2.jvm.events.game.CreatureNoLongerSees;
import es.elv.nwnx2.jvm.events.game.CreatureSees;
import es.elv.nwnx2.jvm.events.game.ItemActivated;
import es.elv.nwnx2.jvm.hierarchy.PlayerCreature;
import es.elv.nwnx2.jvm.hierarchy.bundles.Feature;
import es.elv.nwnx2.jvm.hierarchy.events.Handler;
import es.elv.nwnx2.jvm.script.ScriptHost;
import es.elv.nwnx2.jvm.script.ScriptableEventProvider;
import es.elv.nwnx2.jvm.script.events.IPC;
import es.elv.nwnx2.jvm.script.events.TaskCancelled;
import es.elv.nwnx2.jvm.script.events.TaskCompleted;
import es.elv.nwnx2.jvm.script.events.TaskStarted;
import es.elv.osgi.base.Autolocate;

public class Impl extends Feature implements ScriptableEventProvider {

	@Autolocate
	private ScriptHost sh;

	private void handleObjectEvent(NWObject objSelf, String eventClass, Object... va) {
		sh.handleObjectEvent(objSelf, eventClass, va);
	}
	

	@Override
	public String[] getProvidedEvents() {
		return new String[] {
				"script.ipc",
				
				"creature.task.started",
				"creature.task.completed",
				"creature.task.cancelled",
				
				"creature.chat.talk.heard",
				
				"creature.hears",
				"creature.hearsnot",
				"creature.sees",
				"creature.seesnot",
				"creature.death",
				
				"item.activate",
		};
	}

	@Handler void creatureChatTalkHeard(CreatureChatTalkHeard e) {
		if (NWScript.getIsDM(e.speaker))
			return;
		
		if (e.text.startsWith("/") || e.text.startsWith(",") || e.text.startsWith("."))
			return;
		
		handleObjectEvent(e.listener, "creature.chat.talk.heard", e.speaker, e.text);
	}

	@Handler void scriptIPC(IPC e) {
		if (e.to instanceof PlayerCreature)
			((PlayerCreature) e.to).message("IPC from " + e.from + ": " + e.message);
		else
			handleObjectEvent(e.to, "script.ipc", e.from, e.message);
	}
	@Handler void creatureTaskStarted(TaskStarted e) {
		handleObjectEvent(e.creature, "creature.task.started", e.task);
	}
	@Handler void creatureTaskCompleted(TaskCompleted e) {
		handleObjectEvent(e.creature, "creature.task.completed", e.task);
	}
	@Handler void creatureTaskCancelled(TaskCancelled e) {
		handleObjectEvent(e.creature, "creature.task.cancelled", e.task);
	}
	
	@Handler void creatureHears(CreatureHears e) {
		handleObjectEvent(e.creature, "creature.hears", e.perceived);
	}
	@Handler void creatureNoLongerHears(CreatureNoLongerHears e) {
		handleObjectEvent(e.creature, "creature.hearsnot", e.perceived);
	}
	@Handler void creatureSees(CreatureSees e) {
		handleObjectEvent(e.creature, "creature.sees", e.perceived);
	}
	@Handler void creatureNoLongerSees(CreatureNoLongerSees e) {
		handleObjectEvent(e.creature, "creature.seesnot", e.perceived);
	}
	@Handler void creatureDeath(CreatureDeath e) {
		handleObjectEvent(e.creature, "creature.death", e.killer);
	}
	/*
	@Handler void creatureAttack(CreatureAttacked e) {
		handleAiboEvent(e.creature, "creature.attack", e.attacker, e.attackMode, e.attackType);
	}
	@Handler void creatureDamaged(CreatureDamaged e) {
		handleAiboEvent(e.creature, "creature.damaged");
	}
	@Handler void creatureDialogue(CreatureConversation e) {
		handleAiboEvent(e.creature, "creature.conv");
	}
	@Handler void creatureDisturbed(CreatureDisturbed e) {
		handleAiboEvent(e.creature, "creature.disturbed");
	}
	@Handler void creatureEndRound(CreatureEndOfRound e) {
		handleAiboEvent(e.creature, "creature.attack");
	}
	@Handler void creatureHeartbeat(CreatureHeartbeat e) {
		handleAiboEvent(e.creature, "creature.attack");
	}
	@Handler void creatureBlocked(CreatureBlocked e) {
		handleAiboEvent(e.creature, "creature.attack");
	}
	@Handler void creatureRested(CreatureRest e) {
		handleAiboEvent(e.creature, "creature.rest");
	}
	@Handler void creatureSpawn(CreatureSpawn e) {
		handleObjectEvent(e.creature, "creature.spawn");
	}
	*/
	
	
	@Handler void itemActivated(ItemActivated e) {
		handleObjectEvent(e.item, "item.activate", e.on);
	}
}
