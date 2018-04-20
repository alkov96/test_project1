package ru.gamble.pages.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.TestDataObject;
import ru.sbtqa.tag.datajack.adaptors.JsonDataObjectAdaptor;
import ru.sbtqa.tag.datajack.exceptions.DataException;

public class BeforeTest {
    private static TestDataObject data;
    private static final Logger LOG = LoggerFactory.getLogger(BeforeTest.class);


    public static TestDataObject getData() {
            try {
                data = new JsonDataObjectAdaptor("src/test/resources/", "data");
            } catch (DataException e) {
                LOG.error(e.getMessage());
            }
        return data;
    }

}
