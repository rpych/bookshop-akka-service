package pl.service;

public class ReplyForSearchMsg {

    public Double price;
    public Service serviceType;

    public ReplyForSearchMsg(Double price, Service serviceType) {
        this.price = price;
        this.serviceType = serviceType;
    }
}
