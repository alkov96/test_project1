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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    @ActionTitle("выбирает дату из")
    public void selectDate(String keyBirthDate){
        WebDriver driver = PageFactory.getWebDriver();

        String date = Stash.getValue(keyBirthDate);
        List<String> datePieces = Arrays.asList(date.split("-"));

        //Список датапикеров
        List<WebElement> dataPikersFields = driver.findElements(By.xpath("//div[@class='datepicker-col-1']"));
        //Развёрнутый список датапикеров
        Collections.reverse(dataPikersFields);
        WebElement element;
         for(int i = 0; i < dataPikersFields.size(); i++){
             do{
                 element = dataPikersFields.get(i).findElements(By.xpath("div/div/ul/li[not(contains(@class,'disabled'))]")).get(5);
                 if (Integer.parseInt(element.getText()) == Integer.parseInt(datePieces.get(i))) {
                     break;
                 } else if (Integer.parseInt(element.getText()) > Integer.parseInt(datePieces.get(i))) {
                     swipeElementOnOneVerticalPosition(element, 40);
                 } else if (Integer.parseInt(element.getText()) < Integer.parseInt(datePieces.get(i))) {
                     swipeElementOnOneVerticalPosition(element, -40);
                 }
             }while(Integer.parseInt(element.getText()) != Integer.parseInt(datePieces.get(i)));
         }
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
