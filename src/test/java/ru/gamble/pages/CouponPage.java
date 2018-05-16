package ru.gamble.pages;

import org.apache.commons.logging.Log;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.livePages.DayEventsPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.*;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Купон")
public class CouponPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(CouponPage.class);

    @FindBy(xpath = "//div[@class='list-bet-block-top__title']")
    private WebElement coupon;

    @ElementTitle("Очистить всё")
    @FindBy(xpath = "//span[@class='coupon-clear-all__text ng-binding']")
    private WebElement clearCoupon;

    @ElementTitle("экспресс-бонус ссылка")
    @FindBy(xpath = "//div[@class='coupon-bonus-info coupon-bonus-info-link']")
    private WebElement expressBonusLink;

    @ElementTitle("текст экспресс бонуса")
    @FindBy(xpath = "//div[@class='coupon-bonus-info']")
    private WebElement expressBonusText;

    @ElementTitle("бонус к возможному выйгрышу")
    @FindBy(xpath = "//p[@class='betting-result-info__total-bonus']")
    private WebElement bonus;

    @ActionTitle("убирает события из купона, пока их не станет")
    public void removeEventsFromCoupon(String param) {
        int count = Integer.parseInt(param);
        while (PageFactory.getWebDriver().findElements(By.xpath("//ul[@class='coupon-bet-list ng-scope']")).size() > count) {
            PageFactory.getWebDriver().findElement(By.xpath("//span[@ng-click='removeBet(bet)']")).click();
        }
    }

    @ActionTitle("проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе")
    public void checkBonusFalse() {
        checkExpressBonus(false);
    }

    @ActionTitle("проверяет корректность ссылки О бонусах к экспрессу и текста о бонусе")
    public void checkBonusTrue() {
        checkExpressBonus(true);
    }

    @ActionTitle("заполняет сумму для ставки")
    public void fillSumm(String param1, String param2) {
        if (param1.equals("Экспресс")) {
            PageFactory.getWebDriver().findElement(By.xpath("//input[@id='express-bet-input']")).clear();
            PageFactory.getWebDriver().findElement(By.xpath("//input[@id='express-bet-input']")).sendKeys(param2);
        }
    }

    @ActionTitle("проверяет наличие бонуса к возможному выйгрышу")
    public void checkBonusPresent() {
        assertThat(true, equalTo(checkBonus()));
    }

    @ActionTitle("проверяет отсутствие бонуса к возможному выйгрышу")
    public void checkBonusNotPresent() {
        assertThat(false, equalTo(checkBonus()));
    }

    public boolean checkBonus() {
        try {
            bonus.isDisplayed();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void checkExpressBonus(boolean except) {
        if (!except) {
            assertThat(false, equalTo(expressBonusLink.isDisplayed()));
            assertThat(false, equalTo(expressBonusText.isDisplayed()));
        } else {
            assertThat("о бонусах к экспрессу", equalTo(expressBonusLink.findElement(By.xpath("a[@href='/rules/express-bonus']")).getText())); // проверка корректности ссылки
            assertThat(PageFactory.getWebDriver().findElements(By.xpath("//ul[contains(@class, 'coupon-bet-list')]")).size() + 1 + "% к выигрышу за еще одно событие\nс коэффициентом от 1.25", equalTo(expressBonusText.getText())); // проверка корректности текста
        }
    }

    public CouponPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(coupon));
    }

    @ActionTitle("проверяет, добавилось ли событие в купон")
    public void checkListOfCoupon() {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> сouponList = driver.findElements(By.xpath("//div[@class='coupon-bet-list__wrap']/ul"));
        if (сouponList.isEmpty()) {
            Assertions.fail("События не добавлиись в купон.");
        } else LOG.info("Событие " + сouponList.size());
    }

    @ActionTitle("проверяет, совпадают ли события в купоне с ожидаемыми")
    public void bannerAndTeams() {
        WebDriver driver = PageFactory.getDriver();
        String сouponGame = driver.findElement(By.xpath("//div[@class='coupon-bet-list__wrap']/ul[1]/li[1]/span[contains(@class,'bet-event-title')]")).getText();//cuponGame - наше добавленные события в купоне.
        String team1 = Stash.getValue("team1key");
        String team2 = Stash.getValue("team2key");
        if (CommonStepDefs.stringParse(team1 + team2).equals(CommonStepDefs.stringParse(сouponGame))) {
            LOG.info("Названия команд в купоне совпадают с ожидаемыми: " + team1 + team2 + "=" +сouponGame);
        } else Assertions.fail("Названия команд в купоне не совпадают с ожидаемыми: " + team1+team2 + сouponGame);
    }

    @ActionTitle("проверяет, совпадает ли исход в купоне с ожидаемым")
    public void checkIshod() {
        WebDriver driver = PageFactory.getDriver();
        String ishod = driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[@class='pick ng-binding']")).getText();
        String ishodName = Stash.getValue("ishodKey");//ожидаемое название исхода
        if (CommonStepDefs.stringParse(ishod).equals(CommonStepDefs.stringParse(ishodName))) {
            LOG.info("Выбранных исход в купоне совпадает с ожидаемым: " + ishod+ "-"+ ishodName);
        } else Assertions.fail("Выбранный исход в купоне не совпадает с ожидаемым: " + ishod +" - "+ ishodName);

    }

    @ActionTitle("сравнивает коэфиценты")
    public void compareCoef() {
        WebDriver driver = PageFactory.getDriver();
        float coef = Stash.getValue("coefKey");
        float coefCupon = Float.valueOf(driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[contains(@class,'coupon-betprice')]")).getText());//Кэфицент в купоне
        String oldString = driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[contains(@class,'coupon-betprice_old')]")).getAttribute("class");//oldString - просто переменная, в которую сохраним класс, где лежит старый коэф.
        float coefOld;
        coefOld = oldString.contains("ng-hide") ? 0.0f : Float.valueOf(driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[contains(@class,'coupon-betprice_old')]")).getText());//Краткая запись цикла. ? - часть синтаксиса. Здесь показываем чему равен старый коэфицент. если скрыт, то 0.0.
        if (coef != coefCupon && coef != coefOld) {
            Assertions.fail("Коэфицент в купоне не совпадает с коэфицентом в событии: " + coefCupon + coef);
        } else LOG.info("Коэфицент в купоне совпадает с коэфицентом в событии: " + coefCupon +" - " + coef);

    }
}


