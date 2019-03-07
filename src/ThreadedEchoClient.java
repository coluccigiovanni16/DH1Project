import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ThreadedEchoClient implements Runnable{

    private Socket socket;
    private InputStream is;
    private Reader rd;
    private BufferedReader brd;

    public ThreadedEchoClient(Socket socket) {
        this.socket=socket;
    }

    @Override
    public void run() {
        while(true){
        try{
        is = socket.getInputStream();
        rd = new InputStreamReader(is, StandardCharsets.UTF_16);
        brd = new BufferedReader(rd);
        String answer = brd.readLine();
        System.out.println(answer);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        }

    }
}
