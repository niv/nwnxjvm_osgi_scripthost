package es.elv.nwnx2.jvm.script.api;

public interface ILocation {
	IArea getArea();
	IVector3 getPosition();
	float getFacing();
}
