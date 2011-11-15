package es.elv.nwnx2.jvm.script.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.nwnx.nwnx2.jvm.NWLocation;
import org.nwnx.nwnx2.jvm.NWObject;
import org.nwnx.nwnx2.jvm.NWScript;
import org.nwnx.nwnx2.jvm.NWVector;

import es.elv.nwnx2.jvm.events.game.CreatureHeartbeat;
import es.elv.nwnx2.jvm.hierarchy.Area;
import es.elv.nwnx2.jvm.hierarchy.Creature;
import es.elv.nwnx2.jvm.hierarchy.DM;
import es.elv.nwnx2.jvm.hierarchy.GameObject;
import es.elv.nwnx2.jvm.hierarchy.Item;
import es.elv.nwnx2.jvm.hierarchy.Module;
import es.elv.nwnx2.jvm.hierarchy.bundles.Every;
import es.elv.nwnx2.jvm.hierarchy.bundles.Feature;
import es.elv.nwnx2.jvm.hierarchy.events.Handler;
import es.elv.nwnx2.jvm.script.EventHandler;
import es.elv.nwnx2.jvm.script.LanguageNotFoundException;
import es.elv.nwnx2.jvm.script.ScriptHost;
import es.elv.nwnx2.jvm.script.ScriptHostListener;
import es.elv.nwnx2.jvm.script.ScriptLanguage;
import es.elv.nwnx2.jvm.script.VerifiedScript;
import es.elv.nwnx2.jvm.script.api.IArea;
import es.elv.nwnx2.jvm.script.api.IObject;
import es.elv.nwnx2.jvm.script.api.impl.NArea;
import es.elv.nwnx2.jvm.script.api.impl.NCreature;
import es.elv.nwnx2.jvm.script.api.impl.NItem;
import es.elv.nwnx2.jvm.script.api.impl.NLocation;
import es.elv.nwnx2.jvm.script.api.impl.NModule;
import es.elv.nwnx2.jvm.script.api.impl.NObject;
import es.elv.nwnx2.jvm.script.api.impl.NVector3;

public class ScriptHostImpl extends Feature implements ScriptHost {
	@SuppressWarnings({ "rawtypes" })
	@Override
	public ScriptLanguage<?, ?> findLanguage(String identifier) throws LanguageNotFoundException {
		for (ScriptLanguage lang : locateServicesFor(ScriptLanguage.class))
			if (lang.getIdentifier().equals(identifier))
				return lang;

		throw new LanguageNotFoundException("Language not registered: " + identifier);
	}

	private Object[] convertToAPI(Object[] va) {
		Object[] ret = new Object[va.length];
		for (int i = 0; i < va.length; i++) {
			Object rv = va[i];
			
			if (rv instanceof GameObject)
				rv = getOrCreateManagedObject(((GameObject) rv).getObjectId(), null);
			
			if (rv instanceof NWLocation) {
				NWLocation loc = (NWLocation) rv;
				IArea area = (IArea) getOrCreateManagedObject(loc.getArea().getObjectId(), null);
				NVector3 vec = new NVector3(loc.getX(), loc.getY(), loc.getZ());
				rv = new NLocation(area, vec, loc.getFacing());
			}
			
			if (rv instanceof NWVector) {
				NWVector vec = (NWVector) rv;
				rv = new NVector3(vec.getX(), vec.getY(), vec.getZ());
			}
			
			ret[i] = rv;
		}
		return ret;
	}
	
	private <T> T withAPI(
			IObject hostObject, VerifiedScript<?> script, Callable<T> call) throws Exception {
		try {
			API.instance().setObjectSelf(hostObject);
			API.instance().setScriptHost(this);
			API.instance().setExecutingScript(script);
			
			return call.call();
		} finally {
			API.instance().setObjectSelf(null);
			API.instance().setScriptHost(null);
			API.instance().setExecutingScript(null);
		}
	}

	@Handler
	void onCreatureHB(final CreatureHeartbeat e) {
		if (!isManagedScriptedObject(e.creature.getObjectId()))
			return;
		
		final NCreature managed = (NCreature) this.getManagedObject(e.creature.getObjectId());
		
		log.debug("IManaged tick " + e.creature);
		
		try {
			withAPI(managed, null, new Callable<Object>() {
				@Override public Object call() throws Exception {
					managed.getTaskManager().tick();
					for (ScriptHostListener ll : locateServicesFor(ScriptHostListener.class))
						ll.onManagedObjectTick(e.creature.getObjectId(), managed);

					return null;
				}
				
			});
		} catch (Exception e1) {
			log.error(e1);
		}
	}
	
	@Every(interval=120 * 1000)
	void cleanManaged() {
		List<Integer> toPurge = new LinkedList<Integer>();
		long remaining = 0, scripted = 0;
		for (Entry<Integer, NObject> e : mappedObjects.entrySet())
			if (!NWScript.getIsObjectValid(e.getValue().getWrapped()))
				toPurge.add(e.getKey());
			else {
				if (mappedScripted.containsKey(e.getKey()))
					scripted++;
				remaining++;
			}
		
		for (Integer x : toPurge)
			destroyManagedObject(x);
		
		if (toPurge.size() > 0)
			log.debug("IManaged purged " + toPurge.size() + " invalid objects, " +
					remaining + " mapped, " + scripted + " scripted");
	}

	private Map<Integer, NObject> mappedObjects =
			Collections.synchronizedMap(new HashMap<Integer, NObject>());
	private Map<Integer, Boolean> mappedScripted =
		Collections.synchronizedMap(new HashMap<Integer, Boolean>());


	//@Override
	public IObject getOrCreateManagedObject(int oid, VerifiedScript<?> associatedScript) {
		NWObject apply = NWObject.apply(oid);
		if (!mappedObjects.containsKey(oid)) {
			NObject rr = resolve(apply);
			mappedObjects.put(oid, rr);
			
			for (ScriptHostListener ll : locateServicesFor(ScriptHostListener.class))
				ll.onManagedObjectCreated(oid, rr, associatedScript);
		}
		
		if (associatedScript != null)
			mappedScripted.put(oid, true);
		
		return getManagedObject(oid);
	}

	//@Override
	public boolean isManagedObject(int oid) {
		return mappedObjects.containsKey(oid);
	}

	//@Override
	public boolean isManagedScriptedObject(int oid) {
		return mappedScripted.containsKey(oid);
	}

	//@Override
	public IObject getManagedObject(int oid) {
		return mappedObjects.get(oid);
	}

	//@Override
	public void destroyManagedObject(int oid) {
		mappedObjects.remove(oid);
		mappedScripted.remove(oid);
		
		for (ScriptHostListener ll : locateServicesFor(ScriptHostListener.class))
			ll.onManagedObjectDestroyed(oid);
	}

	@Override
	public void adviseDestroyManagedObject(int oid) {
		destroyManagedObject(oid);
	}
	
	@Override
	public IObject getMappedObjectFor(int oid) {
		return mappedObjects.get(oid);
	}
	
	private <IN extends NWObject> NObject resolve(IN o) {
		NObject r;
		int oid = o.getObjectId();
		if (o instanceof Area)
			r = new NArea(oid);
		else if (o instanceof Creature)
			r = new NCreature(oid);
		else if (o instanceof Item)
			r = new NItem(oid);
		else if (o instanceof Module)
			r = new NModule();
		else
			r = new NObject(oid);
		
		return r;
	}
	
	final private Map<VerifiedScript<?>, Set<NWObject>> attachedScriptObjects =
		Collections.synchronizedMap(new HashMap<VerifiedScript<?>, Set<NWObject>>());
	
	@Override
	public void attachScript(VerifiedScript<?> script, Set<NWObject> hostObjects) {
		if (!attachedScriptObjects.containsKey(script))
			attachedScriptObjects.put(script, new HashSet<NWObject>());
		Set<NWObject> aso = attachedScriptObjects.get(script);
		
		Set<NWObject> toAdd = new HashSet<NWObject>();
		for (NWObject n : hostObjects)
			if (!aso.contains(n))
				toAdd.add(n);
		
		Set<IObject> resolved = new HashSet<IObject>();
		for (NWObject o : toAdd) {
			IObject host = getOrCreateManagedObject(o.getObjectId(), script); 
			resolved.add(host);
			// Exceptions thrown here will automatically invalidate
			// herein created managed objects on the next purge.
			try {
				initScript(script, host);
			} catch (Exception ex) {
				log.error(ex);
				for (ScriptHostListener sl : locateServicesFor(ScriptHostListener.class))
					sl.onScriptError(script, host, ex);
				return;
			}
		}
		
		if (toAdd.size() == 0)
			return;

		attachedScriptObjects.get(script).addAll(toAdd);
		
		for (ScriptHostListener sl : locateServicesFor(ScriptHostListener.class))
			sl.onScriptAttached(script, resolved);
	}
	
	@Override
	public void detachScript(VerifiedScript<?> script, Set<NWObject> hostObjects) {
		if (!attachedScriptObjects.containsKey(script)) {
			log.warn("Script " + script + " was not previously attached.");
			return;
			// throw new IllegalArgumentException("Script " + script + " was not previously attached.");
		}
		Set<NWObject> aso = attachedScriptObjects.get(script);
		
		Set<NWObject> toRemove = new HashSet<NWObject>();
		for (NWObject n : hostObjects)
			if (aso.contains(n))
				toRemove.add(n);
		
		if (toRemove.size() == 0)
			return;
		
		attachedScriptObjects.get(script).removeAll(toRemove);
		
		Set<IObject> resolved = new HashSet<IObject>();
		for (NWObject o : toRemove) {
			IObject managed = getManagedObject(o.getObjectId());
			if (null == managed) {
				log.warn("null host object on detachScript");
				continue;
			}
			resolved.add(managed);
		}
		
		if (attachedScriptObjects.get(script).size() == 0)
			attachedScriptObjects.remove(script);
		
		for (ScriptHostListener sl : locateServicesFor(ScriptHostListener.class))
			sl.onScriptDetached(script, resolved);
	}
	
	@Override
	public void detachScriptFromAll(VerifiedScript<?> script) {
		if (!attachedScriptObjects.containsKey(script)) {
			// log.warn("Script " + script + " was not previously attached.");
			// throw new IllegalArgumentException("Script " + script + " was not previously attached.");
			return;
		}
		
		detachScript(script, attachedScriptObjects.get(script));
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initScript(final VerifiedScript script, final IObject hostObject) throws Exception {
		withAPI(hostObject, script, new Callable<Void>() {
			@Override public Void call() throws Exception {
				ScriptLanguage l = findLanguage(script.getLanguage());
				l.executeScript(ScriptHostImpl.this, hostObject, script);
				return null;
			}
		});
	}	
	
	private boolean checkArgs(Object... va) {
		for (Object a : va)
			if (a instanceof DM)
				return false;
		return true;
	}
	
	@Override
	public void handleObjectEvent(final NWObject objSelf, final String eventClass, final Object... va) {
		final IObject managed = getManagedObject(objSelf.getObjectId());
		// Object not attached to any script.
		if (null == managed)
			return;
		
		if (!checkArgs(va)) {
			log.warn("checkArgs denied scriptevent: " + eventClass + ">> " + va);
			return;
		}
		
		final Object[] apiva = convertToAPI(va);
		
		for (final Entry<VerifiedScript<?>, Set<NWObject>> e : attachedScriptObjects.entrySet()) {
			final EventHandler<?> handler = e.getKey().getEventHandlerFor(eventClass);
			if (null == handler)
				continue;
			
			if (!e.getValue().contains(objSelf))
				continue;			
			
			try {
				withAPI(managed, e.getKey(), new Callable<Object>() {
					@SuppressWarnings("unchecked")
					public Object call() throws Exception {
						@SuppressWarnings("rawtypes")
						ScriptLanguage language = findLanguage(e.getKey().getLanguage());
						return language.excuteEventHandler(ScriptHostImpl.this, managed, e.getKey(), handler, apiva);
					};
				});
				
			} catch (Exception ex) {
				for (ScriptHostListener sl : locateServicesFor(ScriptHostListener.class))
					sl.onScriptError(e.getKey(), managed, ex);
				
				detachScriptFromAll(e.getKey());
			}
		}
	}
	
	@Override
	public Map<VerifiedScript<?>, Set<NWObject>> getVerifiedScriptMap() {
		return attachedScriptObjects;
	}

	@Override
	public Set<IObject> getMappedObjects() {
		Set<IObject> ret = new HashSet<IObject>();
		for (NObject n : mappedObjects.values())
			ret.add(n);
		return ret;
	}


}