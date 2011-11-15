package es.elv.nwnx2.jvm.script.api.impl;

import org.nwnx.nwnx2.jvm.NWScript;

import es.elv.nwnx2.jvm.script.api.IArea;
import es.elv.nwnx2.jvm.script.api.NoAccessException;

public class NArea extends NObject implements IArea {

	public NArea(int oid) {
		super(oid);
	}

	@Override
	public int getWeather() {
		checkAccess();
		
		return NWScript.getWeather(getWrapped());
	}

	@Override
	protected void checkAccess() throws NoAccessException {
		super.checkAccess();
	}
}
