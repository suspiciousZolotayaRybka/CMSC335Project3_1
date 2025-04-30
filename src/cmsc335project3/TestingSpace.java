package cmsc335project3;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestingSpace extends Application {

	private static Pane root;
	private static final ArrayList<Car> cars = new ArrayList<Car>();
	private static Scene scene;
	private static Road road;

	@Override
	public void start(Stage primaryStage) {
		try {

			cars.add(startCarInd0());
			cars.add(startCarInd1());

			road = new Road(1000, new Point2D(0, 150));

			root = new Pane();
			root.getChildren().addAll(road.getCollisionShapeRoad(), cars.get(0).getCollisionShapeCar());
			(new Thread(cars.get(0))).start();

			scene = new Scene(root, 1000, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Car startCarInd0() {
		Point2D point2D = new Point2D(0, 175);
		Color color = Color.RED;
		Velocity velocity = Velocity.EAST_SLOW;
		return new Car(point2D, color, velocity);
	}

	private static Car startCarInd1() {
		Point2D point2D = new Point2D(180, 125);
		Color color = Color.BLUE;
		Velocity velocity = Velocity.WEST_SLOW;
		return new Car(point2D, color, velocity);
	}

	public static Pane getRoot() {
		return root;
	}

	public static ArrayList<Car> getCars() {
		return cars;
	}

	public static Road getRoad() {
		return road;
	}

	public static void main(String[] args) {
		launch(args);
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