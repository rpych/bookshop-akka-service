package pl.service;

public class MessageRequest {
    public String title;
    public Service serviceType;

    public MessageRequest(String title, Service serviceType) {
        this.title = title;
        this.serviceType = serviceType;
    }
}
