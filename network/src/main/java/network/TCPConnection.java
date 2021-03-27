package network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionObserver eventObserver;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionObserver eventObserver, String ipAdr, int port) throws IOException {
        this(eventObserver, new Socket(ipAdr, port));
    }

    public TCPConnection(TCPConnectionObserver eventObserver, Socket socket) throws IOException {
        this.socket = socket;
        this.eventObserver = eventObserver;
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        TCPConnection tcpConnection = this;
        rxThread = new MyThread(eventObserver, socket, tcpConnection);
        rxThread.start();
    }

    public synchronized void sendString(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventObserver.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventObserver.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
