package ru.gamble.pages.registrationPages;

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

@PageEntry(title = "Учётная запись подтверждена")
public class AccountСonfirmedPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(AccountСonfirmedPage.class);

    @FindBy(xpath = "//*[contains(text(),'Чтобы начать заключать пари, введите')]")
    private WebElement pageTitle;

    @ElementTitle("Продолжить")
    @FindBy(xpath = "//a[@class = 'btn_important' and text()='продолжить']")
    private WebElement continueButton;

    public AccountСonfirmedPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }
}
