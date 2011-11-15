package es.elv.nwnx2.jvm.script;

import es.elv.nwnx2.jvm.script.api.IObject;
import es.elv.nwnx2.jvm.script.api.StorageException;

public interface StorageProvider {
	Object get(VerifiedScript<?> script, IObject hostObject, String key);
	void set(VerifiedScript<?> script, IObject hostObject, String key, Object value) throws StorageException;
}
