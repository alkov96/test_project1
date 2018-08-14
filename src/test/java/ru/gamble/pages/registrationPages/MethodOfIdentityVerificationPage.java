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

@PageEntry(title = "Способ подтверждения личности")
public class MethodOfIdentityVerificationPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MethodOfIdentityVerificationPage.class);

    @FindBy(xpath = "//div[contains(.,'Способ подтверждения личности')]")
    private WebElement pageTitle;

    @ElementTitle("Столото")
    @FindBy(xpath = "//div[@class='confirmWay stoloto']")
    private WebElement stolotoButton;


    @ElementTitle("Выйти")
    @FindBy(id = "terminate-registration-logout-button")
    private WebElement outletButton;

    public MethodOfIdentityVerificationPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }
}
