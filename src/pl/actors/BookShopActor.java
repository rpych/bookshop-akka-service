package pl.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import pl.service.*;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;
import static pl.service.Service.ORDER;
import static pl.service.Service.SEARCH;
import static pl.service.Service.STREAM;

public class BookShopActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageRequest.class, m -> {
                    Service serviceType = m.serviceType;
                    String title = m.title;
                    if (serviceType.equals(SEARCH)) {
                        System.out.println("SEARCH actor");
                        context().child("searchActorFirst").get().forward(m, getContext());
                        context().child("searchActorSec").get().forward(m, getContext());
                    } else if (serviceType.equals(ORDER)) {
                        context().child("searchActorFirst").get().forward(m, getContext());
                        context().child("searchActorSec").get().forward(m, getContext());
                        System.out.println("ORDER actor");
                    } else if (serviceType.equals(STREAM)) {
                        System.out.println("STREAM actor");
                        context().child("streamActor").get().forward(title, getContext());
                    }
                })
                .match(ReplyForSearchMsgFromOrder.class, reply -> {
                    String title = reply.title;
                    context().child("orderActor").get().tell(title, getSender());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(SearchActor.class, "database1.txt"), "searchActorFirst");
        context().actorOf(Props.create(SearchActor.class, "database2.txt"), "searchActorSec");
        context().actorOf(Props.create(OrderActor.class, "orders.txt"), "orderActor");
        context().actorOf(Props.create(StreamActor.class), "streamActor");
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
