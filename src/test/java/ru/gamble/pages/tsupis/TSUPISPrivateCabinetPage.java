package ru.gamble.pages.tsupis;

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

@PageEntry(title = "Личный кабинет ЦУПИС")
public class TSUPISPrivateCabinetPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(TSUPISPrivateCabinetPage.class);

    @ElementTitle("Первый ЦУПИС")
    @FindBy(id = "menu-handler")
    private WebElement perconLable;

    @ElementTitle("Пополнить")
    @FindBy(xpath = "/a[@href='/frontend/wallet_refill']")
    private WebElement topUpButton;

    @ElementTitle("Вывести")
    @FindBy(xpath = "/a[@href='/frontend/wallet_withdraw']")
    private WebElement withdrawButton;

    public TSUPISPrivateCabinetPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(perconLable));
    }
}
