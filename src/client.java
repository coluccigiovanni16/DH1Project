import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class client {
    public static final int PORT = 7777;

    public static void main(String[] argv) throws Exception {
        Socket sock = new Socket("localhost", PORT);
        OutputStream os = sock.getOutputStream();
        Writer wr = new OutputStreamWriter(os, StandardCharsets.UTF_16);
        PrintWriter prw = new PrintWriter(wr);
        InputStream is = sock.getInputStream();
        Reader rd = new InputStreamReader(is, StandardCharsets.UTF_16);
        BufferedReader brd = new BufferedReader(rd);
        System.out.println(sock.getInetAddress());
        Scanner s=new Scanner(System.in);
        String toserv=s.nextLine();
        while (!toserv.equalsIgnoreCase("logout")) {
            prw.println(toserv);
            prw.flush();
            String answer = brd.readLine();
            System.out.println(answer);
            toserv=s.nextLine();
        }
    }
}