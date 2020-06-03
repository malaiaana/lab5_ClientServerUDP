package sample.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server
{
    private static DatagramSocket serverSocket;
    private static boolean isWorking = true;
    private static final byte[] message = new byte[1000];
    private static ImageShareHandler imageShareHandler;
    private static Thread imageShareHandlerThread;
    private static InetAddress ip;
    private static int clientPort;

    public static void main(String[] args) throws IOException
    {
        serverSocket = new DatagramSocket(12395);
        while (isWorking)
        {
            DatagramPacket packet = new DatagramPacket(message, message.length);
            String receivedMessage = receiveMessage(packet);
            switch (receivedMessage)
            {
                case "начать демонстрацию экрана":
                    if (imageShareHandler == null || !imageShareHandler.isWorking())
                    {
                        startScreenSharing();
                    }
                    break;
                case "остановить демонстрацию экрана":
                    if (imageShareHandler != null && imageShareHandler.isWorking())
                    {
                        stopScreenSharing();
                    }
                    break;
                default:
                    isWorking = false;
                    break;
            }
        }
        serverSocket.close();
    }

    private static void startScreenSharing()
    {
        imageShareHandler = new ImageShareHandler(serverSocket, ip, clientPort);
        imageShareHandlerThread = new Thread(imageShareHandler);
        imageShareHandlerThread.start();
    }

    private static void stopScreenSharing()
    {
        imageShareHandler.setWorking(false);
        imageShareHandlerThread.interrupt();
    }

    private static String receiveMessage(DatagramPacket packet) throws IOException
    {
        serverSocket.receive(packet);
        ip = packet.getAddress();
        clientPort = packet.getPort();
        return new String(packet.getData(), 0, packet.getLength());
    }
}
