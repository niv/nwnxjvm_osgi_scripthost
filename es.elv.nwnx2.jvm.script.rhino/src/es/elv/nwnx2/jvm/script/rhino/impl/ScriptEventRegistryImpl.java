package es.elv.nwnx2.jvm.script.rhino.impl;

import org.mozilla.javascript.Function;

import es.elv.nwnx2.jvm.script.EventHandler;
import es.elv.nwnx2.jvm.script.api.IScriptEventRegistry;

public class ScriptEventRegistryImpl implements IScriptEventRegistry<Function> {
	private final VerifiedRhinoScript script;

	public ScriptEventRegistryImpl(VerifiedRhinoScript script) {
		this.script = script;
	}

	public void set(String eventClass, final Function registerFun) {
		EventHandler<Function> eht = new EventHandler<Function>() {

			@Override
			public Function getHandler() {
				return registerFun;
			}
		};
		script.registerEvent(eventClass, eht);
	}

	@Override
	public void log(Object... message) {
	}
}