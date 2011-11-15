package es.elv.nwnx2.jvm.script.attachtable.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(schema="sh", name="association")
public class Association {
	@Id
	public int id;
	
	public int version;
	
	public int oid;
	public int otype;
	public String objname;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp created_on;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp touched_on;

	public void setTouched_on(Timestamp touched_on) {
		this.touched_on = touched_on;
	}

	public Timestamp getTouched_on() {
		return touched_on;
	}
}