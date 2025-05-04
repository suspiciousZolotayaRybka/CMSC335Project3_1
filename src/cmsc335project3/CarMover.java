package cmsc335project3;

public class CarMover implements Runnable {

	private final CarSimulationManager carSimulationManager;

	public CarMover(CarSimulationManager carSimulationManager) {
		this.carSimulationManager = carSimulationManager;
	}

	@Override
	public void run() {

		// TODO delete block
		int frameCount = 0;
		int secondCount = 1;
		// TODO delete block

		try {

			while (carSimulationManager.isSimulationRunning()) {

				Thread.sleep(50);
				carSimulationManager.moveCars();
				// TODO delete block
//				System.out.printf("Second=%d,Frame=%d%n", secondCount, frameCount);
				if ((frameCount % 20) == 0) {
					frameCount = 0;
					secondCount++;
					if ((secondCount == 100)) {
						carSimulationManager.setSimulationRunning(false);
					}
				}

				if (carSimulationManager.getCars().size() == 0) {
					carSimulationManager.setSimulationRunning(false);
				}
				frameCount++;
				// TODO delete block

			}
		} catch (InterruptedException ie) {
			System.out.println("Interrupted Exception in CarMover.java, run() method. Stack Trace below");
			ie.printStackTrace();
		}
	}

}
