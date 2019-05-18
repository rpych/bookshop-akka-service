package pl.service;

import java.io.Serializable;

public class ReplyForSearchMsgFromOrder implements Serializable {
    public String title;
    public Service serviceType;

    public ReplyForSearchMsgFromOrder(String title, Service serviceType) {
        this.title = title;
        this.serviceType = serviceType;
    }
}
