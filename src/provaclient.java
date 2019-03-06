import java.io.IOException;

public class provaclient {
    public static void main(String[] args) throws IOException {
        EchoClient c1= new EchoClient("Giovanni");
        c1.login();
        c1.sendMassege("Giuseppe","ciao Giuseppe");
        c1.logout();
        EchoClient c2= new EchoClient("Giuseppe");
        c2.login();
        c2.sendMassege("Giuseppe","ciao Giuseppe");
        c2.logout();
        EchoClient c3= new EchoClient("Valerio");
        c3.login();
        c3.sendMassege("Giuseppe","ciao Giuseppe");
        c3.logout();

    }
}
