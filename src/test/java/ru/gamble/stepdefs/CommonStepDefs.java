package ru.gamble.stepdefs;


import com.thoughtworks.selenium.webdriven.commands.WaitForPageToLoad;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Когда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.utility.BeforeTest;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.sbtqa.tag.pagefactory.drivers.TagWebDriver;
import ru.sbtqa.tag.qautils.properties.Props;

import static ru.gamble.pages.utility.Constants.MAIN_WINDOW_HANDLE;

@Slf4j
public class CommonStepDefs {

    private static final Logger LOG = LoggerFactory.getLogger(CommonStepDefs.class);

    public static void pressButton(String param){
        Page page = null;
        WebElement button = null;
        try {
            page = PageFactory.getInstance().getCurrentPage();
            button = page.getElementByTitle(param);
        } catch (PageInitializationException e) {
            LOG.error(e.getMessage());
        } catch (PageException e1) {
            LOG.error(e1.getMessage());
        }
        if(button.isDisplayed()){ button.click();}
    }



}
