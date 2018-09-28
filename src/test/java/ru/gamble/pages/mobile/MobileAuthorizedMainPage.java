package ru.gamble.pages.mobile;

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

@PageEntry(title = "Авторизованная мобильная главная страница")
public class MobileAuthorizedMainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileAuthorizedMainPage.class);

    @ElementTitle("Профиль")
    @FindBy(xpath = "//a[@href='/private/balance/deposit']")
    private WebElement deposit;

    public MobileAuthorizedMainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        waitingForPreloadertoDisappear(30);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(deposit));
    }

    @ActionTitle("записываем значение баланса в")
    public void writeBalanceIn(String balanceKey){
        String value = deposit.getAttribute("innerText");
        Stash.put(balanceKey, value);
        LOG.info("Сохранили в память key [" + balanceKey + "] <== value [" + value + "]");
    }


}
