package net.javadiscord.challenges.launch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FuelTank {
	private final FuelType type;
	private final float capacity;
	private float stored;

	public FuelTank(FuelType type, float capacity) {
		this(type, capacity, capacity);
	}

	public void consumeFuel(float amount) {
		stored = Math.max(0, stored - amount);
	}
}
