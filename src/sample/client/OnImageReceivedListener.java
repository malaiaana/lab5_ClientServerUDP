package sample.client;

import java.io.IOException;

public interface OnImageReceivedListener
{
    void imageReceived(javafx.scene.image.Image screenshot) throws IOException;
}
