package ru.gamble.pages;


import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.livePages.DayEventsPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.gamble.utility.BetFull;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import javax.annotation.Nullable;
import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.xpath;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Купон")
public class CouponPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(CouponPage.class);

    @FindBy(xpath = "//div[contains(@class,'coupon__types')]")
    private WebElement coupon;

    @ElementTitle("Активация Быстрой ставки")
    @FindBy(xpath = "//div[@class='coupon__toggler']/label")
    protected WebElement quickButton;
    //для ставок экспресс, быстрой ставки - т.е. там где 1 поле для ставки


    @ElementTitle("Флаг активности быстрой ставки")
    @FindBy(xpath = "//div[@class='coupon__toggler']/input")
    protected WebElement quickBetFlag;

    @ElementTitle("Очистить всё")
    @FindBy(xpath = "//span[@class='btn btn_full-width']")
    protected WebElement clearCoupon;

    @ElementTitle("Тип купона")
    @FindBy(xpath = "//div[contains(@class,'coupon__types')]//li[contains(@class,'selected')]")
    private WebElement couponType;

    static By expressBonusText = xpath("//div[contains(@class,'coupon-bet_offer')]//span[contains(@class,'coupon-bet__text')]");
    static By expressBonusLink = xpath("//div[contains(@class,'coupon-bet_offer')]//a[contains(@class,'coupon-bet__link')]");

    static By currentExpressBonus = xpath("//div[@class='coupon__bottom-block']//span[contains(@class,'coupon__sum orange')]");

    @ElementTitle("параметры в купоне")
    @FindBy(xpath = "//i[@class='icon icon-settings-old coupon-tabs__item-icon']")
    private WebElement button_of_param_in_coupon;

    @ElementTitle("поле суммы ставки Ординар")
    @FindBy(xpath = "//input[contains(@class,'input coupon__input') and not(@id='bet-input')]")
    private WebElement couponInputOrdinar;

    @ElementTitle("поле суммы ставки типа Система")
    @FindBy(xpath = "//input[contains(@class,'input coupon__input') and @id='bet-input']")
    private WebElement couponInputSystem;

    @ElementTitle("кнопка Заключить пари для Экспресса и Системы")
    //@FindBy(id="place-bet-button")
    @FindBy(xpath = "//button[contains(@class,'btn_coupon') and normalize-space(text())='Заключить пари']")
    private WebElement buttonBet;

    @ElementTitle("переключатель ставки на бонусы")
    @FindBy(xpath = "//div[@class='coupon__button-group']//label[contains(@class,'coupon-btn_b')]")
    private WebElement bonusBet;


    static By xpathFreeBet = xpath("//div[@class='coupon__button-group']//label[contains(@class,'coupon-btn_f')]");


    @FindBy(className = "coupon__banners") //баннеры в купоне
    private WebElement bannersInCoupon;

    @ElementTitle("текущий тип системы")
    @FindBy(xpath = "//div[contains(@class,'coupon__system-select')]//div[contains(@class,'custom-select__placeholder')]/span")
    private WebElement current_type_of_system;

    public CouponPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(coupon,10, 5);
    }

    @ActionTitle("убирает события из купона, пока их не станет")
    public void removeEventsFromCoupon(String param) {
        int count = Integer.parseInt(param);
        while (PageFactory.getWebDriver().findElements(xpathListBets).size() > count) {
            PageFactory.getWebDriver().findElement(xpath("//span[@class='coupon-bet__close-btn']")).click();
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
            PageFactory.getWebDriver().findElement(xpath("//input[@id='express-bet-input']")).clear();
            PageFactory.getWebDriver().findElement(xpath("//input[@id='express-bet-input']")).sendKeys(param2);
        }
    }

    @ActionTitle("проверяет наличие бонуса к возможному выигрышу")
    public void checkBonusPresent() {
        checkBonus(true);
    }

    @ActionTitle("проверяет отсутствие бонуса к возможному выйгрышу")
    public void checkBonusNotPresent() {
        checkBonus(false);
    }


    public void checkBonus(boolean expect) {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> listBets = driver.findElements(xpathListBets);
        //driver.findElements(xpath("//div[contains(@class,'coupon-bet') and not(contains(@class,'coupon-bet_offer'))]/ul"));
        if (!expect) {
            assertTrue(
                    "Есть эспресс-бонус!!! " + driver.findElements(currentExpressBonus).size(),
                    driver.findElements(currentExpressBonus).isEmpty());
        } else {
            assertTrue(
                    "Неправильный размер экспресс-бонуса (или его вообще нет)   ||| " + driver.findElement(currentExpressBonus).getAttribute("innerText") + " |||",
                    driver.findElement(currentExpressBonus).getAttribute("innerText").contains(listBets.size() + "%")); // проверка корректности текста
        }

    }

    public void checkExpressBonus(boolean expect) {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> listBets = driver.findElements(xpathListBets);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!expect) {
            assertTrue(
                    "Есть экспресс-бонус!!! " + driver.findElements(expressBonusText).size(),
                    driver.findElements(expressBonusText).isEmpty());
            assertTrue(
                    "Есть ссылка на описание экспресс-бонуса в правилах",
                    driver.findElements(expressBonusLink).isEmpty());
        } else {
            assertFalse(
                    "Нет текста про экспресс-бонус!!! ",
                    driver.findElements(expressBonusText).isEmpty());
            assertTrue(
                    "Неправильная текст в описании экспресс-бонуса. Или его вообще нет   ||| " + driver.findElement(expressBonusText).getAttribute("innerText") + " |||",
                    driver.findElement(expressBonusText).getAttribute("innerText").contains("+" + (listBets.size() + 1) + "% к выигрышу")); // проверка корректности текста
            assertTrue(
                    "Неправильная ссылка на описание экспресс-бонуса. Или ссылки вообще нет  ||| " + driver.findElement(expressBonusLink).getAttribute("href") + " |||",
                    driver.findElement(expressBonusLink).getAttribute("href").contains("/rules/express-bonus")); // проверка корректности ссылки

        }

    }


    @ActionTitle("проверяет, добавилось ли событие в купон")
    public void checkListOfCoupon() {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> couponList = driver.findElements(xpath("//li[@class='coupon-bet__row']/span[@class='coupon-bet__title']"));
        if (couponList.isEmpty()) {
            Assertions.fail("События не добавлиись в купон.");
        } else LOG.info("Событие " + couponList.size());
    }

    @ActionTitle("проверяет, совпадают ли события в купоне с ожидаемыми из")
    public void bannerAndTeams(String team1key, String team2key) {
        WebDriver driver = PageFactory.getDriver();
        String couponGame = driver.findElement(xpath("//li[@class='coupon-bet__row']/span[@class='coupon-bet__title']")).getAttribute("innerText");//cuponGame - наше добавленные события в купоне.
        String team1 = Stash.getValue(team1key);
        String team2 = Stash.getValue(team2key);
        if (CommonStepDefs.stringParse(team1 + team2).equals(CommonStepDefs.stringParse(couponGame))) {
            LOG.info("Названия команд в купоне совпадают с ожидаемыми: [" + team1 + "] - [" + team2 + "] <=> [" + couponGame + "]");
        } else
            Assertions.fail("Названия команд в купоне не совпадают с ожидаемыми: [" + team1 + "] - [" + team2 + "] <=> [" + couponGame + "]");
    }


    @ActionTitle("проверяет, совпадает ли исход в купоне с ожидаемым")
    public void checkIshod(String ishodKey) {
        WebDriver driver = PageFactory.getDriver();
        String ishod = driver.findElement(xpath("//ul[@class='coupon-bet__content']/li[2]/div")).getAttribute("innerText").split("\n")[1];
        String ishodName = Stash.getValue(ishodKey);//ожидаемое название исхода
        switch (ishod.trim()) {
            case "П1":
                ishod = Stash.getValue("team1key").toString();
                break;

            case "П2":
                ishod = Stash.getValue("team2key").toString();
                break;
        }

        if (CommonStepDefs.stringParse(ishod).equals(CommonStepDefs.stringParse(ishodName))) {
            LOG.info("Выбранных исход в купоне совпадает с ожидаемым: " + ishod + " <=> " + ishodName);
        } else Assertions.fail("Выбранный исход в купоне не совпадает с ожидаемым: " + ishod + " - " + ishodName);

    }

    @ActionTitle("сравнивает коэфиценты")
    public void compareCoef(String keyOutcome) {
        WebDriver driver = PageFactory.getDriver();
        String coefString = Stash.getValue(keyOutcome).toString();
        float coef = Float.valueOf(coefString);
        float coefCoupon = Float.valueOf(driver.findElement(xpath("//div[@class='coupon-bet__coeffs']/span[2]")).getAttribute("innerText"));//Кэфицент в купоне
        WebElement oldWebElement = driver.findElement(xpath("//span[contains(@class,'coupon-bet__coeff-strikeout')]"));
        float oldCoef = oldWebElement.isDisplayed() ? Float.valueOf(oldWebElement.getAttribute("innerText").trim()) : coefCoupon;
        if (coef != coefCoupon && coef != oldCoef) {
            Assertions.fail("Коэфицент в купоне не совпадает с коэфицентом в событии: " + coefCoupon + coef);
        } else LOG.info("Коэфицент в купоне совпадает с коэфицентом в событии: " + coefCoupon + " <=> " + coef);

    }

    @ActionTitle("устанавливает условие для принятия коэфицентов как 'Никогда'")
    public void neverAccept() {
        WebDriver driver = PageFactory.getDriver();
        button_of_param_in_coupon.click();
        driver.findElement(xpath("//label[@for='betset_none']")).click();
        LOG.info("Установили условие 'Никогда'");
        driver.findElement(xpath("//ul[@class='coupon-tabs coupon-tabs_black']/li[1]")).click();//возращаемся обратно в купон
        //button_of_param_in_coupon.click();
    }


        ExpectedCondition<Boolean> pageLoadCondition = new
                ExpectedCondition<Boolean>() {
                    @Nullable
                    @Override
                    public Boolean apply(@Nullable WebDriver webDriver) {
                        return null;
                    }

                    public Boolean apply(WebDriver driver, By by) {
                        return driver.findElement(by).isDisplayed();
                    }
                };




    @ActionTitle("проверяет, что после изменения условий на 'Никогда' в купоне появляется кнопка 'Принять' и информационное сообщение")
    public void buttonAndMessageIsDisplayed() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        By by = xpath("//div[@class='coupon-bet__coeffs']/span[contains(@class,'coupon-bet__coeff-strikeout') and not (contains (@class, 'ng-hide'))]");
        WebDriverWait wait = new WebDriverWait(PageFactory.getWebDriver(),70);
        wait.withMessage("Не удалось найти события, где меняется коэфицент");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(by, 0));
        List<WebElement> oldCoef = driver.findElements(by).stream().filter(element -> element.isDisplayed()).collect(Collectors.toList());
        if (oldCoef.size() == 0){
            Assertions.fail("Коэфицент не поменялся!");
        }
        Thread.sleep(500);
        WebElement error_message = driver.findElement(xpath("//span[@class='coupon__message-fragment']"));
        if (oldCoef.size() > 0 && !error_message.isDisplayed()) {
            Assertions.fail("Коэф изменился, однако сообщение не отображается.");
        }
        //LOG.info("Изменился коэф и появилось сообщение о принятии коэфиценита");
        Thread.sleep(5000);
        WebElement btn_accept = driver.findElement(xpath("//span[@class='btn btn_full-width' and @ng-click = 'acceptChanges()']"));
        if (!error_message.isDisplayed()
                || !btn_accept.isDisplayed()) {
            Assertions.fail("При изменении условий ставки не появилось сообщение или кнопка о принятии изменений.");
        }
        LOG.info("Появилось сообщение о принятии коэфицента и кнопка");

        LOG.info("Проверка на принятие условия 'Никогда' в купоне прошла успешно.");

    }

    /**
     * функция проверяет, как изменился коэф для конкретной ставки
     *
     * @param param порядковый номер ставки в купоне
     * @return возращает 0, если коэф не изменился, >0, если увеличился, и <0, если уменьшился
     */
    public float compareCoef(int param) {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> allBets = driver.findElements(xpath("//ul[@class='coupon-bet__content']"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        for (int i = 3; i > 0; i--) {
            List<WebElement> oldCoef = driver.findElements(xpath("//div[@class='coupon-bet__coeffs']/span[contains(@class,'coupon-bet__coeff-strikeout') and not (contains (@class, 'ng-hide'))]"))
                    .stream().filter(element -> element.isDisplayed()).collect(Collectors.toList());
            if (!oldCoef.isEmpty()) break;
        }
        float coefCoupon = Float.valueOf(allBets.get(param).findElement(xpath(".//div[@class='coupon-bet__coeffs']/span[2]")).getAttribute("innerText"));
        String oldString = allBets.get(param).findElement(xpath(".//div[@class='coupon-bet__coeffs']/span[1]")).getAttribute("class");
        float coefOld;
        coefOld = oldString.contains("ng-hide") ? coefCoupon : Float.valueOf(allBets.get(param).findElement(xpath(".//div[@class='coupon-bet__coeffs']/span[1]")).getAttribute("innerText"));
        LOG.info("Старый коэф: " + coefOld);
        LOG.info("Текущий коэф: " + coefCoupon);
        return coefCoupon - coefOld;
    }

    @ActionTitle("проверяет изменения коэфицентов в купоне при условии 'Повышенные коэфиценты', удаляет из купона все события, кроме событий, у которых повысился коэфицент")
    public void compareChangeCoef () throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        button_of_param_in_coupon.click();
        driver.findElement(xpath("//ul[@class='coupon-settings__group']/li[3]")).click();
        driver.findElement(By.xpath("//span[text()='Купон']")).click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<WebElement> allBets = driver.findElements(xpath("//ul[@class='coupon-bet__content']"));
        for (int i = allBets.size()-1; i >=0; i--) {
            float s = compareCoef(i);
            LOG.info("для " + i + " события результат = " + s);
               if (s>=0.00){
                   driver.findElements(xpath("//ul[contains(@class,'coupon-bet__content')]//i[contains(@class,'icon-cross-close')]")).get(i).click();//путь до крестика, чтобы удалить событие из купона
                    allBets = driver.findElements(xpath("//ul[@class='coupon-bet__content']"));
               }
        }

        if (allBets.size() == 0){
            LOG.info("Подходящей ставки не нашлось, поэтому заново добавляем события в купон, сравниваем коэфиценты и удаляем ненужные события");
            DayEventsPage.addEventsToCouponF();
            compareChangeCoef();
            //  compareCoef(i);
        }
        LOG.info("Количество событий в купоне: " + driver.findElements(xpath("//ul[@class='coupon-bet__content']")).size());
        if (driver.findElement(xpath("//span[contains(@class,'coupon__message coupon__message_suggestions')]")).isDisplayed()
                || driver.findElement(xpath("//span[@class='btn btn_full-width' and @ng-click = 'acceptChanges()']")).isDisplayed()) {
            LOG.info("Отображается кнопка 'Принять', в купоне события с повышенным коэфицентом");
        } else {
            Assertions.fail("Отображается кнопка и сообщение, хотя не должны - в купоне одни лишь события с повышенным коэфицентом");
        }
    }

    @ActionTitle("выбирает тип ставки")
    public void selectTypeBet(String type) {
        WebDriver driver = PageFactory.getDriver();
        WebElement selectType = driver.findElement(xpath("//span[contains(@class,'coupon-tabs__item-link coupon-text-h') " +
                "and normalize-space(translate(text(),'" + type.toLowerCase() + "','" + type.toUpperCase() + "'))='" + type.toUpperCase() + "']"));
        //этот длинный xpath потому что название типа может быть с большой буквы, или с маленькой, для разного типа по-разному. и в элементе getText() вместе с пробелами идет
        LOG.info("Переключаем тип ставки на '" + selectType.getAttribute("innerText") + "'");
        selectType.click();
    }

    /**
     * Системы это по сути сразу несколько экспрессов, и сумма ставки должна быть не меньше чем их количество.
     * @param less - показывает нужно ли вводить валидную сумму или нет. если less содержит слово меньше, то сумма должна быть меньше чем количество экспрессов - невалид
     */
    @ActionTitle("вводит сумму ставки система")
    public void inputBet(String less) throws InterruptedException {
        BigDecimal sum;
        int countExp = Integer.valueOf(current_type_of_system.getAttribute("innerText").replaceAll("[^0-9?!]", ""));
        String sumBet = less.contains("меньше") ? String.valueOf(countExp - 1) : String.valueOf(countExp);
        LOG.info("Вводим сумму ставки : [" + sumBet + "]");
        fillField(couponInputSystem, sumBet);
        sum = new BigDecimal(sumBet.trim()).setScale(2, RoundingMode.UP);
        Stash.put("sumKey", sum.toString());
        LOG.info("Сохранили в память [sumKey] <== value [" + sum.toString() + "]");
        if (less.contains("меньше")) {
            checkErrorsInCoupon("Минимальная ставка");
            checkErrorsInCoupon("для системы");
        }
    }


    @ActionTitle("вводит сумму ставки экспресс")
    public void inputBetExpress(String sumBet){
        BigDecimal sum;
        LOG.info("Вводим сумму ставки : [" + sumBet + "]");
        fillField(couponInputSystem, sumBet);
        sum = new BigDecimal(sumBet.trim()).setScale(2, RoundingMode.UP);
        Stash.put("sumKey", sum.toString());
        LOG.info("Сохранили в память [sumKey] <== value [" + sum.toString() + "]");
    }

    /**
     * включается быстрая ставка и в поле суммы вводится сумма, указанная в праметре. Если в параметр написано "больше баланса" то вводится (balance+1)
     *
     * @param sum
     */
    @ActionTitle("включает быструю ставку и вводит сумму")
    public void onQuickBet(String sum) {
        if (!quickBetFlag.getAttribute("class").contains("not-empty")) {
            quickButton.click();
            new WebDriverWait(getWebDriver(),10).until(ExpectedConditions.attributeContains(quickBetFlag,"class","not-empty"));
        }
        BigDecimal sumBet;
        BigDecimal one = new BigDecimal(1);
        sumBet = sum.equals("больше баланса") ? new BigDecimal((String) Stash.getValue("balanceKey")).setScale(2, RoundingMode.HALF_UP).add(one) : new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP);
        //coupon_field.clear();
        LOG.info("Вбиваем сумму в поле купона::" + sumBet.toString());
        WebElement quickBetInput = getWebDriver().findElement(xpath("//div[@class='coupon__quickbet-input-group']//input[@type='text']"));
        fillField(quickBetInput, sumBet.toString());
        LOG.info("Ввелось в поле::" + quickBetInput.getAttribute("value"));
        Stash.put("sumKey", sumBet.toString());
        LOG.info("Сохранили в память key [sumKey] <== value [" + sumBet.toString() + "]");
    }


    @ActionTitle("проверяет наличие сообщения об ошибке в купоне")
    public void checkErrorsInCoupon(String expectedError) throws InterruptedException {
        WebDriver driver = PageFactory.getWebDriver();
        Thread.sleep(10000);
        List<WebElement> errorMessages = driver.findElements(xpath("//div[contains(@class,'coupon__message_error')]//span"));
        for (WebElement error : errorMessages) {
            if (error.getAttribute("innerText").contains(expectedError)) return;
        }
        Assert.fail("В купоне нет ожидаемого сообщения об ошибке [" + expectedError + "]");
    }

    @ActionTitle("проверяет наличие сообщения об ошибке в купоне для быстрой ставки")
    public void checkErrorsInCouponForFastBet() {
        WebDriver driver = PageFactory.getWebDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        if (!driver.findElement(xpath("//div[@class='coupon__message coupon__message_suggestions']")).isDisplayed()) {
            Assertions.fail("в купоне не отображается сообщение о том, что нужно поплнить баланс");
        }
    }

    @ActionTitle("заключает пари")
    public void doBet() throws AuthenticationException, InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        String xpathBet = "//input[contains(@class,'input coupon__input') and not(@id='bet-input')]";

        int sizeCoupon = driver.findElements(xpath(xpathBet)).size();
        int i = 0;
        LOG.info("Ищем и нажимаем на шестерёнку в Купоне [" + i + "]");
        WebElement gear = driver.findElement(xpath("//span[contains(@class,'coupon-tabs__item-link')]/i"));
        gear.click();

        LOG.info("Ищем и выбираем 'Любые коэффициенты' [" + i + "]");
        driver.findElement(xpath("//span[text()='Любые изменения']")).click();
        LOG.info("Возвращаемся к списку событий в купоне");
        driver.findElement(xpath("//span[text()='Купон']")).click();

        String typeCoupon = couponType.getAttribute("innerText");
        int expectedCouponSize = typeCoupon.contains("Ординар") ? (sizeCoupon - 1) : 0;

        LOG.info("Жмём 'Заключить пари'");
        buttonBet.click();
        waitingForPreloaderToDisappear(30);

        LOG.info("Ожидаем исчезновения из купона принятой ставки");
        Thread.sleep(10000);
        if (driver.findElements(xpath("//ul[@class='coupon-bet__content']")).size() > expectedCouponSize) {
            Assertions.fail("Ошибка! Ставка не принялась((");
        } else LOG.info("Ставка принялась!");

    }

    /**
     * проверка что баланс изменился успешно. По параметру отепределяется проверка рублей или бонусов
     *
     * @param param если параметр = "бонусов", то проверка бонусов, иначе - првоерка рублевого баланса
     */
    @ActionTitle("проверяет изменение баланса")
    public static void balanceIsOK(String param) {
        WebDriver driver = PageFactory.getDriver();
        BigDecimal currentBalance, previousBalance, sumBet;

        By balance = param.equals("бонусов") ? xpath("//span[contains(@class,'subMenuBonus bonusmoney')]") : By.id("topPanelWalletBalance");
        String key = param.equals("бонусов") ? "balanceBonusKey" : "balanceKey";
        int count = 30;
        previousBalance = new BigDecimal(Stash.getValue(key).toString().replace("Б", "").trim()).setScale(2, RoundingMode.HALF_UP);

        sumBet = new BigDecimal((String) Stash.getValue("sumKey")).setScale(2, RoundingMode.HALF_UP);
        String currentNumber;
        while (count > 0) {
            currentNumber = driver.findElements(balance).stream().filter(WebElement::isDisplayed).findFirst().get().getAttribute("innerText")
                    .replace("Б", "").trim();

            currentBalance = new BigDecimal(currentNumber).setScale(2, RoundingMode.HALF_UP);

            if ((previousBalance.subtract(sumBet).subtract(currentBalance).abs()).compareTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_UP)) < 0) {
                LOG.info("Баланс соответствует ожидаемому: " + currentBalance.toString());
                break;
            }
            LOG.info("тик-так");
            count--;
            if (count % 10 == 0) {
                driver.navigate().refresh();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count == 0) {
                Assertions.fail("Баланс не соответствует ожидаемому. Баланс сейчас: " + currentBalance + ", ожидалось : " + previousBalance.subtract(sumBet));
            }
        }
    }


    @ActionTitle("проверяет изменение количества экспрессов при переключении вида системы")
    public void checkCountExpress() {
        WebDriver driver = PageFactory.getDriver();
        WebElement dropdownOpt = driver.findElement(xpath("//div[contains(@class,'coupon__system-select')]"));
        List<WebElement> systemTypes = dropdownOpt.findElements(xpath("div//li[contains(@clas,'coupon__dropdown-item')]"));
        WebElement openList = dropdownOpt.findElement(xpath("div/div[contains(@class,'custom-select__placeholder')]"));
        int countExp = Integer.valueOf(current_type_of_system.getAttribute("innerText").replaceAll("[^0-9?!]", ""));
        int count;
        for (WebElement type : systemTypes) {
            openList.click();
            type.click();
            count = Integer.valueOf(current_type_of_system.getAttribute("innerText").replaceAll("[^0-9?!]", ""));
            assertFalse(
                    "При переключении разбиения системы не меняется общее количество экспрессов. Было " + countExp + ", стало " + count,
                    count == countExp);
            countExp = count;
        }
    }


    /**
     * Метод проверяет кнопку 'Заключить Пари' в купоне активна или заблокирована и печать ошибок и предупреждений в купоне, если есть.
     *
     * @param status - ожидаемое состояние кнопки(активна, заблокирована)
     */
    @ActionTitle("проверяет что кнопка Заключить Пари")
    public void checkButtonBet(String status) {
        WebDriver driver = PageFactory.getDriver();
        boolean disabled = status.equals("активна");
        if (buttonBet.isEnabled() != disabled) {
            Assertions.fail("Кнопка 'Заключить пари' в неправильном состоянии: не " + status);
        }
        int i = 0;
        LOG.info("Ищем и нажимаем на шестерёнку в Купоне [" + i + "]");
        WebElement gear = driver.findElement(xpath("//span[contains(@class,'coupon-tabs__item-link')]/i"));
        gear.click();

        LOG.info("Ищем и выбираем 'Любые коэффициенты' [" + i + "]");
        driver.findElement(xpath("//span[text()='Любые изменения']")).click();
        LOG.info("Возвращаемся к списку событий в купоне");
        driver.findElement(xpath("//span[text()='Купон']")).click();
        if (buttonBet.isEnabled()!=disabled){
            Assertions.fail("Кнопка 'Заключить пари' в неправильном состоянии: не " + status);
        }
        LOG.info("Кнопка 'Заключить пари' " + status);

        List<WebElement> notifications = driver.findElements(xpath("//div[contains(@class,'bet-notification__suggestion_visible') and @trans]"));
        notifications.addAll(driver.findElements(xpath("//div[contains(@class,'bet-notification__warning_visible')]/span")));
        for (WebElement element : notifications) {
            LOG.info(element.getAttribute("innerText"));
        }
    }

    @ActionTitle("вводит сумму одной ставки Ординар")
    public void inputBetOrdinar(String sum) {
        LOG.info("Вводим для первого события сумму ставки: " + sum);
        fillField(couponInputOrdinar, sum);
        Stash.put("sumKey", sum);
        LOG.info("Сохранили в память key [sumKey] <== value [" + sum + "]");
    }

    @ActionTitle("нажимает кнопку ВНИЗ - дублирование ставки для всех пари")
    public void dublicateBet() {
        BigDecimal sum;
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> listBets = driver.findElements(xpath("//input[contains(@class,'input coupon__input') and not(@id='bet-input')]"));
        String sumFirstBet = listBets.get(0).getAttribute("value");
        LOG.info("Сумма первой ставки = " + sumFirstBet);
        if (listBets.size() > 1) {//если в купоне не одна ставка, то имеет смысл нажать кнопку "вниз" заполняющую сумму ставки для всех одинаковой
            LOG.info("Нажимаем на кнопку ВНИЗ чтобы скопировать размер ставки для второго события");
            driver.findElements(xpath("//span[contains(@class,'coupon-btn_repeat-stake')]")).get(0).click();
            LOG.info("Проверяем что размер ставки продублировался");
            String valueBet = listBets.get(1).getAttribute("value");
            if (!valueBet.equals(sumFirstBet)) {
                Assertions.fail("Ставка не продублировалась для второго события " + valueBet);
            }
        }
    }

    @ActionTitle("переходит в настройки и меняет коэффицент в купоне")
    public void changePreferencesCoeff() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        LOG.info("переходит в настройки и меняет коэффицент");
        String previous;
        WebElement coeff = driver.findElement(xpath("//span[contains(@class,'coupon-bet__coeff') and not (contains (@class, 'strikeout'))]"));
        previous = coeff.getAttribute("innerText");
        preferences.click();
        List<WebElement> list_of_pref = driver.findElements(By.cssSelector("span.prefs__key"));

        LOG.info("Переключаемся на '" + list_of_pref.get(2).getAttribute("innerText") + "' формат отображения");
        list_of_pref.get(2).click();
        LOG.info("Текущее значение коэффициента : " + coeff.getAttribute("innerText"));
        Thread.sleep(350);
        if (previous.equals(coeff.getAttribute("innerText"))) {
            LOG.error("Формат отображения коэффициентов не изменился");
            Assertions.fail("Формат отображения коэффициентов не изменился");
        }
        LOG.info("Смена форматов отображения коэффицентов прошла успешно");
        list_of_pref.get(0).click();
    }

    @ActionTitle("выбирает ставку бонусами")
    public void onBonus(){
        WebDriver driver = PageFactory.getDriver();
        if (driver.findElement(xpath("//span[contains(@class,'bonusmoney-text')]")).isDisplayed()) {
            bonusBet.click();
        }
        LOG.info("Купон перключен на ставку БОНУСАМИ");
    }

    @ActionTitle("проверяет наличие банеров в купоне")
    public void checkBannerInCoupon(){
        if (bannersInCoupon.isDisplayed()){
            LOG.info("Баннер отображается, купон пустой");
        }
        else {
            Assertions.fail("Ошибка! Событий в купоне нет, но банеры при этом не отображаются.");
        }
    }

    @ActionTitle("проверяет отсутствие баннеров в купоне")
    public void checkBannersOutOfCoupon(){
        if (bannersInCoupon.isDisplayed()){
            Assertions.fail("Ошибка! Баннер отображается, хотя в купоне есть событие");
        }
        else {
            LOG.info("Баннер не отображается, так как в купоне есть событие");
        }
    }

    @ActionTitle("выбирает авто-переключение купона на")
    public void autoSelectCoupon(String autoSelect ){
        WebDriver driver = PageFactory.getDriver();
        LOG.info("Переключаемся на вкладку настроек купона");
        driver.findElement(xpath("//i[contains(@class,'coupon-tabs__item-icon')]")).click();
        LOG.info("Выбираем пункт " + autoSelect);
         driver.findElement(xpath("//span[@class='coupon-settings__radio-label-text' and normalize-space(text())='" + autoSelect + "']")).click();
         LOG.info("Возвращаемся во вкладку Купон");
        driver.findElement(xpath("//li[contains(@class,'coupon-tabs__item')]/span[normalize-space(text())='Купон']")).click();
    }

    @ActionTitle("проверяет что текущий тип купона")
    public void checkCurrentTypeCoupon(String expectedType){
        String currentType = couponType.getAttribute("innerText");
        assertTrue(
                "Текущий тип купона неправильный! Ожидалось " + expectedType + ", а на самом деле " + currentType,
                currentType.trim().equalsIgnoreCase(expectedType));
    }

    @ActionTitle("пытается перейти на вкладку в купоне")
    public void changeTabInCoupon(String tab, String active) {
        LOG.info("Переключение на вкладку " + tab + " в купоне");
        WebElement selectTab = PageFactory.getDriver().findElement(xpath("//ul[contains(@class,'coupon-tabs')]//span[normalize-space(text())='" + tab + "']"));
        boolean expectedActive = active.equals("успешно") ? true : false;
        boolean isActive = selectTab.findElement(By.xpath("ancestor::li")).getAttribute("disabled")==null;
        // selectTab.findElement(By.xpath("ancestor::li")).getAttribute("class").contains("selected");
        Assert.assertTrue(
                "Ожидалось что активность вкладки в купоне " + tab + " будет " + expectedActive + ", но на самом деле " + isActive,
                isActive == expectedActive);

        if (expectedActive) {
            try {
                selectTab.click();
                Assert.assertTrue(
                        "Не удался переход на нужную вкладку. Сейчас вместо нужной, активна кладка " +
                                PageFactory.getDriver().findElement(xpath("//ul[contains(@class,'coupon-tabs')]/li[contains(@class,'selected')]/span")).getAttribute("innerText"),
                        selectTab.findElement(By.xpath("ancestor::li")).getAttribute("class").contains("selected"));
                CommonStepDefs.workWithPreloader();
            } catch (NoSuchElementException e) {
                LOG.info("Невозможно переключиться на нужную вкладку купона. Элемент не найден");
            }
        }
    }

    @ActionTitle("включает фрибет если есть и проверяет наличие экспресс-бонуса")
    public void onFreebet() {
        WebDriver driver = PageFactory.getDriver();
        boolean hasFreeBet = !driver.findElements(xpathFreeBet).isEmpty();
        if (hasFreeBet) {
            LOG.info("Фрибета нет у этого пользователя");
        } else {
            driver.findElement(xpathFreeBet).click();
            checkBonus(false);
            checkExpressBonus(false);
        }
    }

    @ActionTitle("включает фильтр в заключённых пари")
    public void filterForHitoryInCoupon(String filter) {
        WebDriver driver = PageFactory.getDriver();
        CommonStepDefs.workWithPreloader();
        By filtrHeadXpath = By.xpath("//div[contains(@class,'custom-select__placeholder_small option')]");
        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(filtrHeadXpath));
        driver.findElement(filtrHeadXpath).click();
        tryToClick(driver.findElement(filtrHeadXpath).
                findElement(By.xpath("following-sibling::div[contains(@class,'scroll-contain')]//span[normalize-space(text())='" + filter + "']")));
        String currentFilter = driver.findElement(filtrHeadXpath).getAttribute("innerText");
        Assert.assertTrue(
                "Не сработал фильтр для истории заключенных пари в купоне. вместо " + filter + ", включен " + currentFilter,
                currentFilter.equalsIgnoreCase(filter));
        CommonStepDefs.workWithPreloader();
    }

    @ActionTitle("запоминает первые события в заключенных пари")
    public void rememberHistoryInCoupon(String nameList){
        WebDriver driver = PageFactory.getDriver();
        int  cou = 3;
        List<BetFull> betsOnMyBets= new ArrayList<>();
        By byListLines = By.xpath("//div[@class='coupon__outcome-betslip-wrapper']/div[contains(@class,'coupon__bet-block')]");
        List<WebElement> linesOnHistory = driver.findElements(byListLines);
        if (linesOnHistory.size()<cou){
            cou=linesOnHistory.size();
        }

        for (int i=0; i<cou;i++){
            betsOnMyBets.add(remeberLineInHistoryCoupon(i));
            LOG.info("Запомнили строчку из истории пари в купоне");
        }

        LOG.info("Первые " + cou + " ставок(ки) в истории в купоне это: \n" +
                betsOnMyBets.get(0).getType() + "\n" + betsOnMyBets.get(1).getType() + "\n" + betsOnMyBets.get(2).getType());
        Stash.put(nameList,betsOnMyBets);
    }



    public BetFull remeberLineInHistoryCoupon(int ind) {

        WebElement sumElement;
        WebDriver driver = PageFactory.getDriver();
        WebElement element = driver.findElements(By.xpath("//div[@class='coupon__outcome-betslip-wrapper']/div[contains(@class,'coupon__bet-block')]")).get(ind);
        By xpathTypeBet = By.xpath("div[contains(@class,'coupon__outcome-betslip-title')]");
        SimpleDateFormat formatNo = new SimpleDateFormat("dd MMMM");
        SimpleDateFormat formatYes = new SimpleDateFormat("dd.MM");
        StringBuilder typeBet = new StringBuilder();
        StringBuilder timeBet = new StringBuilder();
        StringBuilder sumBet = new StringBuilder();
        List<String> names = new ArrayList<>();
        List<String> coefs = new ArrayList<>();
        List<String> dateBets = new ArrayList<>();
        StringBuilder helpString = new StringBuilder();
        List<BetFull> betsOnMyBets = new ArrayList<>();
        BetFull bet = new BetFull();
        LOG.info("путь: " + element);
        //запоминаем тип ставки
        typeBet.append(element.findElements(xpathTypeBet).isEmpty() ? "ординар" : element.findElements(xpathTypeBet).get(0).getAttribute("innerText").trim().split(" ")[0]);
        bet.setType(typeBet.toString());

        //запоминаем время ставки
        helpString.append(element.findElement(By.xpath(".//div[@class='coupon-bet__row']/div")).getAttribute("innerText"));
        int index = helpString.indexOf(":");
        timeBet.append(helpString.substring(index - 2, index + 3));
        bet.setTimeBet(timeBet.toString());

        //запоминаем размер ставки
        sumElement = element.findElement(By.xpath("div[contains(@class,'coupon-bet')]//*[normalize-space(text())='Ставка']/following-sibling::span"));
        sumBet.append(element.findElement(By.xpath("div[contains(@class,'coupon-bet')]//*[normalize-space(text())='Ставка']/following-sibling::span")).getAttribute("title").trim());

        helpString.setLength(0);
       // helpString.append(sumElement.findElement(By.xpath("span")).getAttribute("class"));
        helpString.append(element.findElement(By.xpath("div[contains(@class,'coupon-bet')]//*[normalize-space(text())='Ставка']/following-sibling::span/span")).getAttribute("class"));
        sumBet.append(helpString.toString().contains("rur") ? " Р" : " Б");
        bet.setSum(sumBet.toString());


        //запоминаем названия ставок (или одно название, если ординар)
        element.findElements(By.xpath("div[contains(@class,'coupon-bet')]/ul//div[contains(@class,'coupon-bet__title')]"))
                .forEach(el -> names.add(el.getAttribute("innerText").trim()));
        bet.setNames(names);

        //запоминаем коэффициенты ставок (или один кэф, если ординар)
        element.findElements(By.xpath("div[contains(@class,'coupon-bet')]/ul/li[3]/span[contains(@class,'coupon-bet__sum')]"))
                .forEach(el -> coefs.add(el.getAttribute("title").trim()));
        bet.setCoefs(coefs);

        //запоминаем даты игр
        element.findElements(By.xpath("div[contains(@class,'coupon-bet')]/ul/li[2]/*"))
                .stream()
                .map(WebElement::getText)
                .map(el -> el.substring(0, el.indexOf(":") - 5))
                .map(el -> CommonStepDefs.newFormatDate(formatNo, formatYes, el))
                .forEach(el -> dateBets.add(el));
        bet.setDates(dateBets);
        return bet;
    }


    @ActionTitle("проверяет совпадение записей в купоне и в Моих Пари")
    public void compareBets(String nameListOne, String nameListTwo){
        List<BetFull> listOne = Stash.getValue(nameListOne);
        List<BetFull> listTwo = Stash.getValue(nameListTwo);

        Assert.assertTrue("Не один размер у списков",listOne.size()==listTwo.size());

        for(int i=0; i<listOne.size(); i++){
            listOne.get(i).normalizationBet();
            listTwo.get(i).normalizationBet();
            Assert.assertTrue("Запись под номером " + (i+1) + " не совпадает в 'Моих Пари' и в 'Истории пари' в купоне:",
                    listOne.get(i).equals(listTwo.get(i)));
            LOG.info("Проверили одну строчку");
        }
        LOG.info("Записи в 'Моих пари' и в купоне в 'заключенных пари' совпадают!");
    }
}

