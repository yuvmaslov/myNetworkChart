package network;


import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class MyThread extends Thread {

    private final Socket socket;
    private final TCPConnectionObserver eventObserver;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnection tcpConnection;

    public MyThread(TCPConnectionObserver eventObserver, Socket socket, TCPConnection tcpConnection) throws IOException {
        this.socket = socket;
        this.eventObserver = eventObserver;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        this.tcpConnection = tcpConnection;
    }

    @Override
    public void run() {
        try {
            eventObserver.onConnectionReady(tcpConnection);
            while (!currentThread().isInterrupted()) {
                eventObserver.onReceiveString(tcpConnection, in.readLine());
            }
        } catch (IOException e) {
            eventObserver.onException(tcpConnection, e);
        } finally {
            eventObserver.onDisconnect(tcpConnection);
        }

    }
}
