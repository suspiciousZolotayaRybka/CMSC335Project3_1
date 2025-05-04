package cmsc335project3;

// TODO delete this unless special circumstances call for a cleanup thread

public class EndThread implements Runnable {

	private final CarSimulationManager carSimulationManager;

	/**
	 * Constructor for EndThread
	 *
	 * @param the carSimulationManager to set
	 */
	public EndThread(CarSimulationManager carSimulationManager) {
		this.carSimulationManager = carSimulationManager;
	}

	@Override
	public void run() {
		do {

		} while (carSimulationManager.isSimulationRunning());
	}

}
