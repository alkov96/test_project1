package ru.gamble.pages.mainPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.pages.CouponPage;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;


@PageEntry(title = "Авторизованная Главная страница")
public class AuthenticationMainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationMainPage.class);

    @FindBy(id = "topPanelWalletBalance")
    private WebElement pageTitle;

    @ElementTitle("Мои пари")
    @FindBy(id = "user-bets")
    private WebElement myBetsButton;

    @ElementTitle("Внести депозит")
    @FindBy(id = "btn-header-deposit")
    private WebElement makeDepositButton;


    public AuthenticationMainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(pageTitle,10, 10);
    }

    @ActionTitle("запоминает значение баланса")
    public static void rememberBalnce(String param){
        By top_balance = param.equals("бонусов") ? By.xpath("//span[contains(@class,'subMenuBonus bonusmoney-text')]") : By.id("topPanelWalletBalance");//запоминать нужно бонусы или рубли
        String key = param.equals("бонусов") ? "balanceBonusKey" : "balanceKey";
        List<WebElement> balanceElement = getWebDriver().findElements(top_balance);

        String balance = (balanceElement.isEmpty()) ? "0" : balanceElement.get(0).getAttribute("innerText");
        Stash.put(key, balance);
        LOG.info("Сохранили в память key [" + key + "] <== value [" + balance + "]");
    }

    @ActionTitle("проверяет, увеличился ли баланс на")
    public void checkIsBalance(String keyAmount){
        BigDecimal sumBet;
        waitForElementPresent(By.id("topPanelWalletBalance"), 10);
        sumBet = new BigDecimal((String) Stash.getValue(keyAmount)).setScale(2,RoundingMode.HALF_UP).negate();
        Stash.put("sumKey",sumBet.toString());
        LOG.info("Сохранили в память key [sumKey] <== value [" + sumBet.toString() + "]");
        CouponPage.balanceIsOK("рубли");
    }

    @ActionTitle("если выскочил PopUp 'Перейти в ЦУПИС', закрываем")
    public void closePopUpGoTSUPISIfDisplayed(){
        String xpathGoTSUPIS = "//a[contains(@href,'https://1cupis.ru/auth')]";
        String xpathCross = "//a[contains(@class,'closeBtn')]";
        try{
            new WebDriverWait(PageFactory.getWebDriver(),3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathGoTSUPIS)));
            LOG.info("Нашли кнопку ["
                    + PageFactory.getWebDriver().findElements(By.xpath(xpathGoTSUPIS))
                    .stream().filter(WebElement::isDisplayed).findFirst().get()
                    .getAttribute("innerText").replaceAll("\n", "").trim()
                    + "] и нажимаем на крест");
            PageFactory.getWebDriver().findElements(By.xpath(xpathCross)).stream().filter(WebElement::isDisplayed).findFirst().get().click();
        }catch (Exception e){
            LOG.info("Модальное окно 'Перейти в ЦУПИС' не появилось");
        }
    }

    @ActionTitle("проверяет снятие правильной суммы, и бонусов, если они были начислены")
    public void balanceAfterWithdraw(String keySum){
        LOG.info("Проверка что правильно изменился баланс рублей");
        BigDecimal sum;
        sum = new BigDecimal((String) Stash.getValue(keySum)).setScale(2,RoundingMode.HALF_UP);
        Stash.put("sumKey",sum.toString());
        LOG.info("Сохранили в память key [sumKey] <== value [" + sum.toString() + "]");
        CouponPage.balanceIsOK("рубли");
        sum = new BigDecimal((String) Stash.getValue("bonus")).setScale(2,RoundingMode.HALF_UP).negate();
        if(sum.compareTo(new BigDecimal(0)) > 0){
            LOG.info("Проверка что правильно изменился баланс бонусов");
            Stash.put("sumKey",sum.toString());
            LOG.info("Сохранили в память key [sumKey] <== value [" + sum.toString() + "]");
            CouponPage.balanceIsOK("бонусов");
        }
    }

//    @ActionTitle("проверяет снятие суммы")
//    public void checkbalanceAfterWithdraw(String keySum){
//        BigDecimal sum;
//        sum = new BigDecimal((String) Stash.getValue(keySum)).setScale(2,RoundingMode.HALF_UP);
//        LOG.info("Ранее была снята сумма [" + sum.toString() + "]");
//        Stash.put("sumKey",sum.toString());
//        LOG.info("Сохранили в память key [sumKey] <== value [" + sum.toString() + "]");
//        CouponPage.balanceIsOK("рубли");
//        sum = new BigDecimal((String) Stash.getValue("bonus")).setScale(2,RoundingMode.HALF_UP).negate();
//        if(sum.compareTo(new BigDecimal(0)) == 1){
//            LOG.info("Проверка что правильно изменился баланс бонусов");
//            Stash.put("sumKey",sum.toString());
//            LOG.info("Сохранили в память key [sumKey] <== value [" + sum.toString() + "]");
//            CouponPage.balanceIsOK("бонусов");
//        }
//    }
}
