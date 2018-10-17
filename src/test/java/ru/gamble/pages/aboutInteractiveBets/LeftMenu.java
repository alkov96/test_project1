package ru.gamble.pages.aboutInteractiveBets;

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
 * @since 24.05.2018.
 */
@PageEntry(title = "Левое меню в интерактивных ставках")
public class LeftMenu extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(ru.gamble.pages.rulesPages.LeftMenu.class);

    @FindBy(xpath = "//div[@class='text-page-sidebar__body']")
    private WebElement menu;

    @ElementTitle("Интерактивные ставки и выигрыши в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Интерактивные ставки')]")
    private WebElement interactiveBets;

    @ElementTitle("Как начать пари? в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Как начать пари?')]")
    private WebElement howToStartPary;

    @ElementTitle("Получите фрибет на 1000 бонусов в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Получите фрибет на 1000 бонусов')]")
    private WebElement freeBet;

    public LeftMenu() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(menu));
    }
}
