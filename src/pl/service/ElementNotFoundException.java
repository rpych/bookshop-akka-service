package pl.service;

public class ElementNotFoundException extends Exception {
    public ElementNotFoundException() {
    }

    public ElementNotFoundException(String message) {
        super(message);
    }
}
