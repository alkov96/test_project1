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
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import java.util.List;

public class CommonStepDefs{

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
        if(button.isDisplayed()){
            button.click();
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
       waitHideElement(By.xpath(xpathPreloader));
    }

    // Ожидание появления элемента на странице
    public static void waitShowElement(By by){
        WebDriver driver = PageFactory.getWebDriver();
        WebDriverWait driverWait = new WebDriverWait(driver, 1, 500);
        try{
            driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
            LOG.info("Прелоадер появился");
        }catch (TimeoutException te){
            LOG.info("Прелоадер НЕ появился");
        }
    }

    // Ожидание исчезновения элемента на странице
    public static void waitHideElement (By by){
        WebDriver driver = PageFactory.getWebDriver();
        WebDriverWait driverWait = new WebDriverWait(driver, 1, 500);
        try {
            driverWait.until(ExpectedConditions.invisibilityOfElementLocated(by));
            LOG.info("Прелоадер изчез");
        }catch (TimeoutException te){
            LOG.info("Прелоадер НЕ изчез");
        }

    }
}
