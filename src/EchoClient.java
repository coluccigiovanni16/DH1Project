import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class EchoClient {
    public static final int PORT = 7777;

    private String user;
    private Socket socket;

    public EchoClient() {

    }

    public void login(String name) {
        user = name;
        try {
            socket = new Socket("localhost", PORT);
            //apro un canale e mando un messaggio al server
            OutputStream os = socket.getOutputStream();
            Writer wr = new OutputStreamWriter(os, StandardCharsets.UTF_16);
            PrintWriter prw = new PrintWriter(wr);
            prw.println(user);
            prw.flush();
            InputStream is = socket.getInputStream();
            Reader rd = new InputStreamReader(is, StandardCharsets.UTF_16);
            BufferedReader brd = new BufferedReader(rd);
            String answer = brd.readLine();
            if (answer.equalsIgnoreCase("ack")) {
                while (true) {
                    String receivedFromServer = receiveMessage();
                    if (!receivedFromServer.isEmpty()) {
                        System.out.println(receiveMessage());
                    }
                }
                //autenticazione effettuata aprire nuovo panel con interfaccia per chattare
            } else {
                //stampa "nome utente già utilizzato"
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String receiveMessage() {
        InputStream is = null;
        String answer = null;
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Reader rd = new InputStreamReader(is, StandardCharsets.UTF_16);
        BufferedReader brd = new BufferedReader(rd);
        try {
            answer = brd.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public void logout() {
        //close connection
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMassege(String userReceiver, String msg) {
        // if receiver=="broadcast" massage will send to all users
        // else to the receiver specified by name of user.
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Writer wr = new OutputStreamWriter(os, StandardCharsets.UTF_16);
        PrintWriter prw = new PrintWriter(wr);
        prw.println(user);
        prw.flush();
    }
}