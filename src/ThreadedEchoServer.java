import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class ThreadedEchoServer implements Runnable {
    public static final int PORT = 7777;
    private HashMap<String, Socket> listUser;
    private String username;


    private Socket sock;

    public ThreadedEchoServer(Socket s, HashMap users) {
//        System.out.println("nuova richiesta");
        this.sock = s;
        listUser = users;
//        System.out.println(s.getInetAddress());
    }


    public void run() {
        String received = null;
        while (true) {
            //sendUpdateListUser();
            BufferedReader brd = null;
            try {
                brd = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_16));
                received = brd.readLine();
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
                if (received.contains("<LOGIN>")) {
                    //sinc list
                    synchronized (listUser) {
                        received = received.replace("<LOGIN>", "");
                        if (!listUser.containsKey(received)) {
                            listUser.put(received, sock);
                            this.username = received;
                            prw.println("ack");
                            prw.flush();
                            sendUpdateListUser();
                        } else {
                            prw.println("nack");
                            prw.flush();
                        }
                    }
                } else if (received.contains("<LOGOUT>")) {
                    //sinc list
                    synchronized (listUser) {
                        received = received.replace("<LOGOUT>", "");
                        listUser.remove(received);
                        sendUpdateListUser();
                        try {
                            sock.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Thread.currentThread().interrupt();
                        return;
                    }
                } else {
//                user già connesso
                    received = received.replace("<", "").replace(">", "");
                    String[] msg = received.split("-");
                    synchronized (listUser) {
//                        System.out.println(listUser.keySet());
                        if (msg[0].equalsIgnoreCase("BROADCAST")) {
                            for (String user : listUser.keySet()) {
                                try {
                                    prw = new PrintWriter(new OutputStreamWriter(listUser.get(user).getOutputStream(), StandardCharsets.UTF_16));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                prw.println(this.username + "-" + msg[2]);
                                prw.flush();
                            }
                        } else if (msg[0].equalsIgnoreCase("ONETOONE")) {
                            try {
                                prw = new PrintWriter(new OutputStreamWriter(listUser.get(msg[1]).getOutputStream(), StandardCharsets.UTF_16));
                                prw.println(this.username + "-" + msg[2]);
                                prw.flush();
                                prw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_16));
                                prw.println(this.username + "-" + msg[2]);
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

    private void sendUpdateListUser() {
        String users = "updateuser-";
        PrintWriter prw = null;
        for (String user : listUser.keySet()) {
                users = users + user + "-";
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
//        System.out.println(users);
    }
}

