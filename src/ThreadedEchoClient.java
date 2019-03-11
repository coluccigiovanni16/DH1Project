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
     * @param socket           socket che caratterizza il canale tra client e server
     * @param MESSAGGITextArea campo della GUI in cui sono mostrati i messaggi ricevuti
     * @param list1            campo della GUI in cui viene mostrata la lista degli utenti ancora connessi
     * @param myUsername       username dell'utente a cui è legata la socket
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
     * Ciclo continuo lato server che rimane in ascolto di un possibile messaggio in arrivo dal server a cui è 'collegato',
     * il ciclo si ferma se la connessione viene fermata o se il canale si interrompe;
     * i messaggi possono provenire da altri utenti o mandati dal server per aggiornare la lista utenti
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
     *Metodo richiamato per fermare il ciclo continuo del suddetto Thread,
     * richiamata in caso di eccezione legata alla comunicazione.
     * Agisce settando a false la variabile connectionOK e chiudendo eventualmente la socket richiamanto il metodo closeConnection().
     */
    public void stop() {
        // Thread will end safely
        this.connectionOK = false;
        // Close client connection
        closeConnection();
    }

    /**
     *Metodo utilizzato per chiudere il canale
     */
    private void closeConnection() {
        try {
            if (this.brd != null) {
                this.brd.close();
            }
            if (!this.socket.isClosed()) {
                this.socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
