package pl.user;

import java.io.IOException;

public class MainApp {
    public static void main(String[] args){
        UserInterface ui = new UserInterface();
        try {
            ui.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
