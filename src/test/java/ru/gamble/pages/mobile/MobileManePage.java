package ru.gamble.pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

@PageEntry(title = "Мобильная главная страница")
public class MobileManePage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileManePage.class);

    @ElementTitle("Профиль")
    @FindBy(xpath = "//div/span[contains(@class,'header__button header__profile')]")
    private WebElement buttonProfile;

    public MobileManePage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        waitingForPreloaderToDisappear(30);
        tryingLoadPage(buttonProfile,5, 10);
    }

    @ActionTitle("переключение видов спорта")
    public void checkChangeSport(String widget) {
        String path;
        switch (widget) {
            case "Горячие ставки":
                path = "//div[@class='bets-widget lastMinutesBets']";
                break;
            default:
                path = "//div[@class='bets-widget nearestBroadcasts']";
                break;
        }
    }
}
