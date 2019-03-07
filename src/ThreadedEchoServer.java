import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class ThreadedEchoServer implements Runnable {
    public static final int PORT = 7777;
    private HashMap<String, Socket> listUser;


    private Socket sock;

    public ThreadedEchoServer(Socket s, HashMap users) {
        System.out.println("nuova richiesta");
        this.sock = s;
        listUser = users;
        System.out.println(s.getInetAddress());
    }

    //    public void open () throws IOException {
//        ServerSocket serv = new ServerSocket(PORT);
//        while (true) {
//            Socket sock = serv.accept();
//            ThreadedEchoServer server = new ThreadedEchoServer(sock);
//            Thread t = new Thread(server);
//            t.start();
//        }
//    }
    public void run() {
        String s = null;
        while (true) {
            sendUpdateListUser();
            BufferedReader brd = null;
                try {
                    brd = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_16));
                    s = brd.readLine();
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
            if (s != null) {
                if (s.contains("<login>")) {
                    //sinc list
                    s = s.replace("<login>", "");
                    if (!listUser.containsKey(s)) {
                        listUser.put(s, sock);
                        prw.println("ack");
                        prw.flush();
                        sendUpdateListUser();
                    } else {
                        prw.println("nack");
                        prw.flush();
//                        try {
//                            this.sock.close();
//                            Thread.currentThread().interrupt();
//                            return;
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                } else if (s.contains("<logout>")) {
                    //sinc list
                    s = s.replace("<logout>", "");
                    listUser.remove(s);
                    sendUpdateListUser();
                    try {
                        sock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Thread.currentThread().interrupt();
                    return;

                } else {
//                user già connesso
                    String[] msg = s.split("-");
                    if (msg[0].equalsIgnoreCase("broadcast")) {
                        for (String user : listUser.keySet()) {
                            try {
                                prw = new PrintWriter(new OutputStreamWriter(listUser.get(user).getOutputStream(), StandardCharsets.UTF_16));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            prw.println(msg[1]);
                            prw.flush();
                        }
                    } else {
                        try {
                            prw = new PrintWriter(new OutputStreamWriter(listUser.get(msg[0]).getOutputStream(), StandardCharsets.UTF_16));
                            prw.println(msg[1]);
                            prw.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(s);
                }

            }
        }
    }

    private void sendUpdateListUser() {
        String users="updateuser-";
        PrintWriter prw =null;
        for (String user : listUser.keySet()) {
            users=users+user+"-";
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
        System.out.println(users);
    }
//        finally {
//            try {
//                sock.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
}

