package ru.gamble.pages.mainPages;

import cucumber.api.DataTable;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.utility.JsonLoader;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.pagefactory.exceptions.ElementNotFoundException;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.useDefaultRepresentation;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.*;

@PageEntry(title = "Подвал сайта")
public class FooterPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(FooterPage.class);
    static WebDriver driver = PageFactory.getDriver();

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
    @FindBy(xpath = "//div[@class='footer6__inner']//a[@href='/landing/app']")
    private WebElement mobileAppLink;

    @ElementTitle("Онлайн-чат")
    @FindBy(xpath = "//div[@class='js-open-chat']/span")
    private WebElement onlineChatLink;

    @ElementTitle("Для iOS")
    @FindBy(xpath = "//div[@class='footer6__inner']//a[contains(.,'Для iOS')]")
    private WebElement foriOSLink;

//    @ElementTitle("для айфонов")
//    @FindBy(xpath = "//a[@id='app_desctop_top_block_btn_iphone']")
//    private WebElement forIPhoneLink;

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
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }


    /**
     * Метод открывает новую вкладку в браузере, переходит на неё,
     * проверяет присутсвие нужного элемента, закрывает вкладку
     * и возвращается на предыдущее окно.
     * @param linkTitle - название ссылки по которой нужно открыть новую вкладку
     * @param currentHandle - идентификатор текущей страницы
     * @param xpath - поисковая строка для требуемоего элемента
     */
    public static void opensNewTabAndChecksPresenceOFElement(String linkTitle, String currentHandle, String xpath) {
        String link = "";
        try {
            Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
            String browserName = caps.getBrowserName();
            if (browserName.contains("explorer")){
                goTabAndChecksPresenceOFElement(linkTitle, currentHandle, xpath);
                return;
            }

        }catch (Exception e){
            return;
        }

        // Достаем ссылку из элемента
        try {
            link = PageFactory.getInstance().getCurrentPage().getElementByTitle(linkTitle).getAttribute("href");
        } catch (PageException e) {
            e.printStackTrace();
        }


        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Переходим по полученной ссылке
        js.executeScript("second_window = window.open('" + link + "')");
        LOG.info("Получили и перешли по ссылке::" + link);

        Set<String> windows = driver.getWindowHandles();
        windows.remove(currentHandle);
        String newWindow = windows.toArray()[0].toString();

        driver.switchTo().window(newWindow);
        workWithPreloader();
        checkPageByText(xpath);

        driver.switchTo().window(currentHandle);
        js.executeScript("second_window.close()");
    }

    /**
     * Метод щелкает на элемент и проверяет что открывшаяся вкладка соответсвует ожиданиям
     * @param linkTitle - название ссылки по которой нужно открыть новую вкладку
     * @param currentHandle - идентификатор текущей страницы
     * @param xpath - поисковая строка для требуемоего элемента
     */
    public static void goTabAndChecksPresenceOFElement(String linkTitle, String currentHandle, String xpath) {
        WebElement element = null;
        try {
            element = PageFactory.getInstance().getCurrentPage().getElementByTitle(linkTitle);
        } catch (PageException e) {
            e.printStackTrace();
        }
        element.click();
        workWithPreloader();
        checkPageByText(xpath);
        driver.navigate().back();
        new WebDriverWait(driver,10)
                .withMessage("Не удалось вернуться а предыдущую страницу " + currentHandle)
                .until(ExpectedConditions.titleIs(currentHandle));
    }


    public static void checkPageByText(String xpath){
        List <WebElement> requiredElements;

        // Цикл обновления страницы в случае неудачи её прогрузки
        for(int j = 0; j < 10; j++) {
            //new WebDriverWait(driver, 3);
            requiredElements = driver.findElements(By.xpath(xpath)).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
            LOG.info("Текущая страница::" + driver.getCurrentUrl());
            if(!requiredElements.isEmpty()){
                LOG.info("Понадобилось обновлений страницы::" + j + " Найдено::" + requiredElements.get(0).getAttribute("innerText").replaceAll("\n", " "));
                break;
            }
            driver.navigate().refresh();
            if(j >= 9){
                throw new AutotestError("Ошибка! Не нашли элемент " + xpath + " после " + j + " попыток перезагрузки страницы");
            }
        }
    }


    @ActionTitle("проверяет присутствие ссылки")
    public void checkSportsbook_888ru(String param){
        String expected = "https://888.ru/webdav/sportsbook-888ru.apk";
        String expected2 = Stash.getValue("MAIN_URL")+"/mobile/app/android/last_version";
        String actual = PageFactory.getWebDriver().findElement(By.xpath("//a[contains(.,'" + param + "')]")).getAttribute("href");
        Assertions.assertTrue(
                actual.equals(expected) || actual.equals(expected2),
                "Не найдена ссылка::" + expected + ", или " + expected2);
    }

    @ActionTitle("проверяет что число платёжных систем")
    public void checkNumberPaymentSystem(String number){
 /*       if (!driver.findElement(By.xpath("//div[contains(@class,'footer_collapsible')]")).getAttribute("class").contains("open")){
            driver.findElement(By.xpath("//div[@class='footer__pin']")).click();
            LOG.info("Футер закрыт, раскроем его");
            new WebDriverWait(driver,10).withMessage("На стрелочку кликнули, а футер не развернулся")
                    .until(ExpectedConditions.attributeContains(By.xpath("//div[contains(@class,'footer_collapsible')]"),"class","open"));
        }*/
        workWithPreloader();
        String xpath = "//div[contains(@class,'payment-systems-item')]";
        List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath(xpath)).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
        int expected = Integer.parseInt(number);
        int actual = list.size();
        assertThat(actual).as("Количетво иконок платёжных систем [" + actual + "] не соответсвует ожидаемому [" + expected + "]").isEqualTo(expected);
    }

}
