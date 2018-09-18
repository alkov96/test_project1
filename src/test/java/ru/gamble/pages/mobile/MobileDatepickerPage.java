package ru.gamble.pages.mobile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.Collections;
import java.util.List;

@PageEntry(title = "Datepicker")
public class MobileDatepickerPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileDatepickerPage.class);

    @FindBy(xpath = "//div[@class='datepicker-header']")
    private WebElement pageTitle;

    @ElementTitle("ОК")
    @FindBy(xpath = "//a[@class='datepicker-navbar-btn']")
    private WebElement okButton;


    public MobileDatepickerPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("выбирает дату")
    public void selectDate(String date){
        WebDriver driver = PageFactory.getWebDriver();
        String format = "12.12.2012";
        String[] datePieces = format.split(".");

        //Список датапикеров
        List<WebElement> dataPikersFields = driver.findElements(By.xpath("//div[@class='datepicker-col-1']"));
        //Разворачиваем список
        Collections.reverse(dataPikersFields);
        for(WebElement dataPiker :dataPikersFields){
            WebElement locator = dataPiker.findElements(By.xpath("div/div/ul/li[not(contains(@class,'disabled'))]")).get(5);
//            locator.getText().equals()
            swipeElementOnOneVerticalPosition(locator, -40);
        }

        //Поля колеса
        PageFactory.getWebDriver().findElements(By.xpath("//div[@class='datepicker-col-1']")).get(2).findElements(By.xpath("div/div/ul/li"));
        //Локатор выбираемого элемента колеса
        WebElement element = PageFactory.getWebDriver().findElements(By.xpath("//div[@class='datepicker-col-1']")).get(2).findElements(By.xpath("div/div/ul/li[not(contains(@class,'disabled'))]")).get(5);



    }

    //TODO написать метод генерации дня рождения, чтобы пользователю было от 18 до 100 лет

    //TODO написать метод проверяющий что год, месяц или день соответсвуют задуманному
    private boolean checkIsSelectedStringMatchesExpected(WebElement element, String expected){
       return element.getText().equals(expected);
    }


    //TODO написать метод двигающий элемент колеса лет, месяцев или дней на 40 пикселов вниз или вверх
    /**
     * Метод перетаскивает элемент по координате Y(вниз или вверх)
     * @param element - локатор элемента
     * @param displacementByY - на сколько пикселов вверх или вниз перетаскивать
     */
    private void swipeElementOnOneVerticalPosition(WebElement element, int displacementByY){
        WebDriver driver = PageFactory.getWebDriver();

        //Локатор выбираемого элемента колеса
//        WebElement element = PageFactory.getWebDriver().findElements(By.xpath("//div[@class='datepicker-col-1']")).get(2).findElements(By.xpath("div/div/ul/li[not(contains(@class,'disabled'))]")).get(5);
//        WebElement element = driver.findElement(By.xpath(locator));
//        int left_x = element.getLocation().getX();
//        int right_x = left_x + element.getSize().getWidth();
//        int middle_x = (left_x + right_x) / 2;
//        int upper_y = element.getLocation().getY();
//        int lower_y = upper_y + element.getSize().getHeight();
//        int middle_y = (upper_y + lower_y) / 2;

        Actions actions = new Actions(driver);
        actions
                .moveToElement(element, 0, displacementByY)
                .build()
                .perform();
    }


}
