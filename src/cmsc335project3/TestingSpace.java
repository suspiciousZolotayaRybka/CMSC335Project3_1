package cmsc335project3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TestingSpace extends Application implements Runnable {

	private Pane root;
	private Scene scene;
	private static CarSimulationManager carSimulationManager;

	@Override
	public void start(Stage primaryStage) {
		try {

			root = new Pane();
			root.getChildren().add(carSimulationManager.getRoad().getCollisionShapeRoad());

			scene = new Scene(root, 1000, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//		launch(args);
//	}
//
//	/**
//	 * Update the pane, 20 fps (50 ms)
//	 */
//	@Override
	@Override
	public void run() {

		// Sleep until the root is ready
		while (root == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				System.out.println("Interrupted Exception in TestingSpace.java, run() method. Stack Trace below");
				ie.printStackTrace();
			}
		}

//		while (autopilot.getIsSimulationRunning()) {
//			try {
//				// TODO only change if changes are made in objects on panel
//				System.out.println("Updated frame: " + ++count);
//				autopilot.updatePane(root);
//				Thread.sleep(1000);
//			} catch (InterruptedException ie) {
//				System.out.println("Interrupted Exception in TestingSpace.java, run() method. Stack Trace below");
//				ie.printStackTrace();
//			}
//		}

	}
}

//moveButton.setOnAction(new EventHandler<ActionEvent>() {
//	@Override
//	public void handle(ActionEvent e) {
//		Point2D point = cars.get(0).getPositionCar();
//		double newX = point.getX() + 10;
//		Point2D newPoint = new Point2D(newX, point.getY());
//		cars.get(0).setPositionCar(newPoint);
//		System.out.println("PREssed button:positioncar " + cars.get(0).getPositionCar().toString());
//		cars.get(0).updateCollisionShapeCar();
//		root.getChildren().clear();
//
//		// Check for collisions
//		if (cars.get(0).getCollisionShapeCar().getLayoutBounds()
//				.intersects(cars.get(1).getCollisionShapeCar().getLayoutBounds())) {
//			System.out.println("BOOM");
//			Explosion explosion = new Explosion(cars.get(1).getPositionCar());
//			cars.removeAll(cars);
//			root.getChildren().addAll(road.getCollisionShapeRoad(), explosion.getCollisionShapeExplosion());
//		}
//		if (!cars.isEmpty()) {
//			root.getChildren().add(road.getCollisionShapeRoad());
//			cars.forEach(c -> root.getChildren().add(c.getCollisionShapeCar()));
//		}
//		root.getChildren().add(moveButton);
//
//	}
//});