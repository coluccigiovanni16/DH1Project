import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ThreadedEchoClient {
    public static final int PORT = 7777;

    public static void main(String[] argv) throws Exception {
        Socket sock = new Socket("localhost", PORT);
        //apro un canale e mando un messaggio al server
        OutputStream os = sock.getOutputStream();
        Writer wr = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        PrintWriter prw = new PrintWriter(wr);
        prw.println("hello World");
        prw.flush();
        //leggo dal canale ciò che mi ha inviato il server
        InputStream is = sock.getInputStream();
        Reader rd = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader brd = new BufferedReader(rd);
        String answer = brd.readLine();
        System.out.println(answer);
    }
}