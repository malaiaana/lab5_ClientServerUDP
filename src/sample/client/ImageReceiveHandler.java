package sample.client;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ImageReceiveHandler implements Runnable
{
    private final DatagramSocket clientSocket;
    private boolean isWorking;
    private OnImageReceivedListener onImageReceivedListener;
    private byte[] screenshot;

    public ImageReceiveHandler(DatagramSocket clientSocket)
    {
        this.clientSocket = clientSocket;
        screenshot = new byte[64000];
        isWorking = true;
    }

    @Override
    public void run()
    {
        while (isWorking)
        {
            DatagramPacket packet = new DatagramPacket(screenshot, screenshot.length);
            try
            {
                clientSocket.receive(packet);
                screenshot = packet.getData();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(screenshot);
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                Image screenshot = SwingFXUtils.toFXImage(bufferedImage, null);
                if (onImageReceivedListener != null)
                {
                    onImageReceivedListener.imageReceived(screenshot);
                }
            }
            catch (IOException e)
            {
                System.out.println("Подключение разорвано");
            }
        }
    }

    public void setWorking(boolean working)
    {
        isWorking = working;
    }

    public void setOnImageReceivedListener(OnImageReceivedListener onImageReceivedListener)
    {
        this.onImageReceivedListener = onImageReceivedListener;
    }
}
