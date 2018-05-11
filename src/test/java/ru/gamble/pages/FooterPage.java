package ru.gamble.pages;

import cucumber.api.DataTable;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@PageEntry(title = "Подвал сайта")
public class FooterPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(FooterPage.class);

    @FindBy(xpath = "//*[contains(@class,'f_logo')]")
    private WebElement pageTitle;

    @ElementTitle("О компании")
    @FindBy(xpath = "//a[@href='/about/']")
    private WebElement aboutCompanyLink;

    @ElementTitle("Контакты")
    @FindBy(xpath = "//a[@href='/contact']")
    private WebElement contactsLink;

    @ElementTitle("Как получить выигрыш")
    @FindBy(xpath = "//a[@href='/rules/#withdrawal']")
    private WebElement howGetWinLink;

    @ElementTitle("Мобильное приложение")
    @FindBy(xpath = "//a[@href='/landing/app']")
    private WebElement mobileAppLink;

    @ElementTitle("Онлайн-чат")
    @FindBy(xpath = "//div/a[@href='#']")
    private WebElement onlineChatLink;

    @ElementTitle("Для iOS")
    @FindBy(xpath = "//a[contains(.,'Для iOS')]")
    private WebElement foriOSLink;

    @ElementTitle("Для Android")
    @FindBy(xpath = "//a[contains(.,'Для Android')]")
    private WebElement forAndroidLink;

    @ElementTitle("Facebook")
    @FindBy(xpath = "//a[contains(.,'Facebook')]")
    private WebElement facebookLink;

    @ElementTitle("ВКонтакте")
    @FindBy(xpath = "//a[contains(.,'ВКонтакте')]")
    private WebElement vkontakteLink;

    @ElementTitle("Youtube")
    @FindBy(xpath = "//a[contains(.,'Youtube')]")
    private WebElement youtubeLink;

    @ElementTitle("На футбол")
    @FindBy(xpath = "//a[@href='/football']")
    private WebElement onFootballLink;

    @ElementTitle("На хоккей")
    @FindBy(xpath = "//a[@href='/hockey']")
    private WebElement onHockeyLink;

    @ElementTitle("На волейбол")
    @FindBy(xpath = "//a[@href='/volleyball']")
    private WebElement onVollayballLink;

    @ElementTitle("На баскетбол")
    @FindBy(xpath = "//a[@href='/basketball']")
    private WebElement onBasketballLink;

    @ElementTitle("На теннис")
    @FindBy(xpath = "//a[@href='/tennis']")
    private WebElement onTennisLink;

    @ElementTitle("На киберспорт")
    @FindBy(xpath = "//a[@href='/cybersport']")
    private WebElement onCybersporLink;

    @ElementTitle("Студии Артемия Лебедева")
    @FindBy(xpath = "//a[contains(.,'Лебедева')]")
    private WebElement lebedevLink;


    public FooterPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public final static By live_events = By.id("live-events"); // Лайв - Просмотр событий
    public final static By day_events = By.id("day-events"); // События дня
    public final static By live_overview = By.id("live-overview"); // Лайв-обзор
    public final static By multimonitor = By.id("multimonitor"); // Мультимонитор
    public final static By prematch_events = By.id("prematch-events"); //Прематч - Просмотр событий
    public final static By calendar = By.id("live-calendar"); // Лайв-календарь
    public final static By stats = By.id("stats"); // Статистика
    public final static By results = By.id("results"); //Результаты


    public final static By logo = By.xpath("//div[@class='footer6__inner']//div[contains(@class,'f_logo')]");
    public final static By menu = By.xpath("//div[@class='footer6__inner']//a[@class='f_menu-link']/..");
    public final static By social = By.xpath("//div[@class='footer6__inner']//div[contains(@class,'footer6__block-social')]");
    public final static By mobile = By.xpath("//div[@class='footer6__inner']//div[contains(@class,'footer6__block-mobile')]");
    public final static By forecast = By.xpath("//div[@class='footer6__inner']//div[contains(@class,'footer6__block-forecast')]/div[2]");

    public final static By chat = By.xpath("//div[@class='js-open-chat']/a");
    public final static By under_menu = By.xpath("//div[@class='footer6__block-hdr-sign']/..");
    public final static By lebedev = By.xpath("//div[@class='footer6__block footer6__block-lebedev']");

    public final static By partner_title = By.xpath("//div[@class='footer__inner']/div[@class='f2']/div[@class='footer-partners__title']");
    public final static By partners = By.xpath("//div[@class='footer__inner']/div[@class='f2']/div[@class='footer-partners']");


//    @ActionTitle("проверяет ссылки c")
//    public void checkElementsOfFooter(DataTable dataTable) throws PageInitializationException,PageException {
//        WebDriver driver = PageFactory.getWebDriver();
//        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
//        String linkTitle, expectedText;
//        String link = "";
//        String currentHandle = driver.getWindowHandle();
//
//        for(int i = 0; i < table.size(); i++) {
//            linkTitle = table.get(i).get("Ссылка");
//            expectedText = table.get(i).get("Соответсвует");
//
//            link = PageFactory.getInstance().getCurrentPage().getElementByTitle(linkTitle).getAttribute("href");
//
//            JavascriptExecutor js = (JavascriptExecutor) driver;
//
//            js.executeScript("second_window = window.open('" + link + "')");
//            LOG.info("Получили и перешли по ссылке::"+link);
//
//            Set<String> windows = driver.getWindowHandles();
//            windows.remove(currentHandle);
//            String newWindow = windows.toArray()[0].toString();
//
//            driver.switchTo().window(newWindow);
//            workWithPreloader();
//            List<WebElement> list;
//
//
//            WebElement requiredElement;
//            String xpath = ""
//
//            new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.xpath("bar")));
//
//            for(int j = 0; j < 10; j++) {
//                try {
//                    new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(requiredElement));
//                    requiredElement = driver.findElement(By.xpath("//*[contains(text(),'" + expectedText + "')]"));
//
//                    LOG.info("Понадобилось обновлений страницы::" + j + " Найдено::" + list.get(0).getText());
//                } catch (NoSuchElementException e) {
//                    driver.navigate().refresh();
//                }
//            }
//
//            while (driver.findElements(By.xpath("//*[contains(text(),'" + expectedText + "')]")).isEmpty() && j < 10);{
//                driver.navigate().refresh();
//
//                workWithPreloader();
//                j++;
//            }
//            if(j >= 10){
//                throw new AutotestError("Ошибка! Что-то не так со ссылкой.");
//            } else {
//                list = driver.findElements(By.xpath("//*[contains(text(),'" + expectedText + "')]")).stream().filter(element -> element.isDisplayed()).collect(Collectors.toList());
//                if(list.isEmpty())
//                LOG.info("Понадобилось обновлений страницы::" + j + " Найдено::" + list.get(0).getText());
//                driver.switchTo().window(currentHandle);
//                js.executeScript("second_window.close()");
//            }
//
//        }
//    }



    @ActionTitle("проверяет что число платёжных систем")
    public void checkNumberPaymentSystem(String number){
        String xpath = "//div[contains(@class,'payment-systems-item')]";
        List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath(xpath)).stream().filter(element -> element.isDisplayed()).collect(Collectors.toList());;
        int expected = Integer.parseInt(number);
        int actual = list.size();
        assertThat(actual).as("Количетво иконок платёжных систем [" + actual + "] не соответсвует ожидаемому[" + expected + "]").isEqualTo(expected);
    }

}
