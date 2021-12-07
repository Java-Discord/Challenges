package net.javadiscord.challenges.launch.view;

import net.javadiscord.challenges.launch.model.GameModel;
import net.javadiscord.challenges.launch.model.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

public class GamePanel extends JPanel {
	private static final Font STATS_FONT = new Font("Monospaced", Font.PLAIN, 14);
	private static final float SCALE = 10.0f; // Pixels per meter scale.
	private boolean drawDebug = false;

	private final GameModel model;

	public GamePanel(GameModel model) {
		this.model = model;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(model.getSkyColor());
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());

		drawWorld(g2);
		drawRocket(g2);
		drawStats(g2);
	}

	private void drawStats(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(STATS_FONT);
		g.drawString(String.format("Altitude: %.3f Km", model.getRocket().getAltitude() / 1000.0f), 10, 15);
		g.drawString(String.format("Longitude: %.3f Km", model.getRocket().getLongitude() / 1000.0f), 10, 30);
		g.drawString(String.format("Velocity: (vert = %.3f m/s, long = %.3f m/s)", model.getRocket().getVelocity().y, model.getRocket().getVelocity().x), 10, 45);
		g.drawString(String.format("Pitch: %.2f degrees", model.getRocket().getOrientationDegrees()), 10, 60);
		g.drawString(String.format("Mass: %.2f Kg", model.getRocket().getMass()), 10, 75);
		g.drawString(String.format("Effective Gravity: %.3f m/s^2", model.getEffectiveAccelerationDueToGravity()), 10, 90);
		g.drawString("Fuel:", 10, 105);
		var tankData = model.getRocket().getFuelTanks().stream()
				.sorted(Comparator.comparing(t -> t.getType().getName()))
				.map(tank -> String.format("%s: %.2f Kg, %.2f %%", tank.getType().getName(), tank.getStored(), tank.getStored() * 100 / tank.getCapacity()))
				.toList();
		for (int i = 0; i < tankData.size(); i++) {
			g.drawString(tankData.get(i), 15, i * 15 + 120);
		}
		float t = model.getTimeSinceLaunch();
		g.drawString(String.format("T%s%.3f seconds", t > 0 ? "+" : "", t), 10, getHeight() - 15);
	}

	private void drawRocket(Graphics2D g) {
		g.setStroke(new BasicStroke(0.1f));
		var txOriginal = g.getTransform();
		AffineTransform txRocket = new AffineTransform(txOriginal);
		txRocket.translate(getWidth() / 2.0, getHeight() / 2.0);
		txRocket.rotate(-model.getRocket().getOrientation() + Math.PI / 2);
		txRocket.scale(SCALE, SCALE);
		g.setTransform(txRocket);

		float w = model.getRocket().getWidth();
		float h = model.getRocket().getHeight();

		g.setColor(Color.RED.darker());
		Rectangle2D body = new Rectangle2D.Float(-w / 2, -h / 2, w, h);
		g.fill(body);
		Path2D noseCode = new Path2D.Float();
		noseCode.moveTo(-w / 2, -h / 2);
		noseCode.lineTo(0, -h / 2 - 5);
		noseCode.lineTo(w / 2, -h / 2);
		noseCode.lineTo(-w / 2, -h / 2);
		g.fill(noseCode);

		if (drawDebug) {
			g.setColor(Color.YELLOW);
			Vec2 velocityVector = new Vec2(model.getRocket().getVelocity()).normalize().mul(2);
			Path2D velocityPath = new Path2D.Float();
			velocityPath.moveTo(0, 0);
			velocityPath.lineTo(velocityVector.x, -velocityVector.y);
			g.draw(velocityPath);
		}

		for (var thruster : model.getRocket().getThrusters()) {
			AffineTransform txThruster = new AffineTransform(txRocket);
			txThruster.translate(thruster.getPosition().x, -thruster.getPosition().y);
			txThruster.rotate(-thruster.getCurrentOrientation() + Math.PI / 2);
			txThruster.scale(thruster.getSize(), thruster.getSize());

			AffineTransform txThrusterNoRotate = new AffineTransform(txRocket);
			txThrusterNoRotate.translate(thruster.getPosition().x, -thruster.getPosition().y);
			txThrusterNoRotate.scale(thruster.getSize(), thruster.getSize());

			g.setTransform(txThruster);
			Path2D nozzle = new Path2D.Float();
			nozzle.moveTo(-0.5, -0.5);
			nozzle.lineTo(0, 0.5);
			nozzle.lineTo(0.5, -0.5);
			nozzle.lineTo(-0.5, -0.5);
			g.setColor(Color.GRAY);
			g.fill(nozzle);

			if (thruster.isActive()) {
				Path2D exhaust = new Path2D.Float();
				exhaust.moveTo(-0.5, -0.6);
				exhaust.lineTo(0, -1.5 - 0.25 * Math.sin(50* model.getTimeSinceLaunch() + thruster.getName().hashCode()));
				exhaust.lineTo(0.5, -0.6);
				exhaust.lineTo(-0.5, -0.6);
				g.setColor(Color.ORANGE);
				g.fill(exhaust);

				if (drawDebug) {
					g.setTransform(txThrusterNoRotate);

					var thrust = thruster.getThrust();
					Vec2 thrustDirection = new Vec2(thrust).normalize();
					Vec2 centerDirection = new Vec2(0).sub(thruster.getPosition()).normalize();

					g.setColor(Color.YELLOW);
					Path2D thrustPath = new Path2D.Float();
					thrustPath.moveTo(0, 0);
					thrustPath.lineTo(thrustDirection.x, -thrustDirection.y);
					g.draw(thrustPath);
					g.setColor(Color.BLUE);
					Path2D centerPath = new Path2D.Float();
					centerPath.moveTo(0, 0);
					centerPath.lineTo(centerDirection.x, -centerDirection.y);
					g.draw(centerPath);
				}
			}
		}

		g.setTransform(txOriginal);
	}

	private void drawWorld(Graphics2D g) {
		float screenHeightMeters = getHeight() / SCALE;
		float relativeGroundHeight = -1 * model.getRocket().getAltitude() - (screenHeightMeters / 2);
		if (relativeGroundHeight <= 0.0f) {
			float groundHeight = (-1 * relativeGroundHeight) * SCALE;
			g.setColor(GameModel.GROUND_COLOR);
			Rectangle2D ground = new Rectangle2D.Float(0, groundHeight, getWidth(), getHeight());
			g.fill(ground);
		}
	}
}
