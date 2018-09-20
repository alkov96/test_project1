package ru.gamble.pages;


import org.assertj.core.api.Assertions;
import org.junit.Assert;
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

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Купон")
public class CouponPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(CouponPage.class);

    @FindBy(xpath = "//div[contains(@class,'list-bet-block')]")
    private WebElement coupon;

    @ElementTitle("экспресс-бонус ссылка")
    @FindBy(xpath = "//div[@class='coupon-bonus-info coupon-bonus-info-link']")
    private WebElement expressBonusLink;

    @ElementTitle("текст экспресс бонуса")
    @FindBy(xpath = "//div[@class='coupon-bonus-info ng-binding']")
    private WebElement expressBonusText;

    @ElementTitle("бонус к возможному выйгрышу")
    @FindBy(xpath = "//p[@class='betting-result-info__total-bonus']")
    private WebElement bonus;

    @ElementTitle("параметры в купоне")
    @FindBy(xpath = "//div[@class='list-bet-block-top']//div[@class='bs-type-switcher__wrapper']//i")
    private WebElement button;

    @ElementTitle("поле суммы ставки Ординар")
    @FindBy(xpath="//input[contains(@class,'input coupon__input') and not(@id='bet-input')]")
    private WebElement couponInputOrdinar;

    @ElementTitle("поле суммы ставки типа Система")
    @FindBy(xpath="//input[contains(@class,'input coupon__input') and @id='bet-input']")
    private WebElement couponInputSystem;

    @ElementTitle("кнопка Заключить пари для Экспресса и Системы")
    //@FindBy(id="place-bet-button")
    @FindBy(xpath = "//button[normalize-space(text())='Заключить пари']")
    private WebElement buttonBet;

    @ElementTitle("переключатель ставки на бонусы")
    @FindBy(id="bonusmoney")
    private WebElement bonusBet;

    @FindBy(className="coupon-banners") //баннеры в купоне
    private WebElement bannersInCoupon;

    @ElementTitle("текущий тип системы")
    @FindBy(xpath="//div[contains(@class,'coupon__system-select')]//div[contains(@class,'custom-select__placeholder')]/span")
    private WebElement current_type_of_system;

    public CouponPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(coupon,10, 5);
    }

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

    @ActionTitle("проверяет наличие бонуса к возможному выигрышу")
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



    @ActionTitle("проверяет, добавилось ли событие в купон")
    public void checkListOfCoupon() {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> couponList= driver.findElements(By.xpath("//div[@class='coupon-bet-list__wrap']/ul"));
        if (couponList.isEmpty()) {
            Assertions.fail("События не добавлиись в купон.");
        } else LOG.info("Событие " + couponList.size());
    }

    @ActionTitle("проверяет, совпадают ли события в купоне с ожидаемыми из")
    public void bannerAndTeams(String team1key, String team2key) {
        WebDriver driver = PageFactory.getDriver();
        String couponGame = driver.findElement(By.xpath("//div[@class='coupon-bet-list__wrap']/ul[1]/li[1]/span[contains(@class,'bet-event-title')]")).getText();//cuponGame - наше добавленные события в купоне.
        String team1 = Stash.getValue(team1key);
        String team2 = Stash.getValue(team2key);
        if (CommonStepDefs.stringParse(team1 + team2).equals(CommonStepDefs.stringParse(couponGame))) {
            LOG.info("Названия команд в купоне совпадают с ожидаемыми: [" + team1 + "] - [" + team2 + "] <=> [" + couponGame + "]");
        } else Assertions.fail("Названия команд в купоне не совпадают с ожидаемыми: [" + team1 + "] - [" + team2 + "] <=> [" + couponGame + "]");
    }


    @ActionTitle("проверяет, совпадает ли исход в купоне с ожидаемым")
    public void checkIshod(String ishodKey) {
        WebDriver driver = PageFactory.getDriver();
        String ishod = driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[@class='pick ng-binding']")).getText();
        String ishodName = Stash.getValue(ishodKey);//ожидаемое название исхода
        if (CommonStepDefs.stringParse(ishod).equals(CommonStepDefs.stringParse(ishodName))) {
            LOG.info("Выбранных исход в купоне совпадает с ожидаемым: " + ishod + " <=> " + ishodName);
        } else Assertions.fail("Выбранный исход в купоне не совпадает с ожидаемым: " + ishod + " - " + ishodName);

    }

    @ActionTitle("сравнивает коэфиценты")
    public void compareCoef(String keyOutcome) {
        WebDriver driver = PageFactory.getDriver();
        String coefString = Stash.getValue(keyOutcome).toString();
        float coef = Float.valueOf(coefString);
        float coefCoupon = Float.valueOf(driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[contains(@class,'coupon-betprice')]")).getText());//Кэфицент в купоне
        String oldString = driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[contains(@class,'coupon-betprice_old')]")).getAttribute("class");//oldString - просто переменная, в которую сохраним класс, где лежит старый коэф.
        float coefOld;
        coefOld = oldString.contains("ng-hide") ? 0.0f : Float.valueOf(driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[contains(@class,'coupon-betprice_old')]")).getText());//Краткая запись цикла. ? - часть синтаксиса. Здесь показываем чему равен старый коэфицент. если скрыт, то 0.0.
        if (coef != coefCoupon && coef != coefOld) {
            Assertions.fail("Коэфицент в купоне не совпадает с коэфицентом в событии: " + coefCoupon + coef);
        } else LOG.info("Коэфицент в купоне совпадает с коэфицентом в событии: " + coefCoupon + " <=> " + coef);

    }

    @ActionTitle("устанавливает условие для принятия коэфицентов как 'Никогда'")
    public void neverAccept(){
        WebDriver driver = PageFactory.getDriver();
        button.click();
        driver.findElement(xpath("//label[@class='betslip-settings__option']")).click();
        LOG.info("Установили условие 'Никогда'");
        button.click();
    }

    @ActionTitle("проверяет, что после изменения условий на 'Никогда' в купоне появляется кнопка 'Принять' и информационное сообщение")
    public void buttonAndMessageIsDisplayed() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        List<WebElement> oldCoef = driver.findElements(xpath("//li[@class='coupon-bet-list__item_result']/div[@class='coupon-bet-list__item-column']/span[@class='coupon-betprice_old ng-binding']"));
        Thread.sleep(500);
        WebElement error_message = driver.findElement(xpath("//div[@class='bet-notification__error-text bet-notification__suggestion-wrapper']"));
        if (oldCoef.size() > 0 && !error_message.isDisplayed()) {
            Assertions.fail("Коэф изменился, однако сообщение не отображается.");
        }
        //LOG.info("Изменился коэф и появилось сообщение о принятии коэфиценита");
        Thread.sleep(5000);
        WebElement btn_accept = driver.findElement(xpath("//div[@class='coupon-confirm__btn']"));
        if (!error_message.isDisplayed()
                || !btn_accept.isDisplayed()) {
            Assertions.fail("При изменении условий ставки не появилось сообщение или кнопка о принятии изменений.");
        }
        LOG.info("Появилось сообщение о принятии коэфицента и кнопка");

        LOG.info("Проверка на принятие условия 'Никогда' в купоне прошла успешно.");

    }

    /**
     * функция проверяет, как изменился коэф для конкретной ставки
     * @param param порядковый номер ставки в купоне
     * @return возращает 0, если коэф не изменился, >0, если увеличился, и <0, если уменьшился
     */
    public float compareCoef(int param) {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> allBets = driver.findElements(By.xpath("//ul[contains(@class,'coupon-bet-list')]/li[2]/div[2]"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        for (int i = 2; i > 0; i--) {
            List<WebElement> oldCoef = driver.findElements(xpath("//li[@class='coupon-bet-list__item_result']/div[@class='coupon-bet-list__item-column']/span[@class='coupon-betprice_old ng-binding']"));
            if (!oldCoef.isEmpty()) break;
        }
        float coefCoupon = Float.valueOf(allBets.get(param).findElement(By.xpath("span[contains(@class,'coupon-betprice')]")).getText());
        String oldString = allBets.get(param).findElement(By.xpath("span[contains(@class,'coupon-betprice_old')]")).getAttribute("class");
        float coefOld;
        coefOld = oldString.contains("ng-hide") ? coefCoupon : Float.valueOf(allBets.get(param).findElement(By.xpath("span[contains(@class,'coupon-betprice_old')]")).getText());
        LOG.info("Старый коэф: " + coefOld);
        LOG.info("Текущий коэф: " + coefCoupon);
        return coefCoupon - coefOld;
    }

    @ActionTitle("проверяет изменения коэфицентов в купоне при условии 'Повышенные коэфиценты', удаляет из купона все события, кроме событий, у которых повысился коэфицент")
    public void compareChangeCoef () throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        button.click();
        driver.findElement(xpath("//div[@class='betslip-settings ng-scope active']//label[2]")).click();
        button.click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<WebElement> allBets = driver.findElements(By.xpath("//ul[contains(@class,'coupon-bet-list')]/li[2]/div[2]"));
        for (int i = allBets.size()-1; i >=0; i--) {
            float s = compareCoef(i);
            LOG.info("для " + i + " события результат = " + s);
               if (s<=0){
                   driver.findElement(xpath("//ul[contains(@class,'coupon-bet-list') and position()=" + (i+1) + "]//i[contains(@class,'icon-cross-close')]")).click();
                    allBets = driver.findElements(By.xpath("//ul[contains(@class,'coupon-bet-list')]/li[2]/div[2]"));
               }
        }

        if (allBets.size() == 0){
            LOG.info("Подходящей ставки не нашлось, поэтому заново добавляем события в купон, сравниваем коэфиценты и удаляем ненужные события");
            DayEventsPage.addEventsToCouponF();
            compareChangeCoef();
            //  compareCoef(i);
        }
        LOG.info("Количество событий в купоне: " + driver.findElements(By.xpath("//ul[contains(@class,'coupon-bet-list')]/li[2]/div[2]")).size());
        if (driver.findElement(xpath("//div[@class='bet-notification__error-text bet-notification__suggestion-wrapper']")).isDisplayed()
                || driver.findElement(xpath("//div[@class='coupon-confirm__btn']")).isDisplayed()) {
            Assertions.fail("Отображается кнопка и сообщение, хотя не должны - в купоне одни лишь события с повышенным коэфицентом");
        }
        LOG.info("Не появилась кнопка 'Принять', в купоне события с повышенным коэфицентом");

    }

    @ActionTitle("выбирает тип ставки")
    public void selectTypeBet(String type) {
        WebDriver driver = PageFactory.getDriver();
        WebElement selectType = driver.findElement(By.xpath("//span[contains(@class,'coupon-tabs__item-link coupon-text-h') " +
                "and normalize-space(translate(text(),'" + type.toLowerCase() +"','" + type.toUpperCase() + "'))='" + type.toUpperCase() + "']"));
        //этот длинный xpath потому что название типа может быть с большой буквы, или с маленькой, для разного типа по-разному. и в элементе getText() вместе с пробелами идет
        LOG.info("Переключаем тип ставки на '" + selectType.getText() + "'");
        selectType.click();
    }

    /**
     * Системы это по сути сразу несколько экспрессов, и сумма ставки должна быть не меньше чем их количество.
     * @param less - показывает нужно ли вводить валидную сумму или нет. если less содержит слово меньше, то сумма должна быть меньше чем количество экспрессов - невалид
     */
    @ActionTitle("вводит сумму ставки")
    public void inputBet(String less){
        BigDecimal sum;
        int countExp = Integer.valueOf(current_type_of_system.getText().replaceAll("[^0-9?!]",""));
        String sumBet = less.contains("меньше")?String.valueOf(countExp-1):String.valueOf(countExp);
        LOG.info("Вводим сумму ставки : [" + sumBet + "]");
        fillField(couponInputSystem,sumBet);
        sum = new BigDecimal(sumBet.trim()).setScale(2,RoundingMode.UP);
        Stash.put("sumKey",sum.toString());
        LOG.info("Сохранили в память [sumKey] <== value [" + sum.toString() + "]");
        if (less.contains("меньше")){
            checkErrorsInCoupon("Минимальная ставка");
            checkErrorsInCoupon("для системы");
        }
    }

    public void checkErrorsInCoupon(String expectedError){
        List<WebElement> errorMessages = PageFactory.getDriver().findElements(By.xpath("//div[contains(@class,'coupon__message_error')]//span"));
        for (WebElement error: errorMessages){
            if (error.getText().contains(expectedError)) return;
        }
        Assert.fail("В купоне нет ожидаемого сообщения об ошибке [" + expectedError + "]");
    }




    @ActionTitle("заключает пари")
    public void doBet() throws AuthenticationException {
        WebDriver driver = PageFactory.getDriver();
        String xpathBet ="//input[contains(@class,'input coupon__input') and not(@id='bet-input')]";

        int sizeCoupon = driver.findElements(By.xpath(xpathBet)).size();
        int i = 0;
        LOG.info("Ищем и нажимаем на шестерёнку в Купоне [" + i + "]");
        WebElement gear = driver.findElement(By.xpath("//span[contains(@class,'coupon-tabs__item-link')]/i"));
        gear.click();

        LOG.info("Ищем и выбираем 'Любые коэффициенты' [" + i + "]");
        driver.findElement(By.xpath("//span[text()='Любые изменения']")).click();
        LOG.info("Возвращаемся к списку событий в купоне");
        driver.findElement(By.xpath("//span[text()='Купон']")).click();

        String typeCoupon = driver.findElement(By.xpath("//div[contains(@class,'coupon__types')]//li[contains(@class,'coupon-tabs__item_selected')]/span")).getText();
        int expectedCouponSize = typeCoupon.contains("Ординар")? (sizeCoupon-1):0;

        LOG.info("Жмём 'Заключить пари'");
        //coupon_bet_button.click();
        buttonBet.click();
        //driver.findElement(By.xpath("//button[normalize-space(text())='Заключить пари']")).click();

        waitingForPreloadertoDisappear(30);

        LOG.info("Ожидаем исчезновения из купона принятой ставки");
        new WebDriverWait(driver, 10).until(ExpectedConditions.numberOfElementsToBe(By.xpath(xpathBet),expectedCouponSize));

    }

    /**
     * проверка что баланс изменился успешно. По параметру отепределяется проверка рублей или бонусов
     * @param param если параметр = "бонусов", то проверка бонусов, иначе - првоерка рублевого баланса
     */
    @ActionTitle("проверяет изменение баланса")
    public static void balanceIsOK(String param){
        WebDriver driver = PageFactory.getDriver();
        BigDecimal currentBalance, previousBalance, sumBet;

        By balance = param.equals("бонусов") ? By.xpath("//div[contains(@class,'bonusmoney-sum')]") : By.id("topPanelWalletBalance");
        String key = param.equals("бонусов") ? "balanceBonusKey" : "balanceKey";
        int count = 30;
        previousBalance = new BigDecimal(Stash.getValue(key).toString().replace("Б","").trim()).setScale(2, RoundingMode.HALF_UP);

        sumBet = new BigDecimal((String) Stash.getValue("sumKey")).setScale(2, RoundingMode.HALF_UP);
        String currentNumber;
        while (count > 0){
            currentNumber = driver.findElements(balance).stream().filter(WebElement::isDisplayed).findFirst().get().getText();

            currentBalance = new BigDecimal(currentNumber).setScale(2, RoundingMode.HALF_UP);

            if((previousBalance.subtract(sumBet).subtract(currentBalance).abs()).compareTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_UP)) < 0){
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
            if (count == 0){
                Assertions.fail("Баланс не соответствует ожидаемому. Баланс сейчас: " + currentBalance + ", ожидалось : " + previousBalance.subtract(sumBet));
            }
        }
    }


    @ActionTitle("проверяет изменение количества экспрессов при переключении вида системы")
    public void checkCountExpress(){
        WebDriver driver = PageFactory.getDriver();
        WebElement dropdownOpt = driver.findElement(By.xpath("//div[contains(@class,'coupon__system-select')]"));
        List <WebElement> systemTypes = dropdownOpt.findElements(By.xpath("div//li[contains(@clas,'coupon__dropdown-item')]"));
        WebElement openList = dropdownOpt.findElement(By.xpath("div/div[contains(@class,'custom-select__placeholder')]"));
        int countExp = Integer.valueOf(current_type_of_system.getText().replaceAll("[^0-9?!]",""));
        int count;
        for (WebElement type:systemTypes){
            openList.click();
            type.click();
            count = Integer.valueOf(current_type_of_system.getText().replaceAll("[^0-9?!]",""));
            assertFalse(
                    "При переключении разбиения системы не меняется общее количество экспрессов. Было " + countExp + ", стало " + count,
                    count==countExp);
            countExp = count;
        }
    }


    /**
     * првоерка кнопка Заключить Пари в купоне активна или заблокирована. и Печать ошибок и предупреждений в купоне, если есть
     * @param status - ожидаемое состояние кнопки(активна, заблокирована)
     */
    @ActionTitle("проверяет что кнопка Заключить Пари")
    public void checkButtonBet(String status){
        WebDriver driver = PageFactory.getDriver();
        boolean disabled = status.equals("активна");
        if (buttonBet.isEnabled()!=disabled){
            Assertions.fail("Кнопка 'Заключить пари' в неправильном состоянии: не " + status);
        }
        LOG.info("Кнопка 'Заключить пари' " + status);

        List <WebElement> notifications = driver.findElements(xpath("//div[contains(@class,'bet-notification__suggestion_visible') and @trans]"));
        notifications.addAll(driver.findElements(xpath("//div[contains(@class,'bet-notification__warning_visible')]/span")));
        for (WebElement element:notifications){
            LOG.info(element.getText());
        }
    }

    @ActionTitle("вводит сумму одной ставки Ординар")
    public void inputBetOrdinar(String sum){
        LOG.info("Вводим для первого события сумму ставки: " + sum);
        fillField(couponInputOrdinar,sum);
        Stash.put("sumKey",sum);
        LOG.info("Сохранили в память key [sumKey] <== value [" + sum + "]");
    }

    @ActionTitle("нажимает кнопку ВНИЗ - дублирование ставки для всех пари")
    public void dublicateBet(){
        BigDecimal sum;
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> listBets = driver.findElements(By.xpath("//input[contains(@class,'input coupon__input') and not(@id='bet-input')]"));
        String sumFirstBet = listBets.get(0).getAttribute("value");
        LOG.info("Сумма первой ставки = " + sumFirstBet);
        if (listBets.size()>1) {//если в купоне не одна ставка, то имеет смысл нажать кнопку "вниз" заполняющую сумму ставки для всех одинаковой
            LOG.info("Нажимаем на кнопку ВНИЗ чтобы скопировать размер ставки для второго события");
            driver.findElements(By.xpath("//span[contains(@class,'coupon-btn_repeat-stake')]")).get(0).click();
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
        preferences.click();
        String previous;
        List<WebElement> list = driver.findElements(By.cssSelector("span.prefs__key"));
        WebElement coeff = driver.findElement(cssSelector("span.coupon-betprice"));
        previous = coeff.getText();
        LOG.info("Переключаемся на '" + list.get(2).getText() + "' формат отображения");
        list.get(2).click();
        LOG.info("Текущее значение коэффициента : " + coeff.getText());
        Thread.sleep(350);
        if (previous.equals(coeff.getText())) {
            LOG.error("Формат отображения коэффициентов не изменился");
            Assertions.fail("Формат отображения коэффициентов не изменился");
        }
        LOG.info("Смена форматов отображения коэффицентов прошла успешно");
        list.get(0).click();
    }

    @ActionTitle("выбирает ставку бонусами")
    public void onBonus(){
        WebDriver driver = PageFactory.getDriver();
        if (!driver.findElement(xpath("//div[@id='bonusmoney']/..")).getAttribute("class").contains("active")){
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

}

