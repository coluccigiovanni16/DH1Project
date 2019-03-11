import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class ThreadedEchoServer implements Runnable {
    private HashMap<String, Socket> listUser;
    private String username;
    private boolean connectionOK;
    private BufferedReader brd;
    private PrintWriter prw;
    private Socket socket;

    /**
     * @param s     socket in collegamento con il client
     * @param users mappa degli user online compresa di socket
     */
    public ThreadedEchoServer(Socket s, HashMap users) {
        this.socket = s;
        this.listUser = users;
        this.connectionOK = true;
    }


    /**
     * Ciclo continuo lato server che rimane in ascolto di un possibile messaggio in arrivo dal client a cui è 'collegato',
     * il ciclo si ferma se la connessione viene fermata o se il canale si interrompe
     */
    public void run() {
        String received = null;
        while (connectionOK && this.socket.isConnected()) {
            try {
                this.brd = new BufferedReader( new InputStreamReader( this.socket.getInputStream(), StandardCharsets.UTF_16 ) );
                received = this.brd.readLine();
            } catch (IOException e) {
                stop();
            }

            if (received != null) {
                if (received.split( "-" )[0].equals( "<LOGIN>" )) {
                    synchronized (this.listUser) {
                        received = received.split( "-" )[1];
                        if (!this.listUser.containsKey( received )) {
                            this.listUser.put( received, this.socket );
                            this.username = received;
                            try {
                                this.prw = new PrintWriter( new OutputStreamWriter( this.socket.getOutputStream(), StandardCharsets.UTF_16 ) );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            this.prw.println( "<ACK>" );
                            this.prw.flush();
                            sendUpdateListUser();
                        } else {
                            try {
                                this.prw = new PrintWriter( new OutputStreamWriter( this.socket.getOutputStream(), StandardCharsets.UTF_16 ) );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            this.prw.println( "<NACK>" );
                            this.prw.flush();
                        }
                    }
                } else if (received.split( "-" )[0].equals( "<LOGOUT>" )) {
                    synchronized (this.listUser) {
                        received = received.split( "-" )[1];
                        this.listUser.remove( received );
                        sendUpdateListUser();
                        stop();
                    }
                } else if (received.equals( "<UPDATEUSERLIST>" )) {
                    synchronized (this.listUser) {
                        sendUpdateListUser();
                    }

                } else {
                    received = received.replace( "<", "" ).replace( ">", "" );
                    String[] mexFromUser = received.split( "-" );
                    synchronized (this.listUser) {
                        if (mexFromUser[0].equalsIgnoreCase( "BROADCAST" )) {
                            for (String user : this.listUser.keySet()) {
                                try {
                                    this.prw = new PrintWriter( new OutputStreamWriter( this.listUser.get( user ).getOutputStream(), StandardCharsets.UTF_16 ) );
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                this.prw.println( "<BROADCAST> " + this.username + " : " + mexFromUser[2] );
                                this.prw.flush();
                            }
                        } else if (mexFromUser[0].equalsIgnoreCase( "ONETOONE" )) {
                            try {
                                this.prw = new PrintWriter( new OutputStreamWriter( this.listUser.get( mexFromUser[1] ).getOutputStream(), StandardCharsets.UTF_16 ) );
                                this.prw.println( "<ONETOONE> " + this.username + " : " + mexFromUser[2] );
                                this.prw.flush();
                                this.prw = new PrintWriter( new OutputStreamWriter( this.socket.getOutputStream(), StandardCharsets.UTF_16 ) );
                                this.prw.println( "<ONETOONE> " + this.username + " : " + mexFromUser[2] );
                                this.prw.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Metodo richiamato per fermare il ciclo continuo del suddetto Thread,
     * richiamata in caso di logout o di eccezione legata alla comunicazione.
     * Agisce settando a false la variabile connectionOK e chiudendo eventualmente la socket
     */
    public void stop() {
        // Thread will end safely
        this.connectionOK = false;
        if (!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Close client connection
    }


    /**
     * Invia un messagio a tutti gli user online(grazie alla mappa listUser) in un preciso formato,
     * grazie al quale possono aggiornare la lista degli user ancora online con cui è possibile chattare
     */
    private void sendUpdateListUser() {
        String users = "<UPDATEUSERLIST>";
        for (String user : this.listUser.keySet()) {
            users = users + "-" + user;
        }
        for (String user : this.listUser.keySet()) {
            try {
                this.prw = new PrintWriter( new OutputStreamWriter( this.listUser.get( user ).getOutputStream(), StandardCharsets.UTF_16 ) );
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.prw.println( users );
            this.prw.flush();
        }
    }
}

