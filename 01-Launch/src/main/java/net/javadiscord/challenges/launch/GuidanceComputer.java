package net.javadiscord.challenges.launch;

import net.javadiscord.challenges.launch.model.Rocket;

/**
 * Create an implementation of this interface to build a guidance computer that
 * can control your rocket on its journey to orbit.
 */
public interface GuidanceComputer {
	/**
	 * This method is called a few seconds prior to launch. Use it to get your
	 * bearings and initialize any systems so that launch goes smoothly.
	 * @param rocket The rocket that's launching.
	 * @param t The time until the launch, in seconds.
	 */
	void launchSequenceStart(Rocket rocket, float t);

	/**
	 * This method is called at t = 0.0. Ignite your engines and enter flight
	 * mode!
	 * @param rocket The rocket that's launching.
	 */
	void launch(Rocket rocket);

	/**
	 * This method is called once per physics update, so this is where your
	 * guidance system should read information about the rocket's status and
	 * update its thrusters or other control points accordingly.
	 * @param rocket The rocket that's being controlled.
	 * @param t The time, in seconds, since launch.
	 */
	void controlRocket(Rocket rocket, float t);
}
