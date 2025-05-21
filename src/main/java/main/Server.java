package main;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static final List<ClientHandler> clients = new ArrayList<>();
    private static final Set<Integer> idSet = new TreeSet<>();

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }
    }

    public static void broadcastID(ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                try {
                    client.send(sender.getId());
                } catch (IOException ex) {
                    System.err.println("Ошибка: " + ex.getMessage());
                }
            }
        }
    }

    public static void broadcastMessage(Message message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender & message.getIdOfRecipient() == client.getId()) {
                try {
                    client.send(message);
                } catch (IOException ex) {
                    System.err.println("Ошибка отправки клиенту " + client.getId() + " " + ex.getMessage());
                }
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        idSet.remove(client.getId());
        System.out.println("Клиент " + client.getId() + " отключился.");
    }

    public static Set<Integer> getIDSet() {
        return idSet;
    }

    public static int getUniqueID() {
        int id = new Random().nextInt(100,1000);
        while (idSet.contains(id)) {
            id = new Random().nextInt(100, 1000);
        }
        idSet.add(id);
        System.out.println(id);
        return id;
    }
}