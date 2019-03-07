import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ThreadedEchoClient implements Runnable {

    private Socket socket;
    private InputStream is;
    private Reader rd;
    private BufferedReader brd;
    private JTextArea mexText;
    private JList list1;


    public ThreadedEchoClient(Socket socket, JTextArea MESSAGGITextArea, JList list1) {
        this.socket = socket;
        this.mexText = MESSAGGITextArea;
        this.list1 = list1;
    }

    @Override
    public void run() {
        while (true) {
            try {
                is = this.socket.getInputStream();
                rd = new InputStreamReader(is, StandardCharsets.UTF_16);
                brd = new BufferedReader(rd);
                String answer = brd.readLine();
                DefaultListModel model = new DefaultListModel();
                System.out.println(answer);
                if (answer.contains("updateuser")) {
                    String[] userOnline = answer.split("-");
                    model.addElement("Lista Utenti Online");
                    for (int i = 1; i < userOnline.length; i++) {
                        model.addElement(userOnline[i]);
                    }
                    list1.setModel(model);
                }else{
                    this.mexText.append("\n "+answer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
