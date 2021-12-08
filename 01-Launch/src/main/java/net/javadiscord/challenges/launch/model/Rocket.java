package net.javadiscord.challenges.launch.model;

import lombok.Getter;
import net.javadiscord.challenges.launch.GuidanceComputer;
import net.javadiscord.challenges.launch.MathUtils;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class Rocket {
	/**
	 * Position, where x is longitude in meters, and y is altitude in meters.
	 */
	private final Vec2 position;

	/**
	 * Velocity, where x is longitudinal velocity in m/s, and y is altitude in m/s.
	 */
	private final Vec2 velocity;

	/**
	 * Orientation in radians, where the rocket being vertical is PI / 2 rad.
	 */
	private float orientation;

	/**
	 * Angular velocity, in radians per second.
	 */
	private float angularVelocity;

	/**
	 * The mass of the rocket, in Kg.
	 */
	private final float dryMass = 50_000.0f;

	private final float height;
	private final float width;

	private final Set<Thruster> thrusters;
	private final Set<FuelTank> fuelTanks;
	private final Map<FuelType, FuelTank> fuelTanksMap;

	private final GuidanceComputer guidanceComputer;

	public Rocket(GuidanceComputer guidanceComputer) {
		this.guidanceComputer = guidanceComputer;
		this.position = new Vec2(0, 0);
		this.velocity = new Vec2(0, 0);
		this.height = 20.0f;
		this.width = 6.0f;
		this.orientation = (float) Math.PI / 2;
		this.angularVelocity = 0;
		this.thrusters = new HashSet<>();
		this.fuelTanks = new HashSet<>();
		FuelType rp1 = new FuelType("RP 1");
		FuelType monopropellant = new FuelType("Monopropellant");
		fuelTanks.add(new FuelTank(rp1, 380_000));
		fuelTanks.add(new FuelTank(monopropellant, 5_000));
		this.fuelTanksMap = new HashMap<>();
		for (var tank : fuelTanks) {
			fuelTanksMap.put(tank.getType(), tank);
		}

		var mainEngineFactory = new ThrusterFactory()
				.maxThrust(1_700_000).minThrottle(0.4f).maxThrottle(1.05f).fuelBurnRate(300.0f).fuelType(rp1).gimbalRange(10.0f).size(1.5f);
		var me1 = mainEngineFactory.build("ME 1", new Vec2(-1.5f, -height / 2), (float) -Math.PI / 2);
		var me2 = mainEngineFactory.build("ME 2", new Vec2(0, -height / 2), (float) -Math.PI / 2);
		var me3 = mainEngineFactory.build("ME 3", new Vec2(1.5f, -height / 2), (float) -Math.PI / 2);

		var rcsFactory = new ThrusterFactory()
				.maxThrust(100_000).minThrottle(0.05f).maxThrottle(1.0f).fuelBurnRate(5.0f).fuelType(monopropellant).gimbalRange(0).size(0.75f);
		var rcsTopLeft = rcsFactory.build("RCS Top Left", new Vec2(-width / 2, height / 2), (float) -Math.PI);
		var rcsTopRight = rcsFactory.build("RCS Top Right", new Vec2(width / 2, height / 2), 0);
		var rcsBottomLeft = rcsFactory.build("RCS Bottom Left", new Vec2(-width / 2, -height / 2 + 2), (float) -Math.PI);
		var rcsBottomRight = rcsFactory.build("RCS Bottom Right", new Vec2(width / 2, -height / 2 + 2), 0);
		var rcsForwardLeft = rcsFactory.build("RCS Forward Left", new Vec2(-width / 2 + 1, height / 2), (float) Math.PI / 2);
		var rcsForwardRight = rcsFactory.build("RCS Forward Right", new Vec2(width / 2 - 1, height / 2), (float) Math.PI / 2);
		var rcsBackwardRight = rcsFactory.build("RCS Backward Right", new Vec2(width / 2 - 1, -height / 2 + 2), (float) -Math.PI / 2);
		var rcsBackwardLeft = rcsFactory.build("RCS Backward Left", new Vec2(-width / 2 + 1, -height / 2 + 2), (float) -Math.PI / 2);

		thrusters.addAll(Set.of(
				me1, me2, me3,
				rcsTopLeft, rcsTopRight,
				rcsBottomLeft, rcsBottomRight,
				rcsForwardLeft, rcsForwardRight,
				rcsBackwardLeft, rcsBackwardRight
		));
	}

	public float getFuelRemaining(FuelType type) {
		if (fuelTanksMap.containsKey(type)) {
			return fuelTanksMap.get(type).getStored();
		}
		return 0;
	}

	public void consumeFuel(FuelType type, float quantity) {
		if (fuelTanksMap.containsKey(type)) {
			fuelTanksMap.get(type).consumeFuel(quantity);
		}
	}

	public float getOrientationDegrees() {
		return orientation * (180.0f / (float) Math.PI);
	}

	public void setOrientation(float orientation) {
		this.orientation = MathUtils.normalizeRadians(orientation);
	}

	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public float getAltitude() {
		return position.y;
	}

	public float getLongitude() {
		return position.x;
	}

	/**
	 * Gets the total mass of the rocket, including current fuel weight.
	 * @return The total weight of the rocket.
	 */
	public float getMass() {
		float totalMass = dryMass;
		for (var tank : fuelTanks) {
			totalMass += tank.getStored();
		}
		return totalMass;
	}

	public Thruster getThrusterByName(String name) {
		return thrusters.stream().filter(t -> t.getName().equals(name)).findFirst().orElseThrow();
	}

	/**
	 * Gets a list of all thrusters whose names begin with the given prefix.
	 * @param prefix The prefix to search for.
	 * @return The list of thrusters matching this prefix.
	 */
	public List<Thruster> getAllByPrefix(String prefix) {
		return thrusters.stream().filter(t -> t.getName().startsWith(prefix)).toList();
	}

	/**
	 * Perform some actions on all thrusters whose names begin with the given
	 * prefix.
	 * @param prefix The prefix to search for.
	 * @param action An action to perform for each matching thruster.
	 */
	public void doForAllThrusters(String prefix, Consumer<Thruster> action) {
		getAllByPrefix(prefix).forEach(action);
	}
}
