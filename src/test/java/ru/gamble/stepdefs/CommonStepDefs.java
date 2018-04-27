package ru.gamble.stepdefs;

import cucumber.api.DataTable;
import cucumber.api.java.ru.Когда;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.sbtqa.tag.qautils.properties.Props;

import javax.print.DocFlavor;
import java.net.URL;
import java.util.List;

public class CommonStepDefs{

    private static final Logger LOG = LoggerFactory.getLogger(CommonStepDefs.class);

    @ActionTitle("нажимает на кнопку")
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
        if(button.isDisplayed()){
            if(!button.getAttribute("href").isEmpty()){
                String url = button.getAttribute("href");
                PageFactory.getWebDriver().get(url);
            }else { button.click();}
            workWithPreloader();
        }
    }

    @Когда("^сохраняем в память$")
    public static void saveValueToKey(DataTable dataTable){
        List<String> data = dataTable.asList(String.class);
        String key, value;
        key = data.get(0);
        value = data.get(1);
        Stash.put(key,value);
    }

    // Метод ожидания появления и изчезновения прелоадера при методе click()
    public static void workWithPreloader(){
       String xpathPreloader = "//*[contains(@class,'preloader__container')]";
       waitShowElement(By.xpath(xpathPreloader));
    }

    // Ожидание появления элемента на странице
    public static void waitShowElement(By by){
        WebDriver driver = PageFactory.getWebDriver();
        WebDriverWait driverWait = new WebDriverWait(driver, 3, 250);
        try{
            driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
            List<WebElement> preloaders = driver.findElements(by);
            LOG.info("Найдено прелоадеров::" + preloaders.size());
            driverWait.until(ExpectedConditions.invisibilityOfAllElements(preloaders));
        }catch (TimeoutException te){
            LOG.info("Прелоадер НЕ появился");
        }
    }

    // Метод перехода на главную страницу
    @Когда("^переходит на главную страницу$")
    public static void goToMainPage(){
        PageFactory.getWebDriver().get(Props.get("webdriver.starting.url"));
    }
}
