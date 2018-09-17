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
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Онлайн-чат")
public class OnlineChat extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(OnlineChat.class);

    @FindBy(xpath = "//div[@id='chat-links']")
    private WebElement header;

    @ElementTitle("Свернуть")
    @FindBy(xpath = "//div[@id='webim-chat-close']")
    private WebElement closeChat;

    public OnlineChat() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(header));
    }
}
