package ru.gamble.pages.administrationPages.servicesPages;

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

/**
 * @author p.sivak.
 * @since 18.05.2018.
 */
@PageEntry(title = "подменю Сервисы")
public class ServiceSubMenu extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(ServiceSubMenu.class);

    @FindBy(xpath = "//div[@id='tabbar-1515-innerCt']")
    private WebElement subMenu;

    @ElementTitle("Сервисные сообщения")
    @FindBy(xpath = "//span[@id='tab-1650-btnInnerEl']")
    private WebElement serviceMessages;

    public ServiceSubMenu() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(subMenu));
    }
}
