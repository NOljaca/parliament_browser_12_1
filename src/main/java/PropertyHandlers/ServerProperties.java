package PropertyHandlers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Properties-class for loading the server properties (port).
 * @author Muhammed
 */
public class ServerProperties extends Properties {

    public ServerProperties(String propertiesFile) {
        super();
        loadProperties(propertiesFile);
    }

    private void loadProperties(String propertiesFile) {
        try (InputStream inputStream = new FileInputStream(propertiesFile)) {
            if (inputStream != null) {
                this.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        String port = this.getProperty("port");
        if (port == null) {
            return 8080;
        }
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            return 8080;
        }
    }
}
