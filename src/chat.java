import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class chat {
    private JButton submitButton;
    private JTextField testoMessaggioTextField;
    private JTextPane MESSAGGITextPane;
    private JComboBox comboBox1;
    private JPanel rootPanel;
    private JTextPane listUsers;
    public static final int PORT = 7777;
    private String user;
    private Socket socket;
    private OutputStream os;
    private Writer wr;
    private PrintWriter prw;
    private InputStream is;
    private Reader rd;
    private BufferedReader brd;
    private boolean flagSend;

    public chat() throws IOException {
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
            System.out.println(answer);
            if (answer.equalsIgnoreCase("ack")) {
                System.out.println("utente " + user + " aggiunto");
                JFrame frame = new JFrame("CHAT");
                frame.setContentPane(new chat().rootPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
                flagSend = true;
                //autenticazione effettuata aprire nuovo panel con interfaccia per chattare
            } else if (answer.equalsIgnoreCase("nack")) {
                //stampa "nome utente già utilizzato"
                System.out.println("utente " + user + " non aggiunto");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiveMessage();
    }

    public void receiveMessage() {
        while (flagSend) {
            String answer = "";
            try {
                answer = brd.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (answer.contains("updateuser")) {
                answer.replace("updateuser-", "");
                answer.substring(0, answer.length() - 2);
                String[] users = answer.split("-");
                DefaultListModel model = new DefaultListModel();
                listUsers.setText("UTENTI");
                for (String user : users) {
                    listUsers.setText(listUsers.getText() + "\n" + user);
                    System.out.println(listUsers.getText());
                }
            }
            System.out.println(answer);
        }
    }

    public void logout() {
        //close connection
        try {
            os = socket.getOutputStream();
            wr = new OutputStreamWriter(os, StandardCharsets.UTF_16);
            prw = new PrintWriter(wr);
            prw.println("<logout>" + user);
            prw.flush();
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
        prw.println(userReceiver + "-" + msg);
        prw.flush();
    }
}