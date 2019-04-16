package ru.gamble.pages.mainPages;

import cucumber.api.DataTable;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@PageEntry(title = "Главный Баннер")
public class MainBannerPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(ru.gamble.pages.mainPages.LivePage.class);
    static WebDriver driver = PageFactory.getDriver();

    @ElementTitle("Точки на галвном баннере")
    @FindBy(xpath = "//div[@class='main-slider__wrapper ng-scope']//ol[@class='flickity-page-dots']")
    private WebElement dotsOnBanner;

    @FindBy(xpath = "//div[contains(@class,'main-slider__wrapper')]/descendant::div[@class='flickity-slider']")
    private WebElement top_banner; //Топ-баннер на главной

    public MainBannerPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(dotsOnBanner,5, 10);
    }

@ActionTitle("запоминает состояние главного баннера")
public void rememberPositionSlider(){
    LOG.info("Запрашиваем положение отображаемого баннера");
    String position = top_banner.getAttribute("style");
    LOG.info("позиция сейчас " + position);
    Stash.put("positionBanner",position);
}


@ActionTitle("сравнивает текущее состояние баннера со старым")
public void ckeckPositionSlider() {
    LOG.info("Запрашиваем положение отображаемого баннера");
    String position = top_banner.getAttribute("style");
    LOG.info("позиция новая " + position);
    String oldPosition = Stash.getValue("positionBanner");
    Assert.assertNotEquals("Смена баннеров не произошла", position, oldPosition);
    LOG.info("Баннер на слайдере сменился");
}

@ActionTitle("ищет баннер с коэффициентами")
public void findBannerWithCoeffs(DataTable dataTable){
    String sep = "%";
    Map<String, String> data = dataTable.asMap(String.class, String.class);
    WebElement activeBanner;
    WebElement slider = driver.findElement(By.xpath("//div[contains(@class,'main-slider__wrapper')]"));
    List<WebElement> listBanners = slider.findElements(By.xpath(".//div[contains(@class,'slider__slide')]"));
    LOG.info("Всего баннеров на главном слайдере " + listBanners.size());
    Actions act = new Actions(driver);
    for (int i=0; i<=listBanners.size();i++) {
        Assert.assertFalse(
                "Баннера с коэффициентами не найдено",
                i==listBanners.size());
        act.moveToElement(slider).build().perform();
        activeBanner = slider.findElement(By.xpath(".//div[contains(@class,'slider__slide') and contains(@class,'is-selected')]"));
        LOG.info("Навели мышку на слайдер, чтобы прекратить листание баннеров");
        if (!activeBanner.findElement(By.xpath(".//div[contains(@class,'flickity')]")).getAttribute("class").contains("simple")){
            LOG.info("Нашли баннер с коэффициентам. i = " + i + " : " + activeBanner.findElement(By.xpath(".//div[contains(@class,'flickity')]")).getAttribute("innerText"));
            break;
        }
        driver.findElement(By.id("right_slider_index_main")).click();
    }
    LOG.info("Запоминаем название и значения коэффициентов");
    String dateAndTime =  slider.findElement(By.xpath(".//div[contains(@class,'slider__slide') and contains(@class,'is-selected')]//div[contains(@class,'banner__datetime')]"))
            .getAttribute("innerText");
    StringBuilder name = new StringBuilder();
    driver.findElements(By.xpath(".//div[contains(@class,'slider__slide') and contains(@class,'is-selected')]//div[contains(@class,'banner__team-name')]"))
            .forEach(element -> name.append(element.getAttribute("innerText") + sep));
    List<String> valueCoefs = new ArrayList<>();
    driver.findElements(By.xpath(".//div[contains(@class,'slider__slide') and contains(@class,'is-selected')]//div[contains(@class,'banner__odd-val')]"))
            .forEach(element -> valueCoefs.add(element.getAttribute("innerText")+ sep));
    Calendar today = Calendar.getInstance();
    Calendar dateTimeGame = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");
    SimpleDateFormat format2 = new SimpleDateFormat("dd MMM, EEEE hh:mm");
    LOG.info("Парсим дату и время с баннера");
    try {
        dateTimeGame.setTime(format.parse(dateAndTime.replace("\n"," ")));
    } catch (ParseException e) {
        try {
            dateTimeGame.setTime(format2.parse(dateAndTime.replace("\n"," ")));
            ((GregorianCalendar) dateTimeGame).set(Calendar.YEAR,today.get(Calendar.YEAR));
        } catch (ParseException e1) {
            LOG.info("Не удалось распарсить дату и время игры, указанные на баннере. Возможно не совпадает формат: " + dateAndTime + ". Формат: " + format);
            e.printStackTrace();
        }

    }
    String typeGame = dateTimeGame.before(today)?"live":"prematch";//если игра лайв - то тип 0, если игра премтач, то тип = 1
    LOG.info("name " + name + "\ncoefs " + valueCoefs);

    String dateTimeGameKey = data.get("Дата/время игры");
    String coefsKey = data.get("Коэффициенты исхода");
    String coef1Key = data.get("Коэффициент победителя");
    String typeGameKey = data.get("Тип игры");

    Stash.put(dateTimeGameKey,dateTimeGame);
    Stash.put(coefsKey,valueCoefs);
    Stash.put(coef1Key,valueCoefs.get(0).replace(sep,""));
    Stash.put(typeGameKey,typeGame);


    LOG.info("Нажимаем на коэффициент, переходим на игру");
    driver.findElements(By.xpath(".//div[contains(@class,'slider__slide') and contains(@class,'is-selected')]//div[contains(@class,'banner__odd-val')]")).get(0).click();
    new WebDriverWait(driver,10)
            .withMessage("На станице все еще виден главный баннер, значит переход на игру не удался")
            .until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[contains(@class,'main-slider__wrapper')]"),0));
}
}
