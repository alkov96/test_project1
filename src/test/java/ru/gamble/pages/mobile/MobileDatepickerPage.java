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
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@PageEntry(title = "Datepicker")
public class MobileDatepickerPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileDatepickerPage.class);

    @FindBy(xpath = "//div[@class='datepicker-header']")
    private WebElement pageTitle;

    @ElementTitle("OK")
    @FindBy(xpath = "//div/a[contains(.,'Ok')]")
    private WebElement okButton;

    public MobileDatepickerPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    /**
     * Метод устанавливает дату в Датапикере и проверяет что ввелось верно
     * @param keyBirthDate - строка даты в формате yyyy-MM-dd
     */
    @ActionTitle("выбирает дату из")
    public void selectDate(String keyBirthDate){
        WebDriver driver = PageFactory.getWebDriver();

        String settableDate = Stash.getValue(keyBirthDate);
        LOG.info("Достали из памяти: key[" + keyBirthDate + "]==> value[" + settableDate + "]");
        List<String> datePieces = Arrays.asList(settableDate.split("-"));

        //Список датапикеров
        List<WebElement> dataPikersFields = driver.findElements(By.xpath("//div[@class='datepicker-col-1']"));
        //Развёрнутый список датапикеров
        Collections.reverse(dataPikersFields);
        WebElement element;
         for(int i = 0; i < dataPikersFields.size(); i++){
             element = dataPikersFields.get(i).findElements(By.xpath("div/div/ul/li[not(contains(@class,'disabled'))]")).get(5);
             while(Integer.parseInt(element.getText()) != Integer.parseInt(datePieces.get(i))){

                 if (Integer.parseInt(element.getText()) > Integer.parseInt(datePieces.get(i))) {
                     swipeElementOnOneVerticalPosition(element, 40);
                 } else if (Integer.parseInt(element.getText()) < Integer.parseInt(datePieces.get(i))) {
                     swipeElementOnOneVerticalPosition(element, -40);
                 }
                 element = dataPikersFields.get(i).findElements(By.xpath("div/div/ul/li[not(contains(@class,'disabled'))]")).get(5);
             }

             try {
                 Thread.sleep(1000);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }

         }
         
        // Проверяем введённую дату с той что пытались ввести
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ZoneId defaultZoneId = ZoneId.systemDefault();
         String actualString = driver.findElement(By.xpath("//div[@class='datepicker-header']")).getText();
         LOG.info("На экране [" + actualString + "]");
         DateFormat dateFormat1 = new SimpleDateFormat("dd.MM.yyyy");
         DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
         LocalDate actualDate = null,expectedDate = null;
         try {
             actualDate = dateFormat1.parse(actualString).toInstant().atZone(defaultZoneId).toLocalDate();
             expectedDate = dateFormat2.parse(settableDate).toInstant().atZone(defaultZoneId).toLocalDate();
                    } catch (ParseException e) {
            e.printStackTrace();

         }
        assertThat(actualDate.isEqual(expectedDate)).as("Ожидали[" + expectedDate.toString() + "],а получили[" + actualDate.toString() + "]").isTrue();
    }

    /**
     * Метод перетаскивает элемент по координате Y(вниз или вверх)
     * @param element - локатор элемента
     * @param displacementByY - на сколько пикселов вверх или вниз перетаскивать
     */
    private void swipeElementOnOneVerticalPosition(WebElement element, int displacementByY){
        WebDriver driver = PageFactory.getWebDriver();
        Actions actions = new Actions(driver);
        actions
                .dragAndDropBy(element, 0, displacementByY)
                .build()
                .perform();
    }
}
