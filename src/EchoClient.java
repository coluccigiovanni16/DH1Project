import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class EchoClient {
    public static final int PORT = 7777;

    private String user;
    private Socket socket;
    private OutputStream os;
    private Writer wr;
    private PrintWriter prw;
    private InputStream is;
    private Reader rd;
    private BufferedReader brd;
    private Thread t;

    public EchoClient() throws IOException {
    }

    public void login(String name) {
        try {
            this.user = name;
            this.socket = new Socket("localhost", 7777);
            //apro un canale e mando un messaggio al server
            os = socket.getOutputStream();
            wr = new OutputStreamWriter(os, StandardCharsets.UTF_16);
            prw = new PrintWriter(wr);
            prw.println("<login>" + user);
            prw.flush();
            is = socket.getInputStream();
            rd = new InputStreamReader(is, StandardCharsets.UTF_16);
            brd = new BufferedReader(rd);
            String answer = brd.readLine();
            if (answer.equalsIgnoreCase("ack")) {
                System.out.println("utente " + user + " aggiunto");
                //autenticazione effettuata aprire nuovo panel con interfaccia per chattare
                //avvio thread ascolto
                ThreadedEchoClient client= new ThreadedEchoClient(socket);
                t = new Thread(client);
                t.start();
            } else if (answer.equalsIgnoreCase("nack")) {
                //stampa "nome utente già utilizzato"
                System.out.println("utente " + user + " non aggiunto");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        String answer = "";
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
            os = socket.getOutputStream();
            wr = new OutputStreamWriter(os, StandardCharsets.UTF_16);
            prw = new PrintWriter(wr);
            prw.println("<logout>" + user);
            prw.flush();
            t.stop();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMassege(String userReceiver, String msg) {
        // if receiver=="broadcast" massage will send to all users
        // else to the receiver specified by name of user.
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        prw.println(userReceiver + "-" +user+": "+ msg);
        prw.flush();
    }
}