package ru.gamble.pages.rulesPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

/**
 * @author a.kovtun
 * @since 24.07.2018.
 */

@PageEntry(title = "Азбука беттинга")
public class AzbukaBettingaPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(BonusesPage.class);

    @FindBy(xpath = "//div[@class='abc-betting__header abc-betting__header_img1']")
    private WebElement header;

    public AzbukaBettingaPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(header,10, 5);
        //new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(header));
    }

    @ActionTitle("смотрит ссылку на страницу Термины")
    public void linksOnAzbukaBettinga() {
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
        int x, y;
        y = driver.findElement(By.xpath("//div[@class='abc-betting__tile abc-betting__tile_image']/a")).getLocation().getY() - 100;
        x = driver.findElement(By.xpath("//div[@class='abc-betting__tile abc-betting__tile_image']/a")).getLocation().getX() - 100;
        CommonStepDefs.scrollPage(x, y);
        String linkTerms = driver.findElement(By.xpath("//div[@class='abc-betting__tile abc-betting__tile_image']/a"));
        flag &= CommonStepDefs.goLink(driver.findElement(By.xpath("//div[@class='abc-betting__tile abc-betting__tile_image']/a")), linkTerms);
        LOG.info("Ссылка на Термины работает");
    }
}
