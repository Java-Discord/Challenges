package net.javadiscord.challenges.launch.model;

import lombok.Getter;
import lombok.Setter;
import net.javadiscord.challenges.launch.GuidanceComputer;

import java.awt.*;

@Getter
public class GameModel {
	public static final float KARMAN_LINE = 100_000.0f;
	public static final Color SKY_COLOR = new Color(135, 206, 235);
	public static final Color GROUND_COLOR = new Color(13, 102, 22);
	public static final float ORBITAL_SPEED = 7_840.0f;
	public static final float G = 9.81f;
	public static final float EARTH_CIRCUMFERENCE = 40_075_017.0f;

	private final Rocket rocket;
	private long t0;
	private boolean launched;
	private boolean aborted;

	public GameModel(GuidanceComputer guidanceComputer) {
		this.rocket = new Rocket(guidanceComputer);
		t0 = Long.MAX_VALUE;
	}

	public void startLaunch() {
		t0 = System.currentTimeMillis() + 5000;
		rocket.getGuidanceComputer().launchSequenceStart(rocket, getTimeSinceLaunch());
		launched = false;
		aborted = false;
	}

	public void launch() {
		if (!aborted) {
			rocket.getGuidanceComputer().launch(rocket);
			launched = true;
		}
	}

	public void abortLaunch() {
		launched = false;
		aborted = true;
		t0 = Long.MAX_VALUE;
		rocket.doForAllThrusters("", thruster -> thruster.setActive(false));
	}

	public float getTimeSinceLaunch() {
		return (System.currentTimeMillis() - t0) / 1000.0f;
	}

	public Color getSkyColor() {
		float[] rgb = SKY_COLOR.getRGBColorComponents(null);
		return new Color(rgb[0], rgb[1], rgb[2], getAtmosphericDensity());
	}

	public float getAtmosphericDensity() {
		if (rocket.getAltitude() > KARMAN_LINE) return 0.0f;
		if (rocket.getAltitude() <= 0.0f) return 1.0f;
		return (float) Math.pow((KARMAN_LINE - rocket.getAltitude()) / KARMAN_LINE, 2);
	}

	public float getEffectiveAccelerationDueToGravity() {
		var v = rocket.getVelocity().x;
		if (v == 0.0f) return G;
		return G * (1.0f - (Math.abs(v) / ORBITAL_SPEED));
	}
}
