package org.example.whatsappchat.server;

import java.util.HashSet;
import java.util.Set;

public class Group {
    private String name;
    private Set<ClientHandler> clients;

    public Group(String name) {
        this.name = name;
        this.clients = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Set<ClientHandler> getClients() {
        return clients;
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
