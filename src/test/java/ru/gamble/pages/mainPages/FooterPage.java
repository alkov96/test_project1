package ru.gamble.pages.mainPages;

import cucumber.api.DataTable;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.stepdefs.CommonStepDefs;
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
import static ru.gamble.utility.Constants.ELEMENT;
import static ru.gamble.utility.Constants.LINK;
import static ru.gamble.utility.Constants.TEXT;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@PageEntry(title = "Подвал сайта")
public class FooterPage extends AbstractPage {
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

    @ActionTitle("проверяем ТЕКСТ при переходе по ссылке с")
    public void checkTextWhenClickingOnLinkWith(DataTable dataTable) throws PageInitializationException,PageException {
        WebDriver driver = PageFactory.getWebDriver();
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String linkTitle, expectedText;
        String link = "";
        String currentHandle = driver.getWindowHandle();

        for(int i = 0; i < table.size(); i++) {
            linkTitle = table.get(i).get(LINK);
            expectedText = table.get(i).get(TEXT);
            String xpath = "//*[contains(text(),'" + expectedText + "')]";
            opensNewTabAndChecksPresenceOFElement(linkTitle, currentHandle, xpath);
        }
    }

    @ActionTitle("проверяем ЭЛЕМЕНТ при переходе по ссылке с")
    public void checkElementWhenClickingOnLinkWith(DataTable dataTable) throws PageInitializationException,PageException {
        WebDriver driver = PageFactory.getWebDriver();
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String linkTitle, elementTitle;
        String link = "";
        String currentHandle = driver.getWindowHandle();

        for(int i = 0; i < table.size(); i++) {
            linkTitle = table.get(i).get(LINK);
            elementTitle = table.get(i).get(ELEMENT);
            String xpath = "//a[contains(@class,'no-reload-js active')]//*[contains(.,'" + elementTitle + "')]";
            opensNewTabAndChecksPresenceOFElement(linkTitle, currentHandle, xpath);
        }
    }

    /**
     * Метод открывает новую вкладку в браузере, переходит на неё,
     * проверяет присутсвие нужного элемента, закрывает вкладку
     * и возвращается на предыдущее окно.
     * @param linkTitle - название ссылки по которой нужно открыть новую вкладку
     * @param currentHandle - идентификатор текущей страницы
     * @param xpath - поисковая строка для требуемоего элемента
     */
    private void opensNewTabAndChecksPresenceOFElement(String linkTitle, String currentHandle, String xpath) {
        WebDriver driver = PageFactory.getWebDriver();
        String link = "";

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

            List <WebElement> requiredElements;

            // Цикл обновления страницы в случае неудачи её прогрузки
            for(int j = 0; j < 10; j++) {
                try {
                    try {
                        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
                    }catch (Exception e){
                        e.getMessage();
                    }
                    requiredElements = driver.findElements(By.xpath(xpath)).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
                    LOG.info("Текущая страница::" + driver.getCurrentUrl());
                    if(!requiredElements.isEmpty()){
                        LOG.info("Понадобилось обновлений страницы::" + j + " Найдено::" + requiredElements.get(0).getText());
                        break;
                    }
                } catch (Exception e){
                    driver.navigate().refresh();
                }
                if(j >= 9){
                    throw new AutotestError("Ошибка! Не нашли элемент после " + j + " попыток перезагрузки страницы");
                }
            }
        driver.switchTo().window(currentHandle);
        js.executeScript("second_window.close()");
    }

    @ActionTitle("проверяет присутствие ссылки")
    public void checkSportsbook_888ru(String param){
        String expected = "https://888.ru/webdav/sportsbook-888ru.apk";
        String actual = PageFactory.getWebDriver().findElement(By.xpath("//a[contains(.,'" + param + "')]")).getAttribute("href");
        assertThat(actual)
              .as("Не найдена ссылка::" + expected).isEqualTo(expected);
    }

    @ActionTitle("проверяет что число платёжных систем")
    public void checkNumberPaymentSystem(String number){
        String xpath = "//div[contains(@class,'payment-systems-item')]";
        List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath(xpath)).stream().filter(element -> element.isDisplayed()).collect(Collectors.toList());;
        int expected = Integer.parseInt(number);
        int actual = list.size();
        assertThat(actual).as("Количетво иконок платёжных систем [" + actual + "] не соответсвует ожидаемому[" + expected + "]").isEqualTo(expected);
    }

}
