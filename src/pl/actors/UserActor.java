package pl.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import pl.service.MessageRequest;
import pl.service.ReplyForSearchMsg;


public class UserActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    final ActorSelection bookshopActor = getContext().actorSelection("akka.tcp://remote_system@127.0.0.1:3552/user/remote");


    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageRequest.class, s -> {
                    bookshopActor.tell(s, getSelf());
                })
                .match(String.class, msg -> {
                    System.out.println(msg);
                })
                .match(Boolean.class, approval -> {
                    System.out.println("Status of book ordering = "+ approval);
                })
                .match(ReplyForSearchMsg.class, reply -> {
                    Double price = reply.price;
                    System.out.println("Book price is = " + price);
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
