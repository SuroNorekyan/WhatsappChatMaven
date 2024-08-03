package org.example.whatsappchat.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Map<String, Group> groups;
    private ConcurrentMap<String, ClientHandler> clients;
    private String nickname;

    public ClientHandler(Socket socket, Map<String, Group> groups, ConcurrentMap<String, ClientHandler> clients) {
        this.socket = socket;
        this.groups = groups;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            registerUser();

            String message;
            while ((message = in.readLine()) != null) {
                processCommand(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clients.remove(nickname);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void registerUser() throws IOException {
        out.println("Enter your nickname:");
        nickname = in.readLine();
        clients.put(nickname, this);
        out.println("Welcome, " + nickname + "! Get started with executing /help to list all of the available commands!");
    }

    private void processCommand(String command) {
        System.out.println("Processing command: " + command);  // Print command for debugging
        String[] parts = command.split(" ", 3);
        switch (parts[0]) {
            case "/help":
                displayHelp();
                break;
            case "AddGroup":
                addGroup(parts[1]);
                break;
            case "JoinGroup":
                joinGroup(parts[1]);
                break;
            case "SendMessage":
                sendMessage(parts[1], parts[2]);
                break;
            case "LeaveGroup":
                leaveGroup(parts[1]);
                break;
            case "RemoveGroup":
                removeGroup(parts[1]);
                break;
            case "AddUserToGroup":
                addUserToGroup(parts[1], parts[2]);
                break;
            case "PrivateMessage":
                sendPrivateMessage(parts[1], parts[2]);
                break;
            default:
                out.println("Unknown command. Type /help to see the list of available commands.");
        }
    }


    private void displayHelp() {
        out.println("Available commands:");
        out.println("/help - Display this help message");
        out.println("AddGroup <GroupName> - Create a new group");
        out.println("JoinGroup <GroupName> - Join an existing group");
        out.println("SendMessage <GroupName> <Message> - Send a message to a group");
        out.println("LeaveGroup <GroupName> - Leave a group");
        out.println("RemoveGroup <GroupName> - Remove a group");
        out.println("SendFile <FilePath> - Send a file to a group");
        out.println("AddUserToGroup <GroupName> <UserName> - Add a user to a group");
        out.println("PrivateMessage <UserName> <Message> - Send a private message to a user");
    }

    private void addGroup(String groupName) {
        if (!groups.containsKey(groupName)) {
            groups.put(groupName, new Group(groupName));
            out.println("Group " + groupName + " created");
        } else {
            out.println("Group already exists");
        }
    }

    private void joinGroup(String groupName) {
        Group group = groups.get(groupName);
        if (group != null) {
            if (!group.getClients().contains(this)) {
                group.addClient(this);
                out.println("Joined group " + groupName);
            } else {
                out.println("You are already in this group.");
            }
        } else {
            out.println("Group not found.");
        }
    }

    private void sendMessage(String groupName, String message) {
        Group group = groups.get(groupName);
        if (group != null) {
            if (group.getClients().contains(this)) {
                for (ClientHandler client : group.getClients()) {
                    client.out.println(nickname + " (group " + groupName + "): " + message);
                }
            } else {
                out.println("You must join this group to send a message.");
            }
        } else {
            out.println("Group not found.");
        }
    }

    private void leaveGroup(String groupName) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.removeClient(this);
            out.println("Left group " + groupName);
        } else {
            out.println("Group not found");
        }
    }

    private void removeGroup(String groupName) {
        if (groups.containsKey(groupName)) {
            groups.remove(groupName);
            out.println("Group " + groupName + " removed");
        } else {
            out.println("Group not found");
        }
    }

    private void addUserToGroup(String groupName, String userName) {
        Group group = groups.get(groupName);
        ClientHandler client = clients.get(userName);
        if (group != null) {
            if (group.getClients().contains(this)) {
                if (client != null) {
                    if (!group.getClients().contains(client)) {
                        group.addClient(client);
                        out.println("User " + userName + " added to group " + groupName);
                        client.out.println("You have been added to group " + groupName);
                    } else {
                        out.println("User " + userName + " is already in this group.");
                    }
                } else {
                    out.println("User not found: " + userName);
                }
            } else {
                out.println("You must be a part of the group in order to add other users!");
            }
        } else {
            out.println("Group not found.");
        }
    }

    private void sendPrivateMessage(String recipient, String message) {
        ClientHandler client = clients.get(recipient);
        if (client != null) {
            client.out.println(nickname + " (private): " + message);
            out.println("You sent a private message to " + recipient + ": " + message);
        } else {
            out.println("User not found: " + recipient);
        }
    }
}
