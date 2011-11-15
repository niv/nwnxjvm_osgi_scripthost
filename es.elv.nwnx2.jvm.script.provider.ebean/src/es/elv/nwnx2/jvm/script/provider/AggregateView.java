package es.elv.nwnx2.jvm.script.provider;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(schema="sh",name="aggregate")
public class AggregateView {
	public int script;
	public int otype;
	public int version;
	public boolean restricted;
	public String language;
	public Date created_on;
	public String source;
}
