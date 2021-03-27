package server;

import network.TCPConnection;
import network.TCPConnectionObserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ChatServer implements TCPConnectionObserver {

    private final List<TCPConnection> connections = new ArrayList<>();
    public static final String PATH_TO_PROPERTIES = "server/src/main/resources/application.properties";

    private ChatServer(int port) {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                new TCPConnection(this, serverSocket.accept());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);
            String sPort = prop.getProperty("port");
            int port = Integer.parseInt(sPort);
            new ChatServer(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
