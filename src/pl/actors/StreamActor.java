package pl.actors;

import akka.Done;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import pl.user.UserInterface;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class StreamActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);


    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, title -> {
                    List<String> records = getLinesFromBook(title);
                    final Source<String, NotUsed> source = Source.from(records);
                    //final Flow flow = Flow.of(String.class).map(val -> val * 2);
                    final Materializer materializer = ActorMaterializer.create(UserInterface.system);
                    final Sink<String, CompletionStage<Done>> sinkPrint = Sink.foreach(i -> System.out.println(i));
                    source.runWith(sinkPrint, materializer);
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
