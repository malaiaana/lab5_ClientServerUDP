package sample.server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ImageShareHandler implements Runnable
{
    private boolean isWorking;
    private final DatagramSocket socket;
    private final InetAddress ip;
    private final int clientPort;

    public ImageShareHandler(DatagramSocket serverSocket, InetAddress ip, int clientPort)
    {
        isWorking = true;
        socket = serverSocket;
        this.ip = ip;
        this.clientPort = clientPort;
    }

    @Override
    public void run()
    {
        while (isWorking)
        {
            BufferedImage image;
            try
            {
                image = makeScreenShot();
                byte[] screenshot = bufferedImageToByteArray(image);
                sendScreenshot(screenshot);
            }
            catch (AWTException | IOException e)
            {
                isWorking = false;
                System.out.println("Соединение разорвано");
            }
        }
    }

    private void sendScreenshot(byte[] screenshot) throws IOException
    {
        DatagramPacket packet = new DatagramPacket(screenshot, screenshot.length, ip, clientPort);
        socket.send(packet);
    }

    private byte[] bufferedImageToByteArray(BufferedImage image) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        out.flush();
        return out.toByteArray();
    }

    private BufferedImage makeScreenShot() throws AWTException
    {
        Robot robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle captureRect = new Rectangle(0, 0, screenSize.width, screenSize.height);
        return decreaseSizes(robot.createScreenCapture(captureRect), screenSize.width / 3, screenSize.height / 3);
    }

    private BufferedImage decreaseSizes(BufferedImage screenCapture, int width, int height)
    {
        Image tmpImage = screenCapture.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(tmpImage, 0, 0, null);
        graphics.dispose();

        return resizedImage;
    }

    public boolean isWorking()
    {
        return isWorking;
    }

    public void setWorking(boolean working)
    {
        isWorking = working;
    }
}
