import com.sun.media.jfxmedia.logging.Logger;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;


public class chat {
    public static final int PORT = 7777;
    private JButton submitButton;
    private JTextField testoMessaggioTextField;
    private JComboBox comboBox1;
    private JPanel rootPanel;
    private JList list1;
    private JButton LOGOUTButton;
    private JTextArea textArea1;
    private JLabel usernameLabel;
    private String user;
    private Socket socket = null;
    private OutputStream os;
    private Writer wr;
    private PrintWriter prw;
    private InputStream is;
    private Reader rd;
    private BufferedReader brd;
    private Thread t;
    private JFrame frame;
    private String IpServer = "localhost";
    private ThreadedEchoClient client;


    public chat() throws IOException {
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
                try {
                    logout();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public boolean login() {
        try {
            //apro un canale e mando un messaggio al server
            os = this.socket.getOutputStream();
            wr = new OutputStreamWriter(os, StandardCharsets.UTF_16);
            prw = new PrintWriter(wr);
            prw.println("<LOGIN>-" + this.user);
            prw.flush();
            is = this.socket.getInputStream();
            rd = new InputStreamReader(is, StandardCharsets.UTF_16);
            brd = new BufferedReader(rd);
            String answer = brd.readLine();
            if (answer.equals("<ACK>")) {
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
                        try {
                            logout();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                frame.setVisible(true);
                usernameLabel.setText("USERNAME : " + this.user);
                client = new ThreadedEchoClient(this.socket, textArea1, list1, this.user);
                t = new Thread(client);
                t.start();
                textArea1.setText("SERVER: Ciao " + this.user + " benvenuto nella chat :-)");
                prw.println("<UPDATEUSERLIST>");
                prw.flush();
                return true;
                //autenticazione effettuata aprire nuovo panel con interfaccia per chattare
            } else if (answer.equals("<NACK>")) {
                //stampa "nome utente già utilizzato"
//                System.out.println("utente " + user + " non aggiunto");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void logout() throws IOException {
        //close connection
        int option = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to close the application?",
                "Close Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            prw.println("<LOGOUT>-" + user);
            prw.flush();
            frame.dispose();
            if (this.socket != null) {
                this.socket.close();
            }
            closeConnection();
            client.stop();
        }
    }

    private void closeConnection() {
        try {
            if (prw != null) {
                prw.print("CONNECTION_TERMINATED");
                prw.close();
            }
            if (brd != null) {
                brd.close();
            }
        } catch (Exception e) {
            Logger.logMsg(Level.WARNING.intValue(), e.getMessage());
        }

    }

    public void sendMassege() {
        if (!testoMessaggioTextField.getText().equals("")) {
            if (comboBox1.getSelectedIndex() == 0 && list1.getSelectedIndex() != -1) {
                prw.println("<ONETOONE>-<" + list1.getSelectedValue().toString() + ">-<" + testoMessaggioTextField.getText() + ">");
                prw.flush();

            } else if (comboBox1.getSelectedIndex() == 1) {
                prw.println("<BROADCAST>-<" + user + ">-<" + testoMessaggioTextField.getText() + ">");
                prw.flush();

            }
        }
    }

    public void checkUsername() throws IOException {
        boolean userValid = false;
        boolean ipvalid = false;
        while (!userValid || !ipvalid) {
            userValid = false;
            ipvalid = false;
            JLabel label_login = new JLabel("Inserisci username:");
            JTextField login = new JTextField();
            JLabel label_ip = new JLabel("Inserisci ip del server");
            JTextField ip = new JTextField();
            ip.setText("localhost");
            Object[] array = {label_login, login, label_ip, ip};
            int res = JOptionPane.showConfirmDialog(null, array, "Login",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION && !login.getText().trim().equals("") && !ip.getText().equals("") && login.getText().trim() != null && ip.getText().trim() != null) {
                String userTemp = login.getText().trim();
                this.IpServer = ip.getText();
                if (this.socket == null) {
                    this.socket = new Socket(IpServer, PORT);
                }
                ipvalid = true;
                if (ipvalid) {
                    this.user = userTemp;
                    userValid = login();
                    if (!userValid) {
                        JOptionPane.showMessageDialog(null, "User gia in uso");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Ip errato o server Offline");
                }


            } else if (res == JOptionPane.CANCEL_OPTION || res == JOptionPane.CLOSED_OPTION) {
                if (this.socket != null) {
                    this.socket.close();
                }
                System.exit(1);

            }
        }

    }

}