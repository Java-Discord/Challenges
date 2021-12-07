package net.javadiscord.challenges.launch;

public class MathUtils {
	public static float normalizeRadians(float r) {
		while (r >= 2 * Math.PI) {
			r -= 2 * Math.PI;
		}
		while (r < 0) {
			r += 2 * Math.PI;
		}
		return r;
	}
}
