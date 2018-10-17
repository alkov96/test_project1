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

@PageEntry(title = "Поздравляем!")
public class CongratulationPage extends AbstractPage{

    private static final Logger LOG = LoggerFactory.getLogger(CongratulationPage.class);

    @FindBy(xpath = "//div[@class='modal__title' and text()='Поздравляем!']")
    private WebElement pageTitle;

    @ElementTitle("Ок")
    @FindBy(xpath = "//div[@class='modal__btn-row']/button[text()='Ок']")
    private WebElement oKButton;

    public CongratulationPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }


}
