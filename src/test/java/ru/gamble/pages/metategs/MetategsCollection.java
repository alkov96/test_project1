package ru.gamble.pages.metategs;

import cucumber.api.DataTable;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.gamble.stepdefs.CommonStepDefs.goToMainPage;


@PageEntry(title = "Наборы метатегов")
public class MetategsCollection extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MetategsCollection.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(id = "tabbar-1301-innerCt")
    private WebElement pageTitle;


    public MetategsCollection() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(pageTitle, 10, 10);
    }

    public void addMetategs(String type1, String url1) throws InterruptedException {
        Actions actions = new Actions(driver);
        url1 = Stash.getValue("URL");
        driver.findElement(By.xpath("//label[text()='URL:']/ancestor-or-self::tr//input")).clear();
        driver.findElement(By.xpath("//label[text()='URL:']/ancestor-or-self::tr//input")).sendKeys(url1);
        driver.findElement(By.xpath("//*[contains(text(),'Сохранить')]/..")).click();
        driver.findElement(By.xpath("//*[contains(text(),'OK')]/..")).click();
        switch (type1) {
            case "Шаблоны":
                driver.findElement(By.id("gridcolumn-1318-titleEl")).click();
                driver.findElement(By.id("gridcolumn-1318-titleEl")).click();
                Thread.sleep(1000);
                actions.doubleClick(driver.findElements(By.xpath("//td[@class='x-grid-cell x-grid-td x-grid-cell-gridcolumn-1318 x-grid-cell-first x-unselectable ']")).get(0)).perform();
                break;
            case "Точные URL":
                driver.findElement(By.id("gridcolumn-1340-titleEl")).click();
                driver.findElement(By.id("gridcolumn-1340-titleEl")).click();
                Thread.sleep(1000);
                actions.doubleClick(driver.findElements(By.xpath("//td[@class='x-grid-cell x-grid-td x-grid-cell-gridcolumn-1340 x-grid-cell-first x-unselectable ']")).get(0)).perform();
                break;
        }
        driver.findElement(By.xpath("//*[contains(text(),'Добавить теги')]/..")).click();
    }

    @ActionTitle("устанавливаем метатеги")
    public void setUpMetateg(String type, String url) throws InterruptedException {
        switch (type) {
            case "Шаблоны":
                driver.findElement(By.xpath("//*[contains(text(),'Шаблоны')]/..")).click();
                driver.findElement(By.id("tempBtn")).click();
                break;
            case "Точные URL":
                driver.findElement(By.xpath("//*[contains(text(),'Точные URL')]/..")).click();
                driver.findElement(By.id("urlBtn-btnIconEl")).click();
                break;
            case "Метатеги по дефолту":
                driver.findElement(By.xpath("//*[contains(text(),'Метатеги по дефолту')]/..")).click();
                driver.findElement(By.id("button-1372-btnIconEl")).click();
                break;
        }
        addMetategs(type, url);
    }
    @ActionTitle("заполняем форму с")
    public void fullfillForm(DataTable dataTable) throws InterruptedException {
        Map<String, String> table = dataTable.asMap(String.class, String.class);
        String inputField, date = "", value;
        String a = String.valueOf(1000000 + (int) (Math.random() * 1000000000));
        driver.findElement(By.xpath("//label[text()='Язык:']/ancestor-or-self::td/following-sibling::td")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//li[text()='Русский']")).click();
        Map<String,String> bla =  new HashMap<>();
        for (Map.Entry<String, String> aTable : table.entrySet()) {
            inputField = aTable.getKey();
            value = aTable.getValue();
            driver.findElement(By.xpath("//label[text()= '"+ inputField + ":'"+"]/ancestor-or-self::td/following-sibling::td/textarea")).sendKeys(value);
            bla.put(inputField,value);
        }
        Stash.put("inputFieldKey",bla);
        driver.findElements(By.xpath("//*[contains(text(),'Сохранить')]/..")).get(1).click();
        try {
            driver.findElement(By.xpath("//label[text()='№ п/п:']/ancestor-or-self::td/following-sibling::td/table//input")).clear();
            driver.findElement(By.xpath("//label[text()='№ п/п:']/ancestor-or-self::td/following-sibling::td/table//input")).sendKeys(a);
        }
        catch (Exception e){
        }

        driver.findElements(By.xpath("//*[contains(text(),'Сохранить')]/..")).get(0).click();
        try {
            driver.findElements(By.xpath("//*[contains(text(),'Сохранить')]/..")).get(0).click();
        }
            catch (Exception e){
        }

        Thread.sleep(500);
        driver.findElement(By.xpath("//*[contains(text(),'OK')]/..")).click();


    }

    @ActionTitle("Ищем урл с")
    public void findURL(String urrl){
        urrl = Stash.getValue("URL");
        urrl.replace("/*", "");
        Actions actions = new Actions(driver);
        List<WebElement> urls = driver.findElements(By.xpath("//*[contains(text(), '"+ urrl +"')]/.."));
        try {
            if (!(urls.size()==0)){
                for (WebElement url : urls){
                    actions.doubleClick(url).perform();
                    driver.findElements(By.xpath("//*[contains(text(),'Удалить')]/..")).get(2).click();

                }
            }
        } catch (Exception e){
            driver.findElement(By.id("button-1311-btnIconEl")).click();
            if (!(urls.size()==0)){
                for (WebElement url : urls){
                    actions.doubleClick(url).perform();
                    driver.findElements(By.xpath("//*[contains(text(),'Удалить')]/..")).get(1).click();

                }
            }
        }
    }

    @ActionTitle("проверяем метатеги на сайте")
    public void jsSelector(){
        goToMainPage("https://test-func.gamebet.ru/stavki/live");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Map<String,String> vvv =  new HashMap<>();
        vvv = Stash.getValue("inputFieldKey");
        vvv.remove("title");
        vvv.remove("h1");

        for (Map.Entry<String,String> aTable : vvv.entrySet()){
          String name =  aTable.getKey();
            if (name.equals("keyword")){
                name = "keywords";
            }
            List<WebElement> listNames = driver.findElements(By.xpath("//meta[@name='"+name+"']"));
            String title = driver.findElement(By.xpath("//title[@ng-bind='title']")).getAttribute("innerText");
            List<WebElement> listProperties = driver.findElements(By.xpath("//meta[@property='"+name+"']"));
            String res = new String();
            if (listNames.size()==0){
                res = listProperties.get(0).getAttribute("content");
            } else {
                res = listNames.get(0).getAttribute("content");
            }

            LOG.info("Название:" + name);
//          try {
//              res = (String) js.executeScript("var x = document.querySelector('meta[name=\""+name+"\"]').content; return x;");
//
//          } catch (WebDriverException e){
//                  res = (String) js.executeScript("var x = document.querySelector('meta[property=\""+name+"\"]').content; return x;");
//          }
          String value = aTable.getValue();
          if (value.matches("[{]{2}[_a-zA-Z]*}}")){
              value = Stash.getValue(value.replaceAll("[{]*","").replaceAll("}",""));
          }
          LOG.info(value);
          if (!res.equals(value)){
              Assert.fail("Ошибка! Метатег в админке: "+value+" не соответствует метатегу на сайте "+res+"");
          }
        }
    }
}

