package es.elv.nwnx2.jvm.script;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



public abstract class VerifiedScript<EH> {
	final private String language;
	final private Map<String, EventHandler<EH>> eventMap =
		new HashMap<String, EventHandler<EH>>();
	final private boolean restricted;
	final private UUID uuid = UUID.randomUUID();
	
	final private Map<Object, Object> metadata =
		Collections.synchronizedMap(new HashMap<Object, Object>());
	
	public VerifiedScript(String language, boolean restricted) {
		this.language = language;
		this.restricted = restricted;
	}
	
	public EventHandler<EH> getEventHandlerFor(String eventClass) {
		return eventMap.get(eventClass);
	}
	
	public void registerEvent(String eventClass, EventHandler<EH> ev) {
		eventMap.put(eventClass, ev);
	}


	public String getLanguage() {
		return language;
	}

	public boolean isRestricted() {
		return restricted;
	}

	final public UUID getUUID() {
		return uuid;
	}
	
	public Object getMetaData(Object key) {
		return metadata.get(key);
	}
	
	public void setMetaData(Object key, Object value) {
		metadata.put(key, value);
	}

	@Override
	public String toString() {
		return getClass().getName() +
			" [" +
				"language=" + language +
				", restricted=" + restricted +
				", uuid=" + uuid +
				", metadata=" + metadata +
				", eventMap=" + eventMap +
			"]";
	}
}
