package net.javadiscord.challenges.launch.model;

public class Vec2 {
	public float x;
	public float y;

	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2(float n) {
		this(n, n);
	}

	public Vec2() {
		this(0);
	}

	public Vec2(Vec2 other) {
		this(other.x, other.y);
	}

	public static Vec2 fromPolar(float r, float theta) {
		return new Vec2(r * (float) Math.cos(theta), r * (float) Math.sin(theta));
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public float dot(Vec2 other) {
		return x * other.x + y * other.y;
	}

	public Vec2 add(Vec2 other) {
		x += other.x;
		y += other.y;
		return this;
	}

	public Vec2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vec2 sub(Vec2 other) {
		x -= other.x;
		y -= other.y;
		return this;
	}

	public Vec2 mul(float factor) {
		x *= factor;
		y *= factor;
		return this;
	}

	public Vec2 div(float factor) {
		x /= factor;
		y /= factor;
		return this;
	}

	public Vec2 normalize() {
		return div(length());
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
