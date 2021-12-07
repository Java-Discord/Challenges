package net.javadiscord.challenges.launch.control;

import net.javadiscord.challenges.launch.model.GameModel;
import net.javadiscord.challenges.launch.model.Rocket;
import net.javadiscord.challenges.launch.model.Vec2;
import net.javadiscord.challenges.launch.view.GamePanel;

public class GameUpdater extends Thread {
	public static final double PHYSICS_FPS = 60.0;
	public static final double MILLISECONDS_PER_PHYSICS_TICK = 1000.0 / PHYSICS_FPS;
	public static final double PHYSICS_SPEED = 1.0;

	public static final double DISPLAY_FPS = 60.0;
	public static final double MILLISECONDS_PER_DISPLAY_FRAME = 1000.0 / DISPLAY_FPS;

	private final GameModel model;
	private final GamePanel gamePanel;
	private volatile boolean running = true;

	public GameUpdater(GameModel model, GamePanel gamePanel) {
		this.model = model;
		this.gamePanel = gamePanel;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {
		long lastPhysicsUpdate = System.currentTimeMillis();
		long lastDisplayUpdate = System.currentTimeMillis();
		while (this.running) {
			long currentTime = System.currentTimeMillis();
			long timeSinceLastPhysicsUpdate = currentTime - lastPhysicsUpdate;
			long timeSinceLastDisplayUpdate = currentTime - lastDisplayUpdate;
			if (timeSinceLastPhysicsUpdate >= MILLISECONDS_PER_PHYSICS_TICK) {
				double elapsedSeconds = timeSinceLastPhysicsUpdate / 1000.0;
				this.updateModelPhysics(elapsedSeconds * PHYSICS_SPEED);
				lastPhysicsUpdate = currentTime;
				timeSinceLastPhysicsUpdate = 0L;
			}
			if (timeSinceLastDisplayUpdate >= MILLISECONDS_PER_DISPLAY_FRAME) {
				this.gamePanel.repaint();
				lastDisplayUpdate = currentTime;
				timeSinceLastDisplayUpdate = 0L;
			}
			long timeUntilNextPhysicsUpdate = (long) (MILLISECONDS_PER_PHYSICS_TICK - timeSinceLastPhysicsUpdate);
			long timeUntilNextDisplayUpdate = (long) (MILLISECONDS_PER_DISPLAY_FRAME - timeSinceLastDisplayUpdate);

			// Sleep to reduce CPU usage.
			try {
				Thread.sleep(Math.min(timeUntilNextPhysicsUpdate, timeUntilNextDisplayUpdate));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateModelPhysics(double t) {
		if (model.getTimeSinceLaunch() > 0.0f && !model.isLaunched()) {
			model.launch();
		}
		if (model.isLaunched() || model.isAborted()) {
			updateRocket(t);
		}
	}

	private void updateRocket(double t) {
		var r = model.getRocket();
		var v = r.getVelocity();
		var p = r.getPosition();
		if (p.y > 0) {
			v.y -= model.getEffectiveAccelerationDueToGravity() * t;
		}

		computeRocketAccelerations(r, t);

		p.add((float) (v.x * t), (float) (v.y * t));
		if (p.x > GameModel.EARTH_CIRCUMFERENCE) p.x -= GameModel.EARTH_CIRCUMFERENCE;
		if (p.y < 0) {
			p.y = 0;
			v.y = 0;
		}
		r.setOrientation((float) (r.getOrientation() + r.getAngularVelocity() * t));

		r.getGuidanceComputer().controlRocket(r, model.getTimeSinceLaunch());
	}

	private void computeRocketAccelerations(Rocket r, double t) {
		Vec2 totalForce = new Vec2(0); // relative to the rocket
		float totalAngularAcceleration = 0.0f;
		for (var thruster : r.getThrusters()) {
			if (thruster.isActive() && r.getFuelRemaining(thruster.getFuelType()) > 0) {
				Vec2 thrust = thruster.getThrust();
				Vec2 thrustDir = new Vec2(thrust).normalize();
				Vec2 centerDir = new Vec2(0).sub(thruster.getPosition()).normalize();
				float forceRatio = new Vec2(thrust).normalize().dot(centerDir);
				totalForce.add(new Vec2(thrust).mul(forceRatio));

				Vec2 torqueDir = new Vec2(centerDir.y, -centerDir.x);
				float radius = thruster.getPosition().length();
				float torque = radius * torqueDir.dot(thrustDir) * thrust.length();
				totalAngularAcceleration += torque / (0.5f * r.getMass() * radius * radius);

				r.consumeFuel(thruster.getFuelType(), thruster.getFuelBurnRate() * thruster.getThrottle() * (float) t);
			}
		}
		float forceMagnitude = totalForce.length() * (float) t / r.getMass();
		float angle = (float) Math.atan2(totalForce.y, totalForce.x);
		angle += r.getOrientation() - (float) Math.PI / 2;
		Vec2 worldForce = new Vec2(forceMagnitude * (float) Math.cos(angle), forceMagnitude * (float) Math.sin(angle));
		r.getVelocity().add(worldForce);
		r.setAngularVelocity(r.getAngularVelocity() + totalAngularAcceleration * (float) t);

		// Air resistance
		float airResistance = 1.0f - (float) (0.0001 * Math.random() * model.getAtmosphericDensity());
		r.getVelocity().mul(airResistance);
		r.setAngularVelocity(r.getAngularVelocity() * airResistance);

		// Random perturbations
		float angularPerturbation = (float) (0.005 * (2 * Math.random() - 1.0) * model.getAtmosphericDensity());
		float linearPerturbationMagnitude = (float) (0.0001 * Math.random() * model.getAtmosphericDensity());
		float linearPerturbationDirection = (float) (Math.random() * 2 * Math.PI);
		Vec2 linearPerturbation = Vec2.fromPolar(linearPerturbationMagnitude, linearPerturbationDirection);
		r.getVelocity().add(linearPerturbation);
		r.setAngularVelocity(r.getAngularVelocity() + angularPerturbation);
	}
}
