import java.io.IOException;

public class provaclient {
    public static void main(String[] args) throws IOException {


        EchoClient c1 = new EchoClient();
        c1.login("Giovanni");
        EchoClient c2 = new EchoClient();
        EchoClient c3 = new EchoClient();
        c2.login("Giuseppe");
        c1.sendMassege("Giuseppe", "ciao giuseppe");
        c2.sendMassege("Giovanni", "ciao giovanni");
        c1.sendMassege("Giuseppe", "ciao biondo");
        c3.login("Giuseppe");
        c3.login("Peppe");
        c3.sendMassege("broadcast", "ciao a tutti");
        c1.logout();
        c2.logout();
        c3.logout();





    }
}
