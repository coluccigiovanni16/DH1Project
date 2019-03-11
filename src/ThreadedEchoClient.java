import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ThreadedEchoClient implements Runnable {

    private Socket socket;
    private BufferedReader brd;
    private JTextArea mexText;
    private JList list1;
    private String myUsername;
    private boolean connectionOK;


    /**
     * @param socket
     * @param MESSAGGITextArea
     * @param list1
     * @param myUsername
     */
    public ThreadedEchoClient(Socket socket, JTextArea MESSAGGITextArea, JList list1, String myUsername) {
        this.socket = socket;
        this.mexText = MESSAGGITextArea;
        this.list1 = list1;
        this.myUsername = myUsername;
        this.connectionOK = true;
        try {
            this.brd = new BufferedReader( new InputStreamReader( this.socket.getInputStream(), StandardCharsets.UTF_16 ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        while (this.connectionOK) {
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
                String[] receivedFromServer = answer.split( "-" );
                if (receivedFromServer[0] != null && receivedFromServer[0].contains( "<UPDATEUSERLIST>" )) {
                    for (int i = 1; i < receivedFromServer.length; i++) {
                        if (!receivedFromServer[i].equalsIgnoreCase( this.myUsername )) {
                            model.addElement( receivedFromServer[i] );
                        }
                    }
                    list1.setModel( model );
                } else {
                    this.mexText.append( "\n " + answer );
                    this.mexText.setCaretPosition( this.mexText.getDocument().getLength() );

                }
            }
        }

    }

    /**
     *
     */
    public void stop() {
        // Thread will end safely
        this.connectionOK = false;
        // Close client connection
        closeConnection();
    }

    /**
     *
     */
    private void closeConnection() {
        try {
            if (!this.socket.isConnected()) {
                this.socket.close();
            }
            if (this.brd != null) {
                this.brd.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
