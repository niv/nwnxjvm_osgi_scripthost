package es.elv.nwnx2.jvm.script.api.impl;

import org.nwnx.nwnx2.jvm.NWScript;

import es.elv.nwnx2.jvm.script.api.ICreature;
import es.elv.nwnx2.jvm.script.api.IItem;
import es.elv.nwnx2.jvm.script.impl.API;

public class NItem extends NObject implements IItem {

	public NItem(int oid) {
		super(oid);
	}


	@Override
	public ICreature getOwner() {
		return resolve(ICreature.class, NWScript.getItemPossessor(getWrapped()));
	}
	
	
	@Override
	public boolean mayAccess() {
		if (super.mayAccess())
			return true;
		
		else
			return API.instance().getObjectSelf().getObjectId() ==
				getOwner().getObjectId();
	}
}
