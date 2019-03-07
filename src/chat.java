import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class chat {
    private JButton submitButton;
    private JTextField testoMessaggioTextField;
    private JTextPane MESSAGGITextPane;
    private JComboBox comboBox1;
    private JPanel rootPanel;
    private JList list1;
    private JButton LOGOUTButton;
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
    private Thread t;
    private JFrame frame;

    public chat() throws IOException {
        checkUsername();
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (list1.getSelectedValue().toString().equalsIgnoreCase("one-to-one")) {
                    sendMassege("<ONETOONE>","<"+list1.getSelectedValue().toString()+">", "<"+testoMessaggioTextField.getText()+">");
                } else if (list1.getSelectedValue().toString().equalsIgnoreCase("broadcast")) {
                    sendMassege("<BROADCAST>","<"+user+">", "<"+testoMessaggioTextField.getText()+">");
                }
            }
        });
        LOGOUTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }

    public boolean login() {
        try {
            this.socket = new Socket("localhost", 7777);
            //apro un canale e mando un messaggio al server
            os = socket.getOutputStream();
            wr = new OutputStreamWriter(os, StandardCharsets.UTF_16);
            prw = new PrintWriter(wr);
            prw.println("<LOGIN>" + user);
            prw.flush();
            is = socket.getInputStream();
            rd = new InputStreamReader(is, StandardCharsets.UTF_16);
            brd = new BufferedReader(rd);
            String answer = brd.readLine();
            System.out.println(answer);
            if (answer.equalsIgnoreCase("ack")) {
                System.out.println("utente " + user + " aggiunto");
                frame = new JFrame("CHAT");
                frame.setContentPane(rootPanel);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        System.out.println();
                        logout();
                    }
                });
                frame.setVisible(true);
                flagSend = true;
                ThreadedEchoClient client = new ThreadedEchoClient(socket, MESSAGGITextPane, list1);
                t = new Thread(client);
                t.start();
                return true;
                //autenticazione effettuata aprire nuovo panel con interfaccia per chattare
            } else if (answer.equalsIgnoreCase("nack")) {
                //stampa "nome utente già utilizzato"
                System.out.println("utente " + user + " non aggiunto");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public void receiveMessage() {
//        String answer = "";
//        try {
//            answer = brd.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (answer.contains("updateuser")) {
//            answer.replace("updateuser-", "");
//            answer.substring(0, answer.length() - 2);
//            String[] users = answer.split("-");
//            DefaultListModel model = new DefaultListModel();
//            listUsers.setText("UTENTI");
//            for (String user : users) {
//                listUsers.setText(listUsers.getText() + "\n" + user);
//                System.out.println(listUsers.getText());
//            }
//        }
//        System.out.println(answer);
//    }


    public void logout() {
        //close connection
        try {
            prw.println("<LOGOUT>" + user);
            prw.flush();
            t.stop();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMassege(String type,String userReceiver, String msg) {
        prw.println(type+"-"+userReceiver + "-" + msg);
        prw.flush();
    }

    public void checkUsername() {
        boolean userValid = false;
        while (!userValid) {
            JLabel label_login = new JLabel("Inserisci username:");
            JTextField login = new JTextField();

            Object[] array = {label_login, login};
            int res = JOptionPane.showConfirmDialog(null, array, "Login",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (res == JOptionPane.OK_OPTION && login.getText().trim() != null) {
                System.out.println("username: " + login.getText().trim());
                this.user = login.getText().trim();
                userValid = login();
                if (!userValid) {
                    label_login = new JLabel("Username già in uso riprova.....:");
                    array = new Object[]{label_login, login};
                    res = JOptionPane.showConfirmDialog(null, array, "Login",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);
                }
            }
        }

    }
}