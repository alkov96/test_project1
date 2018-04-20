package ru.gamble.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;


@PageEntry(title = "Авторизованная Главная страница")
public class AutotentificationMainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(AutotentificationMainPage.class);

    @FindBy(id = "topPanelWalletBalance")
    private static WebElement pageTitle;

    @ElementTitle("Иконка юзера")
    @FindBy(id = "user-icon")
    private WebElement userIconButton;

    public AutotentificationMainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }
}
