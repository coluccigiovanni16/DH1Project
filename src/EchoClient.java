import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class EchoClient {
    public static final int PORT = 7777;

    public static void main(String[] argv) throws Exception {
        while (true) {
            Socket sock = new Socket("localhost", PORT);
            OutputStream os = sock.getOutputStream();
            Writer wr = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            PrintWriter prw = new PrintWriter(wr);
            prw.println("hello World");
            prw.flush();
            InputStream is = sock.getInputStream();
            Reader rd = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader brd = new BufferedReader(rd);
            String answer = brd.readLine();
            System.out.println(answer);
            Thread.sleep(5000);
        }
    }
}