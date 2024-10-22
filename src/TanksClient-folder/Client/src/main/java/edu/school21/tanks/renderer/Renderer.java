package edu.school21.tanks.renderer;

import edu.school21.tanks.client.Client;
import edu.school21.tanks.models.GameStatement;
import edu.school21.tanks.models.Shot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.lang.Thread.sleep;
import static javafx.application.Platform.runLater;

@Component("renderer")
public class Renderer extends Application {
    private static final Integer WINDOW_WIDTH = 300;
    private static final Integer WINDOW_HEIGHT = 400;
    private ImageView playerTank;
    private ImageView enemyTank;
    private ImageView playerHpBarBorder;
    private ImageView enemyHpBarBorder;
    private Stage stage;
    private boolean isSpacePressed;
    private static Client client;

    public void setClient(Client client) {
        Renderer.client = client;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Scene scene = menuScene();
        stage.setTitle("Tanks");
        stage.setScene(scene);
        stage.show();
    }

    public void render() {
        launch();
    }

    private Scene gameScene() {
        Image playerTankImage = new Image(Objects.requireNonNull(getClass().getResource("/playerTank.png")).toExternalForm());
        playerTank = new ImageView(playerTankImage);
        playerTank.setY(WINDOW_HEIGHT - 20 - playerTankImage.getHeight());
        playerTank.setX(client.getGameStatement().getPositionPlayer1() - 19);

        Image enemyTankImage = new Image(Objects.requireNonNull(getClass().getResource("/enemyTank.png")).toExternalForm());
        enemyTank = new ImageView(enemyTankImage);
        enemyTank.setY(20);
        enemyTank.setX(client.getGameStatement().getPositionPlayer2() - 19);

        Image hpBar = new Image(Objects.requireNonNull(getClass().getResource("/hpBar.png")).toExternalForm());
        playerHpBarBorder = new ImageView(hpBar);
        playerHpBarBorder.setY(WINDOW_HEIGHT - 2 - hpBar.getHeight());
        playerHpBarBorder.setX(2);
        playerHpBarBorder.setScaleX(0.7);
        playerHpBarBorder.setScaleY(0.7);

        enemyHpBarBorder = new ImageView(hpBar);
        enemyHpBarBorder.setY(2);
        enemyHpBarBorder.setX(WINDOW_WIDTH - 2 - hpBar.getWidth());
        enemyHpBarBorder.setScaleX(0.7);
        enemyHpBarBorder.setScaleY(0.7);

        Rectangle playerHpBarFill = new Rectangle();
        playerHpBarFill.setWidth(hpBar.getWidth() * client.getGameStatement().getHpPlayer2() / 100 * 0.65);
        playerHpBarFill.setHeight(hpBar.getHeight() * 0.5);
        playerHpBarFill.setX(playerHpBarBorder.getX() + hpBar.getWidth() * 0.7 / 4);
        playerHpBarFill.setY(playerHpBarBorder.getY() + hpBar.getHeight() * 0.7 / 3.5);
        playerHpBarFill.setFill(Color.RED);

        Rectangle enemyHpBarFill = new Rectangle();
        enemyHpBarFill.setWidth(hpBar.getWidth() * client.getGameStatement().getHpPlayer1() / 100 * 0.65);
        enemyHpBarFill.setHeight(hpBar.getHeight() * 0.5);
        enemyHpBarFill.setX(enemyHpBarBorder.getX() + hpBar.getWidth() * 0.7 / 4);
        enemyHpBarFill.setY(enemyHpBarBorder.getY() + hpBar.getHeight() * 0.7 / 3.5);
        enemyHpBarFill.setFill(Color.RED);
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: green");

        for (Shot shot : client.getGameStatement().getShots()) {
            ImageView shotImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/shot.png")).toExternalForm()));
            shotImageView.setX(shot.getX());
            shotImageView.setY(shot.getY());

            if (!shot.isOnUp()) {
                shotImageView.setRotate(180);
            }

            pane.getChildren().add(shotImageView);
        }
        pane.getChildren().add(playerHpBarFill);
        pane.getChildren().add(playerHpBarBorder);
        pane.getChildren().add(enemyHpBarFill);
        pane.getChildren().add(enemyHpBarBorder);
        pane.getChildren().add(playerTank);
        pane.getChildren().add(enemyTank);


        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT, Color.GREEN);
        handlerKeyboardPress(scene);
        return scene;
    }

    private void showGameOverModal(boolean isWinner) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle(isWinner ? "Победа!" : "Проигрыш!");

        GameStatement gameStatement = client.getGameStatement();

        Label resultLabel = new Label(isWinner ? "Ты победил!" : "Ты проиграл!");
        Label statsLabel = new Label(
                "Статистика:\n" +
                        "Всего выстрелов: " + gameStatement.getPlayerOneTotalShot() + "\n" +
                        "Попаданий: " + (gameStatement.getPlayerOneTotalShot() - gameStatement.getPlayerOneMissingShot()) + "\n" +
                        "Промахов: " + gameStatement.getPlayerOneMissingShot() + "\n\n" +
                        "Всего выстрелов противника: " + gameStatement.getPlayerTwoTotalShot() + "\n" +
                        "Попаданий противника: " + (gameStatement.getPlayerTwoTotalShot() - gameStatement.getPlayerTwoMissingShot()) + "\n" +
                        "Промахов противника: " + gameStatement.getPlayerTwoMissingShot()
        );

        Button closeButton = new Button("Закрыть");
        closeButton.setOnAction(e -> modalStage.close());

        VBox modalLayout = new VBox(10, resultLabel, statsLabel, closeButton);
        modalLayout.setAlignment(Pos.CENTER);

        Scene modalScene = new Scene(modalLayout, 300, 200);
        modalStage.setScene(modalScene);
        modalStage.showAndWait();
    }

    private Scene endScene(boolean isWinner) {
        Label endTextLabel = new Label("Конец игры!");
        if (isWinner) {
            endTextLabel.setText(endTextLabel.getText() + "\nПобеда!");
        } else {
            endTextLabel.setText(endTextLabel.getText() + "\nПроигрыш!");
        }
        endTextLabel.setLayoutX((WINDOW_WIDTH - endTextLabel.getPrefWidth()) / 2);
        endTextLabel.setLayoutY((WINDOW_HEIGHT - endTextLabel.getPrefHeight()) / 2 - 30);

        Button exitButton = new Button("Выход");
        exitButton.setLayoutX((WINDOW_WIDTH - exitButton.getPrefWidth()) / 2);
        exitButton.setLayoutY((WINDOW_HEIGHT - exitButton.getPrefHeight()) / 2 + 30);
        exitButton.setOnAction(event -> System.exit(0));

        VBox mainBox = new VBox(10, endTextLabel, exitButton);
        mainBox.setAlignment(Pos.CENTER);

        mainBox.setStyle("-fx-background-color: green");

        showGameOverModal(isWinner);

        return new Scene(mainBox, WINDOW_WIDTH, WINDOW_HEIGHT, Color.GREEN);
    }

    private void handlerKeyboardPress(Scene scene) {
        isSpacePressed = false;
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    client.sendWord("left");
                    break;
                case RIGHT:
                    client.sendWord("right");
                    break;
                case SPACE:
                    if (!isSpacePressed) {
                        client.sendWord("space");
                        isSpacePressed = true;
                    }
                    break;
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                isSpacePressed = false;
            }
        });
    }

    private Scene menuScene() {
        Label infoLabel = new Label("");
        infoLabel.setVisible(false);

        Label nameLabel = new Label("Ввод имени:");
        TextField nameField = new TextField("Player1");

        Label ipLabel = new Label("Ввод IP для подключения:");
        TextField ipField = new TextField("localhost");

        Label portLabel = new Label("Ввод порта:");
        TextField portField = new TextField("8088");

        Button startButton = new Button("Присоединиться");
        startButton.setOnAction(event -> {
            if (client.connect(ipField.getText(), Integer.parseInt(portField.getText()), nameField.getText())) {
                infoLabel.setText("Ожидание других игроков");
                infoLabel.setTextFill(Color.RED);
                infoLabel.setVisible(true);
                long startTime = System.currentTimeMillis();
                new Thread(() -> {
                    while (!client.getGameStatement().isGameOver()) {
                        if (client.getGameStatement().getHpPlayer1() <= 0 || client.getGameStatement().getHpPlayer2() <= 0) {
                            client.getGameStatement().setGameOver(true);
                            client.getGameStatement().getShots().clear();
                            continue;
                        }
                        client.startGame();
                        runLater(() -> stage.setScene(gameScene()));
                    }
                    System.out.println("Game over");

                    runLater(() -> {
                        stage.setScene(endScene((client.getGameStatement().getHpPlayer1() > 0)));
                        showGameOverModal((client.getGameStatement().getHpPlayer1() > 0));
                    });
                }).start();
            } else {
                infoLabel.setText("Не удалось подключиться на сервер");
                infoLabel.setTextFill(Color.RED);
                infoLabel.setVisible(true);
            }
        });
        startButton.setLayoutX((double) (WINDOW_WIDTH) / 2);
        startButton.setLayoutY((WINDOW_HEIGHT - startButton.getPrefHeight()) / 2);

        Button exitButton = new Button("Выход");
        exitButton.setLayoutX((WINDOW_WIDTH - exitButton.getPrefWidth()) / 2);
        exitButton.setLayoutY((WINDOW_HEIGHT - exitButton.getPrefHeight()) / 2 + 30);
        exitButton.setOnAction(event -> System.exit(0));

        VBox inputBox = new VBox(10, nameLabel, nameField, ipLabel, ipField, portLabel, portField, startButton, exitButton);
        inputBox.setAlignment(Pos.CENTER);
        VBox errorBox = new VBox(10, infoLabel);
        errorBox.setAlignment(Pos.TOP_CENTER);

        VBox mainBox = new VBox(10, errorBox, inputBox);
        mainBox.setAlignment(Pos.CENTER);

        mainBox.setStyle("-fx-background-color: green");

        return new Scene(mainBox, WINDOW_WIDTH, WINDOW_HEIGHT, Color.GREEN);
    }
}
