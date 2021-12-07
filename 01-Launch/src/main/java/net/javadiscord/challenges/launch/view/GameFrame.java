package net.javadiscord.challenges.launch.view;

import net.javadiscord.challenges.launch.control.GameUpdater;
import net.javadiscord.challenges.launch.model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {

	public GameFrame(GameModel model) {
		super("Launch");
		setPreferredSize(new Dimension(800, 600));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		var panel = new GamePanel(model);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);

		var updater = new GameUpdater(model, panel);
		updater.start();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				updater.setRunning(false);
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					model.startLaunch();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					model.abortLaunch();
				} else if (e.getKeyCode() == KeyEvent.VK_A) {
					model.getRocket().getThrusterByName("RCS Top Right").setActive(true);
				} else if (e.getKeyCode() == KeyEvent.VK_D) {
					model.getRocket().getThrusterByName("RCS Top Left").setActive(true);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_A) {
					model.getRocket().getThrusterByName("RCS Top Right").setActive(false);
				} else if (e.getKeyCode() == KeyEvent.VK_D) {
					model.getRocket().getThrusterByName("RCS Top Left").setActive(false);
				}
			}
		});
	}
}
