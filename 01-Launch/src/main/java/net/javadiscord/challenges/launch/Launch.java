package net.javadiscord.challenges.launch;

import net.javadiscord.challenges.launch.model.GameModel;
import net.javadiscord.challenges.launch.view.GameFrame;

public class Launch {
	public static void main(String[] args) {
		var model = new GameModel(new SimpleLaunchGuidance());
		var frame = new GameFrame(model);
		frame.setVisible(true);
	}
}
