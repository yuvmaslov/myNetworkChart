package network;

import java.io.*;
import java.net.Socket;

public class TCPConnection {

//    private final Socket socket;
    private final Thread rxThread;
//    private final TCPConnectionObserver eventObserver;
//    private final BufferedReader in;
//    private final BufferedWriter out;

    public TCPConnection(TCPConnectionObserver eventObserver, Socket socket) throws IOException {
//        this.socket = socket;
//        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
//        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        TCPConnection tcpConnection = this;
        rxThread = new MyThread(eventObserver, socket, tcpConnection);
        rxThread.start();
    }
}
