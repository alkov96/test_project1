package ru.gamble.pages.administrationPages;

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
@PageEntry(title = "Верхнее меню")
public class TopMenu extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(TopMenu.class);

    @FindBy(xpath = "//div[@id='tabbar-1012-innerCt']")
    private WebElement topMenu;

    @ElementTitle("Сервисы")
    @FindBy(xpath = "//span[@id='tab-1945-btnInnerEl']")
    private WebElement serviceBotton;

    public TopMenu() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(topMenu));
    }

}
