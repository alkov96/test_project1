package ru.gamble.pages.prematchPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.pages.CouponPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author p.sivak.
 * @since 04.05.2018.
 */
@PageEntry(title = "Лайв-календарь")
public class LiveCalendarPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(LiveCalendarPage.class);

    @FindBy(xpath = "//div[@class='livecal-calendar-wrapper']")
    private WebElement centralMarkets;

//    @FindBy(xpath = "span.livecal-days__weekday.ng-binding")


    public LiveCalendarPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(centralMarkets));
    }

    @ActionTitle("ищет событие с коэффициентом")
    public void findCoeff(String param) throws InterruptedException {
        Double coeff = Double.parseDouble(param);
        List<WebElement> allDaysPages = PageFactory.getWebDriver().findElements(By.cssSelector("span.livecal-days__weekday.ng-binding"));
        int tryPage = 0;
        boolean isCoeffFound = false;
        while (isCoeffFound == false && tryPage < allDaysPages.size()-1) {
            waitForElementPresent(By.xpath("//div[contains(@class,'livecal-table__coefficient')]"),1000);
            List<WebElement> correctCoeffs = PageFactory.getWebDriver().findElements(By.xpath("//div[contains(text(), '"+coeff+"')]"));
            if (correctCoeffs.size()>0) {
                correctCoeffs.get(0).click();
                isCoeffFound = true;
            } else {
                tryPage++;
                allDaysPages.get(tryPage).click();
            }
        }
    }

    @ActionTitle("добавляет корректные события, пока их не станет")
    public void fillCouponCorrectEvents(String value) throws InterruptedException {
        fillCouponFinal(Integer.parseInt(value), "correct", By.xpath("//div[contains(@class,'livecal-table__coefficient')]"));
    }

    @ActionTitle("добавляет некорректные события, пока их не станет")
    public void fillCouponIncorrectEvents(String value) throws InterruptedException {
        fillCouponFinal(Integer.parseInt(value), "incorrect", By.xpath("//div[contains(@class,'livecal-table__coefficient')]"));
    }



}
