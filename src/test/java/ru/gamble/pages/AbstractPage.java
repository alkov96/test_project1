package ru.gamble.pages;

import cucumber.api.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;

import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;


public abstract class AbstractPage extends Page {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPage.class);

    @ActionTitle("сохраняет с")
    public void saveKeyValue(DataTable dataTable){ CommonStepDefs.saveValueToKey(dataTable); }

    @ActionTitle("нажимает кнопку")
    public static void pressButton(String param){
        CommonStepDefs.pressButton(param);
        workWithPreloader();
    }

    @ActionTitle("stop")
    public static void stop(){
        LOG.info("STOP");
    }

    @ActionTitle("закрываем браузер")
    public static void closeBrowser(){
        PageFactory.getWebDriver().close();
    }




}
