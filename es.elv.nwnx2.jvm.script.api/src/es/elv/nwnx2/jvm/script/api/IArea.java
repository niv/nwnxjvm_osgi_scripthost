package es.elv.nwnx2.jvm.script.api;

/**
 * An area in the game.
 */
public interface IArea extends IObject {

	/**
	 * Returns the weather in the given area.
	 * One of:
	 *  WEATHER_CLEAR, WEATHER_RAIN, WEATHER_SNOW, WEATHER_INVALID
	 *  (tbd)  
	 */
	int getWeather();
}