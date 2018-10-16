package ru.gamble.pages.livePages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

/**
 * @author p.sivak.
 * @since 10.05.2018.
 */
@PageEntry(title = "Лайв-обзор")
public class LiveViewPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(LiveViewPage.class);

    @FindBy(xpath = "//div[@class='centr-market-contain']")
    private WebElement pageTitle;

    public LiveViewPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(pageTitle,10, 5);
    }
}
