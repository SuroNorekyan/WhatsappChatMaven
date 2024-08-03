package org.example.whatsappchat.client;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientListener extends Thread {
    private BufferedReader in;

    public ClientListener(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
