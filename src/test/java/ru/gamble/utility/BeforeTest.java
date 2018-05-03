package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.TestDataObject;
import ru.sbtqa.tag.datajack.adaptors.JsonDataObjectAdaptor;
import ru.sbtqa.tag.datajack.exceptions.DataException;

import java.io.File;

public class BeforeTest {
    private static TestDataObject data;
    private static final Logger LOG = LoggerFactory.getLogger(BeforeTest.class);
    private static String sep = File.separator;

    public static TestDataObject getData() {
            try {
                data = new JsonDataObjectAdaptor("src" + sep + "test" + sep + "resources", "data");
            } catch (DataException e) {
                LOG.error(e.getMessage());
            }
        return data;
    }

}
