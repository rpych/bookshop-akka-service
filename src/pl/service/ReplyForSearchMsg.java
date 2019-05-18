package pl.service;

import java.io.Serializable;

public class ReplyForSearchMsg implements Serializable {

    public Double price;
    public Service serviceType;

    public ReplyForSearchMsg(Double price, Service serviceType) {
        this.price = price;
        this.serviceType = serviceType;
    }
}
