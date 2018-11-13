package ru.gamble.stepdefs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.neovisionaries.ws.client.*;
import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Когда;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.utility.DBUtils;
import ru.gamble.utility.Generators;
import ru.gamble.utility.JsonLoader;
import ru.gamble.utility.NaiveSSLContext;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.sbtqa.tag.stepdefs.GenericStepDefs;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import javax.net.ssl.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
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
import static org.springframework.util.StreamUtils.BUFFER_SIZE;
import static org.springframework.util.StreamUtils.drain;
import static ru.gamble.pages.AbstractPage.preferences;
import static ru.gamble.pages.AbstractPage.preloaderOnPage;
import static ru.gamble.utility.Constants.*;
import static ru.gamble.utility.Generators.generateDateForGard;


public class CommonStepDefs extends GenericStepDefs {
    private static final Logger LOG = LoggerFactory.getLogger(CommonStepDefs.class);
    private static final String sep = File.separator;

    @ActionTitle("нажимает на кнопку")
    public static void pressButton(String param) {
        Page page;
        WebElement button;
        try {
            page = PageFactory.getInstance().getCurrentPage();
            button = page.getElementByTitle(param);
            button.click();
            workWithPreloader();
        } catch (PageException e) {
            throw new AutotestError("Ошибка! Не удалось нажать на копку [" + param + "]\n" + e.getMessage());
        }

    }

    @Когда("^запрашиваем дату-время и сохраняем в память$")
    public static void requestAndSaveToMamory(DataTable dataTable){
        List<String> data = dataTable.asList(String.class);
        String key, value, date;
        key = data.get(0);
        value = data.get(1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (value.equals("Current")) {
            date = formatter.format(System.currentTimeMillis());
            Stash.put(key,date);
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
        if (value.equals(RANDOM)) {
            value = Generators.randomString(25);
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

        if(value.equals(RANDOME_SEX)){
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
        WebDriver driver = PageFactory.getWebDriver();
        WebDriverWait driverWait = new WebDriverWait(driver, 6, 500);
        try {
            driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
            List<WebElement> preloaders = driver.findElements(by);
            LOG.info("Найдено прелоадеров [" + preloaders.size() + "]");
            driverWait.until(ExpectedConditions.invisibilityOfAllElements(preloaders));
            LOG.info("Прелоадеры закрылись");
        }catch (Exception e){
        }
    }

    @Когда("^разлогиниваем пользователя$")
    public void logOut(){
        WebDriver driver = PageFactory.getWebDriver();
        LOG.info("Переход на главную страницу");
        goToMainPage("site");
        cleanCookies();
        if(driver.getCurrentUrl().contains("mobile")){
            mobileSiteLogOut(driver);
        }else{
            descktopSiteLogOut(driver);
        }
    }

    private void descktopSiteLogOut(WebDriver driver){
        try {
            LOG.info("Ищем наличие кнопки с силуетом пользователя.");
            new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(By.id("user-icon")));
            LOG.info("Нажимаем на кнопку с силуетом пользователя.");
            driver.findElement(By.id("user-icon")).click();
            Thread.sleep(1000);
            LOG.info("Ищем кнопку 'Выход' и нажимаем");
            driver.findElement(By.id("log-out-button")).click();
        }catch (Exception e){
            LOG.info("На сайте никто не авторизован");
        }
    }

    private void mobileSiteLogOut(WebDriver driver){
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
        }catch (Exception e){
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
            default:
                currentUrl = siteUrl;
                break;
        }
            PageFactory.getWebDriver().get(currentUrl);
            LOG.info("Перешли на страницу [" + currentUrl + "]");
        }catch (DataException e) {
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
    public static void getUserID(String key,String keyEmail) {
        String sqlRequest = "SELECT id FROM gamebet. `user` WHERE email='" + Stash.getValue(keyEmail) + "'";
        String id = workWithDBgetResult(sqlRequest, "id");
        Stash.put(key,id);
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
        for (String windowHandle : PageFactory.getWebDriver().getWindowHandles()) {
            PageFactory.getWebDriver().switchTo().window(windowHandle);
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
        WebDriver driver = PageFactory.getDriver();
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
        WebDriver driver = PageFactory.getDriver();
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
    public void checkLinkToGame(){
        WebDriver driver = PageFactory.getDriver();
        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.id("menu-toggler")));
        workWithPreloader();
        boolean flag = true;
        boolean haveButton = Stash.getValue("haveButtonKey");
        String team1 = Stash.getValue("team1BTkey");
        String team2 = Stash.getValue("team2BTkey");
        String sportName = Stash.getValue("sportKey");

        if (haveButton) {
            String sportis = driver.findElement(By.xpath("//div[@class='live-game-summary']/div[1]/div[1]/div[1]/div[contains(@class,'game-info')]")).getAttribute("class").replace("game-info game-info_", "");
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
            String gameName = driver.findElement(By.xpath("//div[contains(@class,'live-container')]//span[contains(@class,'game-center-container__inner-text')]")).getAttribute("title");
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
        WebDriver driver = PageFactory.getDriver();
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
        if(PageFactory.getWebDriver().manage().getCookies().size()>0) {
            LOG.info("Удаляем Cookies");
            PageFactory.getWebDriver().manage().deleteAllCookies();
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
        WebDriver driver = PageFactory.getDriver();
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
        WebDriver driver = PageFactory.getDriver();
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
     * @param element
     * @throws Exception
     */
    public static void waitEnabled(WebElement element){
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
        String actual = JSONValue.toJSONString(Stash.getValue(keyStash));
        String expected = table.get("exepted");
        assertThat(actual).as("ОШИБКА! Ожидался ответ |" + expected + "| в |" + actual + "|").contains(expected);
        LOG.info("|" + expected + "| содержится в |" + actual + "|");
    }



    @Когда("^выбираем одну дату из \"([^\"]*)\" и сохраняем в \"([^\"]*)\" а id_user в \"([^\"]*)\"$")
    public void selectOneDateInResponce(String keyResponce, String keyDate, String keyId) throws ParseException {
        SimpleDateFormat oldFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        String actual = JSONValue.toJSONString(Stash.getValue(keyResponce));
        actual = actual.replace("{\"code\":0,\"data\":","").replace("}","");
        String[] linesResponce = actual.split("swarmUserId");
        int i = new Random().nextInt(linesResponce.length);
        String idUser = linesResponce[i].split(",")[0].replace("\":","");
        String dateForUser = null;
        if (actual.contains("videoIdentDate")){
            int a = linesResponce[i].replaceAll("\"","").indexOf("videoIdentDate");
            dateForUser = linesResponce[i].replaceAll("\"","").substring(a+15,a+34);
            oldFormat = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
            newFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        }
        if (actual.contains("skypeSendDate")){
            int a = linesResponce[i].replaceAll("\"","").indexOf("skypeSendDate");
            dateForUser = linesResponce[i].replaceAll("\"","").substring(a+14,a+30);
            oldFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");
            newFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        }

        LOG.info("Выбранная дата: " + dateForUser + ", для юзера с id = " + idUser);

        LOG.info("Отнимем от даты одну минуту");
        Calendar newDateTime = new GregorianCalendar();

        newDateTime.setTime(oldFormat.parse(dateForUser));
        newDateTime.add(Calendar.MINUTE,-1);
        LOG.info("Теперь переведем дату в нужны формат");
        dateForUser = newFormat.format(newDateTime.getTime()).replace(" ","T")+":00";

        Stash.put(keyDate,dateForUser);
        Stash.put(keyId,idUser);

        LOG.info("Новая дата: " + dateForUser);
    }


    @Когда("^проверка что в ответе \"([^\"]*)\" нет юзера с \"([^\"]*)\"$")
    public void checkResponceNotConains(String keyResponce, String keyId){
        String actual = JSONValue.toJSONString(Stash.getValue(keyResponce));
        String userId = Stash.getValue(keyId);
        Assert.assertFalse("В ответе есть пользователь "  + userId + ", хотя он не вписывается в заданные ts и ts_end:" + Stash.getValue("PARAMS"),
                actual.contains("\"swarmUserId\":" + userId));
        LOG.info("В ответе действительно теперь нет записи о пользователе с id=" + userId);
    }

    @Когда("^проверка что в ответе \"([^\"]*)\" верные даты  \"([^\"]*)\":$")
    public void checkResponceAPIgoodDate(String keyStash, String keyParams) throws ParseException{
        String actual = JSONValue.toJSONString(Stash.getValue(keyStash));
        String params =  Stash.getValue(keyParams).toString();
        SimpleDateFormat formatTS = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat formatResponse = new SimpleDateFormat("dd.MM.yyy hh:mm");
        String ts = params.split("ts=")[1].substring(0,16).replace("T"," ");
        Date tsDate = formatTS.parse(ts);
        Date tsEndDate = new Date();
        String ts_end = null;
        if (params.contains("ts_end")){
            ts_end = params.split("ts_end=")[1].substring(0,16).replace("T"," ");
            tsEndDate = formatTS.parse(ts_end);
        }

        String dateInResponceString = new String();

        Date dateInResponce = new Date();
        boolean a;
        for (int i=1; i<actual.split("\"skypeSendDate"+"\":").length; i++){
            dateInResponceString = actual.split("\"skypeSendDate"+"\":")[i].split("}")[0].replaceAll("\"","");
            dateInResponce = formatResponse.parse(dateInResponceString);
            Assert.assertTrue("В ответе есть результаты, выходящие за пределы ts-ts_end (" + ts + "   ---   " + ts_end + ")"
                    +":\n" + dateInResponceString,
                    dateInResponce.after(tsDate) && dateInResponce.before(tsEndDate));
        }
        LOG.info("Да, все результаты запроса вписываются по времени в значения ts и ts_end");
    }

    @Когда("^проверка что ответ \"([^\"]*)\" \"([^\"]*)\"$")
    public void checkResponceFill(String keyStash,String isEmpty) throws ParseException{
        String actual = JSONValue.toJSONString(Stash.getValue(keyStash));
        actual = actual.replace("{\"code\":0,\"data\":","").replace("}","");
        boolean expectedEmpty=isEmpty.equals("пустой");
        Assert.assertTrue("Ожидалось что результат будет " + isEmpty + ", но это не так.\n" + actual,
                actual.equals("[]")==expectedEmpty);
        LOG.info("Да, RESPONCE действительно " + isEmpty);
    }


    @Когда("^проверка вариантного ответа API из \"([^\"]*)\":$")
    public void checkresponceAPIor(String keyStash, DataTable dataTable) {
        Map<String, String> table = dataTable.asMap(String.class, String.class);
        String actual = JSONValue.toJSONString(Stash.getValue(keyStash));
        String expected = table.get("exepted");
        boolean actualsOk = actual.contains(expected.split("or")[0].trim()) || actual.contains(expected.split("or")[1].trim());
        Assert.assertTrue("ОШИБКА! Ожидался ответ |" + expected + "| в |" + actual + "|",actualsOk);
        LOG.info("|" + expected + "| содержится в |" + actual + "|");
    }

    @Когда("^находим и сохраняем \"([^\"]*)\" из \"([^\"]*)\"$")
    public void fingingAndSave(String keyFingingParams, String sourceString) {
        String tmp;
        Object valueFingingParams, retMap = null;
        ObjectMapper mapper = new ObjectMapper();

        //Преобразуем в строку JSON-объект в зависимости от его структуры
        if(JSONValue.isValidJson(Stash.getValue(sourceString).toString())){
            tmp  = Stash.getValue(sourceString).toString();
        }else {
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
        valueFingingParams = JsonLoader.hashMapper(retMap, keyFingingParams);
        LOG.info("Достаем значение [" + keyFingingParams + "] и записываем в память [" + JSONValue.toJSONString(valueFingingParams) + "]");
        Stash.put(keyFingingParams, valueFingingParams);
    }



    private static Map<String,String> workWithDBgetTwoRows(String sqlRequest) {
        Connection con = DBUtils.getConnection();
        Statement stmt;
        PreparedStatement ps = null;
        ResultSet rs;
        Map<String,String> result = new HashMap<>();
        try {
            con.setAutoCommit(false);// Отключаем автокоммит
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlRequest);
            con.commit();
           // rs.last();
            while (rs.next()){
                result.put(rs.getString(1),rs.getString(2));
            }

            if(result.isEmpty()){
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

            if(result.isEmpty()){
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
            result = rs.getString(1).replaceAll("\n","").replaceAll(" +"," ");
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
     * @param sqlRequest
     * @return
     */
    private static void workWithDBresult(String sqlRequest){
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
            if(count == 0){
                throw new AutotestError("Ошибка! Запрос к базе [" + sqlRequest + "] вернул кол-во результатов [" + count + "]");
            }
            for (int i = 1; i <= count; i++){
                String key = allRows.getColumnName(i);
                String value = rs.getString(key);
                for (String part: key.split("_")){
                    keyNormal.append(part);
                }
                LOG.info(keyNormal + "=" + value);
                if(keyNormal.toString().toUpperCase().equals("PASSWORD")){
                    continue;
                }
                Stash.put(keyNormal.toString().toUpperCase(),value);
                keyNormal.setLength(0);
            }
            } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(con, null, null);
        }
    }

    @Когда("^получаем и сохраняем в память код подтверждения \"([^\"]*)\" телефона \"([^\"]*)\" \"([^\"]*)\"$")
    public static void confirmPhone(String keyCode, String keyPhone, String type) {
        String phone = Stash.getValue(keyPhone);
        String sqlRequest;
        if (type.equals("новый")){
            sqlRequest="SELECT code FROM gamebet. `useroperation` WHERE user_id IN (SELECT id FROM gamebet. `user` WHERE phone='" + phone + "') ORDER BY creation_date";
        }
        else {
            sqlRequest="SELECT code FROM gamebet. `phoneconfirmationcode` WHERE phone='" + phone + "' ORDER BY creation_date";
        }
        String code = workWithDBgetResult(sqlRequest, "code");
        Stash.put(keyCode, code);
        LOG.info("Получили код подтверждения телефона: " + code);
    }

    @Когда("^получаем и сохраняем в память код \"([^\"]*)\" подтверждения почты \"([^\"]*)\"$")
    public static void confirmEmail(String keyEmailCode, String keyEmail) {
        String email = Stash.getValue(keyEmail);
        String sqlRequest = "SELECT id FROM gamebet.`user` WHERE email='" + email + "'";
        String userId = workWithDBgetResult(sqlRequest, "id");
        sqlRequest = "SELECT code FROM gamebet.`useremailconfirmationcode`  WHERE user_id=" + userId;
        String code = workWithDBgetResult(sqlRequest, "code");
        Stash.put(keyEmailCode, code);
        LOG.info("Получили код подтверждения почты: " + code);
    }

    @Когда("^определяем валидную и невалидную дату выдачи паспорта \"([^\"]*)\" \"([^\"]*)\"$")
    public void issueDateGenerate(String validKey, String invalidKey) throws ParseException {
        String birthDateString = Stash.getValue("BIRTHDATE");
        SimpleDateFormat formatDate = new SimpleDateFormat();
        formatDate.applyPattern("dd.MM.yyyy");
        if (birthDateString.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")){
            SimpleDateFormat formatgut = new SimpleDateFormat();
            formatgut.applyPattern("yyyy-MM-dd");
            birthDateString=formatDate.format(formatgut.parse(birthDateString));
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
            data=formatgut.format(formatDate.parse(data));
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
            //Если попадются числовые значения, в JSON объект кладём как строку
            if (value instanceof String && !StringUtils.isBlank((String) value) &&  ((String) value).matches("[0-9]+")) {
                String str  = (String) value;
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
    public static void confirmEmail(String keyPhone) {
        String sqlRequest = "SELECT phone FROM gamebet.`user` WHERE phone LIKE '7111002%' ORDER BY phone";
        String phoneLast = workWithDBgetResult(sqlRequest, "phone");
        String phone = "7111002" + String.format("%4s",Integer.valueOf(phoneLast.substring(7))+1).replace(' ','0');
        Stash.put(keyPhone, phone);
        LOG.info("Вычислили подходящий номер телефона::" + phone);
    }

    @Когда("^берем \"([^\"]*)\"$")
    public static void confirmEmail2(String keyPhone) throws Exception{
        String sqlRequest = "SELECT phone FROM gamebet.`user` WHERE phone LIKE '7111002%' ORDER BY phone";
        String phoneLast = ww(sqlRequest, "phone");
        String phone = "7111002" + String.format("%4s",Integer.valueOf(phoneLast.substring(7))+1).replace(' ','0');
        Stash.put(keyPhone, phone);
        LOG.info("Вычислили подходящий номер телефона::" + phone);
    }

    public static String ww (String sqlRequest, String param) throws Exception {

        Connection con = null;

        String url, user, password;
        JSch jsch = new JSch();
        String proxyHost = "test-int-bet-dbproca.tsed.orglot.office";
        String proxyUser = "tzavaliy";
        String proxyPassword = "2007-12g";

        String nameBD = "gamebet";
           url = "test-int-bet-dbproc.tsed.orglot.office";
//        url = "localhost";
        user = JsonLoader.getData().get(STARTING_URL).get("DB_REGISTRATION").get("DB_USER").getValue();
        password = JsonLoader.getData().get(STARTING_URL).get("DB_REGISTRATION").get("DB_PASSWORD").getValue();

        String result = null;
        int localPort = 3366;
        Session session = jsch.getSession(proxyUser, proxyHost, 22);
        try {

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(proxyPassword);

            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            session.connect();
            session.setPortForwardingL(localPort, url, 3306);


            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "UTF-8");
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("connectTimeout", "6000");
            properties.setProperty("socketTimeout", "2000");
            properties.setProperty("maxReconnects", "1");
            properties.setProperty("retriesAllDown", "1");
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            con = DriverManager.getConnection(
                    "jdbc:mysql://" + url + ":" + localPort + "/" + nameBD , properties);

            Statement stmt;
            PreparedStatement ps = null;
            ResultSet rs;



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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.disconnect();
        }
        return result;
    }



    @Когда("^подтверждаем скайп через админку \"([^\"]*)\"$")
    public void skypeConfirm(String keyPhone) throws Exception{
        WebDriver driver = PageFactory.getDriver();
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
        int yy  =driver.findElement(By.id("confirmSkypeMenuBtn-btnWrap")).getLocation().getY() + 116;
        int xx = driver.findElement(By.id("confirmSkypeMenuBtn-btnWrap")).getLocation().getX() + 156;
        Robot robot = new Robot();
        robot.mouseMove(xx,yy);
        robot.delay(500);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        driver.findElement(By.id("confirmSkypeBtn")).click();
        driver.findElement(By.id("saveUserBtn-btnIconEl")).click();
    }



    @Когда("^в админке смотрим id пользователя \"([^\"]*)\" \"([^\"]*)\"$")
    public void getIDFromAdmin(String keyPhone,String keyId) throws Exception{
        WebDriver driver = PageFactory.getDriver();
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
        Stash.put(keyId,id);


    }


    @Когда("^определяем user_id пользователя \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public static void findUserId(String keyEmail, String keyId) {
        String sqlRequest = "SELECT id FROM gamebet.`user` WHERE email = '" + Stash.getValue(keyEmail) + "'";
        String id = workWithDBgetResult(sqlRequest, "id");
        Stash.put(keyId, id);
        LOG.info("Вычислили id::" + id);
    }


    @Когда("^смотрим изменился ли \"([^\"]*)\" из \"([^\"]*)\"$")
    public void checkTimeLeft(String keyTimeLeft,String keyResponse) {
        Integer timewasS =Stash.getValue(keyTimeLeft);
        fingingAndSave(keyTimeLeft,keyResponse);
        Integer timenowS = Stash.getValue(keyTimeLeft);
        if (timenowS.compareTo(timewasS)>=0){
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
     * @param dataTable - таблица проверяемых параметров
     */
    @Когда("^проверка полей и типов в ответе \"([^\"]*)\":$")
    public void checkFieldsAndTypesInResponse(String keyJSONObject, DataTable dataTable) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String param, type, currentValue;
        Object json =  Stash.getValue(keyJSONObject);

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
            }if (type.equals("Integer")) {
                Integer.valueOf(value);
                return true;
            }if (type.equals("Timestamp")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                Date date = new Date(Long.valueOf(value));
                simpleDateFormat.format(date);
                return true;
            }if(type.equals("Boolean")){
                Boolean.valueOf(value);
                return true;
            }
            if(type.equals("String")) {
                String.valueOf(value);
                return true;
            }
            if(type.equals("List")){
                List<Object> items = Collections.singletonList(JSONValue.parse(value));
                return true;
            }
            return false;
    }

    @Когда("^поиск акаунта со статуом регистрации \"([^\"]*)\" \"([^\"]*)\"$")
    public void searchUserStatus2(String status,String keyEmail) {
        String sqlRequest = "SELECT * FROM gamebet.`user` WHERE email LIKE 'testregistrator+7111%' AND registration_stage_id" + status + " AND tsupis_status=3 AND offer_state=3";
        searchUser(keyEmail,sqlRequest);
    }

    @Когда("^ищем пользователя с ограничениями \"([^\"]*)\"$")
    public void searchUserAlt(String keyEmail) {
        //String sqlRequest = "SELECT * FROM gamebet.`user` WHERE email LIKE 'testregistrator+7111%' AND registration_stage_id"+status + " AND tsupis_status=3 and personal_data_state=3 AND offer_state=3";
        String sqlRequest = "SELECT * FROM gamebet.`user` WHERE identState=1 AND registration_stage_id=2 AND phone LIKE '7111002%'";
        searchUser(keyEmail,sqlRequest);
    }

    @Когда("^обновляем оферту пользователю \"([^\"]*)\" \"([^\"]*)\"$")
    public static void offertUpdate(String offer_state,String keyEmail) {
        String sqlRequest = "UPDATE gamebet.`user` SET offer_state=" + offer_state + " WHERE `email` = '" + Stash.getValue(keyEmail) + "'";
        workWithDB(sqlRequest);
    }

    @Когда("^запоминаем дату рождения пользователя \"([^\"]*)\" \"([^\"]*)\"$")
    public static void rememberBirthDate(String keyBD,String keyEmail) throws ParseException {
        String sqlRequest = "SELECT birth_date FROM gamebet.`user` WHERE email='"+Stash.getValue(keyEmail) + "'";
        String birthDate = workWithDBgetResult(sqlRequest, "birth_date");
        SimpleDateFormat formatDate = new SimpleDateFormat();
        SimpleDateFormat formatgut = new SimpleDateFormat();
        formatgut.applyPattern("dd.MM.yyyy");
        formatDate.applyPattern("yyyy-MM-dd");
        birthDate=formatgut.format(formatDate.parse(birthDate));
        Stash.put(keyBD, birthDate);
        LOG.info("Дата рождения: " + birthDate);
    }

    @Когда("^проверяем значение полей в ответе \"([^\"]*)\":$")
    public void checkValueOfFieldsInResponse(String keyJSONObject, DataTable dataTable) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String param, value, currentValue, tmp;
        Object json =  Stash.getValue(keyJSONObject);

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
//        RandomAccessFile fr = new RandomAccessFile("src" + sep +"test" + sep + "resources"+ sep + "full_alt.txt", "r");
//
//        String line;
//        StringBuilder sbt = new StringBuilder();
//        String user = fr.readLine();
//        String separator = user.contains("\t") ?"\t":"\\s";
//        String phone = user.trim().split(separator)[0];
//        String birthDate = user.trim().split(separator)[1];
//        SimpleDateFormat formatDate = new SimpleDateFormat();
//        SimpleDateFormat formatgut = new SimpleDateFormat();
//        formatgut.applyPattern("dd.MM.yyyy");
//        formatDate.applyPattern("yyyy-MM-dd");
//        birthDate=formatgut.format(formatDate.parse(birthDate));
//        Stash.put(keyPhone,phone);
//        Stash.put(keyBD,birthDate);
//        Thread.sleep(500);
//        while ((line = fr.readLine()) != null){
//            sbt.append(line).append(System.lineSeparator());
//        }
//        FileWriter fw = new FileWriter("src" + sep +"test" + sep + "resources"+ sep + "full_alt.txt");
//        BufferedWriter bw = new BufferedWriter(fw);
//        LOG.info("перезаписываем файл " + "src" + sep +"test" + sep + "resources"+ sep + "full_alt.txt");
//        Thread.sleep(500);
//        bw.write(sbt.toString());
//        bw.flush();
//        bw.close();
//        fr.close();
//        LOG.info(phone + " " +birthDate);
//
//
//        if ( new RandomAccessFile("src" + sep +"test" + sep + "resources"+ sep + "full_alt.txt", "r").readLine().trim().split(separator)[0].equals(phone)){
//            Assert.fail("Все плохо. файл опть не перезаписался!");
//        }




        StringBuilder lal = new StringBuilder();
        FileReader file = new FileReader("src" + sep +"test" + sep + "resources"+ sep + "full_alt.txt");
        Scanner scan = new Scanner(file);
//lal.append(scan.next());

        String user = scan.nextLine();
        String separator = user.contains("\t") ?"\t":"\\s";
        String phone = user.trim().split(separator)[0];
        String birthDate = user.trim().split(separator)[1];
        SimpleDateFormat formatDate = new SimpleDateFormat();
        SimpleDateFormat formatgut = new SimpleDateFormat();
        formatgut.applyPattern("dd.MM.yyyy");
        formatDate.applyPattern("yyyy-MM-dd");
        birthDate=formatgut.format(formatDate.parse(birthDate));
        Stash.put(keyPhone,phone);
        Stash.put(keyBD,birthDate);

        while (scan.hasNext()){
            lal.append(scan.nextLine()).append(System.lineSeparator());
        }

        FileWriter nfile = new FileWriter("src" + sep +"test" + sep + "resources"+ sep + "full_alt.txt",false);
        nfile.write(lal.toString());
        nfile.close();
        file.close();

    }

    @Когда("^поиск пользователя проходившего ускоренную регистрацию \"([^\"]*)\"$")
    public void searchUserNotPD(String keyEmail) {
        String sqlRequest = "SELECT * FROM gamebet.`user` WHERE tsupis_status IN (1,2) AND personal_data_state=1 AND email LIKE 'testregistrator+%'";
        searchUser(keyEmail,sqlRequest);
    }

    @Когда("^сбрасываем пользователю статус ПД до \"([^\"]*)\" \"([^\"]*)\"$")
    public static void updatePDState(String peronal_data_state,String keyEmail) {
        String sqlRequest = "UPDATE gamebet.`user` SET personal_data_state=" + peronal_data_state + " WHERE `email` = '" + Stash.getValue(keyEmail) + "'";
        workWithDB(sqlRequest);
    }

    @Когда("^обновляем поля в БД для пользователя \"([^\"]*)\":$")
    public void NullToField(String keyEmail, DataTable dataTable) {

        Map<String, String> table = dataTable.asMap(String.class, String.class);
        StringBuilder setter = new StringBuilder();
        table.forEach((key, value) -> setter.append(key).append("=").append(value).append(","));
        LOG.info(setter.toString());
        String sqlRequest = "UPDATE gamebet.`user` SET " + setter.delete(setter.length()-1,setter.length()).toString() +  " WHERE email = '" + Stash.getValue(keyEmail) + "'";
        workWithDB(sqlRequest);
    }

    @Когда("^достаём случайную видеотрансляцию из списка \"([^\"]*)\" и сохраняем в переменую \"([^\"]*)\"$")
    public void getRandomTranslationFromListAndSaveToVariable(String keyListTranslation, String keyGameId) {
        Map<String, Object> map;
        String key;
        Object value, selectedObject = null;
        ObjectMapper oMapper = new ObjectMapper();
        Object json =  Stash.getValue(keyListTranslation);
        map = oMapper.convertValue(json, Map.class);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            selectedObject = ((Map) value).entrySet().toArray()[new Random().nextInt(((Map) value).size())];
        }
            Stash.put(keyGameId, selectedObject);
    }

    @Когда("^достаём параметр из \"([^\"]*)\" и сохраняем в переменую:$")
    public void takeParamFromAndSaveInVariable (String keyJSONObject, DataTable dataTable) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String param, keyVariable, currentValue;
        Object json =  Stash.getValue(keyJSONObject);
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
        requestByHTTPS(fullPath,keyStash,"POST",dataTable);
    }

    @Когда("^запрос по прямому адресу \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void requestTo(String fullPath, String keyStash, DataTable dataTable) {
        requestByHTTPS(fullPath,keyStash,"POST",dataTable);
    }

    @Когда("^запрос к API \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public void requestToAPI(String path, String keyStash) {
        String fullPath = collectQueryString(path);
        requestByHTTPS(fullPath, keyStash,"GET",null);
    }

    @Когда("^запрос к IMG \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public void requestToIMGAndSaveIn(String path, String keyStash) {
        String fullPath = (Stash.getValue(path)).toString().replaceAll("\\\\","");
        requestByHTTPS(fullPath, keyStash,"GET",null);
    }


    @Когда("^запрос типа COLLECT \"([^\"]*)\" c параметрами \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public void requestCollect(String path, String keyParams, String keyStash) {
        try {
            StringBuilder fullPath = new StringBuilder();
            fullPath.append(JsonLoader.getData().get(STARTING_URL).get("ESB_URL").getValue() +  "/" + path);
            fullPath.append("?" + Stash.getValue(keyParams));
            LOG.info("Строчка запроса: " + fullPath);
            requestByHTTPGet(fullPath.toString(), keyStash);
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    @Когда("^формирум параметры и сохраняем в \"([^\"]*)\"$")
    public void collectParametrs(String keyParams, DataTable dataTable){
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
            if (value.matches("[A-Z]*")){
                value="fromStash";
            }

            switch (value){
                case "прошлый месяц":
                    dateNow.add(Calendar.DAY_OF_YEAR,-30);
                    value = formatDate.format(dateNow.getTime()) + "T" + formatTime.format(Calendar.getInstance().getTime());
                    break;
                case "текущая дата-время":
                    value = formatDate.format(dateNow.getTime()) + "T" + formatTime.format(Calendar.getInstance().getTime());
                    break;
                case "следующий месяц":
                    dateNow.add(Calendar.DAY_OF_YEAR,30);
                    value = formatDate.format(dateNow.getTime()) + "T" + formatTime.format(Calendar.getInstance().getTime());
                    break;
                case "fromStash":
                    value = Stash.getValue(entry.getValue());
                    break;
            }
            params.append(key+"="+value+"&");
        }
        LOG.info("Параметры получились: " + params);
        Stash.put(keyParams,params);

    }



    private String collectQueryString(String path){
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


    protected void requestByHTTPGet(String requestFull, String keyStash){

        HttpURLConnection connect = null;
        try{
            connect = (HttpURLConnection) new URL(requestFull).openConnection();
            connect.setRequestMethod("GET");
            connect.setUseCaches(false);
            connect.setConnectTimeout(250);
            connect.setReadTimeout(250);
            connect.connect();

            StringBuilder jsonString = new StringBuilder();
            if (HttpURLConnection.HTTP_OK == connect.getResponseCode()){
                BufferedReader in  = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                String line;
                while ((line=in.readLine())!=null){
                    jsonString.append(line);
                    jsonString.append("\n");
                }
                LOG.info(jsonString.toString());
                Stash.put(keyStash, JSONValue.parse(jsonString.toString()));
            }
            else {
                LOG.info("fail" + connect.getResponseCode() + ", " + connect.getResponseMessage());
                Stash.put(keyStash, JSONValue.parse(connect.getResponseMessage()));}
            jsonString.toString();
        }
        catch (Throwable cause){
            cause.printStackTrace();
        }
        finally {
            if (connect!=null){
                connect.disconnect();
            }
        }
    }

    protected void requestByHTTPS(String requestFull, String keyStash, String method, DataTable dataTable) {
        if(!(null == dataTable)) { Map<String, String> table = dataTable.asMap(String.class, String.class); }
        Object params = null;
        URL url;

        LOG.info("Собираем параметы в JSON строку");
        JSONObject jsonObject = new JSONObject();
        if(!(null == dataTable)) {
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
//            SSLContext sc = SSLContext.getInstance("SSL");
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            //************

            url = new URL(requestFull);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod(method);
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
            if(!(null == dataTable)) { writer.write(String.valueOf(params)); }
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
        switch (typeOS){
            case "Android":
                type="1";
                break;
            case "IOS":
                type="2";
                break;
            default:
                type=typeOS;
        }
        Stash.put(keyTypeOS,type);
        String sqlDel = "DELETE FROM  gamebet.`appversion` WHERE (version=" + vers + " OR hard_update_version=" + hardVers + ") AND type_os=" + type + " AND active_version=0";
        workWithDB(sqlDel);
        String     sqlRequest = "UPDATE gamebet.`appversion` SET version=" + vers + ", hard_update_version=" + hardVers + " WHERE active_version=1 AND type_os=" + type;
               // String sqlRequest = "INSERT INTO gamebet.`appversion` (VERSION,apk,hard_update_version,active_version,type_os) VALUES("+vers+", 'android.apk',"+hardVers+",'1',"+type+")";
        workWithDB(sqlRequest);
    }

    private void searchUser(String keyEmail, String sqlRequest){
    if (keyEmail.equals("ALLROWS")){
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
        String newPhone = "7222" + oldPhone.substring(4,11);
        Stash.put(keyNewPhone,newPhone);
        LOG.info("Новый нмоер телефона: " + newPhone);
    }

    @Когда("^смотрим какое время обновления баннера \"([^\"]*)\"$")
    public void delayGromBanner(String keyDelay) {
        String sqlRequest = "SELECT delay FROM gamebet.`bannerslider` WHERE NAME='index_main_default'";
        String delay = workWithDBgetResult(sqlRequest, "delay");
        Stash.put(keyDelay,delay);
    }

    @Когда("^пользователь открывает новый url \"([^\"]*)\"$")
    public void userOpenNewUrl(String url){
        PageFactory.getDriver().get(url);
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
    public void emulationRegistrationFromTerminalWave(String path, String keyStash, DataTable dataTable){
        String fullPath = collectQueryString(path);
        requestByHTTPS(fullPath, keyStash, "POST", dataTable);
    }


    /**
     * это когда активне опции сайта в отдельной таблице
     */
    @Before(value = "@NewUserRegistration_C36189,@MobileNewUserRegistration_C36189,@api,@mobile,@ExpressBonus_C39773")
    public void saveRegistrationValue2(){
        rememberEnabledFeatures("ACTIVE_SITE_OPTIONS");
    }

    @Before()
    public void titleTest(Scenario scenario){
        LOG.info("<================START...TEST================>");
        LOG.info("NAME: " + scenario.getName());
        LOG.info("TAGS: " + scenario.getSourceTagNames());
        LOG.info("ID: " + scenario.getId().replaceAll("\\D+","") );
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
    public void rememberEnabledFeatures (String activeOptionKey){
        Map<String,String> activeOpt =
                workWithDBgetTwoRows("SELECT NAME,state FROM gamebet.`enabled_features`");
        Stash.put(activeOptionKey,activeOpt);
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

        Map<String,String> table = dataTable.asMap(String.class,String.class);
        for (Map.Entry<String, String> entry : table.entrySet()) {
            if (entry.getValue().equals("true")) {
                optionTrue.append(",'"+entry.getKey() + "'");
                LOG.info("Добавление активной опций сайта " + entry.getKey());
            }
            else {
                optionFalse.append(",'" + entry.getKey() + "'");
                LOG.info("Удаление активной опций сайта " + entry.getKey());
            }
        }
        optionTrue.delete(0,1);
        optionFalse.delete(0,1);
        if(!optionTrue.toString().isEmpty()) {
            sqlRequest = "UPDATE gamebet.`enabled_features` SET state=1 WHERE NAME in (" + optionTrue + ")";
            workWithDB(sqlRequest);
            LOG.info("Включили опиции [" + optionTrue.toString() + "]");
        }
        if(!optionFalse.toString().isEmpty()) {
            sqlRequest = "UPDATE gamebet.`enabled_features` SET state=0 WHERE NAME in (" + optionFalse + ")";
            workWithDB(sqlRequest);
            LOG.info("Выключили опиции [" + optionFalse.toString() + "]");
        }
    }

    /**
     * когда активные опции сайта в таблице
     * @param key
     */
    @Когда("^выставляем обратно старое значение активных опций сайта \"([^\"]*)\"$")
    public void revertEnabledFeatures(String key){
        Map<String,String> oldEnabledFeatures = (HashMap)Stash.getValue(key);
        StringBuilder optionFalse = new StringBuilder();
        StringBuilder optionTrue = new StringBuilder();
        for (Map.Entry<String, String> entry : oldEnabledFeatures.entrySet()) {
            if (entry.getValue().equals("1")) {
                optionTrue.append(",'" + entry.getKey() + "'");
                LOG.info("Добавление активной опций сайта " + entry.getKey());
            }
            else {
                optionFalse.append(",'" + entry.getKey() + "'");
                LOG.info("Удаление активной опций сайта " + entry.getKey());
            }
        }
        optionTrue.delete(0,1);
        optionFalse.delete(0,1);
        String sqlRequest = "UPDATE gamebet.`enabled_features` SET state=1 WHERE NAME in (" + optionTrue + ")";
        workWithDB(sqlRequest);
        sqlRequest = "UPDATE gamebet.`enabled_features` SET state=0 WHERE NAME in (" + optionFalse + ")";
        workWithDB(sqlRequest);
        Map<String,String> activeOpt =
                workWithDBgetTwoRows("SELECT NAME,state FROM gamebet.`enabled_features`");
        LOG.info("Вернули активные опции сайта [" + activeOpt + "]");
        Assert.assertTrue("Не удалось вернуть опции сайта к старому значению. было: " + oldEnabledFeatures +
        "\n!!!!!!!!!!!!!!!!!\nCтало:" + activeOpt, activeOpt.equals(oldEnabledFeatures));
    }



    /**
     * Возвращаем активные опции сайста в исходное положение до тестов
     * @param scenario
     */
    @After(value = "@NewUserRegistration_C36189,@ExpressBonus_C39773")
    public void returnRegistrationValueWithScreenshot(Scenario scenario){
        LOG.info("возвращаем значение активных опций сайта из памяти по ключу 'ACTIVE_SITE_OPTIONS'");
        revertEnabledFeatures("ACTIVE_SITE_OPTIONS");
        if(scenario.isFailed()) {
            final byte[] screenshot = ((TakesScreenshot) PageFactory.getWebDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/jpeg");
        }
    }

    /**
     * Возвращаем десятичный тип отображения коэффициентов
     */
    @After(value = "@ChangeTypeOfCoefficientOnMain_C1066,@ChangeTypeOfCoefficientCoupon_C1066,@ChangeTypeOfCoefficientFav_C1066")
    public void returnDecTypeCoef(){
        WebDriver driver = PageFactory.getDriver();
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
     * @param scenario
     */
    @After(value = "@0Registration_mobile,@requestVideoChatConfirmation,@1Registration_fullalt_mobile,@requestPhoneCall, @requestVideoChatConfirmation,@mobile")
    public void returnRegistrationValue(Scenario scenario){
        LOG.info("возвращаем значение активных опций сайта из памяти по ключу 'ACTIVE_SITE_OPTIONS'");
        revertEnabledFeatures("ACTIVE_SITE_OPTIONS");
    }


    @Когда("^определяем дату завтрашнего дня \"([^\"]*)\"$")
    public void tomorrowDate(String keyParams){
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE,1);
        Date dateTomorrow = new Date();
        dateTomorrow.setTime(today.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String date = format.format(dateTomorrow);
        Stash.put(keyParams,date);
        LOG.info("Завтрашняя дата: " + dateTomorrow);
    }


    @Когда("^запоминаем текущую страницу в \"([^\"]*)\"$")
    public void rememberCurenPageTo(String keyCurrentPage){
        Stash.put(keyCurrentPage,PageFactory.getDriver().getWindowHandle());
        LOG.info("Записали key [" + keyCurrentPage + "] <== value [" + PageFactory.getDriver().getWindowHandle() + "]");
    }

    @Когда("^закрываем текущее окно и возвращаемся на \"([^\"]*)\"$")
    public void closingCurrentWinAndReturnTo(String keyPage) {
        try {
            WebDriver driver = PageFactory.getWebDriver();
            driver.close();
            driver.switchTo().window(Stash.getValue(keyPage));
            LOG.info("Вернулись на ранее запомненую страницу");
        }catch (Exception e){
            throw new AutotestError("Ошибка! Не смогли вернуться на страницу.");
        }
    }

    @Когда("^записываем значение баланса бонусов в \"([^\"]*)\"$")
    public void writeValueOfBonusBalanceIn(String bonusKey) {
        List<WebElement> bonusElement = PageFactory.getWebDriver().findElements(By.xpath("//span[contains(@class,'subMenuBonus bonusmoney-text')]"));
        String bonus = bonusElement.isEmpty() ? "0" : bonusElement.get(0).getAttribute("innerText").replaceAll("[^0-9.]","");
        Stash.put(bonusKey,bonus);
        LOG.info("Записали в key [" + bonusKey + "] <== value [" + bonus + "]");
    }

    @Когда("^записываем значение баланса в \"([^\"]*)\"$")
    public void writeValueOfBalanceIn(String balanceKey) {
        List<WebElement> balanceElement = PageFactory.getWebDriver().findElements(By.id("topPanelWalletBalance"));
        String balance = balanceElement.isEmpty() ? "0" : balanceElement.get(0).getAttribute("innerText");
        Stash.put(balanceKey,balance);
        LOG.info("Записали в key [" + balanceKey + "] <== value [" + balance + "]");
    }

    @Когда("^проверяем что с баланса \"([^\"]*)\" снялась сумма \"([^\"]*)\"$")
    public void checkThatBalanceWasWithdrawnAmount(String balanceKey, String amountKey) {
        new WebDriverWait(PageFactory.getWebDriver(),10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='userBalance']")));
        try {
            BigDecimal actualBalance = new BigDecimal(PageFactory.getWebDriver().findElement(By.id("topPanelWalletBalance")).getAttribute("innerText"));
            BigDecimal previousBalance = new BigDecimal(Stash.getValue(balanceKey).toString());
            BigDecimal withdrawnAmount = new BigDecimal(Stash.getValue(amountKey).toString());
            BigDecimal expectedBalance = previousBalance.subtract(withdrawnAmount);
            assertEquals("Ошибка! Ожидаемый баланс [" + expectedBalance.toString() + "] не равен текущему [" + actualBalance.toString() + "]", 0, expectedBalance.compareTo(actualBalance));
        }catch (NumberFormatException nf){
           throw new AutotestError("Ошибка! Одно из полей с суммами оказалось пустым\n" + nf.getMessage());
        }
        BigDecimal actualBalance,previousBalance,withdrawnAmount,expectedBalance;
        actualBalance = new BigDecimal(PageFactory.getWebDriver().findElement(By.id("topPanelWalletBalance")).getAttribute("innerText"));
        previousBalance = new BigDecimal((String) Stash.getValue(balanceKey));
        withdrawnAmount = new BigDecimal((String) Stash.getValue(amountKey));
        expectedBalance = previousBalance.subtract(withdrawnAmount);
        assertThat(expectedBalance.compareTo(actualBalance) == 0).as("Ошибка! Ожидаемый баланс [" + expectedBalance.toString() + "] не равен текущему [" + actualBalance.toString() + "]").isTrue();
    }

    @Когда("^запрашиваем параметры способов пополения и сохраняем в память как \"([^\"]*)\"$")
    public void requestParametersOfRefillMethodsAndStoreToStash(String KeyInStash){
        String sqlRequest = "SELECT value FROM gamebet.params WHERE name ='" + KeyInStash + "'";
        requestFromDBAndSaveToStash(sqlRequest,KeyInStash);
    }

    private void requestFromDBAndSaveToStash(String sqlRequest, String KeyInStash){
        String result = workWithDBgetResult(sqlRequest).replaceAll("\n","");
        Stash.put(KeyInStash,result);
        LOG.info("Сохранили в память key [" + KeyInStash + "] <== value [" + result + "]");
    }

    @Когда("^запрос к WSS \"([^\"]*)\" и сохраняем в \"([^\"]*)\":$")
    public void requestByWSSAndSave(String wSSPath, String KeyInStash, DataTable dataTable){
        requestToWSS(wSSPath,KeyInStash,dataTable);


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
        String therdRequest = "{\"command\":\"payment_services\",\"params\":{},\"rid\":\"15355431498522\"}";
        StringBuilder builder = new StringBuilder();

        try {
            WebSocket ws = connect(builder,requestFull);
            ws.sendText(JSONValue.toJSONString(params));
            Thread.sleep(2000);
            ws.sendText(therdRequest);
            Thread.sleep(2000);
        } catch (IOException | WebSocketException | InterruptedException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new AutotestError("Ошибка! Проблемы с WebSocket.");
        }
        String limits = builder.toString();

        if(!limits.isEmpty()){
            Stash.put(keyStash,limits);
        }else {
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
                        if(message.contains("deposit")) {
                            tmp.append(message);
                            LOG.info("Получили ответ::" + tmp.toString());
                        }
                    }
                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
    }

    @Когда("^вычленяем из названия игры одно слово \"([^\"]*)\" \"([^\"]*)\"$")
    public void oneWordSearch(String keySearch,String type){
        LOG.info(Stash.getValue("nameGameKey") + " время начала ");
        List <String> types = Stash.getValue("typeGameKey");
        int index = types.indexOf(type);
        List<String> names = Stash.getValue("nameGameKey");
        for (String str:names.get(index).split(" ")){
            if (str.length()>3) {
                Stash.put(keySearch,str);
                LOG.info(keySearch  + ": " + str);
                break;
            }
        }
    }

    @Когда("^генерируем дату рождения от 18 до 50 лет и сохраняем в \"([^\"]*)\"$")
    public void generationRandomBerthDate(String keyBirthDate){
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
        Stash.put(keyIssueDate,issueDate);
        LOG.info("Сохранили в память key [" + keyIssueDate + "] <== value [" + issueDate + "]");
    }

    @Когда("^генерим номер карты и сохраняем в \"([^\"]*)\"$")
    public void generateCardNumberAndSaveTo(String keyCardNumber) {
        String numberCard = Generators.generateCardNumber();
        Stash.put(keyCardNumber,numberCard);
        LOG.info("Сохранили в память key [" + keyCardNumber + "] <== value [" + numberCard + "]");
    }

    @Когда("^генерим дату действия краты в \"([^\"]*)\"$")
    public void generateDateOfGard(String keyGardDate) {
        String gardDate = generateDateForGard();
        Stash.put(keyGardDate,gardDate);
        LOG.info("Сохранили в память key [" + keyGardDate + "] <== value [" + gardDate + "]");
    }

    @Когда("^генерим имя и фамилию на латинском в \"([^\"]*)\"$")
    public void translateNameAndFamilyToLatin(String kyeNameAndFamily) {
        String nameAndFamily = Generators.randomBigLatinString(8) + " " + Generators.randomBigLatinString(8);
        Stash.put(kyeNameAndFamily,nameAndFamily);
        LOG.info("Сохранили в память key [" + kyeNameAndFamily + "] <== value [" + nameAndFamily + "]");
    }

    /**
     * Метода переводит один формат даты в другой
     * @old - исходный формат даты
     * @newFormat - необходимый формат даты
     * @oldDate - дата
     */
    public static String newFormatDate(SimpleDateFormat old, SimpleDateFormat newFormat, String oldDate){
        String newDate = new String();
        if(oldDate.isEmpty() || oldDate == null){
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
     * @requestToDB - строка SQL-запроса
     */
    public static String returnCode(String requestToDB){
        return workWithDBgetResult(requestToDB, "code");
    }

    @Когда("^выбираем ФИО \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
    public void selectName(String keyName, String keySurName, String keyPatronymic) throws IOException {
        StringBuilder lal = new StringBuilder();
        FileReader file = new FileReader("src" + sep +"test" + sep + "resources"+ sep + "FIOUsers.txt");
        Scanner scan = new Scanner(file);

        String user = scan.nextLine();
        String strLine;
        List<String> allLines = new ArrayList<>();
        while (scan.hasNext()){
            allLines.add(scan.nextLine());
        }
        String separator = user.contains("\t") ?"\t":"\\s";
        int randomNumber = new Random().nextInt(allLines.size()-1)+1;
        String name = allLines.get(randomNumber).split(separator)[1];
        randomNumber = new Random().nextInt(allLines.size()-1)+1;
        String surname = allLines.get(randomNumber).split(separator)[0];
        randomNumber = new Random().nextInt(allLines.size()-1)+1;
        String patronymic = allLines.get(randomNumber).split(separator)[2];

        Stash.put(keyName,name);
        Stash.put(keySurName,surname);
        Stash.put(keyPatronymic,patronymic);
        LOG.info("Выбранные ФИО: " + surname + " " + name + " " + patronymic);

        file.close();
    }

    @Когда("^вычисляем телефон \"([^\"]*)\"$")
    public void selectPhone(String keyPhone) throws IOException{

        StringBuilder lal = new StringBuilder();
        FileReader file = new FileReader("src" + sep +"test" + sep + "resources"+ sep + "FIOUsers.txt");
        Scanner scan = new Scanner(file);

        String line = scan.nextLine();
        String phone = "700100"+String.valueOf(Integer.valueOf(line.substring(6)) + 1);
        LOG.info("Номер телефона" + phone);
        Stash.put(keyPhone,phone);

        lal.append(phone + "\n");
        while (scan.hasNext()){
            lal.append(scan.nextLine()).append(System.lineSeparator());
        }

        FileWriter nfile = new FileWriter("src" + sep +"test" + sep + "resources"+ sep + "FIOUsers.txt",false);
        nfile.write(lal.toString());
        nfile.close();
        file.close();
    }

    @Когда("^нажмем Продолжить регу на всякий$")
    public void rega(){
        PageFactory.getWebDriver().findElement(By.id("continue-registration")).click();
    }

}

