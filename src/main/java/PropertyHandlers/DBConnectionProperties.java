package PropertyHandlers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Property-class for loading the db-connection data.
 * @author Amal
 */
public class DBConnectionProperties extends Properties {

    public DBConnectionProperties(String propertiesFilePath) {
        super();
        loadFile(propertiesFilePath);
    }

    private void loadFile(String propertiesFilePath) {
        try (InputStream inputStream = new FileInputStream(propertiesFilePath)) {
            if (inputStream != null) {
                load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHost() {
        return this.getProperty("remote_host");
    }

    public String getDatabase() {
        return this.getProperty("remote_database");
    }

    public String getUser() {
        return this.getProperty("remote_user");
    }

    public String getPassword() {
        return this.getProperty("remote_password");
    }

    public String getPort() {
        return this.getProperty("remote_port");
    }

    public String getCollection() {
        return this.getProperty("remote_collection");
    }
}
