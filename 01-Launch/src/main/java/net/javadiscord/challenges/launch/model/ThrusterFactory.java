package net.javadiscord.challenges.launch.model;

public class ThrusterFactory {
	private float maxThrust;
	private float minThrottle;
	private float maxThrottle;
	private float fuelBurnRate;
	private FuelType fuelType;
	private float size;
	private float gimbalRange;

	public ThrusterFactory maxThrust(float maxThrust) {
		this.maxThrust = maxThrust;
		return this;
	}

	public ThrusterFactory minThrottle(float minThrottle) {
		this.minThrottle = minThrottle;
		return this;
	}

	public ThrusterFactory maxThrottle(float maxThrottle) {
		this.maxThrottle = maxThrottle;
		return this;
	}

	public ThrusterFactory fuelBurnRate(float fuelBurnRate) {
		this.fuelBurnRate = fuelBurnRate;
		return this;
	}

	public ThrusterFactory fuelType(FuelType fuelType) {
		this.fuelType = fuelType;
		return this;
	}

	public ThrusterFactory size(float size) {
		this.size = size;
		return this;
	}

	public ThrusterFactory gimbalRange(float gimbalRange) {
		this.gimbalRange = gimbalRange;
		return this;
	}

	public Thruster build(String name, Vec2 position, float orientation) {
		return new Thruster(name, position, orientation, maxThrust, minThrottle, maxThrottle, fuelBurnRate, fuelType, size, gimbalRange);
	}
}
