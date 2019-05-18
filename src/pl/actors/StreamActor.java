package pl.actors;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import scala.concurrent.duration.Duration;

public class StreamActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);


    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, title -> {
                    List<String> records = getLinesFromBook(title);
                    final Source<String, NotUsed> source = Source.from(records);
                    final Materializer materializer = ActorMaterializer.create(getContext().getSystem());  // materialize with system in which actor exists
                    final Sink<String, NotUsed> sinkPrint = Sink.actorRef(getSender(), "DONE");
                    source.throttle(1, Duration.create(1, "seconds"), 1, ThrottleMode.shaping())
                            .runWith(sinkPrint, materializer);
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    public List<String> getLinesFromBook(String title) throws IOException {
        BufferedReader brFile = new BufferedReader(new FileReader(title));
        List<String> records = new LinkedList<>();
        String record = "";
        while((record = brFile.readLine()) != null){
            records.add(record);
        }
        brFile.close();
        return records;
    }
}
