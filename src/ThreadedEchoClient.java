import com.sun.media.jfxmedia.logging.Logger;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ThreadedEchoClient implements Runnable {

    private Socket socket;
    private InputStream is;
    private Reader rd;
    private BufferedReader brd;
    private JTextArea mexText;
    private JList list1;
    private String myUsername;
    private boolean connectionOK;


    public ThreadedEchoClient(Socket socket, JTextArea MESSAGGITextArea, JList list1, String myUsername) {
        this.socket = socket;
        this.mexText = MESSAGGITextArea;
        this.list1 = list1;
        this.myUsername = myUsername;
        this.connectionOK = true;
    }

    @Override
    public void run() {
        while (connectionOK) {
            try {
                brd = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_16));
                String answer = null;
                try {
                    answer = brd.readLine();
                } catch (SocketException e) {
                    stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DefaultListModel model = new DefaultListModel();
                if (answer != null) {
                    String[] receivedFromServer = answer.split("-");
                    if (receivedFromServer[0] != null && receivedFromServer[0].contains("<UPDATEUSERLIST>")) {
                        for (int i = 1; i < receivedFromServer.length; i++) {
                            if (!receivedFromServer[i].equalsIgnoreCase(this.myUsername)) {
                                model.addElement(receivedFromServer[i]);
                            }
                        }
                        list1.setModel(model);
                    } else {
                        this.mexText.append("\n " + answer);
                        this.mexText.setCaretPosition(this.mexText.getDocument().getLength());

                    }
                }
            } catch (SocketException e) {
                try {
                    stop();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void stop() throws IOException {
        // Thread will end safely
        connectionOK = false;
        this.socket.close();
        // Close client connection
        closeConnection();
    }

    private void closeConnection() {
        try {
            if (brd != null) {
                brd.close();
            }
        } catch (Exception e) {
            Logger.logMsg(Level.WARNING.intValue(), e.getMessage());
        }

    }


}
