package ru.gamble.pages.faqPages;

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
@PageEntry(title = "Левое меню в часто задаваемых вопросах")
public class LeftMenu extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(ru.gamble.pages.rulesPages.LeftMenu.class);

    @FindBy(xpath = "//div[@class='text-page-sidebar__body']")
    private WebElement menu;

    @ElementTitle("Технические вопросы в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Технические вопросы')]")
    private WebElement techQuestions;

    @ElementTitle("Словарь в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Словарь')]")
    private WebElement slovar;

    public LeftMenu() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(menu));
    }

}
