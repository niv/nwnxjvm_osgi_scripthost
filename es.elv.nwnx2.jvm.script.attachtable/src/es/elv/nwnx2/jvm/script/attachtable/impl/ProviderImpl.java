package es.elv.nwnx2.jvm.script.attachtable.impl;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nwnx.nwnx2.jvm.NWObject;
import org.nwnx.nwnx2.jvm.NWScript;

import es.elv.nwnx2.jvm.script.ScriptHostListener;
import es.elv.nwnx2.jvm.script.VerifiedScript;
import es.elv.nwnx2.jvm.script.api.IObject;
import es.elv.nwnx2.jvm.script.attachtable.model.Association;
import es.elv.osgi.base.Autolocate;
import es.elv.osgi.base.SCRHelper;
import es.elv.osgi.persistence.ebean.PersistenceService;
import es.elv.osgi.persistence.ebean.PersistentModelClassService;

public class ProviderImpl extends SCRHelper implements PersistentModelClassService, ScriptHostListener {
	@Autolocate
	private PersistenceService ps;
	
	@Override
	public Class<?>[] getAnnotatedModels() {
		return new Class<?>[] {
				Association.class
		};
	}

	void touchAssociation(int oid) {
		Association eq = ps.get().find(Association.class).where().eq("oid", oid).findUnique();
		if (null == eq) {
			log.warn("touchAssociation() null for " + oid);
			return;
		}
		eq.setTouched_on(new Timestamp(System.currentTimeMillis()));
		ps.get().save(eq);
	}

	@Override
	public void onManagedObjectCreated(int oid, IObject resolved, VerifiedScript<?> associatedScript) {}

	@Override
	public void onManagedObjectDestroyed(int oid) {}

	@Override
	public void onManagedObjectTick(int oid, IObject resolved) {}

	@Override
	public void onScriptAttached(VerifiedScript<?> script, Set<IObject> host) {
		final Integer versionId = (Integer) script.getMetaData("scriptVersionId");
		if (versionId == null || versionId == 0)
			return;
		
		for (IObject o : host) {
			int oid = o.getObjectId();
			NWObject obj = new NWObject(oid);
			
			Association assoc = new Association();
			assoc.created_on = new Timestamp(System.currentTimeMillis());
			assoc.setTouched_on(new Timestamp(System.currentTimeMillis()));
			assoc.objname = NWScript.getName(obj, false);
			assoc.oid = oid;
			assoc.version = versionId;
			assoc.otype = NWScript.getObjectType(obj);
			
			ps.get().save(assoc);
		}
	}

	@Override
	public void onScriptDetached(VerifiedScript<?> script, Set<IObject> host) {
		final Integer versionId = (Integer) script.getMetaData("scriptVersionId");
		if (versionId == null || versionId == 0)
			return;
		
		Set<Integer> oidset = new HashSet<Integer>();
		for (IObject o : host)
			oidset.add(o.getObjectId());
		List<Association> eq = ps.get().find(Association.class).where().in("oid", oidset).findList();
		ps.get().delete(eq);
	}

	@Override
	public void onScriptError(VerifiedScript<?> script, IObject hostObject, Exception e) { }

}