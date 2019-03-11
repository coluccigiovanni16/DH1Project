import com.sun.media.jfxmedia.logging.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Level;


public class ThreadedEchoServer implements Runnable {
    public static final int PORT = 7777;
    private HashMap<String, Socket> listUser;
    private String username;
    private boolean connectionOK;
    private BufferedReader brd;
    private PrintWriter prw;
    private Socket sock;

    public ThreadedEchoServer(Socket s, HashMap users) {
//        System.out.println("nuova richiesta");
        this.sock = s;
        listUser = users;
        this.connectionOK = true;
//        System.out.println(s.getInetAddress());
    }


    public void run() {
        String received = null;
        while (connectionOK && this.sock.isConnected()) {
            //sendUpdateListUser();
            try {
                brd = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_16));
                received = brd.readLine();
            } catch (SocketException e) {
                stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter prw = null;
            try {
                prw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_16));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            controlla se l'username è già utilizzato
            if (received != null) {
                if (received.split("-")[0].equals("<LOGIN>")) {
                    //sinc list
                    synchronized (listUser) {
                        received = received.split("-")[1];
                        if (!listUser.containsKey(received)) {
                            listUser.put(received, this.sock);
                            this.username = received;
                            prw.println("<ACK>");
                            prw.flush();

                        } else {
                            prw.println("<NACK>");
                            prw.flush();
                        }
                        try {
                            sendUpdateListUser();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (received.split("-")[0].equals("<LOGOUT>")) {
                    //sinc list
                    synchronized (listUser) {
                        received = received.split("-")[1];
                        listUser.remove(received);
                        try {
                            sendUpdateListUser();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        stop();
                    }
                } else if (received.equals("<UPDATEUSERLIST>")) {
                    //sinc list
                    try {
                        sendUpdateListUser();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
//                user già connesso
                    received = received.replace("<", "").replace(">", "");
                    String[] mexFromUser = received.split("-");
                    synchronized (listUser) {
//                        System.out.println(listUser.keySet());
                        if (mexFromUser[0].equalsIgnoreCase("BROADCAST")) {
                            for (String user : listUser.keySet()) {
                                try {
                                    prw = new PrintWriter(new OutputStreamWriter(listUser.get(user).getOutputStream(), StandardCharsets.UTF_16));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                prw.println("<BROADCAST> "+this.username + " : " + mexFromUser[2]);
                                prw.flush();
                            }
                        } else if (mexFromUser[0].equalsIgnoreCase("ONETOONE")) {
                            try {
                                prw = new PrintWriter(new OutputStreamWriter(listUser.get(mexFromUser[1]).getOutputStream(), StandardCharsets.UTF_16));
                                prw.println("<ONETOONE> "+this.username + " : " + mexFromUser[2]);
                                prw.flush();
                                prw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_16));
                                prw.println("<ONETOONE> "+this.username + " : " + mexFromUser[2]);
                                prw.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }


    public void stop() {
        // Thread will end safely
        connectionOK = false;
        try {
            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Close client connection
        closeConnection();
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


    private void sendUpdateListUser() throws InterruptedException {
        Thread.sleep(10);
        String users = "<UPDATEUSERLIST>";
        PrintWriter prw = null;
        synchronized (listUser) {
            for (String user : listUser.keySet()) {
                users = users + "-" + user;
            }
            for (String user : listUser.keySet()) {
                try {
                    prw = new PrintWriter(new OutputStreamWriter(listUser.get(user).getOutputStream(), StandardCharsets.UTF_16));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                prw.println(users);
                prw.flush();
            }
        }
//        System.out.println(users);
    }
}

