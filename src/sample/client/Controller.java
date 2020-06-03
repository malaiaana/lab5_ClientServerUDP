package sample.client;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

public class Controller
        implements Initializable,
        OnImageReceivedListener
{
    public ImageView screenshot;
    public Button stopShareBtn;
    public Button stopServerBtn;
    public Button startShareBtn;

    private DatagramSocket clientSocket;
    private InetAddress ip;
    private Thread imageHandlerThread;
    private ImageReceiveHandler imageReceiveHandler;

    public void startSharing() throws IOException
    {
        stopShareBtn.setDisable(false);
        startShareBtn.setDisable(true);
        imageReceiveHandler.setOnImageReceivedListener(this);

        byte[] message = "начать демонстрацию экрана".getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, ip, 12395);
        clientSocket.send(packet);

        imageReceiveHandler.setWorking(true);
        imageHandlerThread = new Thread(imageReceiveHandler);
        imageHandlerThread.start();
    }

    public void stopSharing() throws IOException
    {
        stopShareBtn.setDisable(true);
        startShareBtn.setDisable(false);
        imageReceiveHandler.setOnImageReceivedListener(null);

        imageReceiveHandler.setWorking(false);
        byte[] message = "остановить демонстрацию экрана".getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, ip, 12395);
        clientSocket.send(packet);
        imageHandlerThread.interrupt();
    }

    public void stopServer() throws IOException
    {
        stopShareBtn.setDisable(true);
        startShareBtn.setDisable(true);
        stopServerBtn.setDisable(true);
        imageReceiveHandler.setOnImageReceivedListener(null);

        imageReceiveHandler.setWorking(false);
        if (imageHandlerThread != null)
        {
            imageHandlerThread.interrupt();
        }
        byte[] message = "остановить сервер".getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, ip, 12395);
        clientSocket.send(packet);
        clientSocket.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
            clientSocket = new DatagramSocket();
            imageReceiveHandler = new ImageReceiveHandler(clientSocket);
            ip = InetAddress.getByName("localhost");
        }
        catch (SocketException | UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void imageReceived(Image screenshot)
    {
        this.screenshot.setImage(screenshot);
    }
}
