package cmsc335project3;

/* CMSC 335 7382 Object-Oriented and Concurrent Programming
 * Professor Amitava Karmaker
 * Project 3
 * CarProducer.java
 * Isaac Finehout
 * 30 April 2025
 *
 * This is the active producer object that creates cars to drive on the roads in
 * Project 3
 * @author fineh
 *
 */

public class CarProducer implements Runnable {

	private final CarSimulationManager carSimulationManager;
	// TODO verify this is the best data structure
	// Queue FIFO may be more efficient

	/**
	 * @param ap   the autopilot object, the passive concurrency class that controls
	 *             critical sections of code.
	 * @param cars the array list for cars in the simulation
	 */
	public CarProducer(CarSimulationManager carSimulationManager) {
		this.carSimulationManager = carSimulationManager;
	}

	/**
	 * The run method for the CarProducer, active class in the concurrency project 3
	 */
	@Override
	public void run() {
		try {
			carSimulationManager.putCarsInSimulation();
			carSimulationManager.setIsProducerProducing(false);
		} finally {
//			carSimulationManager.setIsProducerProducing(false);
			// TODO delete print statements
			System.out.println("Producer Finished");
		}
	}

	/**
	 *
	 * @return the autopilot
	 */
	public CarSimulationManager getAutopilot() {
		return carSimulationManager;
	}

}
