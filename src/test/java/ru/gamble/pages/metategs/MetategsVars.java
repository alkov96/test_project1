package ru.gamble.pages.metategs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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


@PageEntry(title = "Переменные метатегов")
public class MetategsVars extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MetategsVars.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//a[@data-id='topMenu_metatagManagementView']")
    private WebElement pageTitle;


    public MetategsVars() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(pageTitle,10, 10);
    }

    @ActionTitle("добавляем метатег")
    public void addMetategs(String type, String var, String value) throws InterruptedException {
        switch (type){
            case "Системные":
                String a = String.valueOf(10 + (int) (Math.random() * 1000));
                driver.findElement(By.xpath("//a[@data-id='toolbarBtn_metatagSystemVarsList_deleteAll']")).click();//удаляем все метатгеги
                driver.findElement(By.xpath("//a[@data-id='toolbarBtn_metatagSystemVarsList_create']")).click();
                Thread.sleep(1000);
                driver.findElement(By.id("combobox-2156-inputEl")).click();
                Thread.sleep(1000);
                driver.findElement(By.xpath("//li[text()='Русский']")).click();
                driver.findElement(By.id("numberfield-2157-inputEl")).sendKeys(a);
                driver.findElement(By.id("combobox-2158-inputEl")).click();
                Thread.sleep(1000);
                driver.findElement(By.xpath("//li[text()= '" + var + "']")).click();
                driver.findElement(By.id("textfield-2159-inputEl")).sendKeys(value);
                driver.findElement(By.xpath("//a[@data-id='winBtn_metatagsystemvarsshow_save']")).click();
                Stash.put(var, value);
                break;
            case "Пользовательские":
                driver.findElement(By.xpath("//a[@data-id='subMenu_metatagUserVarList']")).click();
                driver.findElement(By.id("button-1295-btnIconEl")).click(); //удаляем метатеги
                driver.findElement(By.id("button-1294-btnEl")).click();
                Thread.sleep(500);
                driver.findElement(By.id("textfield-2155-inputEl")).sendKeys(var);
                driver.findElement(By.id("textfield-2156-inputEl")).sendKeys(value);
                driver.findElement(By.xpath("//*[contains(text(),'Сохранить')]/..")).click();
                Stash.put(var, value);
                break;
        }
        LOG.info("Имя переменной " + var + " Зачение переменной " + value);

    }

}
