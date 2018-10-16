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

@PageEntry(title = "Лайв")
public class LivePage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(LivePage.class);

    @ElementTitle("Лайв-обзор")
    @FindBy(id = "live-overview")
    private WebElement liveOverviewLink;

    @ElementTitle("События дня")
    @FindBy(id = "day-events")
    private WebElement dayEventLink;

    @ElementTitle("Мультимонитор")
    @FindBy(id = "multimonitor")
    private WebElement multimonitorLink;

    public LivePage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(liveOverviewLink));
    }
}
