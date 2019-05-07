package ru.gamble.stepdefs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.*;
import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Когда;
import io.qameta.allure.Allure;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.pages.prematchPages.EventViewerPage;
import ru.gamble.utility.DBUtils;
import ru.gamble.utility.Generators;
import ru.gamble.utility.JsonLoader;
import ru.gamble.utility.NaiveSSLContext;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.sbtqa.tag.stepdefs.GenericStepDefs;

import javax.net.ssl.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.By.xpath;
import static ru.gamble.pages.AbstractPage.preloaderOnPage;
import static ru.gamble.pages.mainPages.FooterPage.opensNewTabAndChecksPresenceOFElement;
import static ru.gamble.pages.prematchPages.EventViewerPage.onTriggerPeriod;
import static ru.gamble.pages.userProfilePages.FavouritePage.clearFavouriteGames;
import static ru.gamble.utility.Constants.*;
import static ru.gamble.utility.Generators.generateDateForGard;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;


public class CommonStepDefs extends GenericStepDefs {
    private static final Logger LOG = LoggerFactory.getLogger(CommonStepDefs.class);
    static WebDriver driver = PageFactory.getDriver();
    private static final String sep = File.separator;
//private static StringBuilder tmp=new StringBuilder();

    public static String getSMSCode(String phone){
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
            return code;
        }else {
            throw new AutotestError("Ошибка! SMS-код не найден.[" + x + "] раз обновили страницу [" + driver.getCurrentUrl() + "] не найдя номер[" +  phone + "]");
        }
    }

    @Когда("^ждем некоторое время \"([^\"]*)\"$")
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

    @Когда("^запрашиваем дату-время и сохраняем в память$")
    public static void requestAndSaveToMamory(DataTable dataTable) {
        List<String> data = dataTable.asList(String.class);
        String key, value, date;
        key = data.get(0);
        value = data.get(1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (value.equals("Current")) {
            date = formatter.format(System.currentTimeMillis());
            Stash.put(key, date);
            LOG.info(key + "<==[" + date + "]");
        }
    }

    @Когда("^сохраняем в память$")
    public static void saveValueToKey(DataTable dataTable) {
        List<String> data = dataTable.asList(String.class);
        String key, value;
        key = data.get(0);
        value = data.get(1);

        if (value.equals("skypeLoginGenerate")) {
            value = "skype" + Stash.getValue("PHONE");
        }

        if (value.equals(DEFAULT)) {
            try {
                value = JsonLoader.getData().get(STARTING_URL).get(key).getValue();
            } catch (DataException e) {
                e.getMessage();
            }
        }
        if (value.contains(RANDOME_NUMBER)) {
            StringBuilder result = new StringBuilder();
            int count = Integer.valueOf(value.replace(RANDOME_NUMBER, "").trim());
            for (int i = 0; i < count; i++) {
                result.append((char) ('1' + new Random().nextInt(8)));
            }
            value = result.toString();
        }

        if (value.equals(RANDOME_EMAIL)) {
            value = "testregistrator+" + Stash.getValue("PHONE") + "@inbox.ru";
        }

        if (value.split(" ")[0].equals(RANDOM)) {
            int count = value.contains(" ")?Integer.valueOf(value.replace(RANDOM, "").trim()):25;
            value = Generators.randomString(count);
        }

        if (value.equals(RANDOMHEX)) {
            value = Generators.randomStringHex();
        }

        if (value.equals(RANDOMDATE)) {
            int day = (int) (Math.floor(1 + Math.random() * 27));
            int mon = (int) (Math.floor(1 + Math.random() * 11));
            int year = (int) (Math.floor(1950 + Math.random() * 49));
            String mons;
            if (mon < 10) {
                mons = "0" + Integer.toString(mon);
            } else
                mons = Integer.toString(mon);
            value = Integer.toString(day) + "." + mons + "." + Integer.toString(year);
        }

        if (value.equals(RANDOME_PHONE)) {
            value = "70" + Generators.randomNumber(9);
        }

        if (value.equals(RANDOME_SEX)) {
            value = Generators.randomGender();
        }

        Stash.put(key, value);
        LOG.info("key::[" + key + "] value::[" + value + "]");
    }

    // Метод ожидания появления и изчезновения прелоадера при методе click()
    public static void workWithPreloader() {
        waitShowElement(preloaderOnPage);
    }

    // Ожидание появления элемента на странице
    public static void waitShowElement(By by) {
        WebDriverWait driverWait = new WebDriverWait(driver, 6, 500);
        try {
            driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
            List<WebElement> preloaders = driver.findElements(by);
            LOG.info("Найдено прелоадеров [" + preloaders.size() + "]");
            driverWait.until(ExpectedConditions.invisibilityOfAllElements(preloaders));
            LOG.info("Прелоадеры закрылись");
        } catch (Exception e) {
        }
    }

    @Когда("^разлогиниваем пользователя$")
    public void logOut() throws AWTException {
        LOG.info("Переход на главную страницу");
        goToMainPage("site");
        cleanCookies();
        descktopSiteLogOut(driver);

    }


    private void descktopSiteLogOut(WebDriver driver){
        WebDriverWait wait = new WebDriverWait(driver,20);
        LOG.info("Ищем наличие кнопки с силуетом пользователя.");
        List<WebElement> userMan = driver.findElements(By.id("user-icon"));
        List<WebElement> continueRegistartion = driver.findElements(By.id("continue-registration"));//возможно на сате залогинен пользователь , не окончивший регистрацию. тогда балнса и икони у него не будет,  абудт кнопка "продолжить регу"
        if (!userMan.isEmpty()){
            LOG.info("Нажимаем на кнопку с силуетом пользователя.");
            userMan.get(0).click();
            wait
                    .withMessage("Нажали на значок пользователя, но не появилась кнопка для выхода")
                    .until(ExpectedConditions.attributeContains(By.xpath("//div[contains(@class,'subMenuArea user-menu')]"),"class","active"));
            LOG.info("Ищем кнопку 'Выход' и нажимаем");
            int indexX = driver.findElement(By.id("log-out-button")).getLocation().getX() + driver.findElement(By.id("log-out-button")).getSize().getWidth()/2;
            int indexY = driver.findElement(By.id("log-out-button")).getLocation().getY() + driver.findElement(By.id("log-out-button")).getSize().getHeight()/2;
//            new Actions(driver).moveByOffset(indexX,indexY).click().build().perform();
            try {
                Robot r = new Robot();
                r.mouseMove(indexX,indexY);
                r.mousePress(InputEvent.BUTTON1_MASK);
                r.mouseRelease(InputEvent.BUTTON1_MASK);
            } catch (AWTException e) {
                e.printStackTrace();
            }


           // LOG.info("COORDINATE:" + MouseInfo.getPointerInfo().getLocation() + driver.findElement(By.id("log-out-button")).getLocation());
         //   new Actions(driver).moveToElement(driver.findElement(By.id("log-out-button")),10,0).click().build().perform();

//            driver.findElement(By.id("log-out-button")).click();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            driver.navigate().refresh();
        LOG.info("Обновили страницу на всякий случай");
            wait
                    .withMessage("Разлогинивали-разлогинивали, да не ралогинили. На сайте все еще кто-то авторизован")
                    .until(ExpectedConditions.numberOfElementsToBe(By.id("user-icon"),0));
            wait
                    .withMessage("Разлогинивали-разлогинивали, да не ралогинили. На сайте все еще отображается баланс")
                    .until(ExpectedConditions.numberOfElementsToBe(By.id("topPanelWalletBalance"),0));
        }
        else if (!continueRegistartion.isEmpty()){
            driver.findElement(By.id("terminate-registration-logout-button")).click();
            wait
                    .withMessage("Разлогинивали-разлогинивали, да не ралогинили. На сайте все еще кто-то авторизован")
                    .until(ExpectedConditions.numberOfElementsToBe(By.id("continue-registration"),0));
        }
        else{
            LOG.info("На сайте никто не авторизован");
        }
    }

    private void mobileSiteLogOut(WebDriver driver) {
        try {
            LOG.info("Ищем наличие ссылки депозита.");
            new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/private/balance/deposit']")));
            WebElement menuSwitchButton = driver.findElement(By.xpath("//label[@class='header__button header__menu-switch']"));
            LOG.info("Нажимаем на кнопку 'MenuSwitch'");
            menuSwitchButton.click();
            Thread.sleep(500);
            LOG.info("Ищем кнопку 'Выход' нажимаем и обновляем страницу");
            driver.findElement(By.xpath("//span[contains(.,'Выход')]")).click();
            driver.navigate().refresh();
        } catch (Exception e) {
            LOG.info("На сайте никто не авторизован");
        }
    }


    // Метод перехода на главную страницу
    @Когда("^переходит на главную страницу$")
    public static void goToMainPage() {
        goToMainPage("site");
    }

    @Когда("^переходит в админку$")
    public static void goToAdminPage() {
        goToMainPage("admin");
    }


    /**
     * Метод параметризованного перехода на страницу по siteUrl
     * или по-умолчанию указанную в файле application.properties
     *
     * @param siteUrl - URL страницы
     */
    @Когда("^переходит на страницу '(.+)'$")
    public static void goToMainPage(String siteUrl) {
        String currentUrl;
        try {
            switch (siteUrl) {
                case "site":
                    currentUrl = Stash.getValue("MAIN_URL");
                    break;
                case "admin":
                    currentUrl = JsonLoader.getData().get(STARTING_URL).get("ADMIN_URL").getValue();
                    break;
                case "registr":
                    currentUrl = JsonLoader.getData().get(STARTING_URL).get("REGISTRATION_URL").getValue();
                    break;
                default:
                    currentUrl = siteUrl;
                    break;
            }
            driver.get(currentUrl);
            LOG.info("Перешли на страницу [" + currentUrl + "]");
        } catch (DataException e) {
            LOG.error(e.getMessage());
        }

    }

    /**
     * Генератор e-mail
     *
     * @param key - ключ по которому сохраняем е-mail в памяти.
     */
    @Когда("^генерим email в \"([^\"]*)\"$")
    public static void generateEmailAndSave(String key) {
        String value = "testregistrator+" + System.currentTimeMillis() + "@inbox.ru";
        LOG.info("Сохраняем в память key[" + key + "] <== value[" + value + "]");
        Stash.put(key, value);
    }

    @Когда("^определяем \"([^\"]*)\" пользователя \"([^\"]*)\"$")
    public static void getUserID(String key, String keyEmail) {
        String sqlRequest = "SELECT id FROM gamebet. `user` WHERE email='" + Stash.getValue(keyEmail) + "'";
        String id = workWithDBgetResult(sqlRequest, "id");
        Stash.put(key, id);
    }


    @Когда("^подтверждаем видеорегистрацию \"([^\"]*)\"$")
    public void confirmVidochat(String param) {
        String sqlRequest = "UPDATE gamebet.`user` SET personality_confirmed = TRUE, registration_stage_id = 19 WHERE `email` = '" + Stash.getValue(param) + "'";
        workWithDB(sqlRequest);
        LOG.info("Подтвердили видеорегистрацию");

    }

    @Когда("^подтверждаем от ЦУПИС \"([^\"]*)\"$")
    public static void confirmTSUPIS(String param) {
        String sqlRequest = "UPDATE gamebet.`user` SET registered_in_tsupis = TRUE, identified_in_tsupis = TRUE, identState = 1 WHERE `email` ='" + Stash.getValue(param) + "'";
        workWithDB(sqlRequest);
        LOG.info("Подтвердили регистрацию от ЦУПИС");

    }

    private static void workWithDB(String sqlRequest) {
        Connection con = DBUtils.getConnection();

        PreparedStatement ps = null;
        int rs;
        try {
            con.setAutoCommit(false);
            ps = con.prepareStatement(sqlRequest);
            rs = ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(con, ps, null);
        }
    }


    /**
     * Инициализируйте страницу с соответствующим заголовком (определенным через
     * {@link ru.sbtqa.tag.pagefactory.annotations.PageEntry} аннотация)
     * Пользователь|Он ключевые слова являются необязательными
     *
     * @param title название страницы для инициализации
     * @throws PageInitializationException при неудачной инициализации страницы
     */
    public void openPage(String title) throws PageInitializationException {
        for (String windowHandle : driver.getWindowHandles()) {
            driver.switchTo().window(windowHandle);
        }
        PageFactory.getInstance().getPage(title);
    }


    /**
     * ожидание пока аттрибут без учета регистра будет содержать подстроку
     *
     * @param locator
     * @param attribute
     * @param value
     * @return
     */
    public static ExpectedCondition<Boolean> attributeContainsLowerCase(final By locator,
                                                                        final String attribute,
                                                                        final String value) {
        return new ExpectedCondition<Boolean>() {
            private String currentValue = "";

            @Override
            public Boolean apply(WebDriver driver) {
                return driver.findElement(locator).getAttribute(attribute).toLowerCase().contains(value.toLowerCase());
            }

            @Override
            public String toString() {
                return String.format("value to contain \"%s\". Current value: \"%s\"", value, currentValue);
            }
        };
    }


    public static ExpectedCondition<Boolean> attributeContainsLowerCase(final WebElement element,
                                                                        final String attribute,
                                                                        final String value) {
        return new ExpectedCondition<Boolean>() {
            private String currentValue = "";

            @Override
            public Boolean apply(WebDriver driver) {
                return element.getAttribute(attribute).toLowerCase().contains(value.toLowerCase());
            }

            @Override
            public String toString() {
                return String.format("value to contain \"%s\". Current value: \"%s\"", value, currentValue);
            }
        };
    }


    public static void waitOfPreloader() {
        waitOfPreloader(60);
    }

    public static void waitOfPreloader(int num) {
        LOG.debug("Проверка на наличие бесконечных прелоадеров");
        List<WebElement> list = driver.findElements(By.cssSelector("div.preloader__container"));
        int count = num;
        try {
            do {
                //todo del . soup
                System.out.println(count);
                LOG.debug("List size is " + list.size());
                for (WebElement preloader : list) {
                    if (preloader.isDisplayed()) {
                        LOG.debug("Данный прелоадер виден");
                        count--;
                        Thread.sleep(500);
                        count--;
                        continue;
                    } else {
                        LOG.debug("Данный прелоадер не виден");
                        list.remove(preloader);
                    }
                    if (list.isEmpty()) {
                        LOG.debug("List is empty");
                    }
                    break;
                }
            } while (!list.isEmpty() && count > 0);
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            LOG.error("" + e);
        } catch (InterruptedException ie) {
            ie.getMessage();
        }
        if (count <= 0) {
            LOG.error("Количество попыток исчерпано. Прелоадер всё ещё виден");
            throw new AssertionError();
        }
        LOG.debug("Проверка успешно выполнена");
    }


    /**
     * Провкрутка страницы на х и y
     *
     * @param x прокрутка по горизонтали
     * @param y прокрутка по вертикали
     */
    public static void scrollPage(int x, int y) {
        ((JavascriptExecutor) driver).executeScript("window.scroll(" + x + ","
                + y + ");");
    }

    /**
     * Преобразовывает название игры к виду "team1 - team2".
     *
     * @param oldName - название игры, которое удем преобразовывать
     */
    public static String stringParse(String oldName) {
        String nameGame;
        Pattern p = Pattern.compile("(?u)[^а-яА-Я0-9a-zA-Z]");
        Matcher m = p.matcher(oldName);
        nameGame = m.replaceAll("");
        return nameGame;
    }


    /**
     * проверка что из Ближвйших трансляци переход на правильную игру
     * сравнивает на совпадение название спорта, команд и првоеряет есть ли видео если страница Лайв
     *
     * @return - возвращет true если все ОК, и false если что-то не совпадает с ожиданиями
     */
    public void checkLinkToGame() {

        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.id("menu-toggler")));
        workWithPreloader();
        boolean flag = true;
        boolean haveButton = Stash.getValue("haveButtonKey");
        String team1 = Stash.getValue("team1BTkey");
        String team2 = Stash.getValue("team2BTkey");
        String sportName = Stash.getValue("sportKey");

        if (haveButton) {
//            String sportis = driver.findElement(By.xpath("//div[@class='live-game-summary']/div[1]/div[1]/div[1]/div[contains(@class,'game-info')]")).getAttribute("class").replace("game-info game-info_", "");
            String sportis = driver.getCurrentUrl().split("sport=")[1].split("&")[0];
            String team1name = driver.findElement(By.xpath("//div[@class='live-game-summary']//div[contains(@class,'game-info')]/ng-include[1]//div[contains(@class,'team-1')]//p")).getAttribute("title").trim();
            String team2name = driver.findElement(By.xpath("//div[@class='live-game-summary']//div[contains(@class,'game-info')]/ng-include[1]//div[contains(@class,'team-2')]//p")).getAttribute("title").trim();
            LOG.info("Перешли на игру. Ее название в линии: " + team1name + " - " + team2name + ". Спорт: " + sportis);
            if (!team1.equals(team1name) || !team2.equals(team2name)) {
                Assertions.fail("Из Ближайших трансляций переход на неправильную игру. Вместо " + team1 + " " + team2 + "перешли на " + team1name + " " + team2name);
            }
            if (!(sportName.toLowerCase()).equals(sportis.toLowerCase())) {
                Assertions.fail("Из Ближайших трансляций переход на неправильный спорт. Игра " + stringParse(team1 + team2) + "Вместо " + sportName.toLowerCase() + " перешли в " + sportis.toLowerCase());
            }
            if (driver.findElement(By.xpath("//li[contains(@class,'left-menu__list-item-games') and contains(@class,'active')]//div[contains(@class,'icon icon-video-tv')]")).getAttribute("class").contains("js-hide")) {
                Assertions.fail("Для игры, у который в виджете Блжайшие трансляции есть кнопка %смотреть% не оказалось видео. Игра " + stringParse(team1 + team2));
            }
            LOG.info("У игры, у которой на виджете БТ есть кнопка Смотреть действительно есть видео. Проверка Успешна");
        } else {
            String gameName = driver.findElement(By.xpath("//div[contains(@class,'game-center-container__prematch-title')]")).getAttribute("innerText");
            LOG.info("Перешли на игру. Ее название в линии: " + gameName);
            if (!stringParse(gameName).equals(stringParse(team1 + team2))) {
                Assertions.fail("Из виджета переход на неправильную игру. Вместо " + stringParse(team1 + team2) + "перешли на " + stringParse(gameName));
            }
            LOG.info("Название игры в линии совпадает с тем, что ыбло на виджете БТ. Переход прошел успешно");
        }
    }

    @Когда("^(?:пользователь |он |)(?:осуществляет переход в) \"([^\"]*)\"$")
    public void changeFocusOnPage(String title) throws PageInitializationException {
        super.openPage(title);
    }


    public static void addStash(String key, String value) {
        List<String> values = new ArrayList<>();
        if (Stash.asMap().containsKey(key)) {
            values = Stash.getValue(key);
            values.add(value);
            Stash.asMap().replace(key, values);
        } else {
            values.add(value);
            Stash.put(key, values);
        }
    }

    /**
     * Проверка что при нажатии на ссылку открывается нужная страница. Проверка идет по url, причем эти url очищаются от всех символов, кроме букв и цифр. т.е. слеши собого значения тут не имеют
     *
     * @param element - на какой элемент жмакать чтобы открылась ссылка
     * @param pattern - ссылка или ее часть, которая должна открыться
     * @return true - если все ок.
     */

    public static boolean goLink(WebElement element, String pattern) {
        boolean flag = true;
        LOG.info("Проверяем что откроется правильная ссылка " + pattern);
        pattern = stringParse(pattern);
        int CountWind = driver.getWindowHandles().size();
        if (element.findElements(xpath("ancestor-or-self::*[@target='_blank']")).isEmpty()) {

            ((JavascriptExecutor) driver)//открываем ссылку в новой вкладке
                    .executeScript("window.open(arguments[0])", element);
        } else element.click();
        CommonStepDefs.workWithPreloader();
        driver.switchTo().window(driver.getWindowHandles().toArray()[driver.getWindowHandles().size() - 1].toString());
        if ((CountWind + 1) != driver.getWindowHandles().size()) {
            LOG.error("Не открылась ссылка");
            return false;
        }
        LOG.info("Ссылка открылась");
        driver.switchTo().window(driver.getWindowHandles().toArray()[CountWind].toString());
        String siteUrl = stringParse(driver.getCurrentUrl());
        if (!siteUrl.contains(pattern)) {
            flag = false;
            LOG.error("Ссылка открылась, но не то, что надо. Вместо " + pattern + " открылось " + siteUrl);
        }
        driver.close();
        driver.switchTo().window(driver.getWindowHandles().toArray()[CountWind - 1].toString()); //мы знаем что поле открытия ссылки на скачивание количесвто ссылок будет на  больше, незачем переопрелеть CountWind.
        return flag;
    }

    @Когда("^(пользователь |он) очищает cookies$")
    public static void cleanCookies() {
        try {
            if (driver.manage().getCookies().size() > 0) {
                LOG.info("Удаляем Cookies");
                driver.manage().deleteAllCookies();
            }
        } catch (Exception e) {
            LOG.error("Cookies не было!");
        }
    }

    /**
     * прелоадер должен обязательно появиться, если его не было - значит способ пополнения как бы и не выбран. поэтому эта ункция ждет чтобы прелоадер точно был,
     * но чтобы был не бесконечен
     */
    public static void waitToPreloader() {
        int count = 20;
        try {
            while (count > 0) {
                if (driver.findElement(By.cssSelector("div.preloader__container")).isDisplayed()) {
                    waitOfPreloader();
                    break;
                }
                Thread.sleep(500);
                count--;
                if (count == 0) {
                    Assertions.fail("Прелоадер так и не появился!");
                }
            }
        } catch (Exception e) {
            LOG.error("" + e);
        }
    }

    /**
     * открытие новой вкладки по адресу URl из входного параметра
     *
     * @param newUrl - URl, который нужноввести в этой новой вкладке
     */
    public static void newWindow(String newUrl) {
        Set<String> currentHandles = driver.getWindowHandles();
        ((ChromeDriver) driver).executeScript("window.open()");
        Set<String> windows = driver.getWindowHandles();
        windows.removeAll(currentHandles);
        String newWindow = windows.toArray()[0].toString();
        driver.switchTo().window(newWindow);
        driver.get(newUrl);
        new WebDriverWait(driver, 10).until(ExpectedConditions.urlToBe(newUrl));
    }

    /**
     * функиця, которая ждет пока элмент станет доступным. ждет, но не кликает
     *
     * @param element
     * @throws Exception
     */
    public static void waitEnabled(WebElement element) {
        int count = 20;
        try {
            while (count > 0) {
                if (element.isEnabled()) break;
                Thread.sleep(500);
                count--;
                if (count == 0) {
                    Assertions.fail("За 10 секунд элемент " + element + " так и не стал доступным");
                }
            }
        } catch (StaleElementReferenceException | InterruptedException e) {
            LOG.error("" + e);
        }
    }

    @Когда("^проверка ответа API из \"([^\"]*)\":$")
    public void checkresponceAPI(String keyStash, DataTable dataTable) {
        Map<String, String> table = dataTable.asMap(String.class, String.class);
        String actual = JSONValue.toJSONString(Stash.getValue(keyStash)).replaceAll(" ","");
        String expected = table.get("exepted").replaceAll(" ","");
        assertThat(actual).as("ОШИБКА! Ожидался ответ |" + expected + "| в |" + actual + "|").contains(expected);
        LOG.info("|" + expected + "| содержится в |" + actual + "|");
    }

    @Когда("^проверка ответа API из \"([^\"]*)\", значение берем из памяти$")
    public void checkresponceAPI2(String keyStash, DataTable dataTable) {
        Map<String, String> table = dataTable.asMap(String.class, String.class);
        String actual = JSONValue.toJSONString(Stash.getValue(keyStash));
        String expected = Stash.getValue(table.get("exepted"));
        assertThat(actual).as("ОШИБКА! Ожидался ответ |" + expected + "| в |" + actual + "|").contains(expected);
        LOG.info("|" + expected + "| содержится в |" + actual + "|");
    }


    @Когда("^выбираем одну дату из \"([^\"]*)\" и сохраняем в \"([^\"]*)\" а id_user в \"([^\"]*)\"$")
    public void selectOneDateInResponce(String keyResponce, String keyDate, String keyId) throws ParseException {
        SimpleDateFormat oldFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        String actual = JSONValue.toJSONString(Stash.getValue(keyResponce));
        actual = actual.replace("{\"code\":0,\"data\":", "").replace("}", "");
        String[] linesResponce = actual.split("swarmUserId");
        int i = 1 + new Random().nextInt(linesResponce.length - 1);
        String idUser = actual.split("swarmUserId")[i].replace("\":", "").split(",")[0];
        String dateForUser = null;
        if (actual.contains("videoIdentDate")) {
            dateForUser = actual.split("videoIdentDate")[i].replace("\":", "").split(",")[0].replaceAll("\"", "");
            oldFormat = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
            newFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        }
        if (actual.contains("skypeSendDate")) {
            dateForUser = actual.split("skypeSendDate")[i].replace("\":", "").split(",")[0].replaceAll("\"", "");
            oldFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");
            newFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        }

        LOG.info("Выбранная дата: " + dateForUser + ", для юзера с id = " + idUser);

        LOG.info("Отнимем от даты одну минуту");
        Calendar newDateTime = new GregorianCalendar();

        newDateTime.setTime(oldFormat.parse(dateForUser));
        newDateTime.add(Calendar.MINUTE, -1);
        LOG.info("Теперь переведем дату в нужны формат");
        dateForUser = newFormat.format(newDateTime.getTime()).replace(" ", "T") + ":00";

        Stash.put(keyDate, dateForUser);
        Stash.put(keyId, idUser);

        LOG.info("Новая дата: " + dateForUser);
    }


    @Когда("^проверка что в ответе \"([^\"]*)\" нет юзера с \"([^\"]*)\"$")
    public void checkResponceNotConains(String keyResponce, String keyId) {
        String actual = JSONValue.toJSONString(Stash.getValue(keyResponce));
        String userId = Stash.getValue(keyId);
        Assert.assertFalse("В ответе есть пользователь " + userId + ", хотя он не вписывается в заданные ts и ts_end:" + Stash.getValue("PARAMS"),
                actual.contains("\"swarmUserId\":" + userId));
        LOG.info("В ответе действительно теперь нет записи о пользователе с id=" + userId);
    }

    @Когда("^проверка что в ответе \"([^\"]*)\" верные даты  \"([^\"]*)\":$")
    public void checkResponceAPIgoodDate(String keyStash, String keyParams) throws ParseException {
        String actual = JSONValue.toJSONString(Stash.getValue(keyStash));
        String params = Stash.getValue(keyParams).toString();
        SimpleDateFormat formatTS = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat formatResponse = new SimpleDateFormat("dd.MM.yyy hh:mm");
        String ts = params.split("ts=")[1].substring(0, 16).replace("T", " ");
        Date tsDate = formatTS.parse(ts);
        Date tsEndDate = new Date();
        String ts_end = null;
        if (params.contains("ts_end")) {
            ts_end = params.split("ts_end=")[1].substring(0, 16).replace("T", " ");
            tsEndDate = formatTS.parse(ts_end);
        }

        String dateInResponceString = new String();

        Date dateInResponce = new Date();
        boolean a;
        for (int i = 1; i < actual.split("\"skypeSendDate" + "\":").length; i++) {
            dateInResponceString = actual.split("\"skypeSendDate" + "\":")[i].split("}")[0].replaceAll("\"", "");
            dateInResponce = formatResponse.parse(dateInResponceString);
            Assert.assertTrue("В ответе есть результаты, выходящие за пределы ts-ts_end (" + ts + "   ---   " + ts_end + ")"
                            + ":\n" + dateInResponceString,
                    dateInResponce.after(tsDate) && dateInResponce.before(tsEndDate));
        }
        LOG.info("Да, все результаты запроса вписываются по времени в значения ts и ts_end");
    }

    @Когда("^проверка что ответ \"([^\"]*)\" \"([^\"]*)\"$")
    public void checkResponceFill(String keyStash, String isEmpty) throws ParseException {
        String actual = JSONValue.toJSONString(Stash.getValue(keyStash));
        actual = actual.replace("{\"code\":0,\"data\":", "").replace("}", "");
        boolean expectedEmpty = isEmpty.equals("пустой");
        Assert.assertTrue("Ожидалось что результат будет " + isEmpty + ", но это не так.\n" + actual,
                actual.equals("[]") == expectedEmpty);
        LOG.info("Да, RESPONCE действительно " + isEmpty);
    }


    @Когда("^проверка вариантного ответа API из \"([^\"]*)\":$")
    public void checkresponceAPIor(String keyStash, DataTable dataTable) {
        Map<String, String> table = dataTable.asMap(String.class, String.class);
        String actual = JSONValue.toJSONString(Stash.getValue(keyStash));
        String expected = table.get("exepted");
        boolean actualsOk = actual.contains(expected.split("or")[0].trim()) || actual.contains(expected.split("or")[1].trim());
        Assert.assertTrue("ОШИБКА! Ожидался ответ |" + expected + "| в |" + actual + "|", actualsOk);
        LOG.info("|" + expected + "| содержится в |" + actual + "|");
    }

    @Когда("^проверим что время \"([^\"]*)\" уменьшилось в \"([^\"]*)\"$")
    public void checkTimebecomeLes(String keyTime, String keyResponse){
        int oldTime = Stash.getValue(keyTime);
        fingingAndSave(keyTime,keyResponse);
        int newTime = Stash.getValue(keyTime);
        Assert.assertTrue("Время " + keyTime + " должно было уменьшиться в этом запросе, но " + newTime + ">=" + oldTime,
                newTime<oldTime);
    }

    @Когда("^находим \"([^\"]*)\" и сохраняем \"([^\"]*)\" из вложенного \"([^\"]*)\"$")
    public void fingingAndSave3(String keyWhat,String keyWhere, String sourceString) {
        fingingAndSave2(keyWhat.split("-")[0],keyWhat,sourceString);
        List<HashMap> bonusMap = Stash.getValue(keyWhat);
        String bonuses = bonusMap.get(0).get(keyWhat.split("-")[1]).toString();
        Stash.put(keyWhere,bonuses);
    }

    @Когда("^находим и сохраняем \"([^\"]*)\" из \"([^\"]*)\"$")
    public void fingingAndSave(String keyFingingParams, String sourceString) {
        fingingAndSave2(keyFingingParams,keyFingingParams,sourceString);
    }

    @Когда("^находим \"([^\"]*)\" и сохраняем \"([^\"]*)\" из \"([^\"]*)\"$")
    public void fingingAndSave2(String param,String keyFingingParams, String sourceString) {
        String tmp;
        Object valueFingingParams, retMap = null;
        ObjectMapper mapper = new ObjectMapper();

        //Преобразуем в строку JSON-объект в зависимости от его структуры
        if (JSONValue.isValidJson(Stash.getValue(sourceString).toString())) {
            tmp = Stash.getValue(sourceString).toString();
        } else {
            tmp = JSONValue.toJSONString(Stash.getValue(sourceString));
        }

        TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
        };

        try {
            retMap = mapper.readValue(tmp, typeRef);
        } catch (IOException e) {
            TypeReference<ArrayList<Object>> typeRef1 = new TypeReference<ArrayList<Object>>() {
            };
            try {
                retMap = mapper.readValue(tmp, typeRef1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.getMessage();
        }
        valueFingingParams = JsonLoader.hashMapper(retMap, param);
        LOG.info("Достаем значение [" + param + "] и записываем в память [" + JSONValue.toJSONString(valueFingingParams) + "]");
        Stash.put(keyFingingParams, valueFingingParams);
    }


    private static Map<String, String> workWithDBgetTwoRows(String sqlRequest) {
        Connection con = DBUtils.getConnection();
        Statement stmt;
        PreparedStatement ps = null;
        ResultSet rs;
        Map<String, String> result = new HashMap<>();
        try {
            con.setAutoCommit(false);// Отключаем автокоммит
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlRequest);
            con.commit();
            // rs.last();
            while (rs.next()) {
                result.put(rs.getString(1), rs.getString(2));
            }

            if (result.isEmpty()) {
                throw new AutotestError("Ошибка! Запрос к базе [" + sqlRequest + "] вернул [" + result + "]");
            }
            LOG.info("SQL-request [" + result + "]");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AutotestError("Ошибка! Что-то не так в запросе, проверьте руками [" + sqlRequest + "]");
        } finally {
            DBUtils.closeAll(con, null, null);
        }
        return result;
    }


    private static String workWithDBgetResult(String sqlRequest, String param) {
        Connection con = DBUtils.getConnection();
        Statement stmt;
        PreparedStatement ps = null;
        ResultSet rs;
        String result;
        try {
            con.setAutoCommit(false);// Отключаем автокоммит
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlRequest);
            con.commit();
            rs.last();
            result = rs.getString(param);

            if (result.isEmpty()) {
                throw new AutotestError("Ошибка! Запрос к базе [" + sqlRequest + "] вернул [" + result + "]");
            }
            LOG.info("SQL-request [" + result + "]");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AutotestError("Ошибка! Что-то не так в запросе, проверьте руками [" + sqlRequest + "]");
        } finally {
            DBUtils.closeAll(con, null, null);
        }
        return result;
    }

    private static String workWithDBgetResult(String sqlRequest) {
        Connection con = DBUtils.getConnection();
        Statement stmt;
        PreparedStatement ps = null;
        ResultSet rs;
        String result = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlRequest);
            rs.last();
            result = rs.getString(1).replaceAll("\n", "").replaceAll(" +", " ");
            LOG.info("SQL-request [" + result + "]");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(con, null, null);
        }
        return result;
    }

    /**
     * запрос на БД и сохранение всего ответа в map
     *
     * @param sqlRequest
     * @return
     */
    private static void workWithDBresult(String sqlRequest) {
        Connection con = DBUtils.getConnection();
        Statement stmt;
        PreparedStatement ps = null;
        ResultSet rs;
        StringBuilder keyNormal = new StringBuilder();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlRequest);
            rs.last();
            ResultSetMetaData allRows = rs.getMetaData();
            int count = allRows.getColumnCount();
            if (count == 0) {
                throw new AutotestError("Ошибка! Запрос к базе [" + sqlRequest + "] вернул кол-во результатов [" + count + "]");
            }
            for (int i = 1; i <= count; i++) {
                String key = allRows.getColumnName(i);
                String value = rs.getString(key);
                for (String part : key.split("_")) {
                    keyNormal.append(part);
                }
                LOG.info(keyNormal + "=" + value);
                if (keyNormal.toString().toUpperCase().equals("PASSWORD")) {
                    continue;
                }
                Stash.put(keyNormal.toString().toUpperCase(), value);
                keyNormal.setLength(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(con, null, null);
        }
    }


    private static void workWithDBresultTwoRows(String sqlRequest, String keyForMap) {
        Connection con = DBUtils.getConnection();
        Statement stmt;
        PreparedStatement ps = null;
        ResultSet rs;
        StringBuilder keyNormal = new StringBuilder();
        ResultSetMetaData allRows;
        String key, value;
        Map<String, String> mapResult = new HashMap<>();

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlRequest);
            rs.first();
            int count = rs.getMetaData().getColumnCount();
            if (count == 0) {
                throw new AutotestError("Ошибка! Запрос к базе [" + sqlRequest + "] вернул кол-во результатов [" + count + "]");
            }

            do {
                allRows = rs.getMetaData();

                for (int i = 1; i <= count; i++) {
                    key = rs.getString(allRows.getColumnName(1));
                    value = rs.getString(allRows.getColumnName(2));
                    for (String part : key.split("_")) {
                        keyNormal.append(part);
                    }
                    LOG.info(keyNormal + "=" + value);
                    if (keyNormal.toString().toUpperCase().equals("PASSWORD")) {
                        continue;
                    }
                    //Stash.put(keyNormal.toString().toUpperCase(), value);
                    mapResult.put(keyNormal.toString().toUpperCase(), value);
                    keyNormal.setLength(0);
                }
            } while (rs.next());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(con, null, null);
        }
        Stash.put(keyForMap, mapResult);
    }

    public static List<String> workWithDBAndGetFullColumn(String sqlReq){
        Connection con = DBUtils.getConnection();
        Statement stmt;
        ResultSet rs;
        List<String> result=new ArrayList<>();
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlReq);
//            con.commit();
            int count = rs.getMetaData().getColumnCount();
            if (count == 0) {
                throw new AutotestError("Ошибка! Запрос к базе [" + sqlReq + "] вернул кол-во результатов [" + count + "]");
            }
            while (true){
                try{
                    rs.next();
                    result.add(rs.getString(1));
                }
                catch (Exception e){
                    break;
                }
            }
            con.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new AutotestError("Ошибка! Что-то не так в запросе, проверьте руками [" + sqlReq + "]");
        }
        finally {
            DBUtils.closeAll(con, null, null);
        }
        return result;
    }


    @Before(value = "@ChangePassword_C1043")
    public void saveCurrentPassword() throws DataException {
        String email = null;
        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
        String browserName = caps.getBrowserName();
        if (browserName.contains("chrome")) {
            email = JsonLoader.getData().get(STARTING_URL).get("USER_CHROME").getValue();
        } else {
            email = JsonLoader.getData().get(STARTING_URL).get("USER_FIREFOX").getValue();
        }

        String sqlRequest = "select password from gamebet.`user` WHERE `email` = '" + email + "'";
        String currentPassword = workWithDBgetResult(sqlRequest);
        Stash.put("currentUser", email);
        Stash.put("currentPassword", currentPassword);
        LOG.info("Записали хэш текущего пароля " + currentPassword + " для пользователя " + email);
    }

    @After(value = "@ChangePassword_C1043")
    public void revertCurrentPassword() {
        String sqlRequest = "update gamebet.`user` set password = '" + Stash.getValue("currentPassword") + "' WHERE `email` = '" + Stash.getValue("currentUser") + "'";
        workWithDB(sqlRequest);
        sqlRequest = "select password from gamebet.`user` WHERE `email` = '" + Stash.getValue("currentUser") + "'";
        String changeCheck = workWithDBgetResult(sqlRequest);
        LOG.info("вернули изначальный пароль " + changeCheck + " для пользователя " + Stash.getValue("currentUser"));
    }

    @After(value = "@LeftMenuTriggersPrematch_C1057")
    public void offMultigames(){
        LOG.info("!!!АФТЕР!!!");
        EventViewerPage.multiGamesOnOff("выключает");
    }

    @After(value = "@LandingAppFavourite_C1065,@AddBetToCouponFromFavourite_С1050,@Search_C1053,@TriggerPeriodPrematch_C1057,@LinkGameFromFavourite_C1050")
    public void offTriggerPeriod(){
        LOG.info("!!!АФТЕР!!!");
        PageFactory.getDriver().findElement(By.id("prematch")).click();
        workWithPreloader();
        onTriggerPeriod("Выберите время");
    }


    @Когда("^получаем и сохраняем в память код подтверждения \"([^\"]*)\" телефона \"([^\"]*)\" \"([^\"]*)\"$")
    public static void confirmPhone(String keyCode, String keyPhone, String type) {
        String phone = Stash.getValue(keyPhone);
        String sqlRequest;
        if (type.equals("новый")) {
            sqlRequest = "SELECT code FROM gamebet. `useroperation` WHERE user_id IN (SELECT id FROM gamebet. `user` WHERE phone='" + phone + "') ORDER BY creation_date";
        } else {
            sqlRequest = "SELECT code FROM gamebet. `phoneconfirmationcode` WHERE phone='" + phone + "' ORDER BY creation_date";
        }
        String code = workWithDBgetResult(sqlRequest, "code");
        Stash.put(keyCode, code);
        LOG.info("Получили код подтверждения телефона: " + code);
    }

    @Когда("^получаем и сохраняем в память все строки для достависты телефона \"([^\"]*)\"$")
    public static void getAllRowsForDostavista(String keyPhone) {
        String phone = Stash.getValue(keyPhone);
        String sqlRequest;
        sqlRequest = "SELECT * FROM gamebet. `dostavistaorder` WHERE user_id IN (SELECT id FROM gamebet. `user` WHERE phone='" + phone + "')";
        workWithDBresult(sqlRequest);

    }

    @Когда("^запрос к esb \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void requestESB(String path, String keyStash, DataTable dataTable) {
        try {
            StringBuilder fullPath = new StringBuilder();
            fullPath.append(JsonLoader.getData().get(STARTING_URL).get("ESB_URL").getValue() + "/" + path);
            LOG.info("Строчка запроса: " + fullPath);
            requestByHTTP(fullPath.toString(), keyStash, dataTable, "POST");
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    @Когда("^получаем и сохраняем в память код \"([^\"]*)\" подтверждения почты \"([^\"]*)\"$")
    public static void confirmEmail(String keyEmailCode, String keyEmail) {
        String email = Stash.getValue(keyEmail);
        String sqlRequest = "SELECT id FROM gamebet.`user` WHERE email='" + email + "'";
        String userId = workWithDBgetResult(sqlRequest, "id");
        sqlRequest = "SELECT code FROM gamebet.`useremailconfirmationcode`  WHERE user_id=" + userId;
        String code = workWithDBgetResult(sqlRequest, "code");
        Stash.put(keyEmailCode, code);
        Stash.put("userIdKey", userId);
        LOG.info("Получили код подтверждения почты: " + code);
    }

    @Когда("^определяем валидную и невалидную дату выдачи паспорта \"([^\"]*)\" \"([^\"]*)\"$")
    public void issueDateGenerate(String validKey, String invalidKey) throws ParseException {
        String birthDateString = Stash.getValue("BIRTHDATE");
        SimpleDateFormat formatDate = new SimpleDateFormat();
        formatDate.applyPattern("dd.MM.yyyy");
        if (birthDateString.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            SimpleDateFormat formatgut = new SimpleDateFormat();
            formatgut.applyPattern("yyyy-MM-dd");
            birthDateString = formatDate.format(formatgut.parse(birthDateString));
        }

        Date birthDate = formatDate.parse(birthDateString);
        Date now = new Date();
        Date valid = new Date();
        long days = TimeUnit.DAYS.convert(now.getTime() - birthDate.getTime(), TimeUnit.MILLISECONDS) + 2 * 24 * 3600;
        int years = (int) (days / 365);// количество полных лет (считается без учета високосных лет
        valid.setDate(valid.getDate() - 1);//валидная дата выдачи паспорта - это вчерашний день. точно валидна
        Calendar invalid = new GregorianCalendar();
        invalid.setTime(birthDate);
        invalid.add(Calendar.YEAR, 17);
        Stash.put(validKey, formatDate.format(valid));
        Stash.put(invalidKey, formatDate.format(invalid.getTime()));

        LOG.info("Дата рождения: " + birthDateString);
        LOG.info("Валидная дата выдачи паспорта: " + formatDate.format(valid));
        LOG.info("Невалидна дата выдачи паспорта: " + formatDate.format(invalid.getTime()));
    }

    @Когда("^добавляем данные в JSON объект \"([^\"]*)\" сохраняем в память:$")
    public void addDataToJSONObjectStoredInMemory(String keyJSONObject, DataTable dataTable) {

        Object jSONString = collectParametersInJSONString(dataTable);
        Stash.put(keyJSONObject, jSONString);
        LOG.info("Сохранили в память key::(" + keyJSONObject + ") |==> value::(" + String.valueOf(jSONString) + ")");

    }

    @Когда("^добавляем данные в JSON массив \"([^\"]*)\" сохраняем в память:$")
    public void addDataToJSONArrayStoredInMemory(String keyJSONObject, DataTable dataTable) {

        Object jSONString = collectParametersInJSONString(dataTable);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jSONString);
        Stash.put(keyJSONObject, jsonArray);
        LOG.info("Сохранили в память key::(" + keyJSONObject + ") |==> value::(" + String.valueOf(jsonArray.get(0)) + ")");
    }

    @Когда("^приводим дату к формату год-месяц-день \"([^\"]*)\"$")
    public void dataFormatter(String keyData) {

        SimpleDateFormat formatgut = new SimpleDateFormat();
        SimpleDateFormat formatDate = new SimpleDateFormat();
        formatDate.applyPattern("dd.MM.yyyy");
        formatgut.applyPattern("yyyy-MM-dd");
        String data = Stash.getValue(keyData);
        try {
            data = formatgut.format(formatDate.parse(data));
            Stash.put(keyData, data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Object collectParametersInJSONString(DataTable dataTable) {
        Map<String, String> table = dataTable.asMap(String.class, String.class);
        JSONObject jsonObject = new JSONObject();
        String key;
        Object value, params;
        Map<String, Object> map;
        ObjectMapper mapper;
        LOG.info("Собираем параметы в JSON строку");
        for (Map.Entry<String, String> entry : table.entrySet()) {
            key = entry.getKey();
            if (entry.getValue().matches("^[A-Z_]+$")) {
                value = Stash.getValue(entry.getValue());
            } else {
                value = entry.getValue();
            }
            if (entry.getValue().matches("^[/(][A-Z]+[/)]")){
                value = String.valueOf(value).replace("(","").replace(")","");
                JSONObject asd = Stash.getValue( String.valueOf(value));
                JSONObject[] dfg = {Stash.getValue( String.valueOf(value))};
                jsonObject.put(key,dfg);
            }
            //Если попадются числовые значения, в JSON объект кладём как строку
            else if (value instanceof String && !StringUtils.isBlank((String) value) && ((String) value).matches("[0-9]+")) {
                String str = (String) value;
                jsonObject.put(key, value);
            } else {
                jsonObject.put(key, JSONValue.parse(String.valueOf(value)));
            }
        }
        params = jsonObject;
        LOG.info(String.valueOf(params));
        return params;
    }

    @Когда("^определяем незанятый номер телефона и сохраняем в \"([^\"]*)\"$")
    public static void confirmEmail(String keyPhone) throws IOException {
        FileReader file = new FileReader("src" + sep + "test" + sep + "resources" + sep + "maxphone.txt");
        Scanner scan = new Scanner(file);

        String phoneLast = scan.nextLine();
        String phone = "7933000" + String.format("%4s", Integer.valueOf(phoneLast.substring(7)) + 1).replace(' ', '0');
        Stash.put(keyPhone, phone);
        LOG.info(phone);
        FileWriter nfile = new FileWriter("src" + sep + "test" + sep + "resources" + sep + "maxphone.txt", false);
        nfile.write(phone);
        nfile.close();
        file.close();
    }

    @Когда("^определяем занятый номер телефона и сохраняем в \"([^\"]*)\"$")
    public static void getPhone(String keyPhone) {
        String sqlRequest = "SELECT phone FROM gamebet.`user` WHERE phone LIKE '7933000%' ORDER BY phone DESC";
        String phoneLast = workWithDBgetResult(sqlRequest, "phone");
        Stash.put(keyPhone, phoneLast);
        LOG.info("Вычислили подходящий номер телефона::" + phoneLast);
    }

    @Когда("^определяем занятый адрес email и сохраняем в \"([^\"]*)\"$")
    public static void getOldEmail(String keyPhone) {
        String sqlRequest = "SELECT email FROM gamebet.`user` WHERE phone LIKE '7933000%' ORDER BY phone DESC";
        String emailLast = workWithDBgetResult(sqlRequest, "email");
        Stash.put(keyPhone, emailLast);
        LOG.info("Вычислили подходящий адрес почты::" + emailLast);
    }


    @Когда("^подтверждаем скайп через админку \"([^\"]*)\"$")
    public void skypeConfirm(String keyPhone) throws Exception {
        String registrationUrl = "https://test-int-bet-dbproca.tsed.orglot.office/admin/";
        String currentHandle = driver.getWindowHandle();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("registration_window = window.open('" + registrationUrl + "')");

        Set<String> windows = driver.getWindowHandles();
        windows.remove(currentHandle);
        String newWindow = windows.toArray()[0].toString();

        driver.switchTo().window(newWindow);

        Thread.sleep(1500);
        WebElement authForm = driver.findElement(By.xpath("//div[@class='x-panel x-panel-default-framed x-box-item']"));

        WebElement enterBottom = driver.findElement(By.xpath("//span[@id='button-1014-btnIconEl']"));

        WebElement loginField = driver.findElement(By.xpath("//input[@id='textfield-1011-inputEl']"));

        WebElement passField = driver.findElement(By.xpath("//input[@id='textfield-1012-inputEl']"));

        loginField.clear();
        loginField.sendKeys("promo_test_superadmin");
        passField.clear();
        passField.sendKeys("promo_test_superadmin");
        enterBottom.click();

        Thread.sleep(2500);
        String phone = Stash.getValue(keyPhone);

        PageFactory.getActions().doubleClick(driver.findElement(By.xpath("//td[@role='gridcell']/div[text()='" + phone + "']/../.."))).build().perform();
        driver.findElement(By.id("editDataBtn-btnEl")).click();
        int yy = driver.findElement(By.id("confirmSkypeMenuBtn-btnWrap")).getLocation().getY() + 116;
        int xx = driver.findElement(By.id("confirmSkypeMenuBtn-btnWrap")).getLocation().getX() + 156;
        Robot robot = new Robot();
        robot.mouseMove(xx, yy);
        robot.delay(500);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        driver.findElement(By.id("confirmSkypeBtn")).click();
        driver.findElement(By.id("saveUserBtn-btnIconEl")).click();
    }

    @Когда("^в админке смотрим id пользователя \"([^\"]*)\" \"([^\"]*)\"$")
    public void getIDFromAdmin(String keyPhone, String keyId) throws Exception {
        String registrationUrl = "https://test-int-bet-dbproca.tsed.orglot.office/admin/";
        String currentHandle = driver.getWindowHandle();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("registration_window = window.open('" + registrationUrl + "')");

        Set<String> windows = driver.getWindowHandles();
        windows.remove(currentHandle);
        String newWindow = windows.toArray()[0].toString();

        driver.switchTo().window(newWindow);

        Thread.sleep(1500);
        WebElement authForm = driver.findElement(By.xpath("//div[@class='x-panel x-panel-default-framed x-box-item']"));

        WebElement enterBottom = driver.findElement(By.xpath("//span[@id='button-1014-btnIconEl']"));

        WebElement loginField = driver.findElement(By.xpath("//input[@id='textfield-1011-inputEl']"));

        WebElement passField = driver.findElement(By.xpath("//input[@id='textfield-1012-inputEl']"));

        loginField.clear();
        loginField.sendKeys("promo_test_superadmin");
        passField.clear();
        passField.sendKeys("promo_test_superadmin");
        enterBottom.click();

        Thread.sleep(1500);
        String phone = Stash.getValue(keyPhone);

        String id = driver.findElement(By.xpath("//td[@role='gridcell']/div[text()='" + phone + "']/../../td[1]/div")).getAttribute("innerText");
        Stash.put(keyId, id);
    }


    @Когда("^определяем user_id пользователя \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public static void findUserId(String keyEmail, String keyId) {
        String sqlRequest = "SELECT id FROM gamebet.`user` WHERE email = '" + Stash.getValue(keyEmail) + "'";
        String id = workWithDBgetResult(sqlRequest, "id");
        Stash.put(keyId, id);
        LOG.info("Вычислили id::" + id);
    }

    @Когда("^смотрим изменился ли статус \"([^\"]*)\" на \"([^\"]*)\"$")
    public void checkEventType(String keyEventType, String myEvent) throws InterruptedException {
        String eventType = Stash.getValue(keyEventType);
        if (!eventType.equals(myEvent)) {
            Assertions.fail("Статус события и статус в БД  не совпадают!");
        }
        LOG.info("Тип события совпадает: " + eventType + " и " + myEvent);
    }


    @Когда("^смотрим изменился ли \"([^\"]*)\" из \"([^\"]*)\"$")
    public void checkTimeLeft(String keyTimeLeft, String keyResponse) {
        Integer timewasS = Stash.getValue(keyTimeLeft);
        fingingAndSave(keyTimeLeft, keyResponse);
        Integer timenowS = Stash.getValue(keyTimeLeft);
        if (timenowS.compareTo(timewasS) >= 0) {
            Assertions.fail("Время ожидани звонка скайп не изменилось");
        }
    }

    @Когда("^ожидание \"([^\"]*)\" сек$")
    public static void justsleep(String sleep) throws InterruptedException {
        long mcsleep = Long.parseLong(sleep) * 1000;
        Thread.sleep(mcsleep);
    }

    /**
     * Метод сверяет в JSON Object наличие полей и их типы
     * и возвращает либо значение по искомогу ключу или null
     *
     * @param keyJSONObject - Ключ объекта из памяти
     * @param dataTable     - таблица проверяемых параметров
     */
    @Когда("^проверка полей и типов в ответе \"([^\"]*)\":$")
    public void checkFieldsAndTypesInResponse(String keyJSONObject, DataTable dataTable) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String param, type, currentValue;
        Object json = Stash.getValue(keyJSONObject);

        for (Map<String, String> aTable : table) {
            param = aTable.get(PARAMETER);
            type = aTable.get(TYPE);
            currentValue = JSONValue.toJSONString(JsonLoader.hashMapper(json, param));
            assertThat(checkType(currentValue, type)).as("Тип параметра[" + param + "] не совпадает с[" + type + "]").isTrue();
            LOG.info("Тип параметра[" + currentValue + "] соответсвует [" + type + "]");
        }
    }

    private Boolean checkType(String value, String type) {
        if (type.equals("Long")) {
            Long.valueOf(value);
            return true;
        }
        if (type.equals("Integer")) {
            Integer.valueOf(value);
            return true;
        }
        if (type.equals("Timestamp")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            Date date = new Date(Long.valueOf(value));
            simpleDateFormat.format(date);
            return true;
        }
        if (type.equals("Boolean")) {
            Boolean.valueOf(value);
            return true;
        }
        if (type.equals("String")) {
            String.valueOf(value);
            return true;
        }
        if (type.equals("List")) {
            List<Object> items = Collections.singletonList(JSONValue.parse(value));
            return true;
        }
        return false;
    }

    @Когда("^запоминаем код подтверждения аккаунта \"([^\"]*)\" для пользователя \"([^\"]*)\"$")
    public void getConfirmAccountCode(String keyCode, String keyEmail){
        String sqlReq = "SELECT t.value FROM gamebet.`user_profile_edit_code` t LEFT JOIN gamebet.`user_profile_edit` tt ON ( t.edit_id = tt.id) " +
                "WHERE t.is_new_type = FALSE AND tt.user_id = (SELECT id FROM gamebet.`user` WHERE email='" + Stash.getValue(keyEmail).toString() + "')";
        String code = workWithDBgetResult(sqlReq);
        Stash.put(keyCode,code);
    }

    @Когда("^запоминаем код подтверждения смены Базовых Бараметров \"([^\"]*)\" для пользователя \"([^\"]*)\"$")
    public void getConfirmCode(String keyCode, String keyEmail){
        String sqlReq = "SELECT t.value FROM gamebet.`user_profile_edit_code` t LEFT JOIN gamebet.`user_profile_edit` tt ON ( t.edit_id = tt.id) " +
                "WHERE t.is_new_type = TRUE AND tt.user_id = (SELECT id FROM gamebet.`user` WHERE email='" + Stash.getValue(keyEmail).toString() + "')";
        String code = workWithDBgetResult(sqlReq);
        Stash.put(keyCode,code);
    }


    @Когда("^запоминаем значение \"([^\"]*)\" для пользователя с \"([^\"]*)\"$")
    public void rememberPhoneForEmail(String keyPhone,String keyEmail){
        String sqlRequest = "SELECT phone FROM gamebet.`user` WHERE email='" + Stash.getValue(keyEmail) + "'";
        Stash.put(keyPhone,workWithDBgetResult(sqlRequest));
    }

    @Когда("^генерим новый номер телефона \"([^\"]*)\" на основе \"([^\"]*)\"$")
    public void generateNewPhoneBasisOldPhone(String keyMewPhone,String keyOldPhone){
        String phone = Stash.getValue(keyOldPhone);
        String newPhone = phone.replace("7933","7222");
        Stash.put(keyMewPhone,newPhone);
    }




    @Когда("^поиск акаунта со статуом регистрации \"([^\"]*)\" \"([^\"]*)\"$")
    public void searchUserStatus2(String status, String keyEmail) {
        //  String sqlRequest = "SELECT * FROM gamebet.`user` WHERE (email LIKE 'testregistrator+7933%' OR email LIKE 'testregistrator+7111%') AND registration_stage_id" + status + " AND tsupis_status=3 AND offer_state=3 ORDER BY id DESC";
        String sqlRequest = "SELECT * FROM gamebet.`user` WHERE email LIKE 'testregistrator+7933%inbox.ru' AND registration_stage_id" + status + " AND tsupis_status=3 AND offer_state=3 ORDER BY id";
        searchUser(keyEmail, sqlRequest);
    }

    @Когда("^поиск акаунта для проверки изменений базовых параметров \"([^\"]*)\"$")
    public void searchUserForEdit(String keyEmail){
        String sqlRequest = "SELECT email FROM gamebet.`user` WHERE email LIKE 'testregistrator+7933%' AND registration_stage_id=2 ORDER BY id DESC";
        List<String> results = workWithDBAndGetFullColumn(sqlRequest);
        int count = 0;
        int count2 = 0;

        SimpleDateFormat formatgut = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.MONTH,-1);
        String dateOneMonth = formatgut.format(cal.getTime());
        cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,-1);
        String dateOneDay = formatgut.format(cal.getTime());

        for (String email: results){
            count2=0;
            sqlRequest = "SELECT COUNT(*) FROM gamebet.`user_profile_edit` WHERE edit_date_finish>'" + dateOneMonth + "' AND user_id=(SELECT id FROM gamebet.`user` WHERE email='" + email + "')";
            count = Integer.valueOf(workWithDBgetResult(sqlRequest));
            sqlRequest = "SELECT attemps_count FROM gamebet.`user_profile_edit` WHERE edit_date_start>'" + dateOneDay + "' AND user_id=(SELECT id FROM gamebet.`user` WHERE email='" + email + "')";
            List<String> attemps = workWithDBAndGetFullColumn(sqlRequest);
            for (String attemp:attemps){
                count2+=Integer.valueOf(attemp);
            }
            if (count<2 && count2<20){
                LOG.info("email:" + email);
                Stash.put(keyEmail,email);
                break;
            }
        }
    }

    @Когда("^поиск акаунта с закончившимися успешными попытками смены БП \"([^\"]*)\"$")
    public void searchUserWithoutEdit(String keyEmail){
        String sqlRequest = "SELECT email FROM gamebet.`user` WHERE email LIKE 'testregistrator+7933%' AND registration_stage_id=2 ORDER BY id DESC";
        List<String> results = workWithDBAndGetFullColumn(sqlRequest);
        int count = 0;

        SimpleDateFormat formatgut = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.MONTH,-1);
        String dateOneMonth = formatgut.format(cal.getTime());

        for (String email: results){
            sqlRequest = "SELECT COUNT(*) FROM gamebet.`user_profile_edit` WHERE edit_date_finish>'" + dateOneMonth + "' AND user_id=(SELECT id FROM gamebet.`user` WHERE email='" + email + "')";
            count = Integer.valueOf(workWithDBgetResult(sqlRequest));
            if (count==2){
                LOG.info("email:" + email);
                Stash.put(keyEmail,email);
                break;
            }
        }
    }

    @Когда("^поиск акаунта с закончившимися суточными попытками смены БП \"([^\"]*)\"$")
    public void searchUserWithoutEditOneDay(String keyEmail){
        String sqlRequest = "SELECT email FROM gamebet.`user` WHERE email LIKE 'testregistrator+7933%' AND registration_stage_id=2 ORDER BY id DESC";
        List<String> results = workWithDBAndGetFullColumn(sqlRequest);
        int count = 0;

        SimpleDateFormat formatgut = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,-1);
        String dateOneDay = formatgut.format(cal.getTime());

        for (String email: results){
            sqlRequest = "SELECT attemps_count FROM gamebet.`user_profile_edit` WHERE edit_date_start>'" + dateOneDay + "' AND user_id=(SELECT id FROM gamebet.`user` WHERE email='" + email + "')";
            List<String> attemps = workWithDBAndGetFullColumn(sqlRequest);
            for (String attemp:attemps){
                count+=Integer.valueOf(attemp);
            }
            if (count==20){
                LOG.info("email:" + email);
                Stash.put(keyEmail,email);
                break;
            }
        }
    }

    @Когда("^ищем пользователя с ограничениями \"([^\"]*)\"$")
    public void searchUserAlt(String keyEmail) {
        //String sqlRequest = "SELECT * FROM gamebet.`user` WHERE email LIKE 'testregistrator+7111%' AND registration_stage_id"+status + " AND tsupis_status=3 and personal_data_state=3 AND offer_state=3";
        // String sqlRequest = "SELECT * FROM gamebet.`user` WHERE identState=1 AND registration_stage_id=2 AND (phone LIKE '7933002%' OR phone LIKE '7111002%') ORDER BY id DESC";
        //String sqlRequest = "SELECT * FROM gamebet.`user` WHERE identState=1 AND registration_stage_id=2 AND phone LIKE '7933%' ORDER BY id DESC";

        String sqlRequest = "SELECT * FROM gamebet.`user` WHERE identState=1 AND registration_stage_id=2 AND email = '" + Stash.getValue("EMAIL") + "' AND phone LIKE '7933%' ORDER BY id DESC";
        searchUser(keyEmail, sqlRequest);
    }

    @Когда("^обновляем оферту пользователю \"([^\"]*)\" \"([^\"]*)\"$")
    public static void offertUpdate(String offer_state, String keyEmail) {
        String sqlRequest = "UPDATE gamebet.`user` SET offer_state=" + offer_state + " WHERE `email` = '" + Stash.getValue(keyEmail) + "'";
        workWithDB(sqlRequest);


    }

    @Когда("^запоминаем дату рождения пользователя \"([^\"]*)\" \"([^\"]*)\"$")
    public static void rememberBirthDate(String keyBD, String keyEmail) throws ParseException {
        String sqlRequest = "SELECT birth_date FROM gamebet.`user` WHERE email='" + Stash.getValue(keyEmail) + "'";
        String birthDate = workWithDBgetResult(sqlRequest, "birth_date");
        SimpleDateFormat formatDate = new SimpleDateFormat();
        SimpleDateFormat formatgut = new SimpleDateFormat();
        formatgut.applyPattern("dd.MM.yyyy");
        formatDate.applyPattern("yyyy-MM-dd");
        birthDate = formatgut.format(formatDate.parse(birthDate));
        Stash.put(keyBD, birthDate);
        LOG.info("Дата рождения: " + birthDate);
    }

    @Когда("^проверяем значение полей в ответе \"([^\"]*)\":$")
    public void checkValueOfFieldsInResponse(String keyJSONObject, DataTable dataTable) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String param, value, currentValue, tmp;
        Object json = Stash.getValue(keyJSONObject);

        for (Map<String, String> aTable : table) {
            param = aTable.get(PARAMETER);
            tmp = aTable.get(VALUE);
            if (tmp.matches("^[A-Z_]+$")) {
                value = JSONValue.toJSONString(Stash.getValue(tmp));
            } else {
                value = tmp;
            }
            currentValue = String.valueOf(JsonLoader.hashMapper(json, param));
            assertThat(currentValue).as("[" + currentValue + "] не совпадает с [" + value + "]").isEqualToIgnoringCase(value);
            LOG.info("[" + currentValue + "] совпадает с [" + value + "]");
        }
    }

    @Когда("^устанавливаем время ожидания обратного звонка \"([^\"]*)\"$")
    public void setCallWaitingTime(String time) {
        //String sqlRequest = "UPDATE gamebet.`user` SET registered_in_tsupis = TRUE, identified_in_tsupis = TRUE, identState = 1 WHERE `email` ='" + Stash.getValue(param) + "'";
        String sqlRequest = "UPDATE gamebet.`params` SET VALUE=" + time + " WHERE NAME='CALLBACK_OPERATOR_WAIT'";
        workWithDB(sqlRequest);
        LOG.info("Установили время ожидания в [" + time + "]");

    }


    @Когда("^выбираем fullalt пользователя \"([^\"]*)\" \"([^\"]*)\"$")
    public static void searchFullAlt(String keyPhone, String keyBD) throws Exception {
        StringBuilder lal = new StringBuilder();
        FileReader file = new FileReader("src" + sep + "test" + sep + "resources" + sep + "full_alt.txt");
        Scanner scan = new Scanner(file);

        String user = scan.nextLine();
        String separator = user.contains("\t") ? "\t" : "\\s";
        String phone = user.trim().split(separator)[0];
        String birthDate = user.trim().split(separator)[1];
        SimpleDateFormat formatDate = new SimpleDateFormat();
        SimpleDateFormat formatgut = new SimpleDateFormat();
        formatgut.applyPattern("dd.MM.yyyy");
        formatDate.applyPattern("yyyy-MM-dd");
        birthDate = formatgut.format(formatDate.parse(birthDate));
        Stash.put(keyPhone, phone);
        Stash.put(keyBD, birthDate);

        while (scan.hasNext()) {
            lal.append(scan.nextLine()).append(System.lineSeparator());
        }

        FileWriter nfile = new FileWriter("src" + sep + "test" + sep + "resources" + sep + "full_alt.txt", false);
        nfile.write(lal.toString());
        nfile.close();
        file.close();

    }

    @Когда("^поиск пользователя проходившего ускоренную регистрацию \"([^\"]*)\"$")
    public void searchUserNotPD(String keyEmail) {
        String sqlRequest = "SELECT * FROM gamebet.`user` WHERE tsupis_status IN (1,2) AND personal_data_state=1 AND email LIKE 'testregistrator+%'";
        searchUser(keyEmail, sqlRequest);
    }

    @Когда("^сбрасываем пользователю статус ПД до \"([^\"]*)\" \"([^\"]*)\"$")
    public static void updatePDState(String peronal_data_state, String keyEmail) {
        String sqlRequest = "UPDATE gamebet.`user` SET personal_data_state=" + peronal_data_state + " WHERE `email` = '" + Stash.getValue(keyEmail) + "'";
        workWithDB(sqlRequest);
    }

    @Когда("^обновляем поля в БД для пользователя \"([^\"]*)\":$")
    public void NullToField(String keyEmail, DataTable dataTable) {

        Map<String, String> table = dataTable.asMap(String.class, String.class);
        StringBuilder setter = new StringBuilder();
        table.forEach((key, value) -> setter.append(key).append("=").append(value).append(","));
        LOG.info(setter.toString());
        String sqlRequest = "UPDATE gamebet.`user` SET " + setter.delete(setter.length() - 1, setter.length()).toString() + " WHERE email = '" + Stash.getValue(keyEmail) + "'";
        workWithDB(sqlRequest);
    }

    @Когда("^достаём случайную видеотрансляцию из списка \"([^\"]*)\" и сохраняем в переменую \"([^\"]*)\"$")
    public void getRandomTranslationFromListAndSaveToVariable(String keyListTranslation, String keyGameId) {
        Map<String, Object> map;
        String key;
        Object value, selectedObject = null;
        ObjectMapper oMapper = new ObjectMapper();
        Object json = Stash.getValue(keyListTranslation);
        map = oMapper.convertValue(json, Map.class);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            selectedObject = ((Map) value).entrySet().toArray()[new Random().nextInt(((Map) value).size())];
        }
        Stash.put(keyGameId, selectedObject);
    }

    @Когда("^достаём параметр из \"([^\"]*)\" и сохраняем в переменую:$")
    public void takeParamFromAndSaveInVariable(String keyJSONObject, DataTable dataTable) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String param, keyVariable, currentValue;
        Object json = Stash.getValue(keyJSONObject);
        Object key, value;
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> map;
        map = oMapper.convertValue(json, Map.class);

        for (Map<String, String> aTable : table) {
            param = aTable.get(PARAMETER);
            keyVariable = aTable.get(VARIABLE);

            for (Map.Entry<String, Object> entry1 : map.entrySet()) {
                if (param.equals("gameId")) {
                    key = entry1.getKey();
                    Stash.put(keyVariable, key);
                    LOG.info("Cохранили [" + key.toString() + "]==>[" + keyVariable + "]");
                } else {
                    value = entry1.getValue();
                    currentValue = JSONValue.toJSONString(JsonLoader.hashMapper(value, param));
                    Stash.put(keyVariable, currentValue);
                    LOG.info("Cохранили [" + currentValue + "]==>[" + keyVariable + "]");
                }
            }
        }
    }

    @Когда("^запрос к API \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void requestToAPI(String path, String keyStash, DataTable dataTable) {
        String fullPath = collectQueryString(path);
        requestByHTTPS(fullPath, keyStash, "POST", dataTable);
    }


    @Когда("^неудачный запрос к API \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void requestToAPIEr(String path, String keyStash, DataTable dataTable) {
        String fullPath = collectQueryString(path);
        try {
            requestByHTTPS(fullPath, keyStash, "POST", dataTable);
        }
        catch (AutotestError e){
            LOG.info("Ожидали ошибку - получили ошибку:" + e.getMessage());
            return;
        }
        Assert.fail("Ожидали ошибку, но ее не было");
    }

    @Когда("^запрос по прямому адресу \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void requestTo(String fullPath, String keyStash, DataTable dataTable) {
        requestByHTTPS(fullPath, keyStash, "POST", dataTable);
    }


    @Когда("^запрос по адресу \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void requestTo2(String fullPath, String keyStash, DataTable dataTable) throws DataException {
        int index = fullPath.indexOf("}");
        String url = fullPath.substring(1,index);
        fullPath = JsonLoader.getData().get(STARTING_URL).get(url).getValue() + fullPath.substring(index+1);
        requestByHTTPS(fullPath, keyStash, "POST", dataTable);
    }

    @Когда("^запрос к API \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public void requestToAPI(String path, String keyStash) {
        String fullPath = collectQueryString(path);
        requestByHTTPS(fullPath, keyStash, "GET", null);
    }

    @Когда("^запрос к IMG \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public void requestToIMGAndSaveIn(String path, String keyStash) {
        String fullPath = (Stash.getValue(path)).toString().replaceAll("\\\\", "");
        requestByHTTPS(fullPath, keyStash, "GET", null);
    }


    @Когда("^запрос типа COLLECT \"([^\"]*)\" c параметрами \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public void requestCollect(String path, String keyParams, String keyStash) {
        try {
            StringBuilder fullPath = new StringBuilder();
            fullPath.append(JsonLoader.getData().get(STARTING_URL).get("ESB_URL").getValue() + "/" + path);
            fullPath.append("?" + Stash.getValue(keyParams));
            LOG.info("Строчка запроса: " + fullPath);
            requestByHTTPGet(fullPath.toString(), keyStash);
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    /**
     * формирование запроса GET.
     * если ключ = data - значит берем значение из datajson
     * если ключ = string - значит такое значнеие и используем. это просто строка
     * если кдюч = stash - значит значение берем из памяти
     * @param dataTable
     */
    @Когда("^запрос типа GET, результат сохраняем в \"([^\"]*)\"$")
    public void endpointCollect(String keyStash, DataTable dataTable) throws DataException {
        List<List<String>> table = dataTable.raw();
        StringBuilder path = new StringBuilder();
        String value = new String();
        String key = new String();
        LOG.info("Сначала формируем строку запроса");
        for (List<String> entry : table) {
            key = entry.get(0);
            value = entry.get(1);
            switch (key){
                case "data":
                    value = JsonLoader.getData().get(STARTING_URL).get(value).getValue();
                    break;
                case "stash":
                    value = Stash.getValue(value);
                    break;
            }
            path.append(value+"/");
        }
        LOG.info("Теперь посылаем GET запрос " + path);
        requestByHTTPGet(path.toString(), keyStash);
    }


    @Когда("^пополняем пользователю баланс если нужно, судя по ответу \"([^\"]*)\" на сумму, достаточную для \"([^\"]*)\"$")
    public void refillIfNeed(String keyResponce, String keyAmount,DataTable dataTable) throws DataException {

        fingingAndSave("BALANCE",keyResponce);
        String balance = Stash.getValue("BALANCE").toString();
        Long amount = Long.valueOf(Stash.getValue(keyAmount));
        Long br = Long.valueOf(balance);
        if (br<amount) {
            LOG.info("Баланс рублей недостаточен для ставки. Пополним баланс");
            refill("refillResponce", dataTable);
        }else {
            LOG.info("Пополнение средств не требуется");
        }

    }


    @Когда("^пополняем пользователю бонусы если нужно, судя по ответу \"([^\"]*)\" на сумму, достаточную для \"([^\"]*)\"$")
    public void refillIfNeedBonus(String keyResponce, String keyAmount,DataTable dataTable) throws DataException {

        fingingAndSave("BONUS",keyResponce);
        List<HashMap> bonusMap = Stash.getValue("BONUS");
        String bonuses = bonusMap.get(0).get("balance").toString();
        Long amount = Long.valueOf(Stash.getValue(keyAmount));
        Long bb = Long.valueOf(bonuses);
        if (bb<amount){
            LOG.info("Баланс бонусов недостаточен для ставки. Пополним бонусы");
            refill("refillResponce", dataTable);
        }else {
            LOG.info("Пополнение бонусов не требуется");
        }


    }

    @Когда("^формирум параметры и сохраняем в \"([^\"]*)\"$")
    public void collectParametrs(String keyParams, DataTable dataTable) {
        Map<String, String> table = dataTable.asMap(String.class, String.class);
        Map<String, Object> map;
        String key, value;
        ObjectMapper mapper;
        StringBuilder params = new StringBuilder();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatTime = new SimpleDateFormat("kk:mm:ss");
        for (Map.Entry<String, String> entry : table.entrySet()) {
            key = entry.getKey();
            Calendar dateNow = Calendar.getInstance();

            value = entry.getValue();
            if (value.matches("[A-Z]*")) {
                value = "fromStash";
            }

            switch (value) {
                case "прошлый месяц":
                    dateNow.add(Calendar.DAY_OF_YEAR, -30);
                    value = formatDate.format(dateNow.getTime()) + "T" + formatTime.format(Calendar.getInstance().getTime());
                    break;
                case "текущая дата-время":
                    value = formatDate.format(dateNow.getTime()) + "T" + formatTime.format(Calendar.getInstance().getTime());
                    break;
                case "следующий месяц":
                    dateNow.add(Calendar.DAY_OF_YEAR, 30);
                    value = formatDate.format(dateNow.getTime()) + "T" + formatTime.format(Calendar.getInstance().getTime());
                    break;
                case "fromStash":
                    value = Stash.getValue(entry.getValue());
                    break;
            }
            params.append(key + "=" + value + "&");
        }
        LOG.info("Параметры получились: " + params);
        Stash.put(keyParams, params);

    }


    private String collectQueryString(String path) {
        String requestFull = "";
        LOG.info("Собираем строку запроса.");
        try {
            requestFull = JsonLoader.getData().get(STARTING_URL).get("MOBILE_URL").getValue() + "/" + path;
        } catch (DataException e) {
            e.getMessage();
        }
        LOG.info("requestFull [" + requestFull + "]");
        return requestFull;
    }


    protected void requestByHTTPGet(String requestFull, String keyStash) {

        HttpURLConnection connect = null;
        try {
            connect = (HttpURLConnection) new URL(requestFull).openConnection();
            connect.setRequestMethod("GET");
            connect.setUseCaches(false);
            connect.setConnectTimeout(500);
            connect.setReadTimeout(500);
            connect.connect();

            StringBuilder jsonString = new StringBuilder();
            if (HttpURLConnection.HTTP_OK == connect.getResponseCode()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    jsonString.append(line);
                    jsonString.append("\n");
                }
                LOG.info(jsonString.toString());
                Stash.put(keyStash, JSONValue.parse(jsonString.toString()));
            } else {
                LOG.info("fail" + connect.getResponseCode() + ", " + connect.getResponseMessage());
                Stash.put(keyStash, JSONValue.parse(connect.getResponseMessage()));
            }
            jsonString.toString();
        } catch (Throwable cause) {
            cause.printStackTrace();
        } finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }


    protected void requestByHTTP(String requestFull, String keyStash, DataTable dataTable, String method) {

        if (!(null == dataTable)) {
            Map<String, String> table = dataTable.asMap(String.class, String.class);
        }
        Object params = null;
        URL url;

        LOG.info("Собираем параметы в JSON строку");
        JSONObject jsonObject = new JSONObject();
        if (!(null == dataTable)) {
            params = collectParametersInJSONString(dataTable);
        }

        try {

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            url = new URL(requestFull);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod(method);
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //стрчока внизу - это чтоб не было редиректа на мабильную версию (потмоу что при редирексте POST меняеallureтся на GET)
            //   con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");


            byte[] postData = String.valueOf(params).getBytes(StandardCharsets.UTF_8);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());

            wr.write(postData);
            wr.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            con.disconnect();
            LOG.info("Получаем ответ и записываем в память [" + jsonString.toString() + "]");
            if (StringUtils.isNoneEmpty(jsonString)) {
                Stash.put(keyStash, JSONValue.parse(jsonString.toString()));
            } else {
                throw new AutotestError("ОШИБКА! Пустая строка JSON");
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void requestByHTTPS(String requestFull, String keyStash, String method, DataTable dataTable){
        if (!(null == dataTable)) {
            Map<String, String> table = dataTable.asMap(String.class, String.class);
        }
        Object params = null;
        URL url;

        LOG.info("Собираем параметы в JSON строку");
        JSONObject jsonObject = new JSONObject();
        if (!(null == dataTable)) {
            params = collectParametersInJSONString(dataTable);
        }

        //************Этот код нужен для соединения по HTTPS
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            //************
            HttpURLConnection con;
            url = new URL(requestFull);
            if (requestFull.contains("https:")) {
                con = (HttpsURLConnection) url.openConnection();
            } else {
                con = (HttpURLConnection) url.openConnection();
            }
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod(method);
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            //стрчока внизу - это чтоб не было редиректа на мабильную версию (потмоу что при редирексте POST меняеallureтся на GET)
            //   con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");

            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
            if (!(null == dataTable)) {
                writer.write(String.valueOf(params));
            }
            writer.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            con.disconnect();
            LOG.info("Получаем ответ и записываем в память [" + jsonString.toString() + "]");
            if (StringUtils.isNoneEmpty(jsonString)) {
                Stash.put(keyStash, JSONValue.parse(jsonString.toString()));
            } else {
                throw new AutotestError("ОШИБКА! Пустая строка JSON");
            }
        }
            catch (IOException e2){
            throw new AutotestError(e2.getMessage());
        } catch (Exception e1) {
            LOG.error(e1.getMessage(), e1);
        }
    }

    @Когда("^обновим значение минимальной суммы вывода в рублях для вызова инкассатора \"([^\"]*)\"$")
    public void updateMinCash(String value) {
        String sqlRequest = "UPDATE gamebet.`params` SET VALUE=" + value + " WHERE NAME='MIN_DEPOSIT_CASH_AMOUNT'";
        workWithDB(sqlRequest);
        LOG.info("Установили значение Минимальной сумма для вызова инкасатора = " + value);
    }

    @Когда("^обновим версию мобильного приложения для \"([^\"]*)\" \"([^\"]*)\" до \"([^\"]*)\" \"([^\"]*)\"$")
    public void updateVersionApp(String typeOS, String keyTypeOS, String vers, String hardVers) {
        String type;
        switch (typeOS) {
            case "Android":
                type = "1";
                break;
            case "IOS":
                type = "2";
                break;
            default:
                type = typeOS;
        }
        Stash.put(keyTypeOS, type);
        String sqlDel = "DELETE FROM  gamebet.`appversion` WHERE (version=" + vers + " OR hard_update_version=" + hardVers + ") AND type_os=" + type + " AND active_version=0";
        workWithDB(sqlDel);
        String sqlRequest = "UPDATE gamebet.`appversion` SET version=" + vers + ", hard_update_version=" + hardVers + " WHERE active_version=1 AND type_os=" + type;
        // String sqlRequest = "INSERT INTO gamebet.`appversion` (VERSION,apk,hard_update_version,active_version,type_os) VALUES("+vers+", 'android.apk',"+hardVers+",'1',"+type+")";
        workWithDB(sqlRequest);
    }

    private void searchUser(String keyEmail, String sqlRequest) {
        if (keyEmail.equals("ALLROWS")) {
            try {
                workWithDBresult(sqlRequest);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String email = workWithDBgetResult(sqlRequest, "email");
        Stash.put(keyEmail, email);
        LOG.info("Подходящий пользователь найден : " + email);
    }

    @Когда("^составляем новый номер телефона \"([^\"]*)\" вместо старого \"([^\"]*)\"$")
    public void newPhone(String keyNewPhone, String keyOldPhone) {
        String oldPhone = Stash.getValue(keyOldPhone);
        String newPhone = "7444" + oldPhone.substring(4, 11);
        Stash.put(keyNewPhone, newPhone);
        LOG.info("Новый нмоер телефона: " + newPhone);
    }

    @Когда("^смотрим какое время обновления баннера \"([^\"]*)\"$")
    public void delayGromBanner(String keyDelay) {
        String sqlRequest = "SELECT delay FROM gamebet.`bannerslider` WHERE NAME='index_main_default'";
        String delay = workWithDBgetResult(sqlRequest, "delay");
        Stash.put(keyDelay, delay);
    }

    @Когда("^пользователь открывает новый url \"([^\"]*)\"$")
    public void userOpenNewUrl(String url) {
//        Set<String> handles = driver.getWindowHandles();
//        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
//        String browserName = caps.getBrowserName();
//        if (browserName.contains("chrome")){
//            ((ChromeDriver) driver).executeScript("window.open()");
//        }
//        else {
//            driver.findElement(By.cssSelector("Body")).sendKeys(Keys.CONTROL + "n");
//        }
//        Set<String> newHandles = driver.getWindowHandles();
//        newHandles.removeAll(handles);
//        driver.switchTo().window(newHandles.toArray()[0].toString());
        driver.get(url);
        new WebDriverWait(driver, 10).until(ExpectedConditions.urlToBe(url));

    }

    @Когда("^пользователь открывает новое окно с url \"([^\"]*)\"$")
    public void userOpenNewUrl2(String url) {
        WebDriver driver2 = new ChromeDriver();
        driver2.get(url);
        new WebDriverWait(driver2, 10).until(ExpectedConditions.urlToBe(url));
        Stash.put("driver", driver2);
    }

    public static void closingCurrtWin(String title) {
        PageFactory.getWebDriver().close();
        for (String windowHandle : PageFactory.getWebDriver().getWindowHandles()) {
            PageFactory.getWebDriver().switchTo().window(windowHandle);
            if (PageFactory.getWebDriver().getTitle().equals(title)) {
                return;
            }
        }
        throw new AutotestError("Unable to return to the previously opened page: " + title);
    }

    @Когда("^эмулируем регистрацию через терминал Wave \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void emulationRegistrationFromTerminalWave(String path, String keyStash, DataTable dataTable) {
        String fullPath = collectQueryString(path);
        requestByHTTPS(fullPath, keyStash, "POST", dataTable);
    }


    /**
     * это когда активне опции сайта в отдельной таблице
     */
    @Before(value = "@before or @api")
    public void saveRegistrationValue2() {
        rememberEnabledFeatures("ACTIVE_SITE_OPTIONS");
    }

    @Before()
    public void titleTest(Scenario scenario) {
        LOG.info("<================START...TEST================>");
        LOG.info("NAME: " + scenario.getName());
        LOG.info("TAGS: " + scenario.getSourceTagNames());
        LOG.info("ID: " + scenario.getId().replaceAll("\\D+", ""));
        String mainUrl;

        try {
            if (scenario.getSourceTagNames().contains("@mobile")) {
                mainUrl = JsonLoader.getData().get(STARTING_URL).get("MOBILE_URL").getValue();
            } else {
                mainUrl = JsonLoader.getData().get(STARTING_URL).get("MAIN_URL").getValue();
            }
            Stash.put("MAIN_URL", mainUrl);
            LOG.info("Сохранили в память key [MAIN_URL] <== value [" + mainUrl + "]");
        } catch (DataException e) {
            throw new AutotestError("Ошибка! Что-то не так с URL");
        }
    }

    /**
     * когда активные опции сайта в отдельной таблице
     */
    @Когда("^запоминаем все активные опции сайта в \"([^\"]*)\"$")
    public void rememberEnabledFeatures(String activeOptionKey) {
        Map<String, String> activeOpt =
                workWithDBgetTwoRows("SELECT NAME,state FROM gamebet.`enabled_features`");
        Stash.put(activeOptionKey, activeOpt);
        LOG.info("Записали в память: key=>[" + activeOptionKey + "] ; value=>[" + activeOpt + "]");
    }

    /**
     * когда активные опции сайта в таблице. когда параметр удалят - использоватеь этот метод вместо старого (rememberActiveAndOffOption)
     * но не забыть поменять Когда
     */
    @Когда("^редактируем некоторые активные опции сайта")
    public void rememberActiveOption(DataTable dataTable) {
        StringBuilder optionTrue = new StringBuilder();
        StringBuilder optionFalse = new StringBuilder();
        String sqlRequest;

        Map<String, String> table = dataTable.asMap(String.class, String.class);
        for (Map.Entry<String, String> entry : table.entrySet()) {
            if (entry.getValue().equals("true")) {
                optionTrue.append(",'" + entry.getKey() + "'");
                LOG.info("[" + entry.getKey() + "] = true");
            } else {
                optionFalse.append(",'" + entry.getKey() + "'");
                LOG.info("[" + entry.getKey() + "] = false");
            }
        }
        optionTrue.delete(0, 1);
        optionFalse.delete(0, 1);
        if (!optionTrue.toString().isEmpty()) {
            sqlRequest = "UPDATE gamebet.`enabled_features` SET state=1 WHERE NAME in (" + optionTrue + ")";
            workWithDB(sqlRequest);
            LOG.info("Включили опиции [" + optionTrue.toString() + "]");
        }
        if (!optionFalse.toString().isEmpty()) {
            sqlRequest = "UPDATE gamebet.`enabled_features` SET state=0 WHERE NAME in (" + optionFalse + ")";
            workWithDB(sqlRequest);
            LOG.info("Выключили опиции [" + optionFalse.toString() + "]");
        }
    }

    /**
     * когда активные опции сайта в таблице
     *
     * @param key
     */
    @Когда("^выставляем обратно старое значение активных опций сайта \"([^\"]*)\"$")
    public void revertEnabledFeatures(String key) {
        Map<String, String> oldEnabledFeatures = (HashMap) Stash.getValue(key);
        StringBuilder optionFalse = new StringBuilder();
        StringBuilder optionTrue = new StringBuilder();
        for (Map.Entry<String, String> entry : oldEnabledFeatures.entrySet()) {
            if (entry.getValue().equals("1")) {
                optionTrue.append(",'" + entry.getKey() + "'");
                LOG.info("[" + entry.getKey() + "] = true");
            } else {
                optionFalse.append(",'" + entry.getKey() + "'");
                LOG.info("[" + entry.getKey() + "] = false");
            }
        }
        optionTrue.delete(0, 1);
        optionFalse.delete(0, 1);
        String sqlRequest = "UPDATE gamebet.`enabled_features` SET state=1 WHERE NAME in (" + optionTrue + ")";
        workWithDB(sqlRequest);
        sqlRequest = "UPDATE gamebet.`enabled_features` SET state=0 WHERE NAME in (" + optionFalse + ")";
        workWithDB(sqlRequest);
        Map<String, String> activeOpt =
                workWithDBgetTwoRows("SELECT NAME,state FROM gamebet.`enabled_features`");
        LOG.info("Вернули активные опции сайта [" + activeOpt + "]");
        Assert.assertTrue("Не удалось вернуть опции сайта к старому значению. было: " + oldEnabledFeatures +
                "\n!!!!!!!!!!!!!!!!!\nCтало:" + activeOpt, activeOpt.equals(oldEnabledFeatures));
    }


    /**
     * Возвращаем активные опции сайста в исходное положение до тестов
     *
     * @param scenario
     */
    @After(value = "@after")
    public void returnRegistrationValueWithScreenshot(Scenario scenario) {
        LOG.info("возвращаем значение активных опций сайта из памяти по ключу 'ACTIVE_SITE_OPTIONS'");
        revertEnabledFeatures("ACTIVE_SITE_OPTIONS");
        if (scenario.isFailed()) {
            final byte[] screenshot = ((TakesScreenshot) PageFactory.getWebDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/jpeg");
        }
    }

    /**
     * Возвращаем десятичный тип отображения коэффициентов
     */
    @After(value = "@ChangeTypeOfCoefficientOnMain_C1066 or @ChangeTypeOfCoefficientCoupon_C1066 or @ChangeTypeOfCoefficientFav_C1066")
    public void returnDecTypeCoef() {
        WebElement preferences = driver.findElement(By.id("preferences"));
        LOG.info("переходит в настройки и меняет коэффицент");
        String previous;
        LOG.info("Нажимаем на кнопку с шетсерёнкой");
        if (!preferences.getAttribute("class").contains("active")) {
            preferences.click();
        }
        driver.findElement(By.xpath("//ul[@class='prefs']//span[contains(@class, 'prefs__key') and normalize-space(text())='Десятичный']")).click();
    }


    /**
     * Возвращаем активные опции сайста в исходное положение до тестов
     *
     * @param scenario
     */
    @After(value = "@0Registration_mobile or @requestVideoChatConfirmation or @1Registration_fullalt_mobile or @requestPhoneCall or @requestVideoChatConfirmation")
    public void returnRegistrationValue(Scenario scenario) {
        LOG.info("возвращаем значение активных опций сайта из памяти по ключу 'ACTIVE_SITE_OPTIONS'");
        revertEnabledFeatures("ACTIVE_SITE_OPTIONS");
    }

    @Когда("^определяем дату завтрашнего дня \"([^\"]*)\"$")
    public void tomorrowDate(String keyParams) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 1);
        Date dateTomorrow = new Date();
        dateTomorrow.setTime(today.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String date = format.format(dateTomorrow);
        Stash.put(keyParams, date);
        LOG.info("Завтрашняя дата: " + dateTomorrow);
    }


    @Когда("^запоминаем текущую страницу в \"([^\"]*)\"$")
    public void rememberCurenPageTo(String keyCurrentPage) {
        Stash.put(keyCurrentPage, PageFactory.getDriver().getWindowHandle());
        LOG.info("Записали key [" + keyCurrentPage + "] <== value [" + PageFactory.getDriver().getWindowHandle() + "]");
    }

    @Когда("^закрываем текущее окно и возвращаемся на \"([^\"]*)\"$")
    public void closingCurrentWinAndReturnTo(String keyPage) {
        try {
            driver.close();
            driver.switchTo().window(Stash.getValue(keyPage));
            LOG.info("Вернулись на ранее запомненую страницу");
        } catch (Exception e) {
            throw new AutotestError("Ошибка! Не смогли вернуться на страницу.");
        }
    }

    @Когда("^записываем значение баланса бонусов в \"([^\"]*)\"$")
    public void writeValueOfBonusBalanceIn(String bonusKey) {
        List<WebElement> bonusElement = PageFactory.getWebDriver().findElements(By.xpath("//span[contains(@class,'subMenuBonus bonusmoney-text')]"));
        String bonus = bonusElement.isEmpty() ? "0" : bonusElement.get(0).getAttribute("innerText").replaceAll("[^0-9.]", "");
        Stash.put(bonusKey, bonus);
        LOG.info("Записали в key [" + bonusKey + "] <== value [" + bonus + "]");
    }

    @Когда("^записываем значение баланса в \"([^\"]*)\"$")
    public void writeValueOfBalanceIn(String balanceKey) {
        List<WebElement> balanceElement = PageFactory.getWebDriver().findElements(By.id("topPanelWalletBalance"));
        String balance = balanceElement.isEmpty() ? "0" : balanceElement.get(0).getAttribute("innerText");
        Stash.put(balanceKey, balance);
        LOG.info("Записали в key [" + balanceKey + "] <== value [" + balance + "]");
    }

    @Когда("^проверяем что с баланса \"([^\"]*)\" снялась сумма \"([^\"]*)\"$")
    public void checkThatBalanceWasWithdrawnAmount(String balanceKey, String amountKey) {
        new WebDriverWait(PageFactory.getWebDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='userBalance']")));
        try {
            BigDecimal actualBalance = new BigDecimal(PageFactory.getWebDriver().findElement(By.id("topPanelWalletBalance")).getAttribute("innerText"));
            BigDecimal previousBalance = new BigDecimal(Stash.getValue(balanceKey).toString());
            BigDecimal withdrawnAmount = new BigDecimal(Stash.getValue(amountKey).toString());
            BigDecimal expectedBalance = previousBalance.subtract(withdrawnAmount);
            assertEquals("Ошибка! Ожидаемый баланс [" + expectedBalance.toString() + "] не равен текущему [" + actualBalance.toString() + "]", 0, expectedBalance.compareTo(actualBalance));
        } catch (NumberFormatException nf) {
            throw new AutotestError("Ошибка! Одно из полей с суммами оказалось пустым\n" + nf.getMessage());
        }
        BigDecimal actualBalance, previousBalance, withdrawnAmount, expectedBalance;
        actualBalance = new BigDecimal(PageFactory.getWebDriver().findElement(By.id("topPanelWalletBalance")).getAttribute("innerText"));
        previousBalance = new BigDecimal((String) Stash.getValue(balanceKey));
        withdrawnAmount = new BigDecimal((String) Stash.getValue(amountKey));
        expectedBalance = previousBalance.subtract(withdrawnAmount);
        assertThat(expectedBalance.compareTo(actualBalance) == 0).as("Ошибка! Ожидаемый баланс [" + expectedBalance.toString() + "] не равен текущему [" + actualBalance.toString() + "]").isTrue();
    }

    @Когда("^запрашиваем параметры способов пополения и сохраняем в память как \"([^\"]*)\"$")
    public void requestParametersOfRefillMethodsAndStoreToStash(String KeyInStash) {
//        String sqlRequest = "SELECT value FROM gamebet.params WHERE name ='" + KeyInStash + "'";
//        requestFromDBAndSaveToStash(sqlRequest,KeyInStash);
        String sqlRequest = "SELECT  NAME,max_amount FROM gamebet.`paymentservices` WHERE channel=1";
        workWithDBresultTwoRows(sqlRequest, KeyInStash);
    }

    private void requestFromDBAndSaveToStash(String sqlRequest, String KeyInStash) {
        String result = workWithDBgetResult(sqlRequest).replaceAll("\n", "");
        Stash.put(KeyInStash, result);
        LOG.info("Сохранили в память key [" + KeyInStash + "] <== value [" + result + "]");
    }

    @Когда("^запрос к WSS для нулевой маржи \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void requestByWSSAndSaveTest(String wSSPath, String KeyInStash) {
        requestToWSSTest(Stash.getValue(wSSPath), KeyInStash);
    }

    private void requestToWSSTest(String requestFull, String keyStash) {


        StringBuilder res = new StringBuilder();
        String therdRequest = "{\"command\":\"get\",\"params\":{\"source\":\"betting\",\"what\":{\"sport\":[\"id\",\"name\"],\"competition\":[\"id\",\"name\"],\"region\":[\"id\",\"name\"],\"game\":[\"id\",\"native_id\",\"start_ts\",\"team1_name\",\"team2_name\",\"team1_id\",\"team1_native_id\",\"team2_id\",\"team2_native_id\",\"type\",\"show_type\",\"info\",\"events_count\",\"markets_count\",\"is_blocked\",\"is_stat_available\",\"is_live\",\"zero_margin\"],\"event\":[\"id\",\"price\",\"type\",\"name\",\"order\",\"base\"],\"market\":[\"type\",\"express_id\",\"name\",\"base\"]},\"where\":{\"sport\":{\"id\":1},\"competition\":{\"id\":567},\"game\":{\"type\":{\"@in\":[0,2]},\"zero_margin\":true,\"is_blocked\":false},\"market\":{\"type\":{\"@include\":[\"MatchResult\",\"MatchResult\",\"OverUnder\",\"DoubleChance\",\"HalfTimeOverUnder\",\"2ndHalfTotalOver/Under\",\"AsianHandicap\"]}}},\"subscribe\":true},\"rid\":\"42-44-330-1554802387010-22\"}";

        try {
            WebSocket ws = connect(res, requestFull);
            ws.sendText(therdRequest);
            Thread.sleep(2000);
        } catch (IOException | WebSocketException | InterruptedException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new AutotestError("Ошибка! Проблемы с WebSocket.");
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result  = res.toString();
        LOG.info(result);
        if (!result.isEmpty()) {
            Stash.put(keyStash, result);
            Integer size_res = result.split("sport").length;
            Stash.put("key_size", size_res);

        } else {
            throw new AutotestError("Ошибка! По WSS получили[" + result + "]");
        }

    }




    @Когда("^запрос к WSS \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void requestByWSSAndSave(String wSSPath, String KeyInStash, DataTable dataTable) {
        requestToWSS(Stash.getValue(wSSPath), KeyInStash, dataTable);
    }

    private void requestToWSS(String requestFull, String keyStash, DataTable dataTable) {
        if (!(null == dataTable)) {
            Map<String, String> table = dataTable.asMap(String.class, String.class);
        }
        Object params = null;

        LOG.info("Собираем параметы в JSON строку");
        JSONObject jsonObject = new JSONObject();
        if (!(null == dataTable)) {
            params = collectParametersInJSONString(dataTable);
        }
        String therdRequest = "{\"command\":\"payment_services\",\"params\":{},\"rid\":\"" + Stash.getValue("RID") + "\"}";
        StringBuilder builder = new StringBuilder();

        try {
            WebSocket ws = connect(builder, requestFull);
            ws.sendText(JSONValue.toJSONString(params));
            Thread.sleep(2000);
            ws.sendText(therdRequest);
            Thread.sleep(2000);
        } catch (IOException | WebSocketException | InterruptedException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new AutotestError("Ошибка! Проблемы с WebSocket.");
        }
        String limits = builder.toString();

        if (!limits.isEmpty()) {
            Stash.put(keyStash, limits);
        } else {
            throw new AutotestError("Ошибка! По WSS получили[" + limits + "]");
        }
    }

    /**
     * Connect to the server.
     */
    private static WebSocket connect(StringBuilder tmp, String requestFull) throws IOException, WebSocketException, NoSuchAlgorithmException {

        StringBuilder builder = new StringBuilder();
        SSLContext context = NaiveSSLContext.getInstance("TLS");
        WebSocketFactory factory = new WebSocketFactory();
        factory.setSSLContext(context);
        return new WebSocketFactory()
                .setSSLContext(context)
                .setVerifyHostname(false)
                .setConnectionTimeout(5000)
                .createSocket(requestFull)
                .addListener(new WebSocketAdapter() {
                    // A text message arrived from the server.
                    public void onTextMessage(WebSocket websocket, String message) {
//                        if (message.contains("deposit")) {
//                            tmp.append(message);
//                            LOG.info("Получили ответ::" + tmp.toString());
//                        }
                        tmp.append(message);
                    }
                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
    }

    @Когда("^вычленяем из названия игры одно слово \"([^\"]*)\" \"([^\"]*)\"$")
    public void oneWordSearch(String keySearch, String type) {
        LOG.info(Stash.getValue("nameGameKey") + " время начала ");
        List<String> types = Stash.getValue("typeGameKey");
        int index = types.indexOf(type);
        List<String> names = Stash.getValue("nameGameKey");
        for (String str : names.get(index).split(" ")) {
            if (str.length() > 3) {
                Stash.put(keySearch, str);
                LOG.info(keySearch + ": " + str);
                break;
            }
        }
    }

    @Когда("^генерируем дату рождения от 18 до 50 лет и сохраняем в \"([^\"]*)\"$")
    public void generationRandomBerthDate(String keyBirthDate) {
        String birthDate = Generators.generateDateInRequiredRange();
        Stash.put(keyBirthDate, birthDate);
        LOG.info("Сохранили в память key [" + keyBirthDate + "] <== value [" + birthDate + "]");
    }

    @Когда("^генерируем дату выдачи паспорта в зависимости от \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public void generationPassportIssueDate(String keyBirthDate, String keyIssueDate) {
        String birthDate = Stash.getValue(keyBirthDate);
        String issueDate = null;
        try {
            issueDate = Generators.generatePassportIssueDate(birthDate);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AutotestError("Ошибка! Что-то не так с форматом даты");
        }
        Stash.put(keyIssueDate, issueDate);
        LOG.info("Сохранили в память key [" + keyIssueDate + "] <== value [" + issueDate + "]");
    }

    @Когда("^генерим номер карты и сохраняем в \"([^\"]*)\"$")
    public void generateCardNumberAndSaveTo(String keyCardNumber) {
        String numberCard = Generators.generateCardNumber();
        Stash.put(keyCardNumber, numberCard);
        LOG.info("Сохранили в память key [" + keyCardNumber + "] <== value [" + numberCard + "]");
    }

    @Когда("^генерим дату действия краты в \"([^\"]*)\"$")
    public void generateDateOfGard(String keyGardDate) {
        String gardDate = generateDateForGard();
        Stash.put(keyGardDate, gardDate);
        LOG.info("Сохранили в память key [" + keyGardDate + "] <== value [" + gardDate + "]");
    }

    @Когда("^генерим имя и фамилию на латинском в \"([^\"]*)\"$")
    public void translateNameAndFamilyToLatin(String kyeNameAndFamily) {
        String nameAndFamily = Generators.randomBigLatinString(8) + " " + Generators.randomBigLatinString(8);
        Stash.put(kyeNameAndFamily, nameAndFamily);
        LOG.info("Сохранили в память key [" + kyeNameAndFamily + "] <== value [" + nameAndFamily + "]");
    }

    /**
     * Метода переводит один формат даты в другой
     *
     * @old - исходный формат даты
     * @newFormat - необходимый формат даты
     * @oldDate - дата
     */
    public static String newFormatDate(SimpleDateFormat old, SimpleDateFormat newFormat, String oldDate) {
        String newDate = new String();
        if (oldDate.isEmpty() || oldDate == null) {
            LOG.info("Дату [" + oldDate + "] не удалось перевести");
            return null;
        } else {
            try {
                newDate = newFormat.format(old.parse(oldDate));
            } catch (ParseException e) {
                throw new AutotestError("Ошибка! Не удалось перевести дату [" + oldDate + "] в нужный формат [" + newFormat.toString() + "]");
            }
        }
        return newDate;
    }

    /**
     * Метода запрашивает у базы код SMS
     *
     * @requestToDB - строка SQL-запроса
     */
    public static String returnCode(String requestToDB) {
        return workWithDBgetResult(requestToDB, "code");
    }

    @Когда("^выбираем ФИО \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
    public void selectName(String keyName, String keySurName, String keyPatronymic) throws IOException {
        StringBuilder lal = new StringBuilder();
        FileReader file = new FileReader("src" + sep + "test" + sep + "resources" + sep + "FIOUsers.txt");
        Scanner scan = new Scanner(file);

        String user = scan.nextLine();
        String strLine;
        List<String> allLines = new ArrayList<>();
        while (scan.hasNext()) {
            allLines.add(scan.nextLine());
        }
        String separator = user.contains("\t") ? "\t" : "\\s";
        int randomNumber = new Random().nextInt(allLines.size() - 1) + 1;
        String name = allLines.get(randomNumber).split(separator)[1];
        randomNumber = new Random().nextInt(allLines.size() - 1) + 1;
        String surname = allLines.get(randomNumber).split(separator)[0];
        randomNumber = new Random().nextInt(allLines.size() - 1) + 1;
        String patronymic = allLines.get(randomNumber).split(separator)[2];

        Stash.put(keyName, name);
        Stash.put(keySurName, surname);
        Stash.put(keyPatronymic, patronymic);
        LOG.info("Выбранные ФИО: " + surname + " " + name + " " + patronymic);

        file.close();
    }

    @Когда("^вычисляем телефон \"([^\"]*)\"$")
    public void selectPhone(String keyPhone) throws IOException {

        StringBuilder lal = new StringBuilder();
        FileReader file = new FileReader("src" + sep + "test" + sep + "resources" + sep + "FIOUsers.txt");
        Scanner scan = new Scanner(file);

        String line = scan.nextLine();
        String phone = "700100" + String.valueOf(Integer.valueOf(line.substring(6)) + 1);
        LOG.info("Номер телефона" + phone);
        Stash.put(keyPhone, phone);

        lal.append(phone + "\n");
        while (scan.hasNext()) {
            lal.append(scan.nextLine()).append(System.lineSeparator());
        }

        FileWriter nfile = new FileWriter("src" + sep + "test" + sep + "resources" + sep + "FIOUsers.txt", false);
        nfile.write(lal.toString());
        nfile.close();
        file.close();
    }

    @Когда("^нажмем Продолжить регу на всякий$")
    public void rega() {
        PageFactory.getWebDriver().findElement(By.id("continue-registration")).click();
    }

    @Когда("^проверяем ТЕКСТ при переходе по ссылке с$")
    public static void checkTextWhenClickingOnLinkWith(DataTable dataTable) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String linkTitle, expectedText;
        String link = ""; //а это тут зачем?))
        String currentHandle = driver.getWindowHandle();

        for (Map<String, String> aTable : table) {
            linkTitle = aTable.get(LINK);
            expectedText = aTable.get(TEXT);
            String xpath = "//*[contains(text(),'" + expectedText + "')]";
            LOG.info("Переходим по клику на элемент " + linkTitle);
            try {
                opensNewTabAndChecksPresenceOFElement(linkTitle, currentHandle, xpath);
                Thread.sleep(500);
            } catch (TimeoutException e) {
                LOG.info("С первого раза ссылка " + linkTitle + " не открылась, попробуем второй раз");
                driver.close();
                driver.switchTo().window(currentHandle);
                driver.navigate().refresh();
                opensNewTabAndChecksPresenceOFElement(linkTitle, currentHandle, xpath);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    @Когда("^проверяем ТЕКСТ при переходе по ссылкам из футера$")
    public static void checkTextWhenClickingOnLinkWithFromFooter(DataTable dataTable) throws InterruptedException, PageException {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String linkTitle, expectedText;

        for (Map<String, String> aTable : table) {
            linkTitle = aTable.get(LINK);
            expectedText = aTable.get(TEXT);
            String xpath = "//*[contains(text(),'" + expectedText + "')]";
            LOG.info("Переходим по клику на элемент " + linkTitle);
            try {
                PageFactory.getInstance().getCurrentPage().getElementByTitle(linkTitle).click();
            } catch (Exception e) {
                Thread.sleep(3000);
                try {
                    PageFactory.getInstance().getCurrentPage().getElementByTitle(linkTitle).click();
                } catch (Exception exc) {
                    LOG.info("Не удалось кликнуть на " + linkTitle);
                    exc.printStackTrace();
                }
            }
            workWithPreloader();
            ArrayList<String> multipleTabs = new ArrayList<String>(driver.getWindowHandles());
            if (multipleTabs.size() >= 2) {
                switchHandle(false);
                Assert.assertFalse("Текст " + expectedText + " не обнаружен", driver.findElements(By.xpath(xpath)).isEmpty());
                LOG.info("Текст " + expectedText + " найден на странице, возвращаемся");
                switchHandle(true);
            } else {
                Assert.assertFalse("Текст " + expectedText + " не обнаружен", driver.findElements(By.xpath(xpath)).isEmpty());
                LOG.info("Текст " + expectedText + " найден на странице, возвращаемся");
                driver.get(Stash.getValue("MAIN_URL"));
                workWithPreloader();
            }
        }
    }

    /**
     * Метод меняет активную вкладку. Если требуется не только сменить активность, но и закрыть - передаём true.
     */
    public static void switchHandle(Boolean needClose) {
        ArrayList<String> multipleTabs = new ArrayList<String>(driver.getWindowHandles());
        String currentHandle = driver.getWindowHandle();
        String finalCurrentHandle = currentHandle;
        multipleTabs.removeIf(element -> element.equals(finalCurrentHandle));
        if (needClose == true) {
            driver.close();
        }
        driver.switchTo().window(multipleTabs.get(0));
    }

    public static void pressButton(String param) {
        Page page;
        WebElement button;
        try {
            page = PageFactory.getInstance().getCurrentPage();
            button = page.getElementByTitle(param);
            button.click();
            workWithPreloader();
        } catch (PageInitializationException e) {
            e.printStackTrace();
        } catch (PageException e) {
            throw new AutotestError("Ошибка! Не удалось нажать на копку [" + param + "]\n" + e.getMessage());
        }

    }

    @Когда("^очищаем избранное$")
    public void clearFavourite() throws Exception {
        clearFavouriteGames();
    }

    @Когда("^нажимает кнопку НАЗАД$")
    public void backToPage() {
        driver.navigate().back();
    }

    @Когда("^редактируем параметры сайта$")
    public void rememberParams(DataTable param) {
        Map<String, String> table = param.asMap(String.class, String.class);
        List<String> paramsList = new ArrayList<>();
        for (Map.Entry<String, String> entry : table.entrySet()) {
            String paramTitle = entry.getKey();
            paramsList.add(paramTitle);
        }
        Stash.put("paramsList", paramsList);
        for (Map.Entry<String, String> entry : table.entrySet()) {
            String newValue = entry.getValue();
            String paramTitle = entry.getKey();
            String sqlRequest = "SELECT * FROM gamebet.`params` WHERE title='" + paramTitle + "'";
            String oldValue = workWithDBgetResult(sqlRequest, "value");
            Stash.put("old " + paramTitle, oldValue);
            LOG.info("Запомнено старое значение параметра " + "old " + paramTitle + " " + oldValue);
            sqlRequest = "UPDATE gamebet.`params` SET value = '" + newValue + "' WHERE title='" + paramTitle + "'";
            workWithDB(sqlRequest);
            LOG.info("Установлено новое значение параметра " + paramTitle + " " + newValue);
        }
    }

    //    @Когда("^выставляем обратно старые значения параметров сайта$")
    public void removeParams() {
        List<String> params = Stash.getValue("paramsList");
        for (String p : params) {
            String sqlRequest = "UPDATE gamebet.`params` SET value = '" + Stash.getValue("old " + p) + "' WHERE title='" + p + "'";
            workWithDB(sqlRequest);
            sqlRequest = "SELECT * FROM gamebet.`params` WHERE title='" + p + "'";
            String newValue = workWithDBgetResult(sqlRequest, "value");
            LOG.info("Вернули значение параметра " + p + " " + newValue);
        }
    }

    @Когда("^включает симпл-баннер$")
    public void onSimpleBanner() {
        Calendar dateBegin = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();
        String activeStartTime, activeFinishTime;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        dateBegin.add(Calendar.DAY_OF_YEAR, -1);
        activeStartTime = format.format(dateBegin.getTime());
        dateEnd.add(Calendar.DAY_OF_YEAR, +1);
        activeFinishTime = format.format(dateEnd.getTime());
        String sqlRequest = "SELECT * FROM gamebet.`banner` WHERE bannertemplate_id=1 AND slider_id=1";
        String id = workWithDBgetResult(sqlRequest, "id");
        LOG.info("Нашли и запомнинил ID баннера");

        sqlRequest = "UPDATE gamebet.`banner` SET active=1,url='https://music.yandex.ru/tag/newyear',activeStartTime='" + activeStartTime + "',activeFinishTime='" + activeFinishTime + "' WHERE id=" + id;
        workWithDB(sqlRequest);

        LOG.info("Добавлен в автивные simple баннер с id = " + id);
    }


    @Когда("^включает матчевый баннер \"([^\"]*)\"$")
    public void onMatchBanner(String keyGameID) {
        Calendar dateBegin = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();
        String activeStartTime, activeFinishTime;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        dateBegin.add(Calendar.DAY_OF_YEAR, -1);
        activeStartTime = format.format(dateBegin.getTime());
        dateEnd.add(Calendar.DAY_OF_YEAR, +1);
        activeFinishTime = format.format(dateEnd.getTime());


        String sqlRequest = "SELECT * FROM gamebet.`banner` WHERE bannertemplate_id=3 AND slider_id=1";
        String id = workWithDBgetResult(sqlRequest, "id");
        String gameID = Stash.getValue(keyGameID);
        LOG.info("Нашли и запомнинил ID баннера");

        sqlRequest = "UPDATE gamebet.`banner` SET active=1,game_id=" + gameID + " ,activeStartTime='" + activeStartTime + "',activeFinishTime='" + activeFinishTime + "' WHERE id" + id;
        workWithDB(sqlRequest);

        LOG.info("Добавлен в автивные баннер с id = " + id + ", ведущий на игру gameID = " + gameID);
    }


    @Когда("^проверяем, совпадает ли дата и время игры с ожидаемыми \"([^\"]*)\" \"([^\"]*)\"$")
    public void checkDateTimeGame(String keyData, String typeGamekey) {
        String fullDateTime = Stash.getValue(keyData).toString().replace("\n", " ");
        String typeGame = Stash.getValue(typeGamekey);
        switch (typeGame) {
            case "live":
                LOG.info("Судя по времени, указанному на баннере, игра должна быть лайвовской. Проверять будем только что раздел соответствует ЛАЙВу " + fullDateTime);
                Assert.assertTrue(
                        "Раздел ЛАЙВ не активен",
                        driver.findElement(By.xpath("//*[@id='live']/..")).getAttribute("class").contains("active"));
                break;
            case "prematch":
                LOG.info("Судя по времени, указанному на баннере, игра должна быть прематчевской. Проверять будем и дату, и время игры в ПРЕМАТЧе " + fullDateTime);
                Assert.assertTrue(
                        "Раздел ПРЕМАТЧ не активен",
                        driver.findElement(By.xpath("//*[@id='prematch']/..")).getAttribute("class").contains("active"));
                String dateTimeGameOnP = driver.findElement(By.xpath("//div[contains(@class,'prematch-competition-games_active')]//*[contains(@class,'prematch-competition-games__item-date')]")).getAttribute("innerText");
                SimpleDateFormat formatPrematch = new SimpleDateFormat("hh:mm - dd MMM yyyy");
                Calendar datePrematch = Calendar.getInstance();
                Calendar dateTimeGame = Stash.getValue(keyData);

                try {
                    datePrematch.setTime(formatPrematch.parse(dateTimeGameOnP));
                    Assert.assertTrue(
                            "Время игры на баннере и на странице ПРЕМАТЧ не свопадает. На баннере: " + fullDateTime + ", в прематче: " + dateTimeGameOnP,
                            datePrematch.equals(dateTimeGame));
                } catch (ParseException e) {
                    LOG.info("Не удалось распарсить дату и время игры, указанные на баннере. Возможно не совпадает формат: " + dateTimeGameOnP + ". Формат: " + formatPrematch);
                    e.printStackTrace();
                }
                break;
        }

    }


    @Когда("^проверяем, совпадают ли коэффициенты на странице с теми, что на баннере \"([^\"]*)\"$")
    public void checkAllCoefs(String keyCoefs) {
        LOG.info("Формируем список коэффициентов для маркета ИСход на странице игры");
        List<String> coefsOnPage =
                driver.findElements(By.xpath("//div[contains(@class,'game-center-container__live')]//div[contains(@class,'bets-block__bet-cell_active')]/..//span[contains(@class,'bets-block__bet-cell-content-price')]"))
                        .stream().map(el -> el.getAttribute("innerText") + "%").collect(Collectors.toList());
        if (coefsOnPage.size()==2){
            coefsOnPage.add(1,"—%");
        }
        LOG.info(coefsOnPage + "\n% - это просто разделитель. все норм");
        LOG.info("Теперь сравним этот список,с тем что было на баннере");
        List<String> coefsOnBanners = Stash.getValue(keyCoefs);
        Assert.assertTrue(
                "Коэффициенты на странице и на баннере не совпадают. \n" + coefsOnPage + " вместо; \n" + coefsOnBanners,
                coefsOnBanners.equals(coefsOnPage)
        );
        LOG.info("Коэффициенты на беннере и на странице совпадают");
    }


    //выставление даты начала (например в истории операций или в моих пари) на самую раннюю из возможных
    @Когда("^отматывает дату начала на самую раннюю$")
    public void datapickerOnBegin() {
        new WebDriverWait(driver,15).until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[contains(@class,'datepicker__form') and position()=1]"),0));
        WebElement myBets = driver.findElement(xpath("//div[contains(@class,'subMenuArea subMenuArea_fullwidth subMenuArea_fullheight')]"));
        if(myBets.getAttribute("class").contains("active")){
            driver.findElements(By.xpath("//span[contains(@class,'datapicker__form-text')]")).get(0).click();
        } else {
            driver.findElements(By.xpath("//span[contains(@class,'datapicker__form-text')]")).get(2).click();
        }
//        WebElement datapickerBegin = driver.findElement(By.xpath("//div[contains(@class,'datepicker__form')]"));
//
//            new WebDriverWait(driver, 10)
//                    .withMessage("Дата начала не раскрылась")
//                    .until(ExpectedConditions.attributeContains(datapickerBegin, "class", "active"));

        LOG.info("Если доступно - нажимаем на стрелочку 'год назад'");
        By BYarrowLeft;
        for (int lineNumber = 1; lineNumber <= 2; lineNumber++) {
            BYarrowLeft = By.xpath("//div[@class='datepicker__line' and position()=" + lineNumber + "]//i[contains(@class,'arrow-left6')]");

            while (!driver.findElement(BYarrowLeft).findElement(By.xpath("ancestor-or-self::div[contains(@class,'datepicker__btn')]")).getAttribute("class").contains("bound")) {
                driver.findElement(BYarrowLeft).click();
            }
        }
        LOG.info("Год и месяц отщелкали на начало. Теперь день выбирем самый ранний");
        driver.findElement(By.xpath("//div[contains(@class,'datepicker__day-btn') and not(contains(@class,'disabled'))]")).click();
        CommonStepDefs.workWithPreloader();
    }


    private void getScreenshot(Scenario scenario){
        LOG.info("fail? " + scenario.isFailed());
        if (scenario.isFailed()) {
            LOG.info("screenshot!!! ");
            final byte[] screenshotCoupon = ((TakesScreenshot) PageFactory.getWebDriver()).getScreenshotAs(OutputType.BYTES);
            InputStream targetStream = new ByteArrayInputStream(screenshotCoupon);
            Allure.addAttachment("Результат",targetStream);
        }
    }

    @After(value = "@AzbukaBettingaLinks_C76652")
    public void closeSecondWindow(Scenario scenario) throws InterruptedException {
        ((JavascriptExecutor) PageFactory.getWebDriver()).executeScript("second_window.close()");
    }

    @After(value = "@coupon")
    public void clearCouponAfter(Scenario scenario) throws InterruptedException {
        getScreenshot(scenario);
        goToMainPage("site");
        driver.findElement(By.id("prematch")).click(); //переходим в прематч
        driver.navigate().refresh();
        workWithPreloader();
        Thread.sleep(5000);
        LOG.info("Перешли в прематч и сейчас будет чистить купон");
        AbstractPage.clearCoupon();
        LOG.info("Очистили купон");
    }

    public static ExpectedCondition<Boolean> elementIsOnPage(final By locator, final String messages) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                return (!driver.findElements(locator).isEmpty());
            }

            @Override
            public String toString() {
                return messages;
            }
        };
    }


    @Когда("^комментарий \"([^\"]*)\"$")
    public void loginfo(String message){
        LOG.info("\n\n\u001B[33m" + message + "\n\n\u001B[37m");
    }

    @Когда("^запрос в swagger на пополнение баланса рублей/бонусов \"([^\"]*)\"$")
    public void refill(String responseKey, DataTable dataTable) throws DataException {
        String path = JsonLoader.getData().get(STARTING_URL).get("SWAGGER").getValue();
        path=path+"/refill";
        LOG.info("path:" + path);
        requestByHTTPS(path,responseKey,"POST",dataTable);
    }

    @Когда("^запрос в swagger для получения getHolds \"([^\"]*)\" \"([^\"]*)\"$")
    public void getHolds(String HoldKeyRub,String HoldKeyBonus, DataTable dataTable) throws DataException {
        String path = JsonLoader.getData().get(STARTING_URL).get("SWAGGER").getValue();
        path=path+"/getHolds";
        LOG.info("path:" + path);
        requestByHTTPS(path,"RESPONSE_SWAGGER","POST",dataTable);
        LOG.info("Теперь запоминаем сколько захолдировано средств");
        fingingAndSave("DATA","RESPONSE_SWAGGER");
        List<HashMap> dataMap = Stash.getValue("DATA");
        String amount = new String();
        long holdRub = 0;
        long holdBonus = 0;
        for (HashMap<String,String> mapOne: dataMap){
            amount = String.valueOf(mapOne.get("amount"));
            switch (String.valueOf(mapOne.get("currency"))){
                case "643":
                    LOG.info("hold рублей " + amount);
                    holdRub+=Long.valueOf(amount);
                    break;
                case "999":
                    LOG.info("hold бонусов " + amount);
                    holdBonus+=Long.valueOf(amount);
                    break;
            }
        }
        LOG.info("Итого захолдировано рублей " + holdRub + " и бонусов " + holdBonus);
        Stash.put(HoldKeyRub,holdRub);
        Stash.put(HoldKeyBonus,holdBonus);
    }


    @Когда("^запрос в swagger на добавление НДФЛ \"([^\"]*)\"$")
    public void addNDFL(String responseKey,DataTable dataTable) throws DataException {
        String path = JsonLoader.getData().get(STARTING_URL).get("SWAGGER").getValue();
        path=path+"/createPacket";
        LOG.info("path:" + path);
        requestByHTTPS(path,responseKey,"POST",dataTable);
    }


    @Когда("^формируем параметры для запроса swagger$")
    public void generateBodyRequest(DataTable dataTable){
        Map<String, String> bodyRequest = dataTable.asMap(String.class,String.class);
        String value = new String();
        Date dateNow = new Date();
        for (String param : bodyRequest.keySet()) {
                value = String.valueOf(dateNow.getTime());
                Stash.put(bodyRequest.get(param),value);
        }
    }

    @Когда("^добавляем ссылку главного меню \"([^\"]*)\"$")
    public void onmenuLink(String namePage) {
        String sqlRequest = "UPDATE gamebet.`mainmenulink` SET active = 1 WHERE name='" + namePage + "'";
        workWithDB(sqlRequest);
        LOG.info("Включили отображение ссылки главного меню: " + namePage);

    }
    @Когда("^проверим что \"([^\"]*)\" больше \"([^\"]*)\" на \"([^\"]*)\"$")
    public void checkDifferenceBetweenNumbers(String keyNum1,String keyNum2,String diff){
         long firstN = Long.parseLong(String.valueOf(Stash.getValue(keyNum1).toString()));
         long secondN = Long.parseLong(String.valueOf(Stash.getValue(keyNum2).toString()));
         String difference = diff.replace("-","");
         long diffLong = diff.matches("-*[A-Z]*")
                 ?Integer.valueOf(Stash.getValue(diff.replace("-","")))
                 :Integer.valueOf(diff.replace("-",""));
         if (diff.contains("-")){
            diffLong=0-diffLong;
         }
         Assert.assertEquals("Разница между числами не такая, как ожидалось: " + firstN + "  " + secondN,
                 firstN-diffLong,secondN);
         LOG.info("Разница между между числами " + firstN + "," + secondN + " совпадает с ожиданием <" + diffLong + ">");
    }
}

