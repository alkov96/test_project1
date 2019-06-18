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
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Основные положения")
public class RulesPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(RulesPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//span[@class='static-menu__link static-menu__link_active']")
    private WebElement header;


    @ActionTitle("проверяет, что перешел именно по ссылке Как получить выйгрыш")
    public void checkIfComeFromHowToGetMoney(){
        assertThat(true, equalTo(PageFactory.getWebDriver().getCurrentUrl().contains("/rules#withdrawal")));
    }

    public RulesPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(header));
    }
}
