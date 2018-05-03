package ru.gamble.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

public class PassportDataPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(PassportDataPage.class);

    @FindBy(xpath = "//*[text()='Паспортные данные']")
    private WebElement pageTitle;

    @ElementTitle("Серия")
    @FindBy(id = "passpserial")
    private WebElement passpSerialInput;

    @ElementTitle("Номер")
    @FindBy(id = "passpserialnum")
    private WebElement passpNumberInput;





    public PassportDataPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }
}
