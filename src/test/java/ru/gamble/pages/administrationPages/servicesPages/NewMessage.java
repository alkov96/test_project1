package ru.gamble.pages.administrationPages.servicesPages;

import cucumber.api.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author p.sivak.
 * @since 21.05.2018.
 */
@PageEntry(title = "Новое сервисное сообщение")
public class NewMessage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(NewMessage.class);

    @FindBy(xpath = "//span[contains(text(),'Редактирование сервисного сообщения')]")
    private WebElement message;

    @ElementTitle("элемент для поиска id")
    @FindBy(xpath = "//table[@class='x-field x-table-plain x-form-item x-form-type-checkbox x-field-default x-anchor-form-item']")
    private WebElement elementForId;

    @ElementTitle("Сохранить")
    @FindBy(xpath = "//a[@class='x-btn x-unselectable x-box-item x-toolbar-item x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon']")
    private WebElement saveBotton;

    public NewMessage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOf(message));
    }

    @ActionTitle("создаёт новое сообщение с")
    public void createMessage(DataTable params) throws InterruptedException {
        Map<String, String> data = params.asMap(String.class, String.class);
        String afterMinutes = data.get("Активно через, мин");
        String isActive = data.get("Активность");
        String possibleClose = data.get("Можно скрыть");
        String name = data.get("Название");
        String time = data.get("Время активности, мин");
        String id = elementForId.getAttribute("id");
        if (isActive.equals("Да")) {
            PageFactory.getWebDriver().findElement(By.xpath("//input[@id='" + id + "-inputEl']")).click();
        }
        if (possibleClose.equals("Да")) {
            id = elementForId.getAttribute("id");
            PageFactory.getWebDriver().findElement(By.xpath("//input[@id='" + id + "-inputEl']")).click();
        }
        List<WebElement> textAreas = PageFactory.getWebDriver().findElements(By.xpath("//table[@class='x-field x-table-plain x-form-item x-form-type-text x-field-default x-anchor-form-item']"));
        PageFactory.getWebDriver().findElement(By.xpath("//textarea[@id='" + textAreas.get(0).getAttribute("id") + "-inputEl']")).clear();
        PageFactory.getWebDriver().findElement(By.xpath("//textarea[@id='" + textAreas.get(0).getAttribute("id") + "-inputEl']")).sendKeys(name);
        Date dateStart = startAfterMins(Integer.parseInt(afterMinutes));
        SimpleDateFormat formatForDateStart = new SimpleDateFormat("dd.MM.yyyy' 'kk:mm:ss");
        textAreas = PageFactory.getWebDriver().findElements(By.xpath("//table[@class='x-field x-table-plain x-form-item x-form-type-text x-field-default x-anchor-form-item']"));
        PageFactory.getWebDriver().findElement(By.xpath("//input[@id='" + textAreas.get(1).getAttribute("id") + "-inputEl']")).clear();
        PageFactory.getWebDriver().findElement(By.xpath("//input[@id='" + textAreas.get(1).getAttribute("id") + "-inputEl']")).sendKeys(formatForDateStart.format(dateStart));
        SimpleDateFormat formatForMinutesEnd = new SimpleDateFormat("mm");
        SimpleDateFormat formatForHoursEnd = new SimpleDateFormat("kk");
        int hoursEnd = Integer.parseInt(formatForHoursEnd.format(dateStart));
        int minsEnd = Integer.parseInt(formatForMinutesEnd.format(dateStart)) + Integer.parseInt(time);
        SimpleDateFormat formatForDateEnd = new SimpleDateFormat("dd.MM.yyyy' 'kk:mm:ss");
        while (minsEnd > 60) {
            minsEnd = minsEnd - 60;
            hoursEnd = hoursEnd + 1;
        }
        dateStart.setMinutes(minsEnd);
        dateStart.setHours(hoursEnd);
        textAreas = PageFactory.getWebDriver().findElements(By.xpath("//table[@class='x-field x-table-plain x-form-item x-form-type-text x-field-default x-anchor-form-item']"));
        PageFactory.getWebDriver().findElement(By.xpath("//input[@id='" + textAreas.get(1).getAttribute("id") + "-inputEl']")).clear();
        PageFactory.getWebDriver().findElement(By.xpath("//input[@id='" + textAreas.get(1).getAttribute("id") + "-inputEl']")).sendKeys(formatForDateEnd.format(dateStart));
        Thread.sleep(1000);
        saveBotton.click();
    }

    public Date startAfterMins(int after) {
        Date date = new Date();
        SimpleDateFormat formatForMinutesEnd = new SimpleDateFormat("mm");
        SimpleDateFormat formatForHoursEnd = new SimpleDateFormat("kk");
        int mins = Integer.parseInt(formatForMinutesEnd.format(date));
        int hours = Integer.parseInt(formatForHoursEnd.format(date));
        if (after == 0) {
            return date;
        } else {
            if (after > 0) {
                mins = mins + after;
                while (mins > 60) {
                    mins = mins - 60;
                    hours = hours + 1;
                }
                date.setMinutes(mins);
                date.setHours(hours);
                return date;
            } else {
                mins = mins + after;
                while (mins < 0) {
                    mins = 60 + mins;
                    hours = hours - 1;
                }
                date.setMinutes(mins);
                date.setHours(hours);
                return date;
            }
        }
    }
}
