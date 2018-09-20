package ru.gamble.pages;

import cucumber.api.Scenario;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.gamble.utility.Generators;
import ru.gamble.utility.JsonLoader;
import ru.gamble.utility.YandexPostman;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.qautils.errors.AutotestError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.openqa.selenium.By.xpath;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.RANDOM;
import static ru.gamble.utility.Constants.STARTING_URL;
import static ru.gamble.utility.Generators.randomString;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;


public abstract class AbstractPage extends Page {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPage.class);

    @ElementTitle("На главную")
    @FindBy(id = "main-logo")
    protected WebElement onMainPageButton;

    @ElementTitle("Иконка юзера")
    @FindBy(id = "user-icon")
    protected WebElement userIconButton;

    @ElementTitle("Бургер")
    @FindBy(id = "service-list")
    protected WebElement burgerBottom;

    @ElementTitle("День")
    @FindBy(className = "inpD")
    protected WebElement fieldDay;

    @ElementTitle("Месяц")
    @FindBy(xpath = "//div[contains(@class,'dateInput')]/div[@class='inpM']")
    protected WebElement fieldMonth;

    @ElementTitle("Год")
    @FindBy(className = "inpY")
    protected WebElement fieldYear;

    @ElementTitle("Подвал")
    @FindBy(xpath = "//*[@class='footer__pin']")
    protected WebElement footerButton;

    @ElementTitle("Настройки")
    @FindBy(xpath = "//span[@class='coupon-tabs__item-link coupon-text-h']")
    protected WebElement preferences;

    @ElementTitle("Активация Быстрой ставки")
    @FindBy(id = "quickbet")
    protected WebElement quickButton;
    //для ставок экспресс, быстрой ставки - т.е. там где 1 поле для ставки

    @ElementTitle("Очистить всё")
    @FindBy(xpath = "//span[@class='btn btn_full-width']")
    protected WebElement clearCoupon;

//для ставок экспресс, быстрой ставки - т.е. там где 1 поле для ставки
    @ElementTitle("поле суммы общей ставки")
    @FindBy(id = "express-bet-input")
    protected WebElement coupon_field;

    @ElementTitle("Сервисное сообщение")
    @FindBy(xpath = "//div[contains(@class,'tech-msg__content')]")
    private WebElement serviceMessage;

    @ElementTitle("Иконка закрытия сервисного сообщения")
    @FindBy(xpath = "//span[contains(@class,'tech-msg__close')]")
    private WebElement closeServiceMessage;

    @ElementTitle("Прематч")
    @FindBy(id = "prematch")
    private WebElement prematchBottom;

    @ElementTitle("Азбука беттинга")
    @FindBy (xpath = "//a[@href='/azbuka-bettinga']")
    private WebElement azbuka;

    // Метод три раза пытается обновить главную страницу

    @ActionTitle("открывает Избранное")
    public static void openFavourite() {
        LOG.info("vot");
        WebDriver driver = PageFactory.getDriver();
        driver.findElement(By.id("elected")).click();//нажали на кнопку избранного
    }

    @ActionTitle("нажимает кнопку")
    public static void pressButtonAP(String param){
        CommonStepDefs.pressButton(param);
        LOG.info("Нажали на [" + param + "]");
    }

    @ActionTitle("stop")
    public static void stop() {
        LOG.info("STOP");
    }

    /**
     * Метод который по имени WebElement находит его на текущей странице,
     * достаёт его ссылку и переходит по ней
     *
     * @param param - имя WebElement
     */
    @ActionTitle("переходит по ссылке")
    public static void goesByReference(String param) throws PageException {
        Page page;
        page = PageFactory.getInstance().getCurrentPage();
        String link = page.getElementByTitle(param).getAttribute("href");
        PageFactory.getWebDriver().get(link);
        LOG.info("Получили и перешли по ссылке::" + link);
        workWithPreloader();
    }

    public static void clickElement(final WebElement element) {
        WebElement myDynamicElement = (new WebDriverWait(PageFactory.getWebDriver(), 10))
                .until(ExpectedConditions.elementToBeClickable(element));
        myDynamicElement.click();
    }

    public void tryingLoadPage(WebElement element, int count, int waitSeconds) {
        WebDriver driver = PageFactory.getWebDriver();
        LOG.info("Ищем элемент [" + element + "] на странице::" + driver.getCurrentUrl());

        for (int j = 0; j < count; j++) {
            try {
                new WebDriverWait(PageFactory.getDriver(), waitSeconds ).until(ExpectedConditions.visibilityOf(element));
                break;
            } catch (Exception e) {
                driver.navigate().refresh();
            }
            if (j >= count - 1) {
                throw new AutotestError("Ошибка! Не нашли элемент после " + j + " попыток перезагрузки страницы");
            }
        }
    }

    /**
     * Метод получения письма и перехода по ссылке для завершения регистрации на сайте
     *
     * @param key - ключ по которому получаем е-mail из памяти.
     */
    @ActionTitle("завершает регистрацию перейдя по ссылке в")
    public void endRegistrationByEmailLink(String key) throws InterruptedException {
        WebDriver driver = getWebDriver();
        String email = Stash.getValue(key);
        String link = "";
        String url = "";
        Scenario scenario;
        try {
            url = Stash.getValue("MAIN_URL");
            link = YandexPostman.getLinkForAuthentication(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutotestError("Ошибка! Не смогли получить ссылку для аутентификации.");
        }

        if(url.contains("mobile")){
            new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(.,'Продолжить')]")));
            driver.findElement(By.xpath("//a[contains(.,'Продолжить')]")).click();
        }else {
            new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(.,'Войти')]")));
        }
        LOG.info("Переходим по ссылке из e-mail");
        driver.get(url + "?action=verify&" + link);

        if(url.contains("mobile")){
           return;
        }else {
            LOG.info("Ожидаем диалогового окна с надписью 'Спасибо!'");
            Thread.sleep(5000);
            if (!driver.findElement(By.cssSelector("a.modal__closeBtn.closeBtn")).isDisplayed()) {
                Assert.fail("Ошибка! Не пояивлось диалоговое окно с надписью 'Спасибо!'");
            }
            LOG.info("Закрываем уведомление об успешном подтверждении почты");
            driver.findElement(By.cssSelector("a.modal__closeBtn.closeBtn")).click();
        }
    }

    /**
     * Открывает выпадающий список и выбирает оттуда пункт случайным образом
     *
     * @param element - поле где ждем выпадающий список
     * @param max     - максимальный пункт, который можно выбрать (включая этот max). Второго параметра может и не быть. тогда максимум - это длина всего списка
     */
    protected void selectMenu(WebElement element, int max) {
        int menuSize = element.findElements(By.xpath("custom-select/div[2]/div")).size();
        menuSize -= (max + 1);
        int select = 1 + (int) (Math.random() * menuSize);
        element.findElement(By.xpath("custom-select")).click();
        element.findElement(By.xpath("custom-select/div[2]/div[" + select + "]")).click();
    }

    protected void selectMenu(WebElement element) {
        selectMenu(element, 0);
    }

    protected String enterDate(String value) {
        StringBuilder date = new StringBuilder();
        String day, month, year;
        if (value.equals(RANDOM)) {

            do {
                selectMenu(fieldYear);
                selectMenu(fieldMonth);
                selectMenu(fieldDay);
                LOG.info("Вводим случайную дату::");
            } while (PageFactory.getWebDriver().findElement(By.className("inpErrText")).isDisplayed());
        } else {
            String[] tmp = value.split(".");
            LOG.info("Вводим дату");
            selectMenu(fieldMonth, Integer.parseInt(tmp[1]));
            selectMenu(fieldDay, Integer.parseInt(tmp[0]));
            selectMenu(fieldYear, Integer.parseInt(tmp[2]));
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        day = fieldDay.getText();
        month = fieldMonth.getText();
        year = fieldYear.getText();
        return date.append(year).append("-").append(month).append("-").append(day).toString();
    }

    /**
     * В выбранном поле вводит один символ и если появляется выпадающий список - выбирает первый пункт из него.
     * Иначе либо заново вводит символ и ждет список, либо заполняет поле рандомной последовательностью
     *
     * @param field    - поле, которое заполняем
     * @param authFill - булев параметр, говорит о том обязательно ли выбирать из списка (true), или можно заполнить рандомом (false)
     */
    public void fillAddress(WebElement field, boolean authFill) {
        List<WebElement> list;
        int count = 10;
        do {
            field.clear();
            StringBuilder n = new StringBuilder();
            n.append((char) ('А' + new Random().nextInt(64)));
            field.sendKeys(n);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list = field.findElements(By.xpath("../ul[1]/li"));
            count--;
            if(count<=0){ break;}
        } while (list.isEmpty() && authFill);
        if (field.findElements(By.xpath("../ul[1]/li")).isEmpty()) {
            field.clear();
            field.sendKeys(randomString(20));
        } else
            field.findElement(By.xpath("../ul[1]/li[" + (new Random().nextInt(list.size()) + 1) + "]")).click();

        if (field.getAttribute("value").length() == 0) {
            LOG.info("ОШИБКА. Нажали на пункт в выпадающем списке для города,а знчение не выбралось!!");
            field.sendKeys(randomString(20));
        }
    }

    @ActionTitle("проверяет присутствие текста")
    public void checksPresenceOfText(String text) {
        List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath("//*[text()='" + text + "']"))
                .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
        assertThat(!list.isEmpty()).as("Ошибка.Не найден::[" + text + " ]").isTrue();
    }

    @ActionTitle("проверяет наличие сообщения с текстом")
    public void checkServiceMessageTrue(String param) throws InterruptedException {
        MatcherAssert.assertThat(true, equalTo(checkServiceMessage(param)));
    }

    @ActionTitle("проверяет отсутствие сообщения с текстом")
    public void checkServiceMessageFalse() throws InterruptedException {
        MatcherAssert.assertThat(false, equalTo(checkServiceMessage(null)));
    }

    @ActionTitle("проверяет отсутствие сообщения с текстом после закрытия")
    public void checkServiceMessageFalseAfterClose() {
        MatcherAssert.assertThat(false, equalTo(checkCloseServiceMessage(serviceMessage)));
    }

    @ActionTitle("проверяет наличие иконки закрытия")
    public void chectCloseServiceMessageTrue(){
        MatcherAssert.assertThat(true, equalTo(checkCloseServiceMessage(closeServiceMessage)));
    }

    @ActionTitle("проверяет отсутствие иконки закрытия")
    public void chectCloseServiceMessageFalse(){
        MatcherAssert.assertThat(false, equalTo(checkCloseServiceMessage(closeServiceMessage)));
    }

    public void fillCouponFinal(int count, String ifForExperss, By findCoeffs) throws InterruptedException {
        if (ifForExperss.equals("correct")) {
            List<WebElement> eventsInCoupon;
            List<WebElement> correctMarkets;
            Thread.sleep(3000);
            waitForElementPresent(findCoeffs, 10);
            correctMarkets = getWebDriver().findElements(findCoeffs)
                    .stream().filter(e -> e.isDisplayed() && !e.getText().contains("-") && Double.parseDouble(e.getText()) >= 1.260)
                    .limit(count + 10).collect(Collectors.toList());
            for (WebElement coefficient : correctMarkets) {
                tryToClick(coefficient);
                eventsInCoupon = PageFactory.getWebDriver().findElements(By.xpath("//li[@class='coupon-bet-list__item']"));
                LOG.info("коэф: " + coefficient.getText());
                if (eventsInCoupon.size() == count) {
                    break;
                }
            }
        }
        if (ifForExperss.equals("incorrect")) {
            List<WebElement> eventsInCoupon;
            List<WebElement> inCorrectMarkets = null;
            waitForElementPresent(findCoeffs, 10);
            List<WebElement> allDaysPages = PageFactory.getWebDriver().findElements(By.cssSelector("span.livecal-days__weekday.ng-binding"));
            int tryPage = 0;
            int counter = 10;
            do {
                try {
                    inCorrectMarkets = getWebDriver().findElements(findCoeffs)
                            .stream().filter(e -> e.isDisplayed() && !e.getText().contains("-") && Double.parseDouble(e.getText()) < 1.25)
                            .limit(count + 3).collect(Collectors.toList());
                } catch (StaleElementReferenceException e) {
                    tryPage++;
                    allDaysPages.get(tryPage).click();
                }
                counter--;
                Assert.assertNotEquals("Не нашли достаточное количество некорректных событий.", 0, counter);
            } while (Objects.requireNonNull(inCorrectMarkets).size() < count && tryPage < allDaysPages.size() - 1);
            for (WebElement coefficient : inCorrectMarkets) {
                clickElement(coefficient);
                eventsInCoupon = PageFactory.getWebDriver().findElements(By.xpath("//ul[@class='coupon-bet-list ng-scope']"));
                LOG.info("коэф: " + coefficient.getText());
                if (eventsInCoupon.size() == count) {
                    break;
                }
            }
        }
    }

    public void waitForElementPresent(final By by, int timeout) {
        WebDriverWait wait = (WebDriverWait) new WebDriverWait(PageFactory.getWebDriver(), timeout)
                .ignoring(StaleElementReferenceException.class);
        wait.until((ExpectedCondition<Boolean>) webDriver -> {
            WebElement element = Objects.requireNonNull(webDriver).findElement(by);
            return element != null && element.isDisplayed();
        });
    }

    public void tryToClick(WebElement element) {

       for(int count = 0; count < 10; count ++){
        try {
            element.click();
            break;
        } catch (StaleElementReferenceException e) {
            tryToClick(element);
        }
        }
    }


    /**
     * включается быстрая ставка и в поле суммы вводится сумма, указанная в праметре. Если в параметр написано "больше баланса" то вводится (balance+1)
     * @param sum
     */
    @ActionTitle("включает быструю ставку и вводит сумму")
    public void onQuickBet(String sum) {
        if (!quickButton.findElement(By.xpath("..")).getAttribute("class").contains("active")) {

            quickButton.click();
        }
        BigDecimal sumBet;
        BigDecimal one = new BigDecimal(1);
        sumBet = sum.equals("больше баланса") ? new BigDecimal((String) Stash.getValue("balanceKey")).setScale(2, RoundingMode.HALF_UP).add(one): new BigDecimal(sum).setScale(2,RoundingMode.HALF_UP);
        //coupon_field.clear();
        LOG.info("Вбиваем сумму в поле купона::" + sumBet.toString());
        fillField(coupon_field,sumBet.toString());
        LOG.info("Ввелось в поле::" + coupon_field.getAttribute("value"));
        Stash.put("sumKey", sumBet.toString());
        LOG.info("Сохранили в память key [sumKey] <== value [" + sumBet.toString() + "]");
    }


    @ActionTitle("проверяет наличие сообщения об ошибке в купоне")
    public void checkError(String pattern) {
        WebDriver driver = PageFactory.getWebDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        List<WebElement> listErrors = driver.findElements(By.xpath("//div[contains(@class,'bet-notification__warning_visible')]"));
        if (listErrors.isEmpty()) {
            Assertions.fail("Нет никаких предупреждений в купоне");
        }
        for (WebElement error : listErrors) {
            if (error.getText().contains(pattern)) {
                LOG.info("Искомое предупреждение в купоне найдено: " + pattern);
                break;
            }
            if (listErrors.indexOf(error) == (listErrors.size() - 1)) {
                Assertions.fail("Искомого предупреждения нет в купоне!");
            }
        }
    }
    @ActionTitle("ждёт мс")
    public void whait(String ms) throws InterruptedException {
        int time = Integer.parseInt(ms);
        Thread.sleep(time);
    }

    @ActionTitle("перезагружает страницу")
    public void refresh(){
        PageFactory.getWebDriver().navigate().refresh();
    }


    private boolean checkCloseServiceMessage(WebElement element) {
        return element.isDisplayed();
    }

    private boolean checkServiceMessage(String text) throws InterruptedException {
        int count = 0;
        while (count < 40) {
            if (serviceMessage.isDisplayed()) {
                MatcherAssert.assertThat(true, equalTo(serviceMessage.getText().equals(text)));
                return true;
            } else {
                count++;
                Thread.sleep(1000);
                PageFactory.getWebDriver().navigate().refresh();
            }
        }
        return false;
    }

    @ActionTitle("ждет некоторое время")
    public void waiting(String sec) throws InterruptedException {
        int seconds;
        if (sec.matches("^[0-9]+")) {
            seconds = Integer.parseInt(sec);
        }
        else
        {
            seconds = Integer.parseInt(Stash.getValue(sec));
        }
        Thread.sleep(seconds*1000);
    }

    @ActionTitle("очищает купон")
    public void clearCoupon(){
        if (clearCoupon.isDisplayed()){
            clearCoupon.click();
        }
    }

    protected void waitingForPreloadertoDisappear(int timeInSeconds){
        WebDriver driver = PageFactory.getWebDriver();
        try {
            new WebDriverWait(driver, timeInSeconds).until(ExpectedConditions.invisibilityOfElementLocated(xpath("//*[contains(@class,'preloader__container')]")));
        }catch (Exception e){
            throw new AutotestError("Ошибка! Прелоадер не исчез в течение::"+ timeInSeconds + " сек.");
        }
    }

    @ActionTitle("закрывает всплывающее окно 'Перейти в ЦУПИС'")
    public void closePopUpWindowGoToTSUPIS(){
        WebDriver driver = PageFactory.getWebDriver();
        String xpathGoToTSUPIS = "//div[contains (@class,'after-reg')]/a[contains(@class,'btn_important')]";
        try{
            LOG.info("Ждём появление всплывающего окна.");
            new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathGoToTSUPIS)));
            LOG.info("Появилось окно c кнопкой [" + driver.findElement(By.xpath(xpathGoToTSUPIS)).getText() + "]");
            driver.findElements(By.xpath("//div/a[@class='modal__closeBtn closeBtn']")).stream().filter(WebElement::isDisplayed).findFirst().get().click();
            LOG.info("Закрыли всплывающего окно");
        }catch (Exception e){
            LOG.info("Окно не появилось.");
        }
    }

    @ActionTitle("проверяет, что присутствует сообщение")
    public void checksThatMessageIsPresent(String message){
        try {
            new WebDriverWait(PageFactory.getWebDriver(),3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(.,'" + message + "')]")));
        }catch (Exception e){
            throw new AutotestError("Ошибка! Текст [" + message + "] не появился");
        }
    }

    @ActionTitle("закрываем окно 'Перейти в ЦУПИС' если выскочит")
    public void closePopUpWindowGoToTSUPISIfOpened(){
        WebDriver driver = PageFactory.getWebDriver();
        try{
            new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href,'https://1cupis.ru/auth')]")));
            LOG.info("Открылось окно 'Перейти в ЦУПИС' - закрываем");
            driver.findElements(By.xpath("//a[contains(@class,'modal__closeBtn closeBtn')]")).stream().filter(WebElement::isDisplayed).findFirst().get().click();
        }catch (Exception e){
            LOG.info("Окно 'Перейти в ЦУПИС' не появилось");
        }
    }

    /**
     * Метод ввода поле номера телефона
     *
     * @param value вводимое значение
     */
    protected void enterSellphone(String value, WebElement cellFoneInput, WebElement cellFoneConformationInput){
        WebDriver driver = PageFactory.getWebDriver();
        String phone;
        int count = 1;
        do {
            if(value.contains(RANDOM)) {
                phone = "0" + Generators.randomNumber(9);
                LOG.info("Вводим случайный номер телефона::+7[" + phone + "]");
                fillField(cellFoneInput,phone);
            } else {
                phone = (value.matches("^[A-Z_]+$")) ? Stash.getValue(value) : value;
                LOG.info("Вводим номер телефона без первой 7-ки [" + phone.substring(1,11) + "]");
                fillField(cellFoneInput,phone.substring(1,11));
            }

            LOG.info("Попыток ввести номер::" + count);
            if (count > 5) {
                throw new AutotestError("Использовано 5 попыток ввода номера телефона");
            }
            ++count;

        } while (!driver.findElements(By.xpath("//div[contains(@class,'inpErrTextError')]")).isEmpty());


        LOG.info("Копируем смс-код для подтверждения телефона");
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(cellFoneConformationInput));

        String currentHandle = driver.getWindowHandle();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String registrationUrl = "";

        try {
            registrationUrl =  JsonLoader.getData().get(STARTING_URL).get("REGISTRATION_URL").getValue();
        } catch (DataException e) {
            LOG.error(e.getMessage());
        }

        js.executeScript("registration_window = window.open('" + registrationUrl + "')");

        Set<String> windows = driver.getWindowHandles();
        windows.remove(currentHandle);
        String newWindow = windows.toArray()[0].toString();

        driver.switchTo().window(newWindow);

        String xpath = "//li/a[contains(.,'" + phone + "')]";
        WebElement numberSring = null;
        int x = 0;

        LOG.info("Пытаемся найти код подтверждения телефона");
        for(int y = 0; y < 3; y++) {
            try {
                new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
                numberSring = driver.findElements(By.xpath(xpath)).stream().filter(WebElement::isDisplayed).findFirst().get();
            } catch (Exception e) {
                driver.navigate().refresh();
            }
            x++;
            if (numberSring != null){break;}
        }

        if(numberSring != null && !numberSring.getText().isEmpty()) {
            String code = numberSring.getText().split(" - ")[1];
            driver.switchTo().window(currentHandle);
            js.executeScript("registration_window.close()");

            LOG.info("Вводим SMS-код::" + code);
            fillField(cellFoneConformationInput,code);
        }else {
            throw new AutotestError("Ошибка! SMS-код не найден.[" + x + "] раз обновили страницу [" + driver.getCurrentUrl() + "] не найдя номер[" +  phone + "]");
        }
        Stash.put("PHONE_NUMBER",phone);
        LOG.info("Сохранили номер телефона в память::" + phone + "[PHONE_NUMBER]");

    }
}

