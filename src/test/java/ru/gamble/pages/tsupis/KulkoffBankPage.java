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

@PageEntry(title = "АО Кулькофф Банк")
public class KulkoffBankPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(KulkoffBankPage.class);
    static WebDriver driver = PageFactory.getDriver();

        @FindBy(xpath = "//form[contains(.,'АО Кулькофф Банк')]")
        private WebElement pageTitle;


        @ElementTitle("Подтвердить")
        @FindBy(xpath = "//input[@type='submit']")
        private WebElement confirmButton;


        public KulkoffBankPage() {
            PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
            new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOf(pageTitle));
        }

    }
