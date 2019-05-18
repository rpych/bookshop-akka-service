package pl.user;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pl.actors.UserActor;
import pl.service.MessageRequest;
import pl.service.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface {
    static File configFile = new File("remote_app.conf");
    static Config config = ConfigFactory.parseFile(configFile);
    public final static ActorSystem system = ActorSystem.create("local_system", config);
    public final ActorRef actor = system.actorOf(Props.create(UserActor.class), "local1");
    //final ActorRef bookshopActor = system.actorSelection("akka.tcp://remote_system@127.0.0.1:3552/user/remote").anchor();            //system.actorOf(Props.create(BookShopActor.class), "bookshop");

    public UserInterface() { }

    public void run() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("To SEARCH for a book enter search \n" +
                "To ORDER for a book enter order \n" +
                "To STREAM for a book enter stream \n" +
                "to QUIT enter q");
        String line = "";
        String title;
        MessageRequest msg;
        while(!line.equals("q")){
            System.out.print("\n>>");
            line  = br.readLine();
            switch(line.toUpperCase()){
                case "SEARCH":
                    title = chooseBook();
                    msg = new MessageRequest(title, Service.SEARCH);
                    actor.tell(msg, null);
                    break;
                case "ORDER":
                    title = chooseBook();
                    msg = new MessageRequest(title, Service.ORDER);
                    actor.tell(msg, null);
                    break;
                case "STREAM":
                    title = chooseBook();
                    msg = new MessageRequest(title, Service.STREAM);
                    actor.tell(msg, null);
                    break;
                default:
                    System.out.println("Undefined service type");
            }
        }
        system.terminate();
    }

    public String chooseBook() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String title = "";
        System.out.print("Enter book title to search for:\n>>");
        title = br.readLine();
        return title;
    }



}
