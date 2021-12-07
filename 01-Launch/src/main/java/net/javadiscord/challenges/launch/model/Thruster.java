package net.javadiscord.challenges.launch.model;

import lombok.Getter;
import net.javadiscord.challenges.launch.MathUtils;

/**
 * A thruster is an individual component of a rocket that consumes fuel to apply
 * force to the rocket. A thruster is placed at a specific location on the
 * rocket, at a specific orientation, in order to be of use either in normal
 * linear propulsion, or in orientating the spacecraft while in space.
 */
@Getter
public class Thruster {
	private final String name;
	private final Vec2 position;
	private final float orientation;
	private final float maxThrust;
	private final float minThrottle;
	private final float maxThrottle;
	private final float fuelBurnRate;
	private final FuelType fuelType;
	private final float size;
	private final float gimbalRange;

	private boolean active;
	private float gimbal;
	private float throttle;

	public Thruster(String name, Vec2 position, float orientation, float maxThrust, float minThrottle, float maxThrottle, float fuelBurnRate, FuelType fuelType, float size, float gimbalRange) {
		this.name = name;
		this.position = position;
		this.orientation = MathUtils.normalizeRadians(orientation);
		this.gimbal = 0;
		this.maxThrust = maxThrust;
		this.minThrottle = minThrottle;
		this.maxThrottle = maxThrottle;
		this.fuelBurnRate = fuelBurnRate;
		this.fuelType = fuelType;
		this.size = size;
		this.gimbalRange = gimbalRange;
		setThrottle(minThrottle);
		this.active = false;
	}

	/**
	 * Sets the gimbal of this thruster to the given value, bounded within this
	 * thruster's gimbal range.
	 * @param gimbal The gimbal setting, in degrees. Positive indicates counter-
	 *               clockwise rotation.
	 */
	public void setGimbal(float gimbal) {
		this.gimbal = Math.min(gimbalRange, Math.max(-gimbalRange, gimbal));
	}

	/**
	 * Sets the throttle of this thruster to the given value, bounded within
	 * this thruster's throttle range.
	 * @param throttle The throttle setting. Should be a floating point value
	 *                 between the min and max throttle for this thruster.
	 */
	public void setThrottle(float throttle) {
		this.throttle = Math.min(maxThrottle, Math.max(minThrottle, throttle));
	}

	/**
	 * Sets this thruster as active or inactive. Active means the thruster will
	 * produce thrust.
	 * @param active Whether the thruster should be active.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Gets the current orientation of the thruster, taking into account any
	 * gimbal settings applied to it.
	 * @return The current orientation of the thruster, in radians.
	 */
	public float getCurrentOrientation() {
		return orientation + (float) Math.toRadians(gimbal);
	}

	/**
	 * Gets the thrust vector produced by this thruster, relative to the rocket
	 * it's placed on.
	 * @return A vector representing the thrust produced by this thruster.
	 */
	public Vec2 getThrust() {
		if (!active) return new Vec2(0);
		float t = maxThrust * throttle;
		double thrustAngle = getCurrentOrientation() + Math.PI;
		float x = t * (float) Math.cos(thrustAngle);
		float y = t * (float) Math.sin(thrustAngle);
		return new Vec2(x, y);
	}
}
