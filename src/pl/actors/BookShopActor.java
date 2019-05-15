package pl.actors;

import akka.NotUsed;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import pl.service.*;
import pl.user.UserInterface;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;
import static pl.service.Service.ORDER;
import static pl.service.Service.SEARCH;
import static pl.service.Service.STREAM;

public class BookShopActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private boolean isBookSearchedInDatabase = false;
    private Double bookPrice = -1.0;
    private ActorRef streamActor;

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageRequest.class, m -> {
                    Service serviceType = m.serviceType;
                    String title = m.title;
                    if (serviceType.equals(SEARCH)) {
                        System.out.println("SEARCH actor");
                        context().child("searchActorFirst").get().tell(m, getSelf());
                        context().child("searchActorSec").get().tell(m, getSelf());
                    } else if (serviceType.equals(ORDER)) {
                        context().child("searchActorFirst").get().tell(m, getSelf());
                        context().child("searchActorSec").get().tell(m, getSelf());
                        System.out.println("ORDER actor");
                    } else if (serviceType.equals(STREAM)) {
                        System.out.println("STREAM actor");
                        context().child("streamActor").get().tell(title, getSelf());
                        //throttle(1, Duration.ofSeconds(1)).
                    }
                })
                .match(Double.class, price -> {
                    if(!isBookSearchedInDatabase){
                        bookPrice = price;
                        isBookSearchedInDatabase = true;
                    }
                    else if(isBookSearchedInDatabase && price > 0.0){
                        bookPrice = price;
                    }
                    if(isBookSearchedInDatabase){
                        isBookSearchedInDatabase = false;
                        System.out.println("Book price is = " + bookPrice);
                        //getSender().tell(bookPrice, getSelf());
                    }

                })
                .match(ReplyForSearchMsg.class, reply -> {
                    Double price = reply.price;
                    System.out.println("Book price is = " + price);
                })
                .match(ReplyForSearchMsgFromOrder.class, reply -> {
                    String title = reply.title;
                    context().child("orderActor").get().tell(title, getSelf()); // send task to child
                })
                .match(String.class, line -> {
                    System.out.println(line);
                })
                .match(Boolean.class, approval -> {
                    System.out.println("Book successfully ordered = "+ approval);
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(SearchActor.class, "database1.txt"), "searchActorFirst");
        context().actorOf(Props.create(SearchActor.class, "database2.txt"), "searchActorSec");
        context().actorOf(Props.create(OrderActor.class, "orders.txt"), "orderActor");
        streamActor = context().actorOf(Props.create(StreamActor.class), "streamActor");
    }

    private static SupervisorStrategy strategy
        = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
                match(ElementNotFoundException.class, e -> resume()).
                matchAny(o -> restart()).
                build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

}
