import network.TCPConnection;
import network.TCPConnectionObserver;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class TestChatServer {

    TCPConnectionObserver observer;
    TCPConnection tcpConnection;

    @Test
    public void testOnConnectionReady() {
        tcpConnection = Mockito.mock(TCPConnection.class);
        final List<TCPConnection> connections = new ArrayList<>();
        connections.add(tcpConnection);
    }
}
