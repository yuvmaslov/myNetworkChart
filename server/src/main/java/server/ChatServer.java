package server;

import network.TCPConnection;
import network.TCPConnectionObserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class ChatServer implements TCPConnectionObserver {

    private final List<TCPConnection> connections = new ArrayList<>();
    public static final String PATH_TO_PROPERTIES = "server/src/main/resources/application.properties";
    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

    private ChatServer(int port) {
        logger.info("Server is running...");
//        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                new TCPConnection(this, serverSocket.accept());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            String PATH_TO_PROPERTIES = "server/src/main/resources/serverLogs.logs";
            Handler handler = new FileHandler(PATH_TO_PROPERTIES, true);
//            logger.setUseParentHandlers(false);
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);
            String sPort = prop.getProperty("port");
            int port = Integer.parseInt(sPort);
            new ChatServer(port);
        } catch (IOException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        logger.info("Adding new connection to list on server");
        connections.add(tcpConnection);
        logger.info("New connection is added to list on server");
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
