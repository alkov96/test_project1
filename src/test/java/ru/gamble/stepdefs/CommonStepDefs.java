package ru.gamble.stepdefs;


import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.sbtqa.tag.pagefactory.drivers.TagWebDriver;

//import static ru.sbtqa.tag.pagefactory.drivers.TagWebDriver.webDriver;

@Slf4j
public class CommonStepDefs {

    private static final Logger LOG = LoggerFactory.getLogger(CommonStepDefs.class);
    private static WebDriver webDriver;


    @ActionTitle("устанавливаем размер окна браузера")
    public static void setSizeWindowBrowser(String param){
            String[] size = param.split("x");
            int width = Integer.parseInt(size[0]);
            int height = Integer.parseInt(size[1]);
        WebDriver driver =  TagWebDriver.getDriver();
        webDriver.manage().window().setSize(new Dimension(width, height));

    }


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
