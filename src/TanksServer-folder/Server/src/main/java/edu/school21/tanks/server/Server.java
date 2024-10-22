package edu.school21.tanks.server;


import edu.school21.tanks.exceptions.ServerBusyException;
import edu.school21.tanks.models.GameStatement;
import edu.school21.tanks.models.Match;
import edu.school21.tanks.models.Shot;
import edu.school21.tanks.repositories.GamesRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component("server")
public class Server {
    private final GamesRepository gamesRepository;

    private static ServerSocket server; // серверсокет
    private boolean readyPlayerOne;
    private boolean readyPlayerTwo;
    private ObjectOutputStream PlayerOneOut;  //  Исходящий поток для Игрока 1

    private BufferedReader PlayerOneIn;  //  Входящий поток от игрока 1
    private ObjectOutputStream PlayerTwoOut;  //  Исходящий поток для Игрока 2
    private BufferedReader PlayerTwoIn;  //  Входящий поток от игрока 2
    private String playerOneName;  //  Имя игрока 1
    private String playerTwoName;  //  Имя игрока 2

    public Server(@Qualifier("GamesRepositoryImpl") GamesRepository gamesRepository) throws IOException {
        this.gamesRepository = gamesRepository;
        this.readyPlayerOne = false;  //  Игрок 1 подключен и готов
        this.readyPlayerTwo = false;  //  Игрок 2 подключен и готов
    }


    public void run(ApplicationContext context, int port) {  //  Старт сервера
        System.out.println("Starting server...");
        try {
            server = new ServerSocket(port);
            System.out.println("Waiting for client on port " + port + "...");
            while (true) {  //  ждет подключений
                Socket clientSocket = server.accept();
                Thread thread = new ClientSocketThread(context, clientSocket);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Server start failed");
            e.printStackTrace();
        }
    }

    private class ButtonsStatement {
        private boolean left;
        private boolean right;
        public boolean space;

        public ButtonsStatement(boolean left, boolean right, boolean space) {
            this.left = left;
            this.right = right;
            this.space = space;
        }

        public boolean isLeft() {
            return left;
        }

        public boolean isRight() {
            return right;
        }

        public boolean isSpace() {
            return space;
        }
    }

    private class ClientSocketThread extends Thread {
        final private ApplicationContext context;
        final private Socket clientSocket;

        public ClientSocketThread(ApplicationContext context, Socket clientSocket) {
            this.context = context;
            this.clientSocket = clientSocket;
        }


        public void run() {
            try {
                if (readyPlayerOne && readyPlayerTwo) {
                    throw new ServerBusyException("Server busy");
                }
                if (readyPlayerOne && !readyPlayerTwo) {
                    readyPlayerTwo = true;
                    PlayerTwoOut = new ObjectOutputStream(clientSocket.getOutputStream());
                    PlayerTwoIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    playerTwoName = PlayerTwoIn.readLine();
                    startMatch(playerOneName, playerTwoName, PlayerOneOut, PlayerOneIn, PlayerTwoOut, PlayerTwoIn);
                }
                if (!readyPlayerOne && !readyPlayerTwo) {
                    readyPlayerOne = true;
                    PlayerOneOut = new ObjectOutputStream(clientSocket.getOutputStream());
                    PlayerOneIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    playerOneName = PlayerOneIn.readLine();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void startMatch(String playerOneName, String playerTwoName, ObjectOutputStream playerOneOut, BufferedReader playerOneIn, ObjectOutputStream playerTwoOut, BufferedReader playerTwoIn) {
            boolean gameOver = false;
            Match match = new Match(playerOneName, playerTwoName);
            GameStatement gameStatement = new GameStatement();
            System.out.println(playerOneName + " VS " + playerTwoName);
            int i = 0;
            while (!gameOver) {
                try {
                    if (playerOneIn.ready()) {
                        String button = getButton(playerOneIn.readLine());
                        if (button.equals("space")) {
                            gameStatement.addShot(new Shot(gameStatement.getPositionPlayer1(), 51, true));
                            gameStatement.incrementPlayerOneTotalShot();
                            System.out.println("Player One: space. Statement: " + gameStatement.toString());
                        }
                        if (button.equals("left") && (gameStatement.getPositionPlayer1() > 15)) {
                            gameStatement.setPositionPlayer1(gameStatement.getPositionPlayer1() - 1);
                            System.out.println("Player One: left. Statement: " + gameStatement.toString());
                        }
                        if (button.equals("right") && (gameStatement.getPositionPlayer1() < 285)) {
                            gameStatement.setPositionPlayer1(gameStatement.getPositionPlayer1() + 1);
                            System.out.println("Player One: right. Statement: " + gameStatement.toString());
                        }
                    }
                    if (playerTwoIn.ready()) {
                        String button = getButton(playerTwoIn.readLine());
                        if (button.equals("space")) {
                            gameStatement.addShot(new Shot(gameStatement.getPositionPlayer2(), 349, false));
                            gameStatement.incrementPlayerTwoTotalShot();
                            System.out.println("Player Two: space. Statement: " + gameStatement.toString());
                        }
                        if (button.equals("left") && (gameStatement.getPositionPlayer2() > 15)) {
                            gameStatement.setPositionPlayer2(gameStatement.getPositionPlayer2() - 1);
                            System.out.println("Player Two: left. Statement: " + gameStatement.toString());
                        }
                        if (button.equals("right") && (gameStatement.getPositionPlayer2() < 285)) {
                            gameStatement.setPositionPlayer2(gameStatement.getPositionPlayer2() + 1);
                            System.out.println("Player Two: right. Statement: " + gameStatement.toString());
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                shotMove(gameStatement);
                CollisionCheck(gameStatement);
                gameOver = GameOverCheck(gameStatement);

                try {
                    playerOneOut.writeObject(new GameStatement(gameStatement.getHpPlayer2(), gameStatement.getHpPlayer1(), gameStatement.getPositionPlayer1(), gameStatement.getPositionPlayer2(), gameStatement.getPlayerOneTotalShot(), gameStatement.getPlayerTwoTotalShot(), gameStatement.getPlayerOneMissingShot(), gameStatement.getPlayerTwoMissingShot(), invertShots(gameStatement.getShots()), gameStatement.isGameOver()));  //  Отправка состояния Игроку1

                    playerTwoOut.writeObject(new GameStatement(gameStatement.getHpPlayer1(), gameStatement.getHpPlayer2(), gameStatement.getPositionPlayer2(), gameStatement.getPositionPlayer1(), gameStatement.getPlayerOneTotalShot(), gameStatement.getPlayerTwoTotalShot(), gameStatement.getPlayerOneMissingShot(), gameStatement.getPlayerTwoMissingShot(), realShots(gameStatement.getShots()), gameStatement.isGameOver()));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            saveResult(gameStatement, playerOneName, playerTwoName);
        }

        private List<Shot> realShots(List<Shot> shots) {
            List<Shot> realShots = new ArrayList<>();
            for (Shot shot : shots) {
                if (shot.isOnUp()) {
                    realShots.add(new Shot(shot.getX(), shot.getY(), false));
                } else {
                    realShots.add(new Shot(shot.getX(), shot.getY(), true));
                }
            }
            return realShots;
        }

        private List<Shot> invertShots(List<Shot> shots) {
            List<Shot> invertedShots = new ArrayList<>();
            for (Shot shot : shots) {
                if (shot.isOnUp()) {
                    invertedShots.add(new Shot(shot.getX(), 400 - shot.getY(), true));
                } else {
                    invertedShots.add(new Shot(shot.getX(), 400 - shot.getY(), false));
                }
            }
            return invertedShots;
        }

        private GameStatement invertGameStatement(GameStatement gameStatement) {

            List<Shot> invertedShots = new ArrayList<Shot>();
            for (Shot shot : gameStatement.getShots()) {
                if (shot.isOnUp()) {
                    invertedShots.add(new Shot(shot.getX(), 400 - shot.getY(), false));
                } else {
                    invertedShots.add(new Shot(shot.getX(), 400 - shot.getY(), true));
                }
            }
            GameStatement invertedGameStatement = new GameStatement(gameStatement.getHpPlayer2(), gameStatement.getHpPlayer1(), gameStatement.getPositionPlayer2(), gameStatement.getPositionPlayer1(), gameStatement.getPlayerTwoTotalShot(), gameStatement.getPlayerOneTotalShot(), gameStatement.getPlayerTwoMissingShot(), gameStatement.getPlayerOneMissingShot(), invertedShots, gameStatement.isGameOver());
            return invertedGameStatement;
        }

        private void shotMove(GameStatement gameStatement) {
            Random rn = new Random();
            int randomNum = rn.nextInt(100) + 1;
            for (Shot shot : gameStatement.getShots()) {
                if (randomNum >= 99) {
                    if (shot.isOnUp()) {
                        shot.setXY(shot.getX(), shot.getY() + 1);
                    } else {
                        shot.setXY(shot.getX(), shot.getY() - 1);
                    }
                }
            }
        }

        private String getButton(String s) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = (JSONObject) new JSONParser().parse(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject.get("button").toString();
        }

        private void saveResult(GameStatement gameStatement, String playerOneName, String playerTwoName) {
            gamesRepository.saveResultOfGame(gameStatement, playerOneName, playerTwoName);
        }


        private JSONObject jsonConverter(GameStatement gameStatement, boolean gameOver) {
            JSONObject j = new JSONObject();
            j.put("gameOver", gameOver);
            j.put("playerOnePosition", gameStatement.getPositionPlayer1());
            j.put("playerTwoPosition", gameStatement.getPositionPlayer2());
            j.put("playerOneHp", gameStatement.getHpPlayer1());
            j.put("playerTwoHp", gameStatement.getHpPlayer2());
            j.put("playerOneTotalShot", gameStatement.getPlayerOneTotalShot());
            j.put("playerTwoTotalShot", gameStatement.getPlayerTwoTotalShot());
            j.put("playerOneMissingShot", gameStatement.getPlayerOneMissingShot());
            j.put("playerTwoMissingShot", gameStatement.getPlayerTwoMissingShot());
            j.put("shots", gameStatement.getShots());
            return j;
        }

        private boolean GameOverCheck(GameStatement gameStatement) {
            return gameStatement.getHpPlayer1() <= 0 || gameStatement.getHpPlayer2() <= 0;
        }

        private void CollisionCheck(GameStatement gameStatement) {
            List<Shot> deleteCandidates = new ArrayList<Shot>();
            for (Shot shot : gameStatement.getShots()) {
                if (shot.isOnUp()) {
                    if ((shot.getY() > 350) && (shot.getX() > gameStatement.getPositionPlayer2() - 25) && (shot.getX() < gameStatement.getPositionPlayer2() + 25)) {
                        gameStatement.setHpPlayer2(gameStatement.getHpPlayer2() - 5);
                        deleteCandidates.add(shot);
                    } else if (shot.getY() > 400) {
                        gameStatement.incrementPlayerOneMissingShot();
                        deleteCandidates.add(shot);
                    }
                }
                if (!shot.isOnUp()) {
                    if ((shot.getY() < 50) && (shot.getX() > gameStatement.getPositionPlayer1() - 25) && (shot.getX() < gameStatement.getPositionPlayer1() + 25)) {
                        gameStatement.setHpPlayer1(gameStatement.getHpPlayer1() - 5);
                        deleteCandidates.add(shot);
                    } else if (shot.getY() < 0) {
                        gameStatement.incrementPlayerTwoMissingShot();
                        deleteCandidates.add(shot);
                    }
                }

            }
            for (Shot deleteCandidate : deleteCandidates) {
                gameStatement.getShots().remove(deleteCandidate);
            }
        }


        private ButtonsStatement getButtonsStatement(String message) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = (JSONObject) new JSONParser().parse(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ButtonsStatement((boolean) jsonObject.get("left"), (boolean) jsonObject.get("right"), (boolean) jsonObject.get("space"));
        }
    }
}
