package pl.user;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pl.actors.BookShopActor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerApp {
    static File configFile = new File("remote_app2.conf");
    static Config config = ConfigFactory.parseFile(configFile);
    public static final ActorSystem system = ActorSystem.create("remote_system", config);

    public static void main(String[] args) throws IOException {
        // create actor system & actors

        final ActorRef remote = system.actorOf(Props.create(BookShopActor.class), "remote");

        System.out.println("Server started, to quit enter \"q\" ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        while(!line.equals("q")){
            line = br.readLine();
        }
        system.terminate();
    }
}
