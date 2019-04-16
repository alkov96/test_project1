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
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

@PageEntry(title = "PopUp Заключить Пари")
public class PopUpBettingPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(PopUpBettingPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @ElementTitle("Заключить пари")
    @FindBy(xpath = "//a[contains(text(),'Заключить пари')]")
    private WebElement bettingButton;

    public PopUpBettingPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(bettingButton));
    }
}
