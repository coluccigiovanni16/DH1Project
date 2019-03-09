import com.sun.media.jfxmedia.logging.Logger;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
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
        while (connectionOK && !this.socket.isClosed()) {
            try {
                brd = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_16));
                String answer = brd.readLine();
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
                        this.mexText.append("\n " + receivedFromServer[0] + " : " + receivedFromServer[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void stop() {
        // Thread will end safely
        connectionOK = false;
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
