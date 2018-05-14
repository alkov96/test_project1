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
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.time.LocalTime;


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
        new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOf(pageTitle));
    }
}
