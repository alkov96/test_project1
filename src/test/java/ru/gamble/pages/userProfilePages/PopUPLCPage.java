package ru.gamble.pages.userProfilePages;

import org.assertj.core.api.Assertions;
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
import ru.gamble.pages.CouponPage;
import ru.gamble.pages.mainPages.AuthenticationMainPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import sun.awt.windows.WEmbeddedFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;


@PageEntry(title = "Мини Личный Кабинет")
public class PopUPLCPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(PopUPLCPage.class);
    private final String passCUPIS = "Regfordepoit_0601";
    private final String phone = "70010020601"; //телефон пользователя

    @ElementTitle("адрес почты")
    @FindBy(id = "user-profile")
    private WebElement emailBotton;

    @ElementTitle("Выйти")
    @FindBy(id = "log-out-button")
    private WebElement exitButton;

    @ElementTitle("Личный кабинет")
    @FindBy(id = "user-profile")
    private WebElement profileButton;

    @ElementTitle("Вывод")
    @FindBy(id = "withdraw-button")
    private WebElement withdrawButton;

    @FindBy(id = "money_in_amount2")
    private WebElement withdraw_field;

    @ElementTitle("кнопка ВЫВЕСТИ на попапе")
    @FindBy(xpath = "//div[@class='money-in-out__line']/button[contains(@class,'money-in-out__btn')]")
    private WebElement withdrawOk;

    @ElementTitle("поле пароля при входе в ЦУПИС")
    @FindBy(id="form_login_password")
    private WebElement passwordCUPIS;

    @ElementTitle("кнопка ВОЙТИ в личный кабинет ЦУПИС")
    @FindBy (id="btn_authorization_enter")
    private WebElement authorizCUPIS;


    public PopUPLCPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(exitButton));
    }

    @ActionTitle("вводит крупную сумму для вывода")
    public void bigSummWithdraw() {
        WebDriver driver = PageFactory.getDriver();
        withdraw_field.click();
        withdraw_field.clear();
        withdraw_field.sendKeys("9999999");
    }

    @ActionTitle("проверяет, появляется ли сообщение о недостаточном количестве средств на балансе")
    public void excessOfLimit() {
        WebDriver driver = PageFactory.getDriver();
        if (!driver.findElement(By.xpath("//div[@class='money-in-out__messages']/div[contains(@class, 'money-in-out__message money-in-out__message-error')]")).isDisplayed()) {
            LOG.error("Предупреждение о превышении лимита не найдено или кнопка вывода средств активна");
            Assertions.fail("Предупреждение о превышении лимита не найдено или кнопка вывода средств активна");
        } else {
            LOG.info("Предупреждение о превышении лимита отображается. Кнопка вывода средств неактивна");
        }
    }

    @ActionTitle("вводит крупную сумму, превышающую текущее значение баланса на 1")
    public void summWithdraw1() {
        WebDriver driver = PageFactory.getDriver();
        AuthenticationMainPage.rememberBalnce("рубли");
        float balance1 = Stash.getValue("balanceKey");
        int balance;
        balance = (int) balance1;
        withdraw_field.click();
        withdraw_field.clear();
        withdraw_field.sendKeys(String.valueOf(balance + 1));
    }

    @ActionTitle("вводит сумму меньше минимальной и проверяем для каждого способа")
    public void summMin() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        withdraw_field.click();
        withdraw_field.clear();
        withdraw_field.sendKeys("1");

        LOG.info("Кликаем на каждый доступный способ вывода и проверяем, что появляет ошибка лимитов.");
        List<WebElement> minSumElements = driver.findElements(By.xpath("//table[@class='moneyInOutTable']//div[not(contains(@class,'not-available')) and @ng-repeat='type in withdrawalTypes']" +
                "//label[contains(@for,'withdraw-method')]/following-sibling::div/span[1]"));
        List<String> minSumStrings = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?u)[^0-9]");
        minSumElements.forEach(element -> minSumStrings.add(pattern.matcher(element.getText()).replaceAll("")));
        String errorMessage;
        String errorMessageSum;
        LOG.info("Кликаем на каждый доступный способ вывода и проверяем, что появляет ошибка лимитов.");
        for (int i = 0; i < minSumElements.size(); i++) {
            minSumElements.get(i).findElement(By.xpath("../../label/div")).click();//кликаем на способ вывода
            CommonStepDefs.workWithPreloader();
            errorMessage = driver.findElement(By.xpath("//div[@class='money-in-out__messages']/div[contains(@class, 'money-in-out__message money-in-out__message-error')]")).getText();
            errorMessageSum = pattern.matcher(errorMessage).replaceAll("");
            Assert.assertEquals(minSumStrings.get(i), errorMessageSum);
        }
        LOG.info("При попытке вывода суммы меньше достустимой появляется ошибка лимитов");
        Stash.put("minSumElementsKey", minSumElements);
    }

    @ActionTitle("проверяет, что при нажатии на сумму-ссылку появляется НДФЛ и бонусы")
    public void summLink() throws Exception {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> minSumElements = Stash.getValue("minSumElementsKey");
        String ndfl;
        String linkBalance;
        String sumOnButton;
        float ndflFloat;
        float linkBalanceFloat;
        float sumOnButtonFloat;
        float sumOnButtonShouldBe;
        for (int i = 0; i < minSumElements.size(); i++) {//Если нажимаем на сумму на серой ссылке, то должны отображаться бонусы и НДФЛ.
            Thread.sleep(1000);
            driver.findElement(By.xpath("//div[@class='smallJsLink__wrapper']/span[1]")).click(); //кликаем на сумму на ссылке
            Thread.sleep(1000);
            CommonStepDefs.waitToPreloader();
            minSumElements.get(i).findElement(By.xpath("../../label/div")).click();//кликаем на способ вывода
            CommonStepDefs.waitToPreloader();
            CommonStepDefs.workWithPreloader();
            ndfl = driver.findElement(By.xpath("//div[contains(@class,'money-in-out__line')]/p/span")).getText();
            sumOnButton = driver.findElement(By.xpath("//button[@type = 'submit']/span[1]/span[1]")).getText().replace(" ", "").replace(",", ".");//сумма на кнопке
            linkBalance = driver.findElement(By.xpath("//div[@class='smallJsLink__wrapper']/span[1]")).getText().replace(" ", "");//сумма баланса на ссылке;
            ndflFloat = Float.parseFloat(ndfl);
            linkBalanceFloat = Float.parseFloat(linkBalance);
            sumOnButtonFloat = Float.parseFloat(sumOnButton);
            sumOnButtonShouldBe = linkBalanceFloat - ndflFloat;
            if (!driver.findElement(By.xpath("//div[@class='modal__body modal__body_moneyInOutBox']//p[contains(@class,'money-in-out__bonus-money')]")).isDisplayed()) {
                LOG.error("Бонусы ндфл не отображаются.");
                Assertions.fail("Бонусы ндфл не отображаются.");
            } else {
                LOG.info("Бонусы отображаются.");
            }

            LOG.info("Првоерка на то ,что сумма на кнопке совпадает с суммой баланса минус ндфл");

            if (sumOnButtonFloat != sumOnButtonShouldBe) {
                Assertions.fail("Сумма на кнопке и сумма вводимой суммы минус ндфл не совпадают.");
            } else {
                LOG.info("Суммы совпадают: " + sumOnButtonFloat + sumOnButtonShouldBe);

            }
        }
    }


    /**
     * попытка найти ту сумму вывода, при которой будет насчитываться НДФЛ, но не максимум
     * будет начинать с минимума и прибавлять кажды раз 5 рублей
     * @param way
     * @throws Exception
     */
    @ActionTitle("вводит минимальную сумму вывода для способа")
    public void inputSum(String way) throws Exception {
        int i=0;
        WebDriver driver = PageFactory.getDriver();
        Pattern pattern = Pattern.compile("(?u)[^0-9]");
        List<WebElement> allWayWithdraw = driver.findElements(By.xpath("//table[@class='moneyInOutTable']//div[not(contains(@class,'not-available')) and @ng-repeat='type in withdrawalTypes']"));
        String min = allWayWithdraw.get(i).findElement(By.xpath("span//span[@ng-if='withdrawMethods[type].limit.min']")).getText();//минимум для первого способа вывода
        String max = allWayWithdraw.get(i).findElement(By.xpath("span//span[@ng-if='withdrawMethods[type].limit.max']")).getText();//минимум для первого способа вывода
        max = pattern.matcher(max).replaceAll("");//максимум для вбранного способа вывода
        min = pattern.matcher(min).replaceAll("");//минимум для вбранного способа вывода

        LOG.info("Для выбранного способа вывода " + allWayWithdraw.get(i).findElement(By.xpath("span//div[contains(@class,'payPartner')]")).getAttribute("class"));
        LOG.info("Минимальная сумма вывода = " + min);
        LOG.info("Максимальная сумма вывода = " + max);

        int sum = Integer.valueOf(min);
        int allowMax = Integer.valueOf(max);
        float balanceF = Stash.getValue("balanceKey");
        int balance = (int) balanceF;
        allowMax=Math.min(allowMax,balance); //сумму вывода можно увеличивать либо до ограничения на способе вывода, либо до достижения суммы баланса
        LOG.info("Максимальная сумма вывода, с учетом текущего баланса = " + allowMax);

        LOG.info("попробуем найти сумму вывода, при которой будет высчитываться НДФЛ");
        allWayWithdraw.get(i).click(); //выбор первого способа вывода
       // CommonStepDefs.waitToPreloader();
        List<WebElement> bonuses = new ArrayList<>();
        Pattern patternBonus = Pattern.compile("(?u)[^0-9.]");
        while (sum<allowMax){
            withdraw_field.clear();
            withdraw_field.sendKeys(String.valueOf(sum));

            new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(withdrawOk));
     //       Thread.sleep(2000);//вместо слипа.вот тут нужно ожидание что кнопка Вывести - активна.


            bonuses=driver.findElements(By.xpath("//p[@ng-if and contains(@class,'money-in-out__bonus-money')]"));//проверка появился ли НДФЛ
            if (!bonuses.isEmpty()) {//если НДФЛ появился, то такую сумму и будем выводить
                String bonus = bonuses.get(0).getText();
                bonus=patternBonus.matcher(bonus).replaceAll("");
                String sumOnButton = driver.findElement(By.xpath("//button[@type = 'submit']/span[1]/span[1]")).getText().replace(" ", "").replace(",", ".");//сумма на кнопке
                Stash.put("bonus",bonus);
                Stash.put("withdrawRub",sumOnButton);
                LOG.info("Сумма вывода, при которой высчитывается НДФЛ = " + sum + ", НДФЛ = " + bonus);
                break;
            }
            sum+=50000;   //если НДФЛ на такой сумме не высчитывается - увеличим сумму на 5 рублей
        }
        if (Stash.getValue("bonus")==null){
            LOG.info("НДФЛ не начисляется. Проверка вывода без начисления бонусов при сумме вывода = " + min);
            withdraw_field.clear();
            withdraw_field.sendKeys(String.valueOf(min));
            Stash.put("bonus","0");
            Stash.put("withdrawRub",min);
            new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(withdrawOk));
        }
        String siteHandle = driver.getWindowHandle();
        withdrawOk.click();
        withdrawCUPIS(siteHandle);
        driver.switchTo().window(siteHandle);
        Thread.sleep(500);


//todo дальше совершение стаки и проверка баланса прямо отсюда
    }

    /**
     * проверка вывода - все действия в ЦУПИС. в качестве параметра принимается адрес url -сата. чтобы знатчь куда возвращаться
     */
    public void withdrawCUPIS(String currentHandle){
        WebDriver driver = PageFactory.getDriver();
        new WebDriverWait(driver,10).until(numberOfWindowsToBe(2));
        Set<String> allHandles= driver.getWindowHandles();
        allHandles.remove(currentHandle);
        String urlCUPIS = allHandles.toArray()[0].toString();
        driver.switchTo().window(urlCUPIS);
        LOG.info("Перешли в ЦУПИС. Авторизация с паролем = " + passCUPIS);
//        for (String handle:allHandles){ //для переключения на вкладку ЦУПИС, т.к. точного адреса ЦУПИС не знаю
//            driver.switchTo().window(handle);
//            if (!driver.getCurrentUrl().contains(urlSite)) break;
//        }

        new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOf(passwordCUPIS));
        passwordCUPIS.clear();
        passwordCUPIS.sendKeys(passCUPIS);
        authorizCUPIS.click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!driver.findElements(By.id("card_tq09e1wn_sms")).isEmpty()) { //для кошелька ЦУПИС не нужно вводить код. для карт - нужно. поэтому вот такой сценари:есть поле для кода - вводим код. нету - значит не надо
            LOG.info("Для подтверждения вывода нужно ввести код из смс");
            urlCUPIS = driver.getWindowHandle();
            CommonStepDefs.newWindow("http://88.198.200.81:27000/testservice/");
            String mySMS = driver.findElement(By.xpath("//li[contains(text(),'" + phone + "')]")).getText();
            String code = mySMS.substring(mySMS.length() - 5, mySMS.length() - 1);
            LOG.info("Код подтверждения:" + code);
            LOG.info("Возвращаемся обратно в ЦУПИС и подверждаем вывод");
            driver.switchTo().window(urlCUPIS);
            new WebDriverWait(driver, 10).until(ExpectedConditions.titleIs("Первый ЦУПИС"));
            driver.findElement(By.id("card_tq09e1wn_sms")).clear();
            driver.findElement(By.id("card_tq09e1wn_sms")).sendKeys(code);
            driver.findElement(By.id("card_tq09e1wn_submit")).click();
        }

        if (!driver.findElements(By.id("wallet_dg09y5ch_submit")).isEmpty()) {//при выводе на карту - сразу после подтвержедния кода происходит вывод. при выводе на кошелек ЦУПИС - отдельная кнопочка.
            driver.findElement(By.id("wallet_dg09y5ch_submit")).click();
            LOG.info("Подверждаем вывод в ЦУПИС");
        }
        new WebDriverWait(driver,10).until(titleIs("Первый ЦУПИС — Перевод обрабатывается"));
    }


    @ActionTitle("проверяет что снлась праивльная сумма, и бонусы(если были) начислены")
    public void balanceAfterWithdraw(){
        LOG.info("Проверка что правильно изменился баланс рублей");
        float sum = Float.valueOf(Stash.getValue("withdrawRub"));
        Stash.put("sumKey",sum);
        CouponPage.balanceIsOK("рубли");


        sum = -Float.valueOf(Stash.getValue("bonus"));
        if (sum>0.0f){
            LOG.info("Проверка что правильно изменился баланс бонусов");
            Stash.put("sumKey","-"+sum);
            CouponPage.balanceIsOK("бонусов");
        }
    }
}


