import java.io.IOException;

public class provaclient {
    public static void main(String[] args) throws IOException {

        EchoClient c1= new EchoClient();
        c1.login("Giovanni");
        EchoClient c2= new EchoClient();
        c2.login("Giuseppe");
        c2.sendMassege("Giovanni", "ciao giovanni");
        c2.logout();

    }
}
