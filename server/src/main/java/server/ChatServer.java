package server;

import network.TCPConnection;
import network.TCPConnectionObserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatServer implements TCPConnectionObserver {

    private final List<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                new TCPConnection(this, serverSocket.accept());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String msg) {
        sendToAllConnections(msg);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception exception) {
        System.out.println("TCPConnection exception: " + exception);
    }

    private void sendToAllConnections(String msg) {
        System.out.println(msg);
        for (TCPConnection connection : connections) connection.sendString(msg);
    }
}
