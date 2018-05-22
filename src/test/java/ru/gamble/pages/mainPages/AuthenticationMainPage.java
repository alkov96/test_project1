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
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;


@PageEntry(title = "Авторизованная Главная страница")
public class AuthenticationMainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationMainPage.class);

    @FindBy(id = "topPanelWalletBalance")
    private WebElement pageTitle;

    @ElementTitle("Иконка юзера")
    @FindBy(id = "user-icon")
    private WebElement userIconButton;

    public AuthenticationMainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(pageTitle,10);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("запоминает значение баланса")
    public void rememberBalnce(String param){
        By top_balance = param.equals("бонусов")?By.id("bonus-balance"):By.id("topPanelWalletBalance");//запоминать нужно бонусы или рубли
        float balance = Float.valueOf(getWebDriver().findElement(top_balance).getText());
        LOG.info("значение баланса: " + balance);
        Stash.put("balanceKey",balance);
    }
}
