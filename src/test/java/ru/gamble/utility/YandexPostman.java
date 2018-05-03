package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static ru.gamble.utility.PropPoint.getEmailProperty;

public class YandexPostman {
    private static final Logger LOG = LoggerFactory.getLogger(YandexPostman.class);

    public static String getLinkForAuthentication(String mail) throws Exception {


        Properties properties = new Properties();
        MailSearcher searcher = new MailSearcher();

        //Указываем свойства и значение для properties
        properties.put(EmailParameters.USER, getEmailProperty(EmailParameters.USER));
        properties.put(EmailParameters.HOST, getEmailProperty(EmailParameters.HOST));
        properties.put(EmailParameters.PORT, getEmailProperty(EmailParameters.PORT));
        properties.put(EmailParameters.SSL, getEmailProperty(EmailParameters.SSL));
        properties.put(EmailParameters.PROTOCOL, getEmailProperty(EmailParameters.PROTOCOL));
        properties.put(EmailParameters.DEBUG, getEmailProperty(EmailParameters.DEBUG));
        properties.put(EmailParameters.PASS, getEmailProperty(EmailParameters.PASS));

        return searcher.getEmailLink(mail, properties);
    }
}
