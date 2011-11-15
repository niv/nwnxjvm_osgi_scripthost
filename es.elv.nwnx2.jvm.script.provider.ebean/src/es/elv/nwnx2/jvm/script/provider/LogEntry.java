package es.elv.nwnx2.jvm.script.provider;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.annotation.EnumValue;

@Entity
@Table(schema="sh", name="log")
public class LogEntry {
	public static enum Type {
		@EnumValue("user")
		USER,
		@EnumValue("warning")
		WARNING,
		@EnumValue("error")
		ERROR,
		@EnumValue("internal")
		INTERNAL
	};
	@Id
	public int id;
	
	public int version;
	public int oid;
	public String oname;
	public Type type;
	public String context;
	public String message;
}