import java.io.IOException;

public class provaclient {
    public static void main(String[] args) throws IOException {


        EchoClient c1 = new EchoClient();
        c1.login("Giovanni");
        EchoClient c2 = new EchoClient();
        c2.login("Giuseppe");
        c1.sendMassege("Giuseppe", "ciao giuseppe");
        System.out.println(c2.receiveMessage());
        c2.sendMassege("Giovanni", "ciao giovanni");
        System.out.println(c1.receiveMessage());
        c1.sendMassege("Giuseppe", "ciao biondo");
        System.out.println(c2.receiveMessage());
        EchoClient c3=new EchoClient();
        c3.login("Giuseppe");
        c3.login("Peppe");
        c3.sendMassege("broadcast", "ciao a tutti");
        System.out.println(c1.receiveMessage());
        System.out.println(c2.receiveMessage());
        System.out.println(c3.receiveMessage());
        c1.logout();
        c2.logout();
        c3.logout();





    }
}
