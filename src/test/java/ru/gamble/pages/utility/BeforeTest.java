package ru.gamble.pages.utility;

import cucumber.api.java.ru.Когда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.EventViewerPage;
import ru.sbtqa.tag.datajack.TestDataObject;
import ru.sbtqa.tag.datajack.adaptors.JsonDataObjectAdaptor;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.drivers.TagWebDriver;

@Slf4j
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
