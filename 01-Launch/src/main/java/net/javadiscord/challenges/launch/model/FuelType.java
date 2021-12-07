package net.javadiscord.challenges.launch.model;

import lombok.Getter;

@Getter
public class FuelType {
	private final String name;

	public FuelType(String name) {
		this.name = name;
	}
}
