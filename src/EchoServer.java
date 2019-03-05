import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class EchoServer {
    public static final int PORT = 7777;

    public static void main(String[] argv) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(PORT);
        Socket connectionSocket = welcomeSocket.accept();
        InputStream is = connectionSocket.getInputStream();
        Reader rd = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader brd = new BufferedReader(rd);
        String answer = brd.readLine();
        System.out.println(answer);
        PrintWriter prw = new PrintWriter(new OutputStreamWriter(connectionSocket.getOutputStream(), "UTF-8"));
        prw.println("shabala");
        prw.flush();
        connectionSocket.close();

    }
}
