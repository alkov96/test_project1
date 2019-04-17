package ru.gamble.pages.rulesPages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Бонусы")
public class BonusesPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(BonusesPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//h2[@class='get-bonuses__h2 get-bonuses__h2_compens']")
    private WebElement header;

    public BonusesPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(header));
    }
}
