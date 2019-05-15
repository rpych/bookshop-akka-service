package pl.user;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import pl.actors.BookShopActor;
import pl.service.MessageRequest;
import pl.service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface {
    public final static ActorSystem system = ActorSystem.create("local_system");
    final ActorRef actor = system.actorOf(Props.create(BookShopActor.class), "bookshop");

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
        System.out.println("Enter book title to search for:\n>>");
        title = br.readLine();
        return title;
    }


}
