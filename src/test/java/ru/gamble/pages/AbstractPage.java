package ru.gamble.pages;

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.ru.Когда;
import org.assertj.core.api.AssertionsForClassTypes;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.attributeContains;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.RANDOM;
import static ru.gamble.utility.Constants.STARTING_URL;
import static ru.gamble.utility.Generators.randomString;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;


public abstract class AbstractPage extends Page{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPage.class);

    public static By xpathListBets = xpath("//div[contains(@class,'coupon-bet') and not(contains(@class,'coupon-bet_offer'))]/ul");

    public static By preloaderOnPage = By.xpath("//div[contains(@class,'preloader__container')]");

    @ElementTitle("Вход")
    @FindBy(id = "log-in")
    private WebElement enterButton;

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

    @ElementTitle("Настройки")
    @FindBy(id = "preferences")
    protected WebElement preferences;

    @ElementTitle("Активация Быстрой ставки")
    @FindBy(xpath = "//div[@class='coupon__toggler']/label")
    protected WebElement quickButton;
    //для ставок экспресс, быстрой ставки - т.е. там где 1 поле для ставки


    @ElementTitle("Флаг активности быстрой ставки")
    @FindBy(xpath = "//div[@class='coupon__toggler']/input")
    protected WebElement quickBetFlag;

    @ElementTitle("Очистить всё")
    @FindBy(xpath = "//*[@class='btn btn_full-width' and normalize-space(text())='Очистить купон']")
    protected WebElement clearCoupon;

    protected By pathToclearCoupon = By.xpath("//*[@class='btn btn_full-width' and normalize-space(text())='Очистить купон']");

    @ElementTitle("Сервисное сообщение")
    @FindBy(xpath = "//div[contains(@class,'tech-msg active')]")
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

    @ElementTitle("Подвал")
    @FindBy (xpath = "//div[@class='footer__pin']")
    private WebElement podval;


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

    @ActionTitle("открывает/закрывает Мои пари")
    public void openMyBets(){
        PageFactory.getDriver().findElement(By.xpath("//span[@class='icon-svg__my-bets_selected']")).click();
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


    public void tryingLoadPage(By by, int count, int waitSeconds) {
        WebDriver driver = PageFactory.getWebDriver();
        LOG.info("Ищем элемент [" + by + "] на странице::" + driver.getCurrentUrl());

        for (int j = 0; j < count; j++) {
            try {
                new WebDriverWait(PageFactory.getDriver(), waitSeconds ).until(ExpectedConditions.visibilityOfElementLocated(by));
                break;
            } catch (Exception e) {
                driver.navigate().refresh();
            }
            if (j >= count - 1) {
                throw new AutotestError("Ошибка! Не нашли элемент после " + j + " попыток перезагрузки страницы");
            }
        }
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
        driver.get(url + "registration/email/verify?" + link);

        if(url.contains("mobile")){
           return;
        }else {
            LOG.info("Ожидаем диалогового окна с надписью 'Спасибо!'");
            Thread.sleep(5000);
            if (!driver.findElement(By.cssSelector("a.modal__closeBtn.closeBtn")).isDisplayed()) {
                Assert.fail("Ошибка! Не появилось диалоговое окно с надписью 'Спасибо!'");
            }
            LOG.info("Закрываем уведомление об успешном подтверждении почты");
            driver.findElement(By.cssSelector("a.modal__closeBtn.closeBtn")).click();
        }
    }

    /**
     * Открывает выпадающий список и выбирает оттуда пункт случайным образом
     *
     * @param element - поле где ждем выпадающий список.
     * @param select - выбираемый пункт меню.
     */
    protected void selectMenu(WebElement element, int select) {
        element.findElement(By.xpath("custom-select")).click();
        element.findElement(By.xpath("custom-select/div[2]/div[contains(.,'" + select + "')]")).click();
    }
    protected void selectMenu(WebElement element) {
        selectMenu(element, 0);
    }

    protected String enterDate(String value) {
        StringBuilder date = new StringBuilder();
        String day, month, year;
            String[] tmp = value.split("-");
            LOG.info("Вводим дату");
            selectMenu(fieldYear, Integer.parseInt(tmp[0]));
            selectMenu(fieldMonth, Integer.parseInt(tmp[1]));
            selectMenu(fieldDay, Integer.parseInt(tmp[2]));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        day = fieldDay.getAttribute("innerText");
        month = fieldMonth.getAttribute("innerText");
        year = fieldYear.getAttribute("innerText");
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
        WebDriver driver = PageFactory.getWebDriver();
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
                    .stream().filter(e -> e.isDisplayed() && !e.getAttribute("innerText").contains("-") && Double.parseDouble(e.getAttribute("innerText")) >= 1.260)
                    .limit(count + 10).collect(Collectors.toList());
            for (WebElement coefficient : correctMarkets) {
                tryToClick(coefficient);
                eventsInCoupon = PageFactory.getWebDriver().findElements(xpathListBets);
                        //PageFactory.getWebDriver().findElements(By.xpath("//li[@class='coupon-bet-list__item']"));
                LOG.info("коэф: " + coefficient.getAttribute("innerText"));
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
                            .stream().filter(e -> e.isDisplayed() && !e.getAttribute("innerText").contains("-") && Double.parseDouble(e.getAttribute("innerText")) < 1.25)
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
                eventsInCoupon = PageFactory.getWebDriver().findElements(xpathListBets);
                LOG.info("коэф: " + coefficient.getAttribute("innerText"));
                if (eventsInCoupon.size() == count) {
                    break;
                }
            }
        }
        WebDriverWait wait = new WebDriverWait(PageFactory.getWebDriver(),10);
        wait.withMessage("Попытались добавить " + count + " событий в купон. Но добавилось только " + getWebDriver().findElements(xpathListBets).size()
                + "\nВероятно, просто нет подходящих событий");
        wait.until(ExpectedConditions.numberOfElementsToBe(xpathListBets,count));
    }

    public void waitForElementPresent(final By by, int timeout) {
        WebDriverWait wait = (WebDriverWait) new WebDriverWait(PageFactory.getWebDriver(), timeout)
                .ignoring(StaleElementReferenceException.class);
        wait.withMessage("Элемент " + by + " так и не появился за " + timeout + " секунд");
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
        try {
            return element.isDisplayed();
        } catch (Exception e){
            return false;
        }
    }

    private boolean checkServiceMessage(String text) throws InterruptedException {
        int count = 0;
        while (count < 5) {
            try {
                serviceMessage.isDisplayed();
                MatcherAssert.assertThat(true, equalTo(serviceMessage.findElement(By.xpath("//div[contains(@class,'tech-msg__content')]")).getAttribute("innerText").equals(text)));
                return true;
            } catch (Exception e){
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
//        if (clearCoupon.isDisplayed()){
//            clearCoupon.click();
//        }

        if (!getWebDriver().findElements(pathToclearCoupon).isEmpty() && clearCoupon.isDisplayed()){
            clearCoupon.click();
        }
        WebDriverWait wait = new WebDriverWait(PageFactory.getWebDriver(),10);
        wait.withMessage("Очистить купон. Но остались события " + getWebDriver().findElements(xpathListBets).size());
        wait.until(ExpectedConditions.numberOfElementsToBe(xpathListBets,0));
    }

    protected void waitingForPreloaderToDisappear(int timeInSeconds){
        WebDriver driver = PageFactory.getWebDriver();
        try {
            new WebDriverWait(driver, timeInSeconds).until(ExpectedConditions.invisibilityOfElementLocated(xpath("//*[contains(@class,'preloader__container')]")));
        }catch (Exception e){
            throw new AutotestError("Ошибка! Прелоадер не исчез в течение [" + timeInSeconds + "] сек.");
        }
    }

    @ActionTitle("закрывает всплывающее окно 'Перейти в ЦУПИС'")
    public void closePopUpWindowGoToTSUPIS(){
        WebDriver driver = PageFactory.getWebDriver();
        String xpathGoToTSUPIS = "//div[contains (@class,'after-reg')]/a[contains(@class,'btn_important')]";
        try{
            LOG.info("Ждём появление всплывающего окна.");
            new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathGoToTSUPIS)));
            LOG.info("Появилось окно c кнопкой [" + driver.findElement(By.xpath(xpathGoToTSUPIS)).getAttribute("innerText") + "]");
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
        try {
            new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOf(cellFoneConformationInput));
        }catch (Exception e){
            e.getMessage();
            throw new AutotestError("Ошибка! Не появилось окно для ввода SMS");
        }

        String sQLRequest = "SELECT CODE FROM gamebet.`phoneconfirmationcode` WHERE  phone = '" + phone + "' ORDER BY creation_date DESC LIMIT 1";
		String code = CommonStepDefs.returnCode(sQLRequest);
		LOG.info("Вводим SMS-код [" + code + "]");
		fillField(cellFoneConformationInput,code);
        Stash.put("PHONE_NUMBER", phone) ;
        LOG.info("Сохранили в память key [PHONE_NUMBER] <== value [" + phone + "]");
    }

    protected void enterSellphoneForOrtax(String value, WebElement cellFoneInput, WebElement cellFoneConformationInput){
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

        //Начало кода для получения СМС
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

        String xpath = "//li/a[contains(text(),'" + phone + "')]";
        WebElement numberSring = null;
        int x = 0;

        LOG.info("Пытаемся найти код подтверждения телефона");
        for(int y = 0; y < 5; y++) {

            try {
                LOG.info("Ожидаем 2 сек. для сервера TEST_INT");
                Thread.sleep(2000);
                new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),'Статус регистрации пользователя')]")));
                if (driver.findElements(By.xpath(xpath)).isEmpty()){
                    driver.navigate().refresh();
                }
                else {
                    numberSring = driver.findElements(By.xpath(xpath)).get(0);
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
        }

        if(numberSring != null && !numberSring.getAttribute("innerText").isEmpty()) {
            String code = numberSring.getAttribute("innerText").split(" - ")[1];
            driver.switchTo().window(currentHandle);
            js.executeScript("registration_window.close()");

            try {
                new WebDriverWait(driver, 70).until(ExpectedConditions.visibilityOf(cellFoneConformationInput));
            }catch (Exception e){
                e.getMessage();
                throw new AutotestError("Ошибка! Не появилось окно для ввода SMS");
            }

            LOG.info("Вводим SMS-код::" + code);
            fillField(cellFoneConformationInput,code);

            Stash.put("PHONE_NUMBER",phone ) ;
            LOG.info("Сохранили в память key [PHONE_NUMBER] <== value [" + phone + "]");
        }else {
            throw new AutotestError("Ошибка! SMS-код не найден.[" + x + "] раз обновили страницу [" + driver.getCurrentUrl() + "] не найдя номер[" +  phone + "]");
        }

    }


    @ActionTitle("нажимает в поле ввода")
    public void clickInputField(String inputFieldName) {
        pressButtonAP(inputFieldName);
    }

    /**
     * В выбранном поле вводит один символ и если появляется выпадающий список - выбирает первый пункт из него.
     * Иначе либо заново вводит символ и ждет список, либо заполняет поле рандомной последовательностью
     *
     * @param field    - поле, которое заполняем
     * @param authFill - булев параметр, говорит о том обязательно ли выбирать из списка (true), или можно заполнить рандомом (false)
     */
    public void fillAddressForMobile(WebElement field, boolean authFill) {
        WebDriver driver = PageFactory.getWebDriver();
        List<WebElement> list;
        String xpath = "//div[@class='form-input-menu__item']";
        int count = 10;
        do {
            field.clear();
            StringBuilder n = new StringBuilder();
            n.append((char) ('А' + new Random().nextInt(32)));
            field.sendKeys(n);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                list = field.findElements(By.xpath(xpath)).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
                count--;
            if(count<=0){ break;}
        } while (list.isEmpty() && authFill);

        if (field.findElements(By.xpath(xpath)).isEmpty()) {
            field.clear();
            field.sendKeys(randomString(20));
        } else
            field.findElements(By.xpath(xpath)).get(new Random().nextInt(list.size())).click();

        if (field.getAttribute("value").length() == 0) {
            LOG.info("ОШИБКА. Нажали на пункт в выпадающем списке для города,а знчение не выбралось!!");
            field.sendKeys(randomString(20));
        }
    }

    /**
     * сворачивание всех видов спорта
     */
    public void closeSports(){
        WebDriver driver = PageFactory.getDriver();
        if (driver.findElement(By.id("sports-toggler-opened")).isDisplayed()){
            driver.findElement(By.id("sports-toggler-opened")).click();
        }else if (driver.findElement(By.id("sports-toggler")).isDisplayed()){
            driver.findElement(By.id("sports-toggler")).click();
        }
    }

    /**
     * сворачивание или разворачивание левого меню
     * false - свернуть
     * true - развернуть
     */
    public static void setExpandCollapseMenusButton(boolean collapsOrNot){
        WebDriver driver = PageFactory.getDriver();
        WebDriverWait wait =  new WebDriverWait(driver,10);
        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (menu.getAttribute("class").contains("collapsed")!=collapsOrNot){
            menu.click();
            CommonStepDefs.workWithPreloader();
            if (!driver.findElements(preloaderOnPage).isEmpty()){
                driver.navigate().refresh();
                CommonStepDefs.workWithPreloader();
            }
        }

        if (collapsOrNot) {
            wait.withMessage("Не удалось развернуть левое меню");
            wait.until(attributeContains(By.id("menu-toggler"), "class", "collapsed"));
        }
        else {
            wait.withMessage("Не удалось свернуть левое меню");
            wait.until(ExpectedConditions.not(attributeContains(By.id("menu-toggler"), "class", "collapsed")));
        }
    }

    @ActionTitle("при необходимости логинимся в 'Первый ЦУПИС'")
    public void loginInTSUPIS(){
        WebDriver driver = PageFactory.getDriver();
        goToThisPage("23bet-pay");

        String phone = "", password = "";
        try {
            Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
            String browserName = caps.getBrowserName();
            phone = browserName.contains("chrome") ?
                    JsonLoader.getData().get(STARTING_URL).get("PHONE").getValue() :
                    JsonLoader.getData().get(STARTING_URL).get("PHONE_FIREFOX").getValue();
            password = JsonLoader.getData().get(STARTING_URL).get("PASSWORD").getValue();

            LOG.info("Пытаемся найти поле для ввода номера телефона по id[form_login_phone]");
            new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("form_login_phone")));

        }catch (Exception e){
         return;
        }
        LOG.info("Перешли на страницу [" + driver.getCurrentUrl() + "]");

        WebElement inputPhone = driver.findElement(By.id("form_login_phone"));
        WebElement inputPassword = driver.findElement(By.id("form_login_password"));

        slowFillField(inputPhone, phone, 250);
        String actual = inputPhone.getAttribute("value").replaceAll("\\D","");
        AssertionsForClassTypes.assertThat(actual).as("ОШИБКА! Вводимый номер [" + phone + "] не соответсвует [" + actual + "]").contains(phone);
        LOG.info("В поле [Телефон] ввели [" + actual  +"]");

        slowFillField(inputPassword, password, 250);
        LOG.info("В поле [Пароль] ввели [" + password  +"]");

        WebElement buttonEnter = driver.findElement(By.id("btn_authorization_enter"));
        LOG.info("Нажимаем кнопку [Войти]");
        buttonEnter.click();


    }

    protected void goToThisPage(String peaceURL){
        WebDriver driver = PageFactory.getWebDriver();
        Set<String> windows = driver.getWindowHandles();
        for(String windowHandle: windows) {
            driver.switchTo().window(windowHandle);
            if (driver.getCurrentUrl().contains(peaceURL)) {
                break;
            }
        }
    }

    /**
     * Метод ввода по символу с задержкой чтобы JS-маски успевали обрабатывать
     * @inputField поле ввода
     * @text текст, который нужно ввести
     * @delay задержка между вводом каждого символа в миллисекундах
     */
    protected void slowFillField(WebElement inputField, String text, int delay){
        inputField.clear();
        char[] tmp = text.toCharArray();
        for(int i = 0; i < tmp.length; i++) {
            inputField.sendKeys(String.valueOf(tmp[i]));
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @ActionTitle("завершает регу в Евросети с")
    public void EuroReg(DataTable dataTable){
        LOG.info("sdf");
        Map<String,String> table = dataTable.asMap(String.class, String.class);
        WebDriver driver = Stash.getValue("driver");
        String key = null;
        String value = null;
        String tagField = null;
        StringBuilder asd = new StringBuilder();
        WebElement field;
        int aa = 0;
        for (Map.Entry<String, String> entry : table.entrySet()) {
            key=entry.getKey();
            value = entry.getValue();
            if (value.matches("[A-Z]*")){
                value=Stash.getValue(value);
            }

            if (driver.findElements(By.xpath("//*[contains(text(),'"+key+"')]/following-sibling::*")).isEmpty()){
                continue;
            }
            field = driver.findElements(By.xpath("//*[contains(text(),'"+key+"')]/following-sibling::*")).get(0);
            tagField = field.getTagName();
            asd.append(tagField);

            if (tagField.equals("input")) {
                field.clear();
                field.sendKeys(value);
            } else if (tagField.equals("select")) {
                Select select = new Select(field);
                select.selectByVisibleText(value);
            }
        }
        driver.findElement(By.xpath("//*[contains(@value,'"+table.get("button")+"')]")).click();
        value = table.get("textis");
        new WebDriverWait(driver,10).until(attributeContains(By.xpath("//body"),"innerText",value));
        driver.close();
        driver.quit();
    }


    @ActionTitle("запоминает смс-код для подтверждения вывода средств")
    public void rememberSMSforWithdraw(String keyPhone, String keyCode){
        WebDriver driver = Stash.getValue("driver");
        LOG.info("Ищем и запоминаем смс-код подтверждения вывода");
        TestingServicePage.userSearchesForLastSentSMSByNumberAndRemembersIn(keyPhone,keyCode,driver);
        driver.close();
        driver.quit();
    }
}

