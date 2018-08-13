package ru.gamble.pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

@PageEntry(title = "Мобильная Залогиненная страница")
public class MobileAlreadyLoggedInPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileAlreadyLoggedInPage.class);

    @ElementTitle("Баланс")
    @FindBy(xpath = "//a[contains(@href,'/private/balance/deposit')]")
    private WebElement balanceField;

    @ElementTitle("Бонус")
    @FindBy(xpath = "//a[contains(@href,'/private/balance/bonus')]")
    private WebElement bonusField;


    public MobileAlreadyLoggedInPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(balanceField,5, 10);
    }
}
