package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropPoint {
    private static final Logger LOG = LoggerFactory.getLogger(PropPoint.class);
    private static final String sep = File.separator;
    private static Properties instance;
    private static InputStream input = null;

    public static String getEmailProperty(EmailParameters property){
        return getInstance().getProperty(property.toString());
    }

    private static Properties getInstance() {

        if (instance == null) {
            instance = new Properties();

            try {
                String filename = "config" + sep + "email.properties";
                input = PropPoint.class.getClassLoader().getResourceAsStream(filename);

                if (input == null) {
                    LOG.info("Sorry, unable to find " + filename);
                }
                instance.load(input);
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }
}
