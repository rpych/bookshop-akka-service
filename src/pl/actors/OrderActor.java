package pl.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OrderActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private String ordersDatabaseName;

    public OrderActor(String ordersDatabaseName) {
        this.ordersDatabaseName = ordersDatabaseName;
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    Boolean approval = orderBook(s);
                    getSender().tell(approval, getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    public Boolean orderBook(String title) throws IOException {
        BufferedWriter bwFile = new BufferedWriter(new FileWriter(ordersDatabaseName, true)); //true for append
        bwFile.write(title + "\n");
        bwFile.close();
        return true;
    }
}
