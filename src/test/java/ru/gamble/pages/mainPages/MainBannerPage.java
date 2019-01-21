package ru.gamble.pages.mainPages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
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

import java.util.List;


@PageEntry(title = "Главный Баннер")
public class MainBannerPage extends AbstractPage{

    private static final Logger LOG = LoggerFactory.getLogger(ru.gamble.pages.mainPages.LivePage.class);

    @ElementTitle("Точки на галвном баннере")
    @FindBy(xpath = "//div[@class='main-slider__wrapper ng-scope']//ol[@class='flickity-page-dots']")
    private WebElement dotsOnBanner;

    @FindBy(xpath = "//div[contains(@class,'main-slider__wrapper')]/descendant::div[@class='flickity-slider']")
    private WebElement top_banner; //Топ-баннер на главной

    public MainBannerPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
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
    Assert.assertNotEquals("Смена баннеров не произошла", position, oldPosition);
    LOG.info("Баннер на слайдере сменился");
}

@ActionTitle("ищет баннер с коэффициентами")
public void findBannerWithCoeffs(){
    WebDriver driver = PageFactory.getDriver();
    WebElement activeBanner;
    WebElement slider = driver.findElement(By.xpath("//div[contains(@class,'main-slider__wrapper')]"));
    List<WebElement> listBanners = slider.findElements(By.xpath(".//div[contains(@class,'slider__slide')]"));
    LOG.info("Всего баннеров на главном слайдере " + listBanners.size());
    Actions act = new Actions(driver);
    for (int i=0; i<listBanners.size();i++) {
        act.moveToElement(slider).build().perform();
        activeBanner = slider.findElement(By.xpath(".//div[contains(@class,'slider__slide') and contains(@class,'is-selected')]"));
        LOG.info("Навели мышку на слайдер, чтобы прекратить листание баннеров");
        if (!activeBanner.findElement(By.xpath(".//div[contains(@class,'flickity')]")).getAttribute("class").contains("simple")){
            LOG.info("Нашли баннер с коэффициентам. i = " + i + " : " + activeBanner.findElement(By.xpath(".//div[contains(@class,'flickity')]")).getAttribute("innerText"));
            break;
        }
        driver.findElement(By.id("right_slider_index_main")).click();
    }
}
}
