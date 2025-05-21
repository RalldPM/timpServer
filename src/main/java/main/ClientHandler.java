package main;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private int id;

    public ClientHandler(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            this.id = Server.getUniqueID();
            out.writeInt(this.id);
            out.writeObject(Server.getIDSet());
            out.flush();
            Server.broadcastID(this);
            while (true) {
                Message message = (Message) in.readObject();
                Server.broadcastMessage(message, this);
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Ошибка с клиентом " + id + " " + ex.getMessage());
        } finally {
            try {
                Server.broadcastID(this);
                clientSocket.close();
                Server.removeClient(this);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public void send(Message message ) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    public void send(int id) throws IOException {
        out.writeObject(id);
        out.flush();
    }

    public Integer getId() {
        return id;
    }
}