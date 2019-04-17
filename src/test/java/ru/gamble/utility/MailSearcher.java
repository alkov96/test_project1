package ru.gamble.utility;

import com.google.common.base.Strings;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.Stash;

import javax.mail.*;
import java.io.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static ru.gamble.utility.EmailParameters.*;


public class MailSearcher {
    private static final Logger LOG = LoggerFactory.getLogger(MailSearcher.class);

    public String getEmailLink(String mail, Properties properties){

        String link;
        String linkSpam;


        for (int i = 1; i >= 0; i--) {
            LOG.info("INBOX");
            link = linkSearcher(mail, properties, "INBOX");
            LOG.info("SPAM");
            linkSpam = linkSearcher(mail, properties, "Спам");
            if (link != null || linkSpam != null) {
                LOG.info("Письмо найдено в папке " + link==null?"Спам":"INBOX");
                return link==null?linkSpam:link;
            } else {
                LOG.info("Connect to email. Remained attempts : " + i);
            }
        }
        LOG.error("Время ожидания истекло. Письмо не было доставлено.");
        Assertions.fail("Время ожидания истекло. Письмо не было доставлено.");
        return null;
    }

    private String linkSearcher(String mail, Properties properties, String nameFolder)  {
        Store store;
        Folder inbox;
        String link = "";
        Message message;

        //Получить почтовую сессию
        Session session = Session.getInstance(properties);
        session.setDebug(false);

        try {
            store = session.getStore(properties.get(PROTOCOL).toString());
            //подключаемся к почтовому серверу
            store.connect(properties.get(HOST).toString(), properties.get(USER).toString(), properties.get(PASS).toString());
            //открываем её для чтения и записи
            inbox = store.getFolder(nameFolder);
            inbox.open(Folder.READ_WRITE);
            int count = 5;
            while (inbox.getMessageCount() == 0 && count >= 0) {
                if (count == 0) {
                    LOG.info("Время ожидания истекло. Письмо не было доставлено.");
                    return null;
                }
                LOG.info("SIZE FOLDER:" + inbox.getMessages().length);
                LOG.info("Нет новых писем. Количество попыток : " + count);
                Thread.sleep(5000);
                count--;
            }

            //Ищем в коллекции письмо с нужным адресатом
            for (int i = 1; i <= inbox.getMessageCount(); i++) {
                message = inbox.getMessage(i);
                if (message.getHeader("To")[0].contains(mail)) {
                    LOG.info("Письмо получено");
                    LOG.info("NEW MAIL DETECTED");
                    link = getFullLink(message).replace("amp;","");//
                    Stash.put("fullLink",link);
                    LOG.info("Ссылка полностью : " + link);
                    LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!! LINK !!!!!!!!!!!!!!!!!!!!!! : " + link);
                    link = getVerifyLink(link);
                    if (!Strings.isNullOrEmpty(link)) {
                        LOG.info("Параметр для аутентификации : " + link);
                        message.setFlag(Flags.Flag.DELETED, true);
                        break;
                    }
                    LOG.info("Link is null or empty. Return null");
                    message.setFlag(Flags.Flag.DELETED, true);
                    continue;
                }
                LOG.info("Письмо адресовано к " + message.getHeader("To")[0] + ". Письмо будет удалено после окончания сессии");
                message.setFlag(Flags.Flag.DELETED, true);
            }
            LOG.info("Получено писем::" + String.valueOf(inbox.getMessageCount()));
            inbox.close(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return link;
    }

    private String getVerifyLink(String line){

//        LOG.info("Поиск ссылки в письме");
//        InputStream stream = box.getInputStream();
//        BufferedInputStream bis = new BufferedInputStream(stream);
//        InputStreamReader streamReader = new InputStreamReader(bis);
//        BufferedReader buffer = new BufferedReader(streamReader);
//
//        String line;
//
//        while ((line = buffer.readLine()) != null) {
//            if (line.contains("verify"))
//                break;
//        }
//        if (line == null) {
//            LOG.error("Line is null");
//            throw new IllegalArgumentException("Line is null");
//        }

        Pattern pattern = Pattern.compile("(code=[0-9_a-zA-Z]*)");
        Matcher matcher = pattern.matcher(line);

        if (!matcher.find()) {
            LOG.error("Something is wrong. Maybe regular expression did not work.\n Line: " + line + "\nPattern: " + pattern);
            Assertions.fail("Something is wrong. Maybe regular expression did not work.\n Line: " + line + "\nPattern: " + pattern);
        }
        return matcher.group(0);
    }

    private String getFullLink(Message box) throws IOException, MessagingException {
        LOG.info("Поиск ссылки в письме");
        InputStream stream = box.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(stream);
        InputStreamReader streamReader = new InputStreamReader(bis);
        BufferedReader buffer = new BufferedReader(streamReader);

        String line = new String();


        while (!buffer.ready()){
            line = buffer.readLine();
            LOG.info(line.substring(0,10));
            if (line.contains("confirm") || line.contains("verify"))
                break;
        }
//        while ((line = buffer.readLine()) != null ) {
//            if (line.contains("confirm") || line.contains("verify"))
//                break;
//        }
        if (line == null) {
            LOG.error("Line is null");
            throw new IllegalArgumentException("Line is null");
        }
        int index = line.indexOf(Stash.getValue("MAIN_URL"));
        int index2 = line.substring(index).indexOf("\"");
        return line.substring(index,index+index2);
    }
}
