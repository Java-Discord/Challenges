package net.javadiscord.challenges.launch;

import net.javadiscord.challenges.launch.model.Rocket;

/**
 * Sample launch guidance system which just starts up the engines and lifts off
 * vertically, with no stabilization or targeting.
 */
public class SimpleLaunchGuidance implements GuidanceComputer {
	@Override
	public void launchSequenceStart(Rocket rocket, float t) {
		System.out.println("Starting launch sequence.");
		// Prepare the thrusters' throttles and the RCS thrusters' controls for maximum maneuverability.
		rocket.doForAllThrusters("ME", th -> {
			th.setThrottle(1.0f);
			th.setActive(false);
			System.out.println("Main engine " + th.getName() + " initialized.");
		});
		rocket.getThrusterByName("ME 1").setGimbal(10);
		rocket.getThrusterByName("ME 3").setGimbal(-10);
		System.out.println("Gimbal test started!");
		new Thread(() -> {
			try {
				Thread.sleep(1000);
				rocket.getThrusterByName("ME 1").setGimbal(-10);
				rocket.getThrusterByName("ME 3").setGimbal(10);
				Thread.sleep(1000);
				rocket.getThrusterByName("ME 1").setGimbal(0);
				rocket.getThrusterByName("ME 3").setGimbal(0);
				System.out.println("Gimbal test complete!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		rocket.doForAllThrusters("RCS", rcs -> {
			rcs.setThrottle(rcs.getMaxThrottle());
			rcs.setActive(false);
			System.out.println("RCS thruster " + rcs.getName() + " initialized.");
		});
		System.out.println("All systems initialized for launch!");
	}

	@Override
	public void launch(Rocket rocket) {
		System.out.println("Launching...");
		rocket.doForAllThrusters("ME", t -> t.setActive(true));
	}

	@Override
	public void controlRocket(Rocket rocket, float t) {
		System.out.println(t);
	}
}
