package es.elv.nwnx2.jvm.script.events;

import org.nwnx.nwnx2.jvm.NWObject;

import es.elv.nwnx2.jvm.hierarchy.events.CoreEvent;

public class IPC extends CoreEvent {

	public final NWObject from;
	public final NWObject to;
	public final Object message;

	public IPC(NWObject from, NWObject to, Object message) {
		this.from = from;
		this.to = to;
		this.message = message;
	}

}
