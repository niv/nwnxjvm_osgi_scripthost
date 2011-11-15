package es.elv.nwnx2.jvm.script.rhino.impl;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import es.elv.nwnx2.jvm.script.VerifiedScript;

public class VerifiedRhinoScript extends VerifiedScript<Function> {
	
	private final Scriptable scope;
	private final Script compiled;

	public VerifiedRhinoScript(RhinoImpl rhino, Scriptable scope, Script compiled, boolean restricted) {
		super(rhino.getIdentifier(), restricted);
		this.scope = scope;
		this.compiled = compiled;
	}
	
	public Script getCompiled() {
		return compiled;
	}

	public Scriptable getScope() {
		return scope;
	}
	
}