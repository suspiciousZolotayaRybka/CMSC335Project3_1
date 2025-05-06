package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * CarSimulationClock.java
 * Isaac Finehout
 * 5 May 2025
 *
 * This is a class that controls the timer runnable
 * @author fineh
 *
 */
import java.util.Date;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class CarSimulationClock implements Runnable {
	private Label labelCarSimulationClock;
	private final CarSimulationManager carSimulationManager;

	public CarSimulationClock(CarSimulationManager carSimulationManager) {
		labelCarSimulationClock = new Label((new Date()).toString());
		labelCarSimulationClock.setFont(Font.font("Roboto", 20));
		labelCarSimulationClock.setLayoutX(30);
		labelCarSimulationClock.setLayoutY(300);
		this.carSimulationManager = carSimulationManager;
	}

	/**
	 * Update the clock every second
	 */
	@Override
	public void run() {
		try {
			while (carSimulationManager.isSimulationRunning()) {
				// Check for pauses from the pause button
				synchronized (carSimulationManager.getPauseLock()) {
					while (carSimulationManager.isPaused()) {
						carSimulationManager.getPauseLock().wait();
					}
				}
				// update the clock every second
				carSimulationManager.updateCarSimulationClockText();
				Thread.sleep(1000);
			}
		} catch (InterruptedException ie) {
			System.out.println("Interrupted Exception in CarSimulationClock.java, run() method. Stack Trace below");
			ie.printStackTrace();
		}
	}

	/**
	 * @return the labelCarSimulationClock
	 */
	public Label getLabelCarSimulationClock() {
		return labelCarSimulationClock;
	}

	/**
	 *
	 * @param the labelCarSimulationClock to set
	 */
	public void setLabelCarSimulationClock(Label labelCarSimulationClock) {
		this.labelCarSimulationClock = labelCarSimulationClock;
	}
}
