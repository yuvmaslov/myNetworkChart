package client;

import network.TCPConnection;
import network.TCPConnectionObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.*;

public class Client extends JFrame implements ActionListener, TCPConnectionObserver {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private static final int WIGHT = 600;
    private static final int HEIGHT = 400;
    private final JTextArea log = new JTextArea();
    private final JTextField nickName = new JTextField("Yury");
    private final JTextField fieldInput = new JTextField();
    int port;
    String ip;

    private TCPConnection connection;

    {
        final String PATH_TO_PROPERTIES = "client/src/main/resources/client.properties";

        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);
            String sPort = prop.getProperty("port");
            port = Integer.parseInt(sPort);
            ip = prop.getProperty("host");
            logger.info("Properties is got");
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
            logger.info("Try create new connection");
            connection = new TCPConnection(this, ip, port);
        } catch (IOException e) {
            printMessage("Connection exception " + e);
        }
    }

    public static void main(String[] args) throws IOException {
        String PATH_TO_LOGS = "client/src/main/resources/clientLogs.log";
        Handler handler = new FileHandler(PATH_TO_LOGS, true);

        handler.setFormatter(new MyFormatter());
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.info("Create new session");
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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);
        connection.sendString(time, msg, nickName.getText());
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
        logger.info("Connection ready..." + tcpConnection);
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String msg) {
        logger.info(tcpConnection + " got the message " + msg);
        printMessage(msg);
        logger.info(tcpConnection + " sent the message " + msg);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection closed...");
        logger.info(tcpConnection + " is disconnect");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        logger.info(tcpConnection + " has " + exception);
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

    static class MyFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            var dateTime = ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault());
            return dateTime + " " + record.getLoggerName() + ": " + record.getMessage() + "\n";
        }
    }
}
