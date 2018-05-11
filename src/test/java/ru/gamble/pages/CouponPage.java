package ru.gamble.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Купон")
public class CouponPage extends AbstractPage {

    @FindBy(xpath = "//div[@class='list-bet-block-top__title']")
    private WebElement coupon;

    @ElementTitle("Очистить всё")
    @FindBy(xpath = "//span[@class='coupon-clear-all__text ng-binding']")
    private WebElement clearCoupon;

/*    @ActionTitle("очищает купон")
    public void clearCoupon(){
        WebElement clearAllBottom = PageFactory.getWebDriver().findElement(By.xpath("//span[@class='coupon-clear-all__text ng-binding']"));
        if (clearAllBottom.isDisplayed()){ //очистка купона
            PageFactory.getWebDriver().findElement(By.xpath("//span[@class='coupon-clear-all__text ng-binding']")).click();
        }
    }*/

    @ActionTitle("убирает события из купона, пока их не станет")
    public void removeEventsFromCoupon(String param){
        int count = Integer.parseInt(param);
        while (PageFactory.getWebDriver().findElements(By.xpath("//ul[@class='coupon-bet-list ng-scope']")).size()>count){
            PageFactory.getWebDriver().findElement(By.xpath("//span[@ng-click='removeBet(bet)']")).click();
        }
    }

    @ActionTitle("проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе")
    public void checkBonusFalse(){
        checkBonus(false);
    }

    @ActionTitle("проверяет корректность ссылки О бонусах к экспрессу и текста о бонусе")
    public void checkBonusTrue(){
        checkBonus(true);
    }

    public void checkBonus(boolean except){
        if (!except){
            WebElement couponBonusInfo = PageFactory.getWebDriver().findElement(By.xpath("//span[@ng-bind='getExpressBonusPercent()']"));
            assertThat(false, equalTo(couponBonusInfo.isDisplayed()));
            WebElement couponBonusInfoLink = PageFactory.getWebDriver().findElement(By.xpath("//div[@class='coupon-bonus-info coupon-bonus-info-link']"));
            assertThat(false, equalTo(couponBonusInfoLink.isDisplayed()));
        } else {
            assertThat("о бонусах к экспрессу", equalTo(PageFactory.getWebDriver().findElement(By.xpath("//a[@href='/rules/express-bonus']")).getText())); // проверка корректности ссылки
            assertThat(PageFactory.getWebDriver().findElements(By.xpath("//ul[contains(@class, 'coupon-bet-list')]")).size() + 1 + "% к выигрышу за еще одно событие\nс коэффициентом от 1.25", equalTo(PageFactory.getWebDriver().findElement(By.xpath("//div[@class='coupon-bonus-info']")).getText())); // проверка корректности текста
        }
    }

    public CouponPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(coupon));
    }
}
