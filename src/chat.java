import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class chat {
    private JButton submitButton;
    private JTextField testoMessaggioTextField;
    private JComboBox comboBox1;
    private JPanel rootPanel;
    private JList list1;
    private JButton LOGOUTButton;
    private JTextArea textArea1;
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
    private JFrame frame;

    public chat() {
        checkUsername();
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMassege();
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
            this.socket = new Socket("localhost", PORT);
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
//                System.out.println("utente " + user + " aggiunto");
                frame = new JFrame("CHAT");
                frame.setContentPane(rootPanel);
                frame.pack();
                testoMessaggioTextField.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        testoMessaggioTextField.setText("");
                    }
                });
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        logout();
                    }
                });
                frame.setVisible(true);
                ThreadedEchoClient client = new ThreadedEchoClient(socket, textArea1, list1, this.user);
                t = new Thread(client);
                t.start();
                textArea1.setText("SERVER: Ciao " + this.user + " benvenuto nella chat :-)");
                return true;
                //autenticazione effettuata aprire nuovo panel con interfaccia per chattare
            } else if (answer.equalsIgnoreCase("nack")) {
                //stampa "nome utente già utilizzato"
//                System.out.println("utente " + user + " non aggiunto");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void logout() {
        //close connection
        int option = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to close the application?",
                "Close Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {

            try {
                prw.println("<LOGOUT>" + user);
                prw.flush();
                t.stop();
                socket.close();
                frame.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMassege() {
        if (comboBox1.getSelectedIndex() == 0 && list1.getSelectedIndex() != -1) {
            prw.println("<ONETOONE>-<" + list1.getSelectedValue().toString() + ">-<" + testoMessaggioTextField.getText() + ">");
            prw.flush();

        } else if (comboBox1.getSelectedIndex() == 1) {
            prw.println("<BROADCAST>-<" + user + ">-<" + testoMessaggioTextField.getText() + ">");
            prw.flush();

        }
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