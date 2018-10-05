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
@PageEntry(title = "Фрибет")
public class FreebetPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(FreebetPage.class);

    @FindBy(xpath = "//h1[text()='Получите фрибет на 1000 бонусов']")
    private WebElement header;

    public FreebetPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(driver, this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(header));
    }
}
