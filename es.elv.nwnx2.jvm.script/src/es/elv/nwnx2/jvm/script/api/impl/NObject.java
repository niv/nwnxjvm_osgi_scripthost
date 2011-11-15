package es.elv.nwnx2.jvm.script.api.impl;

import org.nwnx.nwnx2.jvm.NWLocation;
import org.nwnx.nwnx2.jvm.NWObject;
import org.nwnx.nwnx2.jvm.NWScript;
import org.nwnx.nwnx2.jvm.NWVector;

import es.elv.nwnx2.jvm.script.VerifiedScript;
import es.elv.nwnx2.jvm.script.api.IArea;
import es.elv.nwnx2.jvm.script.api.ILocation;
import es.elv.nwnx2.jvm.script.api.IObject;
import es.elv.nwnx2.jvm.script.api.IVector3;
import es.elv.nwnx2.jvm.script.api.NoAccessException;
import es.elv.nwnx2.jvm.script.api.StorageException;
import es.elv.nwnx2.jvm.script.impl.API;

public class NObject implements IObject {
	private final int oid;

	private NWObject wrappedCached;
	
	public NObject(int oid) {
		this.oid = oid;
	}
	
	public NWObject getWrapped() {
		if (wrappedCached == null)
			wrappedCached = NWObject.apply(this.oid);
		
		return wrappedCached;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends IObject> T resolve(Class<T> classOf, int oid) {
		return (T) API.instance().getScriptHost().getOrCreateManagedObject(oid, null);
	}
	
	protected <T extends IObject> T resolve(Class<T> classOf, NWObject o) {
		return resolve(classOf, o.getObjectId());
	}
	
	protected ILocation resolve(NWLocation o) {
		return new NLocation(resolve(IArea.class, o.getArea()),
				o.getX(), o.getY(), o.getZ(), o.getFacing());
	}
	protected IVector3 resolve(NWVector o) {
		return new NVector3(o.getX(), o.getY(), o.getZ());
	}
	
	@Override
	public int getObjectId() {
		return getWrapped().getObjectId();
	}

	@Override
	public boolean isValid() {
		return API.instance().getScriptHost().isManagedObject(oid) &&
				NWScript.getIsObjectValid(getWrapped());
	}

	@Override
	public String getName() {		
		return NWScript.getName(getWrapped(), false);
	}
	
	@Override
	public void ipc(IObject to, Object message) {
		API.instance().ipc(this, (NObject) to, message);
	}
	
	@Override
	public boolean mayAccess() {
		if (!API.instance().isRestricted())
			return true;
		
		return API.instance().getObjectSelf().getObjectId() ==
				this.getObjectId();
	}

	protected void checkAccess() throws NoAccessException{
		if (!mayAccess())
			throw new NoAccessException();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getWrapped() == null) ? 0 : getWrapped().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NObject other = (NObject) obj;
		if (getWrapped() == null) {
			if (other.getWrapped() != null)
				return false;
		} else if (!getWrapped().equals(other.getWrapped()))
			return false;
		return true;
	}

	@Override
	public float distanceTo(IObject other) {
		return NWScript.getDistanceBetween(getWrapped(), ((NObject) other).getWrapped());
	}
	
	@Override
	public String toString() {
		Class<?>[] interfaces = this.getClass().getInterfaces();
		String className;
		if (interfaces.length < 1)
			className = "NativeUnknown";
		else {
			String[] classNameA = interfaces[0].getName().split("\\.");
			className = classNameA[classNameA.length-1];
		}
		
		return String.format("%s[0x%x]", className, getObjectId());
	}

	@Override
	public void set(String key, Object value) throws StorageException {
		VerifiedScript<?> script = API.instance().getExecutingScript();
		API.instance().getStorageProvider().set(script, this, key, value);
	}

	@Override
	public Object get(String key) {
		VerifiedScript<?> script = API.instance().getExecutingScript();
		return API.instance().getStorageProvider().get(script, this, key);
	}
	
	@Override
	public void log(Object... message) {
		// API.instance().get
	}

	@Override
	public void destroy() {
		checkAccess();
		
		NWScript.destroyObject(getWrapped(), 1f);
	}
}