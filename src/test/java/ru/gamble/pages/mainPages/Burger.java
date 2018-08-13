package ru.gamble.pages.mainPages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.time.LocalTime;

/**
 * @author p.sivak.
 * @since 10.05.2018.
 */

@PageEntry(title = "Бургер")
public class Burger extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(Burger.class);

    @FindBy(xpath = "//div[@class='subMenuArea subMenuArea_burger subMenuArea_fullwidth active']")
    private WebElement subMenuArea;

    @ElementTitle("Лайв")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Лайв']")
    private WebElement liveBottom;

    @ElementTitle("Прематч")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Прематч']")
    private WebElement prematchBottom;

    @ElementTitle("События дня")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='События\n" +
            "                                            дня']")
    private WebElement dayEventsBottom;

    @ElementTitle("Лайв-обзор")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Лайв-обзор']")
    private WebElement liveViewBottom;

    @ElementTitle("Мультимонитор")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Мультимонитор']")
    private WebElement multimonitorBottom;

    @ElementTitle("Новости")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Новости']")
    private WebElement newsBottom;

    @ElementTitle("Статистика")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='\n" +
            "                                                Статистика\n" +
            "                                            ']")
    private WebElement statisticBottom;

    @ElementTitle("Результаты")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Результаты']")
    private WebElement resultsBottom;

    @ElementTitle("ЦУПИС")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='ЦУПИС']")
    private WebElement tsupisBottom;

    @ElementTitle("Супербет")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Супербет']")
    private WebElement superbetBottom;

    @ElementTitle("Тарифы")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Тарифы']")
    private WebElement tarifsBottom;

    @ElementTitle("Как пополнить баланс")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Как\n" +
            "                                            пополнить баланс']")
    private WebElement howToFillBalanceBottom;

    @ElementTitle("Как начать пари")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Как начать пари']")
    private WebElement hoToStartParyBottom;

    @ElementTitle("Как получить выйгрыш")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Как\n" +
            "                                            получить выигрыш']")
    private WebElement howToGetMoneyBottom;

    @ElementTitle("Фрибет")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Фрибет']")
    private WebElement freeBetBottom;

    @ElementTitle("Бонусы")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[text()='Бонусы']")
    private WebElement bonusesBottom;

    @ElementTitle("Правила")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[contains(.,'Правила')]")
    private WebElement rulesBottom;

    @ElementTitle("Приложения для iOS и Android")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[contains(.,'Приложения для iOS и Android')]")
    private WebElement landingAppBottom;

    @ElementTitle("Онлайн-чат")
    @FindBy(xpath = "//span[contains(@class,'subMenuArea') and contains(.,'Онлайн-чат')]")
    private WebElement onlineChatBottom;

    @ElementTitle("Часто задаваемые вопросы")
    @FindBy(xpath = "//li[@class='subMenuArea__col-item']/a[contains(.,'Часто задаваемые вопросы')]")
    private WebElement faqBottom;

    public Burger() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOf(subMenuArea));
    }
}
