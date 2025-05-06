package cmsc335project3;

public class CarMover implements Runnable {

	private final CarSimulationManager carSimulationManager;

	public CarMover(CarSimulationManager carSimulationManager) {
		this.carSimulationManager = carSimulationManager;
	}

	@Override
	public void run() {
		try {

			while (carSimulationManager.isSimulationRunning()) {

				Thread.sleep(50);
				carSimulationManager.moveCars();
			}
		} catch (InterruptedException ie) {
			System.out.println("Interrupted Exception in CarMover.java, run() method. Stack Trace below");
			ie.printStackTrace();
		}
	}

}
