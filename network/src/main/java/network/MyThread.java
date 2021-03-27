package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MyThread extends Thread {

    private final TCPConnectionObserver eventObserver;
    private final BufferedReader in;
    private final TCPConnection tcpConnection;

    public MyThread(TCPConnectionObserver eventObserver, Socket socket, TCPConnection tcpConnection) throws IOException {
        this.eventObserver = eventObserver;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.tcpConnection = tcpConnection;
    }

    @Override
    public void run() {
        try {
            eventObserver.onConnectionReady(tcpConnection);
            while (!currentThread().isInterrupted()) {
                eventObserver.onReceiveString(tcpConnection, in.readLine());
            }
        } catch (IOException | InterruptedException e) {
            eventObserver.onException(tcpConnection, e);
        } finally {
            eventObserver.onDisconnect(tcpConnection);
        }

    }
}
