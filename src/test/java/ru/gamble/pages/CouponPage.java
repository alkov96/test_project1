package ru.gamble.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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
    public void removeEventsFromCoupon(String param){
        int count = Integer.parseInt(param);
        while (PageFactory.getWebDriver().findElements(By.xpath("//ul[@class='coupon-bet-list ng-scope']")).size()>count){
            PageFactory.getWebDriver().findElement(By.xpath("//span[@ng-click='removeBet(bet)']")).click();
        }
    }

    @ActionTitle("проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе")
    public void checkBonusFalse(){
        checkExpressBonus(false);
    }

    @ActionTitle("проверяет корректность ссылки О бонусах к экспрессу и текста о бонусе")
    public void checkBonusTrue(){
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

    public boolean checkBonus(){
        try {
            bonus.isDisplayed();
            return true;
        } catch (NoSuchElementException e){
            return false;
        }
    }

    public void checkExpressBonus(boolean except){
        if (!except){
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
}
