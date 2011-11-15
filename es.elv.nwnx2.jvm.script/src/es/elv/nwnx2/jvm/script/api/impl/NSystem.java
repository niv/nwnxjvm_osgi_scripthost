package es.elv.nwnx2.jvm.script.api.impl;

import es.elv.nwnx2.jvm.script.api.ISystem;

public class NSystem implements ISystem {

	@Override
	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}
}