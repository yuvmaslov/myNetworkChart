package client;

import network.TCPConnection;
import network.TCPConnectionObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Client extends JFrame implements ActionListener, TCPConnectionObserver {

    private static final int WIGHT = 600;
    private static final int HEIGHT = 400;
    private final JTextArea log = new JTextArea();
    private final JTextField nickName = new JTextField("Yury");
    private final JTextField fieldInput = new JTextField();
    int port;
    String ip;

    private TCPConnection connection;

    {
        final String PATH_TO_PROPERTIES = "server/src/main/resources/application.properties";
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);
            String sPort = prop.getProperty("port");
            port = Integer.parseInt(sPort);
            ip = prop.getProperty("host");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIGHT, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        fieldInput.addActionListener(this);

        add(log, BorderLayout.CENTER);
        add(fieldInput, BorderLayout.SOUTH);
        add(nickName, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this, ip, port);
        } catch (IOException e) {
            printMessage("Connection exception " + e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(nickName.getText() + " " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String msg) throws InterruptedException {
        printMessage(msg);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection closed...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        printMessage("Connection exception " + exception);
    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
