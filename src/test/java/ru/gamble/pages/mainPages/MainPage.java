package ru.gamble.pages.mainPages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan;


@PageEntry(title = "Главная страница")
public class MainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MainPage.class);

    @FindBy(xpath = "//*[@class='topLogo888__link topLogo888__link_show']")
    private WebElement pageTitle;


    @ElementTitle("Регистрация")
    @FindBy(id = "register")
    private WebElement registrationButton;

    @ElementTitle("Вход")
    @FindBy(id = "log-in")
    private WebElement enterButton;

    @ElementTitle("Прематч")
    @FindBy(id = "prematch")
    private WebElement prematchButton;

    @ElementTitle("Лайв")
    @FindBy(id = "live")
    private WebElement liveButton;

    public MainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("переключение видов спорта")
    public static void checkChangeSport() {
                //boolean flag = true;
        // DemoSingleton allError = DemoSingleton.getInstance();
        WebDriver driver = PageFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver,10);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {
            CommonStepDefs.waitOfPreloader();
        }catch (InterruptedException e2){
            LOG.error(e2.getMessage());
        }
        LOG.info("Смотрим что страницы в виджете переключаются и содержимое контейнера соответсвует выбранному виду спорта");
        String sportName;
        List<WebElement> allSport = driver.findElements(By.xpath("//div[contains(@class,'nearestBroadcasts')]//li[contains(@class,'sport-tabs__item') and not(contains(@class,'no-link'))]"));
        for (WebElement selectSport : allSport) {
            selectSport.click();
            sportName = selectSport.findElement(By.xpath("i")).getAttribute("class").replace("sport-tabs__icon sport-icon icon-", "");
            LOG.info(sportName);
            wait.until(CommonStepDefs.attributeContainsLowerCase(
                            By.xpath("//div[@class='bets-widget nearestBroadcasts']//div[contains(@class,'bets-widget-table__inner')]"),"class",sportName));

            if (driver.findElements(By.xpath("//div[@class='bets-widget nearestBroadcasts']//div[contains(@class,'bets-widget-table__inner')]/table[1]/tbody/tr")).size() == 1) {
                LOG.error("В ближайших трансляциях есть вкладка спорта " + sportName + ", но список для него пустой");
            }
        }

    }
}
