package ru.gamble.pages.mainPages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;


@PageEntry(title = "Главный Баннер")
public class MainBannerPage extends AbstractPage{

    private static final Logger LOG = LoggerFactory.getLogger(ru.gamble.pages.mainPages.LivePage.class);

    @ElementTitle("Точки на галвном баннере")
    @FindBy(xpath = "//div[@class='main-slider__wrapper ng-scope']//ol[@class='flickity-page-dots']")
    private WebElement dotsOnBanner;

    @FindBy(css = "div.flickity-slider")
    private WebElement top_banner; //Топ-баннер на главной

    public MainBannerPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(dotsOnBanner,5, 10);
    }

@ActionTitle("запоминает состояние главного баннера")
public void rememberPositionSlider(){
    WebDriver driver = PageFactory.getDriver();
    LOG.info("Запрашиваем положение отображаемого баннера");
    String position = top_banner.getAttribute("style");
    LOG.info("позиция сейчас " + position);
    Stash.put("positionBanner",position);
}


@ActionTitle("сравнивает текущее состояние баннера со старым")
public void ckeckPositionSlider() {
    WebDriver driver = PageFactory.getDriver();
    LOG.info("Запрашиваем положение отображаемого баннера");
    String position = top_banner.getAttribute("style");
    LOG.info("позиция новая " + position);
    String oldPosition = Stash.getValue("positionBanner");
    Assert.assertFalse("Смена баннеров не произошла",position.equals(oldPosition));
    LOG.info("Баннер на слайдере сменился");
}
}
