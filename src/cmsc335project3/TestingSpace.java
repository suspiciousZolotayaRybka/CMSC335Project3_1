package cmsc335project3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TestingSpace extends Application implements Runnable {

	private Pane root;
	private Scene scene;
	private static Autopilot autopilot;

	@Override
	public void start(Stage primaryStage) {
		try {

			root = new Pane();
			root.getChildren().add(autopilot.getRoad().getCollisionShapeRoad());

			scene = new Scene(root, 1000, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the root
	 */
	public Pane getRoot() {
		return root;
	}

	/**
	 * @return the scene
	 */
	public Scene getScene() {
		return scene;
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(Pane root) {
		this.root = root;
	}

	/**
	 * @param scene the scene to set
	 */
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public static void setAutopilot(Autopilot autopilot) {
		TestingSpace.autopilot = autopilot;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void run() {
		launch();
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