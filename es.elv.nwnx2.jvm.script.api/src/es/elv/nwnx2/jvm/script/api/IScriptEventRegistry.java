package es.elv.nwnx2.jvm.script.api;


public interface IScriptEventRegistry<HANDLER> {
	
	void set(String eventClass, HANDLER handler);
	
	void log(Object... message);
	
}