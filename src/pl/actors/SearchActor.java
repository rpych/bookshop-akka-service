package pl.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import pl.service.*;

import java.io.*;

import static pl.service.Service.ORDER;
import static pl.service.Service.SEARCH;

public class SearchActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private String databaseName;

    public SearchActor(String databaseName){
        this.databaseName = databaseName;
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageRequest.class, s -> {
                    Service serviceType = s.serviceType;
                    if(serviceType.equals(SEARCH)){
                        Double price = searchForBook(s.title);
                        ReplyForSearchMsg reply = new ReplyForSearchMsg(price, s.serviceType);
                        getSender().tell(reply, getSelf());
                    }
                    else if(serviceType.equals(ORDER)){
                        searchForBook(s.title);
                        ReplyForSearchMsgFromOrder reply = new ReplyForSearchMsgFromOrder(s.title, s.serviceType);
                        getContext().getParent().forward(reply, getContext());
                    }
                    }

                    )
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    public Double searchForBook(String name) throws IOException, ElementNotFoundException {
        BufferedReader brFile = new BufferedReader(new FileReader(databaseName));
        String record = "";
        while((record = brFile.readLine()) != null){
            String[] recordData = record.split("#"); //recordData[0] = book name, recordData[1] = book price
            recordData[0] = recordData[0].trim();
            if(recordData[0].equals(name)){
                brFile.close();
                return Double.parseDouble(recordData[1]);
            }
        }
        brFile.close();
        throw new ElementNotFoundException("Book with this title not found");
    }

}
