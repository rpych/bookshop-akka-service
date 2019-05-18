package pl.service;

import java.io.Serializable;

public class MessageRequest implements Serializable {
    public String title;
    public Service serviceType;

    public MessageRequest(String title, Service serviceType) {
        this.title = title;
        this.serviceType = serviceType;
    }
}
