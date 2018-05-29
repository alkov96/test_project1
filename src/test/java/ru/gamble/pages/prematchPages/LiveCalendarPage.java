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
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

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
            List<WebElement> correctCoeffs = PageFactory.getWebDriver().findElements(By.xpath("//table[@class='table livecal-table ng-scope']/div[contains(text(), '"+coeff+"')]"));
            if (correctCoeffs.size()>0) {
                for(WebElement element : correctCoeffs){
                    if (element.isDisplayed()){
                        element.click();
                        isCoeffFound = true;
                        break;
                    }
                }
            } else {
                tryPage++;
                allDaysPages.get(tryPage).click();
                workWithPreloader();
            }
        }
        allDaysPages.get(0).click();
        workWithPreloader();
    }

    @ActionTitle("добавляет корректные события, пока их не станет")
    public void fillCouponCorrectEvents(String value) throws InterruptedException {
        fillCouponFinal(Integer.parseInt(value), "correct", By.xpath("//div[contains(@class,'livecal-table__coefficient')]"));
    }

    @ActionTitle("добавляет некорректные события, пока их не станет")
    public void fillCouponIncorrectEvents(String value) throws InterruptedException {
        fillCouponFinal(Integer.parseInt(value), "incorrect", By.xpath("//div[contains(@class,'livecal-table__coefficient')]"));
    }

    /**
     * Добавление в купон нескольких ставок со страницы Лайв-календарь
     * @param param - сколько ставок нужно доабвить в купон
     */
    @ActionTitle("добавляет ставки из разных событий в количестве")
    public void addToCouponDifferentBets(String param){
        //  List<WebElement> coefficients = driver.findElements(By.cssSelector("td.table__body-cell.livecal-table__col_1"));WebDriver driver = PageFactory.getDriver();
        WebDriver driver = PageFactory.getDriver();
     //   List<WebElement> coefficients = driver.findElements(By.xpath("//td[contains(@class,'livecal-table__col_1')]//span[@class='ng-hide']/../.."));
        List<WebElement> coefficients = driver.findElements(By.xpath("//td[contains(@class,'livecal-table__col_1')]//span[@class='ng-hide']/ancestor::td[contains(@class,'livecal-table__col_1')]"));
        int count = 0;
        int number = Integer.valueOf(param)-1;
        Random random = new Random();
        int num;

        LOG.info("Жмём на коэфициенты");
        do {
            LOG.info("coefficients = " + coefficients.size());
            num = random.nextInt(coefficients.size()-1-count);
            LOG.info("num = " + num);
            if (coefficients.get(num).isDisplayed()) {
                coefficients.get(num).click();
                LOG.info(coefficients.get(num).findElement(By.xpath("preceding-sibling::td[contains(@class,'livecal-table__col_event')]")).getText());
                coefficients.remove(num);
                count++;
            }
        } while (count <= number);
    }



}
