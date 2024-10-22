package edu.school21.tanks.client;

import edu.school21.tanks.models.GameStatement;
import edu.school21.tanks.renderer.Renderer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import edu.school21.tanks.models.Shot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static java.lang.Thread.sleep;

@Component("client")
public class Client {
    private static Socket clientSocket;
    private static ObjectInputStream in;
    private static PrintWriter out;
    private static BufferedReader reader;
    private PipedOutputStream pipedOutputStream;
    private GameStatement gameStatement;
    String address = "";
    int port = 0;
    private Renderer renderer;

    @Autowired
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void run() {
        gameStatement = new GameStatement();
        renderer.setClient(this);
        renderer.render();
    }

    public GameStatement getGameStatement() {
        return gameStatement;
    }

    public boolean connect(String address, int port, String username) {
        this.address = address;
        this.port = port;
        try {
            clientSocket = new Socket(address, port);

            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
            PipedInputStream pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream(pipedInputStream);
            reader = new BufferedReader(new InputStreamReader(pipedInputStream));

            out.println(username);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void startGame() {
        try {
            gameStatement = (GameStatement) in.readObject();
            System.out.println("Game state received: " + gameStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject getJsonObject(String clientWord) {
        JSONObject jsonToSending = new JSONObject();
        jsonToSending.put("button", clientWord);  //  Сервер понимет значения параметра "button": "left", "right" и "space"
        return jsonToSending;
    }

    public void sendWord(String word) {
        JSONObject jsonToSending = getJsonObject(word);  //  Собрали в json
        out.println(jsonToSending);  //  Отправили
    }
}
