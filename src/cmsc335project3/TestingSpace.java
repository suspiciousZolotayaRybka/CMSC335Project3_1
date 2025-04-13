package cmsc335project3;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TestingSpace extends Application {

	private Pane root;
	private Button moveButton;
	private Car car;
	private Scene scene;
	private Rectangle rectangle;

	@Override
	public void start(Stage primaryStage) {
		try {

			rectangle = new Rectangle();
			rectangle.setX(50);
			rectangle.setY(50);
			rectangle.setWidth(200);
			rectangle.setHeight(100);
			rectangle.setArcWidth(20);
			rectangle.setArcHeight(20);

			moveButton = new Button("Move car right");
			car = startCar();

			moveButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					Point2D point = car.getPositionCar();
					double newX = point.getX() + 10;
					Point2D newPoint = new Point2D(newX, point.getY());
					car.setPositionCar(newPoint);
					System.out.println("PREssed button:positioncar " + car.getPositionCar().toString());
					car.updateCollisionShapeCar();
					root.getChildren().clear();
					root.getChildren().addAll(car.getCollisionShapeCar(), moveButton);
				}
			});

			root = new Pane();
			root.getChildren().addAll(car.getCollisionShapeCar(), moveButton);

			scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Car startCar() {
		Point2D point2D = new Point2D(0, 150);
		Color color = Color.RED;
		double speed = 20;
		Velocity velocity = Velocity.EAST_SLOW;
		return new Car(point2D, color, speed, velocity);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
