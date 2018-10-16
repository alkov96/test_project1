package ru.gamble.pages.rulesPages;

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
import ru.gamble.pages.LandingAppPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.openqa.selenium.By.xpath;
import static ru.gamble.utility.Constants.LINK;
import static ru.gamble.utility.Constants.TEXT;

/**
 * @author a.kovtun
 * @since 24.07.2018.
 */

@PageEntry(title = "Азбука беттинга")
public class AzbukaBettingaPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(BonusesPage.class);

    @FindBy(xpath = "//div[contains(@class,'abc-betting__header abc-betting__header')]")
    private WebElement pageTitle;

    public AzbukaBettingaPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }


    @ElementTitle("Для айфонов")
    @FindBy(xpath = "//i[@class='icon icon-mac']")
    private WebElement foriOSLink;

    @ElementTitle("Что такое экспресс?")
    @FindBy(xpath = "//a[text()='Что такое экспресс?']")
    private WebElement whatIsExpress;

    @ElementTitle("Что такое ординар?")
    @FindBy(xpath = "//a[text()='Что такое ординар?']")
    private WebElement whatIsOrdinar;

    @ElementTitle("Что такое система?")
    @FindBy(xpath = "//a[text()='Что такое система?']")
    private WebElement whatIsSystem;

    @ElementTitle("Словарь терминов")
    @FindBy(xpath = "//a[text()='Словарь терминов']")
    private WebElement termsVoc;

    @ElementTitle("Игровые стратегии")
    @FindBy(xpath = "//a[text()='Игровые стратегии']")
    private WebElement gamesStrategy;

    @ElementTitle("Руководство для новичка")
    @FindBy(xpath = "//a[text()='Руководство для новичка']")
    private WebElement rukovodstvoNovichok;

    @ElementTitle("Лайв-ставки")
    @FindBy(xpath = "//a[text()='Лайв-ставки']")
    private WebElement liveBets;

    @ElementTitle("Прематч-ставки")
    @FindBy(xpath = "//a[text()='Прематч-ставки']")
    private WebElement prematchBets;

    @ElementTitle("Для Android")
    @FindBy(xpath = "//i[@class='icon icon-android']")
    private WebElement forAndroidLink;

    @ElementTitle("Facebook")
    @FindBy(xpath = "//div[contains(@class,'abc-betting-socials')]/a[1]")
    private WebElement facebookLink;

    @ElementTitle("ВКонтакте")
    @FindBy(xpath = "//div[contains(@class,'abc-betting-socials')]/a[2]")
    private WebElement vkontakteLink;

    @ElementTitle("Youtube")
    @FindBy(xpath = "//div[contains(@class,'abc-betting-socials')]/a[3]")
    private WebElement youtubeLink;

    @ActionTitle("пролистываем страницу до блока 'Новичку'")
    public void scrollAzbukaNovichok() {
        WebDriver driver = PageFactory.getDriver();
        int x, y;
        y = driver.findElement(By.xpath("//div[@class='abc-betting__tile abc-betting__tile_image']/a")).getLocation().getY() - 100;
        x = driver.findElement(By.xpath("//div[@class='abc-betting__tile abc-betting__tile_image']/a")).getLocation().getX() - 100;
        CommonStepDefs.scrollPage(x, y);
    }

    @ActionTitle("в блоке 'Новичку' проверяем ссылку на 'Термины'")
    public void linksOnAzbukaTerms() {
        WebDriver driver = PageFactory.getDriver();
        WebElement link = driver.findElement(By.xpath("//a[@href='/azbuka-bettinga/terminy' and text()='Разобраться в терминах']"));
        CommonStepDefs.goLink(link, "azbuka-bettinga/terminy");
        LOG.info("Ссылка на Термины работает");
    }

    @ActionTitle("в блоке 'Новичку' проверяем ссылку на 'Стратегии'")
    public void linksOnAzbukaStrategies() {
        WebDriver driver = PageFactory.getDriver();
        WebElement link = driver.findElement(By.xpath("//a[@href='/azbuka-bettinga/igrovye-strategii' and text()='Разобраться в стратегиях']"));
        CommonStepDefs.goLink(link, "azbuka-bettinga/igrovye-strategii");
    }

    @ActionTitle("в блоке 'Новичку' проверяем ссылку на 'Курс молодого бойца'")
    public void linksOnAzbukaNovichok() {
        WebDriver driver = PageFactory.getDriver();
        WebElement link = driver.findElement(By.xpath("//a[@href='/azbuka-bettinga/novichku' and text()='Разобраться в видах ставок']"));
        CommonStepDefs.goLink(link, "azbuka-bettinga/novichku");
    }

    @ActionTitle("пролистываем страницу до блока 'Платформы'")
    public void scrollAzbukaPlatforms() {
        WebDriver driver = PageFactory.getDriver();
        int x, y;
        y = driver.findElement(By.xpath("//a[@name='platformy']")).getLocation().getY() - 100;
        x = driver.findElement(By.xpath("//a[@name='platformy']")).getLocation().getX() - 100;
        CommonStepDefs.scrollPage(x, y);
    }

    @ActionTitle("проверяем переход на страницы с")
    public void checkPagesOnPlatforms(DataTable dataTable) throws PageException {
        WebDriver driver = PageFactory.getWebDriver();
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String linkElement, titleOnLink;
        String currentHandle = driver.getWindowHandle();
        String link = "";

        for (int i = 0; i < table.size(); i++) {
            linkElement = table.get(i).get(LINK);
            titleOnLink = table.get(i).get(TEXT);
            CommonStepDefs.goLink(getElementByTitle(linkElement), titleOnLink);

        }
    }

    @ActionTitle("нажимает на кнопку для загрузки приложения на android")
    public void clickDownloadAndroid() {
        WebDriver driver = PageFactory.getDriver();
        driver.findElement(xpath("//i[@class='icon icon-android']")).click();
    }

    @ActionTitle("проверяет скачивание приложения на android")
    public void downloadAndroidForLanding() throws IOException, InterruptedException {
        LandingAppPage.downloadAndroid();
    }


}
