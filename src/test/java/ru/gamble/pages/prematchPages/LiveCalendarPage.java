package ru.gamble.pages.prematchPages;

import org.junit.jupiter.api.Assertions;
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
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    @FindBy(xpath = "//div[contains(@class,'select__toggler')]/span[2]")
    private WebElement menuForSelectingSports;


    public LiveCalendarPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(centralMarkets));
        tryingLoadPage(menuForSelectingSports,5, 10);
        workWithPreloader();
    }

    @ActionTitle("ищет событие с коэффициентом")
    public void findCoeff(String param) {
        Double coeff = Double.parseDouble(param);
        List<WebElement> allDaysPages = PageFactory.getWebDriver().findElements(By.cssSelector("span.livecal-days__weekday.ng-binding"));
        int tryPage = 0;
        boolean isCoeffFound = false;
        while (!isCoeffFound && tryPage < allDaysPages.size()-1) {
            try {
                waitForElementPresent(By.xpath("//div[contains(@class,'livecal-table__coefficient')]"), 3);
            }catch (Exception e){
                LOG.info("На странице [" + allDaysPages.get(tryPage).getText() + "] нет событий");
            }
            List<WebElement> correctCoeffs = PageFactory.getWebDriver().findElements(By.xpath("//table[@class='table livecal-table ng-scope']/div[contains(text(), '" + coeff + "')]"));
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
                waitingForPreloadertoDisappear(10);
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
        String xpathCoefficient = "//preceding-sibling::td[contains(@class,'livecal-table__col_event')]";
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> coefficients = driver.findElements(By.xpath("//td[contains(@class,'livecal-table__col_1')]//span[@class='ng-hide']/ancestor::td[contains(@class,'livecal-table__col_1')and not(contains(@class,'empty'))]"));
        int count = 0;
        int number = (Integer.valueOf(param)) - 1;
        Random random = new Random();
        int num;

        LOG.info("Жмём на коэфициенты");
        if(coefficients.size() == 0){
            throw new AutotestError("Ошибка! Количество событий::" + coefficients.size());
        }
        do {
            if (coefficients.size()<=number){
                LOG.info("Всего добавлось ставок" + count);
                break;
            }
            LOG.info("coefficients = " + coefficients.size());
            num = random.nextInt(Math.abs(coefficients.size() - 1 - count));
            LOG.info("num = " + num);
            if (coefficients.get(num).isDisplayed()) {
                coefficients.get(num).click();
                LOG.info("Ожидаем прогрузки коеффициента.");
                new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathCoefficient)));
                LOG.info(coefficients.get(num).findElement(By.xpath(xpathCoefficient)).getText());
                coefficients.remove(num);
                count++;
            }
        } while (count <= number);
    }

    @ActionTitle("в меню выбора видов спорта выбирает")
    public void inSportsSelectionMenuSelect(String sport){
        WebDriver driver = PageFactory.getWebDriver();
        LOG.info("Нажимаем на выпадающее меню видов спорта");
        waitingForPreloadertoDisappear(30);
        menuForSelectingSports.click();
        LOG.info("Выбираем вид спорта::" + sport);
        WebElement selectSport = menuForSelectingSports.findElement(By.xpath("//li/label[contains(.,'" + sport + "')]"));
        selectSport.click();
        new WebDriverWait(driver,5).until(ExpectedConditions.visibilityOf(menuForSelectingSports));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info("Нажимаем на выпадающее меню видов спорта");
        menuForSelectingSports.click();
    }

    @ActionTitle("выбирает следующий день недели с более чем событиями")
    public void selectsNextDayOfWeek(String numberOfIvents){
        String xpathCurrentDayOfWeek = "//li[contains(@class,'tabs__tab tabs__tab_livecal') and contains(@class,'tabs__tab_active')]";
        String xpathNextDayOfWeek = "following-sibling::li";

        new WebDriverWait(PageFactory.getWebDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathCurrentDayOfWeek)));

        WebElement currentDayOfWeek = PageFactory.getDriver().findElement(By.xpath(xpathCurrentDayOfWeek));
        List<WebElement> listOtherDeysOfWeek = currentDayOfWeek.findElements(By.xpath(xpathNextDayOfWeek));
        int actualIvents = 0;

        for(WebElement el:listOtherDeysOfWeek){
            actualIvents = el.findElements(By.xpath("//span[@class='ng-hide']/ancestor::td[contains(@class,'livecal-table__col_1') and not(contains(@class,'empty'))]"))
                    .stream().collect(Collectors.toList()).size();
            if(actualIvents > Integer.parseInt(numberOfIvents)) {
                return;
            }
            el.click();
            LOG.info("Нажали на::[" + el.getText() + "]");
            workWithPreloader();
        }
        throw new AutotestError("Ошибка! Недостаточно событий. Ожидали[" + numberOfIvents + "], а фактически[" + actualIvents + "]");
    }



}
