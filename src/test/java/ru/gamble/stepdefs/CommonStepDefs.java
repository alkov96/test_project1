package ru.gamble.stepdefs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.*;
import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Когда;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.utility.DBUtils;
import ru.gamble.utility.Generators;
import ru.gamble.utility.JsonLoader;
import ru.gamble.utility.NaiveSSLContext;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.sbtqa.tag.stepdefs.GenericStepDefs;

import javax.net.ssl.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.By.xpath;
import static ru.gamble.utility.Constants.*;


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
            LOG.error(e.getMessage());
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
            value = "testregistrator+" + Stash.getValue("PHONE") + "@yandex.ru";
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
        String xpathPreloader = "//*[contains(@class,'preloader__container')]";
        waitShowElement(By.xpath(xpathPreloader));
    }

    // Ожидание появления элемента на странице
    public static void waitShowElement(By by) {
        WebDriver driver = PageFactory.getWebDriver();
        WebDriverWait driverWait = new WebDriverWait(driver, 6, 500);
        try {
            driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
            List<WebElement> preloaders = driver.findElements(by);
            LOG.info("Найдено прелоадеров::" + preloaders.size());
            driverWait.until(ExpectedConditions.invisibilityOfAllElements(preloaders));
            LOG.info("Прелоадеры закрылись");
        }catch (Exception e){
        }
    }

    @Когда("^разлогиниваем пользователя$")
    public static void logOut(){
        WebDriver driver = PageFactory.getWebDriver();
        goToMainPage("site");
        cleanCookies();
        try {
            LOG.info("Ищем кнопку с силуетом пользователя.");
            new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(By.id("user-icon")));
            List<WebElement> userIcon = PageFactory.getWebDriver().findElements(By.id("user-icon"))
                    .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
            if(!userIcon.isEmpty()){
                LOG.info("Нажимаем на кнопку с силуетом пользователя.");
                userIcon.get(0).click();
                Thread.sleep(1000);
                LOG.info("Ищем кнопку выхода");
                List<WebElement> logOutButton = PageFactory.getWebDriver().findElements(By.id("log-out-button"))
                        .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
                if(!logOutButton.isEmpty()) {
                    LOG.info("Нажимаем на кнопку выхода");
                    logOutButton.get(0).click();
                }
            }
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
                currentUrl = JsonLoader.getData().get(STARTING_URL).get("MAIN_URL").getValue();
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
            PageFactory.getDriver().get(currentUrl);
            LOG.info("Перешли на страницу ==>[" + currentUrl + "]");
        }catch (DataException e) {
            LOG.error(e.getMessage());
        }

    }

    @Когда("^сохраняем в память таблицу$")
    public static void saveKeyValueTable(DataTable dataTable) {
        Map<String, String> date = dataTable.asMap(String.class, String.class);
        int birthDay, birthMonth, birthYear;
        String berthdayDate;
    }

    /**
     * Генератор e-mail
     *
     * @param key - ключ по которому сохраняем е-mail в памяти.
     */
    @Когда("^генерим email в \"([^\"]*)\"$")
    public static void generateEmailAndSave(String key) {
        String value = "testregistrator+" + System.currentTimeMillis() + "@yandex.ru";
        LOG.info("Сохраняем в память key:" + key + "|value::" + value);
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
        if(PageFactory.getWebDriver().manage().getCookies().size()>0) {
            try {
                LOG.info("Удаляем Cookies");
                PageFactory.getWebDriver().manage().deleteAllCookies();
            } catch (Exception e) {
                LOG.error("Cookies не было!");
            }
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
    public static void waitEnabled(WebElement element) throws Exception {
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
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
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


    private static String workWithDBgetResult(String sqlRequest, String param) {
        Connection con = DBUtils.getConnection();
        Statement stmt;
        PreparedStatement ps = null;
        ResultSet rs;
        String result;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlRequest);
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
        RandomAccessFile fr = new RandomAccessFile("src" + sep +"test" + sep + "resources"+ sep + "full_alt.txt", "r");

        String line;
        StringBuilder sbt = new StringBuilder();
        String user = fr.readLine();
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
        while ((line = fr.readLine()) != null){
            sbt.append(line).append(System.lineSeparator());
        }
        FileWriter fw = new FileWriter("src" + sep +"test" + sep + "resources"+ sep + "full_alt.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(sbt.toString());
        bw.flush();
        bw.close();
        fr.close();
        LOG.info(phone + " " +birthDate);
    }

    @Когда("^поиск пользователя проходившего ускоренную регистрацию \"([^\"]*)\"$")
    public void searchUserNotPD(String keyEmail) {
        String sqlRequest = "SELECT * FROM gamebet.`user` WHERE tsupis_status IN (1,2) AND personal_data_state=1 AND email LIKE 'testregistrator+%@yandex.ru'";
        searchUser(keyEmail,sqlRequest);
    }

    @Когда("^сбрасываем пользователю статус ПД до \"([^\"]*)\" \"([^\"]*)\"$")
    public static void updatePDState(String peronal_data_state,String keyEmail) {
        String sqlRequest = "UPDATE gamebet.`user` SET personal_data_state=" + peronal_data_state + " WHERE `email` = '" + Stash.getValue(keyEmail) + "'";
        workWithDB(sqlRequest);
    }

    @Когда("^обновляем поля в БД для юзера \"([^\"]*)\":$")
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
        String sqlRequest = "UPDATE gamebet.`appversion` SET version=" + vers + ", hard_update_version=" + hardVers + " WHERE active_version=1 AND type_os=" + type;
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

    @Когда("^включаем экспресс-регистрацию$")
    public void onExpressReg() {
        String sqlRequest = "SELECT * FROM gamebet.`params` WHERE NAME='ENABLED_FEATURES'";
        String activeOpt = workWithDBgetResult(sqlRequest, "value");
        if (!activeOpt.contains("fast_registration")){
            sqlRequest = "UPDATE gamebet.`params` SET value='" + activeOpt + ", fast_registration' WHERE NAME='ENABLED_FEATURES'";
            workWithDB(sqlRequest);
        }
    }

    @Когда("^включаем экспресс-бонус через SQL$")
    public void onExpressBonus() {
        String sqlRequest = "SELECT * FROM gamebet.`params` WHERE NAME='ENABLED_FEATURES'";
        String activeOpt = workWithDBgetResult(sqlRequest, "value");
        if (!activeOpt.contains("express_bonus")){
            sqlRequest = "UPDATE gamebet.`params` SET value='" + activeOpt + ", express_bonus' WHERE NAME='ENABLED_FEATURES'";
            workWithDB(sqlRequest);
        }
    }

    @Когда("^переходим на мобильную версию сайта$")
    public void goToSiteAndTurnOnEmulationMode(){
        WebDriver driver = PageFactory.getWebDriver();
        try {
            driver.get(JsonLoader.getData().get(STARTING_URL).get("MOBILE_URL").getValue());
        } catch (DataException e) {
            e.printStackTrace();
            throw new AutotestError("Ошибка! Не смогли перейти на url мобильной версии сайта");
        }
    }

    @Когда("^закрываем браузер$")
    public static void closeBrowser() {
        PageFactory.dispose();
        LOG.info("Браузер закрыт");
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


    @Когда("^добавляем активную опцию сайта \"([^\"]*)\"$")
    public void addActive(String option) {
        String sqlRequest = "SELECT * FROM gamebet.`params` WHERE NAME='ENABLED_FEATURES'";
        String activeOpt = workWithDBgetResult(sqlRequest, "value");
        if (!activeOpt.contains(option)){
            sqlRequest = "UPDATE gamebet.`params` SET value='" + activeOpt + ", " + option + "' WHERE NAME='ENABLED_FEATURES'";
            workWithDB(sqlRequest);
        }
    }

    @Before(value = "@NewUserRegistration_C36189,@api,@mobile")
    public void saveRegistrationValue(){
        String activeOptionKey = "ACTIVE";
        String activeOpt = getActiveOptions();
        Stash.put(activeOptionKey,activeOpt);
        LOG.info("Записали в память: key=>[" + activeOptionKey + "] ; value=>[" + activeOpt + "]");
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


    @Когда("^запоминаем значение активных опций сайта в \"([^\"]*)\"$")
    public void rememberActive (String activeOptionKey){
        LOG.info("В памяти лежит: key=>[" + activeOptionKey + "] ; value=>[" + Stash.getValue(activeOptionKey) + "]");
    }

    private String getActiveOptions(){
        return workWithDBgetResult("SELECT * FROM gamebet.`params` WHERE NAME='ENABLED_FEATURES'", "value");
    }

    @Когда("^переключаем регистрацию на \"([^\"]*)\"$")
    public void switchesRegistrationOn(String typeRegistration) {
        String sqlRequest;
        String activeOpt = getActiveOptions();

        if(typeRegistration.equals("WAVE")) {
            LOG.info("Удаление активных опций сайта identification_with_video и identification_with_euroset, и последнего символа, если это запятая");
            activeOpt = activeOpt.replace(", identification_with_video", "");
            activeOpt = activeOpt.replace(", identification_with_euroset", "");
            activeOpt = activeOpt.replace(",identification_with_video", "");
            activeOpt = activeOpt.replace(",identification_with_euroset", "");
            activeOpt = activeOpt.trim();
            activeOpt = activeOpt.substring(activeOpt.length() - 1).equals(",") ? activeOpt.substring(0, activeOpt.length() - 1) : activeOpt;
            sqlRequest = "UPDATE gamebet.`params` SET value='" + activeOpt + "' WHERE NAME='ENABLED_FEATURES'";
            workWithDB(sqlRequest);
        }
    }

    @Когда("^редактируем активные опции сайта, а старое значение сохраняем в \"([^\"]*)\"$")
    public void rememberActiveAndOffOption(String key,DataTable dataTable) {
        String sqlRequest = "SELECT * FROM gamebet.`params` WHERE NAME='ENABLED_FEATURES'";
        StringBuilder activeOpt = new StringBuilder(workWithDBgetResult(sqlRequest, "value"));
        Stash.put(key, activeOpt.toString());

        Map<String,String> table = dataTable.asMap(String.class,String.class);
        for (Map.Entry<String, String> entry : table.entrySet()) {
            String option = entry.getKey();
            if (entry.getValue().equals("true")) {
                activeOpt.append(", ").append(entry.getKey());
                continue;
            }
            activeOpt = new StringBuilder(activeOpt.toString().replace(", " + option, ""));
            activeOpt = new StringBuilder(activeOpt.toString().replace("," + option, ""));
            LOG.info("Удаление активной опций сайта " + option);
        }

        activeOpt = new StringBuilder(activeOpt.toString().trim());
        activeOpt = new StringBuilder(activeOpt.substring(activeOpt.length() - 1).equals(",") ? activeOpt.substring(0, activeOpt.length() - 1) : activeOpt.toString());
        sqlRequest = "UPDATE gamebet.`params` SET value='" + activeOpt + "' WHERE NAME='ENABLED_FEATURES'";
        workWithDB(sqlRequest);
    }

    @Когда("^выставляем обратно старое значение активных опций сайта \"([^\"]*)\"$")
    public void changeActive(String key) {
        String activeOpt = Stash.getValue(key) == null ? "back_call, remove_limits_btn, identification_with_euroset, identification_with_courier, dentification_with_video, identification_with_wave" : Stash.getValue(key);
        Stash.put(key,activeOpt);
        String sqlRequest = "UPDATE gamebet.`params` SET value='" + activeOpt + "' WHERE NAME='ENABLED_FEATURES'";
        LOG.info("Возвращаем активные значения сайта =>[" + activeOpt + "]");
        workWithDB(sqlRequest);
    }

    @After(value = "@NewUserRegistration_C36189")
    public void returnRegistrationValueWithScreenshot(Scenario scenario){
        LOG.info("возвращаем значение активных опций сайта из памяти по ключу 'ACTIVE'");
        changeActive("ACTIVE");
        final byte[] screenshot = ((TakesScreenshot) PageFactory.getWebDriver()).getScreenshotAs(OutputType.BYTES);
        scenario.embed(screenshot, "image/png");
        closeBrowser();
    }



    @After(value = "@0Registration_mobile,@requestVideoChatConfirmation,@1Registration_fullalt_mobile,@requestPhoneCall, @requestVideoChatConfirmation,@mobile")
    public void returnRegistrationValue(Scenario scenario){
        LOG.info("возвращаем значение активных опций сайта из памяти по ключу 'ACTIVE'");
        changeActive("ACTIVE");
    }


    @Когда("^возвращаем регистрацию на предыдущий способ из \"([^\"]*)\"$")
    public void returnRegistrationToPreviousMethod(String keyParams){
        String sqlUpdate = "UPDATE gamebet.`params` SET value = '" + Stash.getValue(keyParams)  + "'";
        workWithDBgetResult(sqlUpdate, "value");
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
        List<WebElement> bonusElement = PageFactory.getWebDriver().findElements(By.id("bonus-balance"));
        String bonus = bonusElement.isEmpty() ? "0" : bonusElement.get(0).getText();
        Stash.put(bonusKey,bonus);
        LOG.info("Записали в key [" + bonusKey + "] <== value [" + bonus + "]");
    }

    @Когда("^записываем значение баланса в \"([^\"]*)\"$")
    public void writeValueOfBalanceIn(String balanceKey) {
        List<WebElement> balanceElement = PageFactory.getWebDriver().findElements(By.id("topPanelWalletBalance"));
        String balance = balanceElement.isEmpty() ? "0" : balanceElement.get(0).getText();
        Stash.put(balanceKey,balance);
        LOG.info("Записали в key [" + balanceKey + "] <== value [" + balance + "]");
    }

    @Когда("^проверяем что с баланса \"([^\"]*)\" снялась сумма \"([^\"]*)\"$")
    public void checkThatBalanceWasWithdrawnAmount(String balanceKey, String amountKey) {
        try {
            BigDecimal actualBalance = new BigDecimal(PageFactory.getWebDriver().findElement(By.id("topPanelWalletBalance")).getText());
            BigDecimal previousBalance = new BigDecimal(Stash.getValue(balanceKey).toString());
            BigDecimal withdrawnAmount = new BigDecimal(Stash.getValue(amountKey).toString());
            BigDecimal expectedBalance = previousBalance.subtract(withdrawnAmount);
            assertEquals("Ошибка! Ожидаемый баланс [" + expectedBalance.toString() + "] не равен текущему [" + actualBalance.toString() + "]", 0, expectedBalance.compareTo(actualBalance));
        }catch (NumberFormatException nf){
           throw new AutotestError("Ошибка! Одно из полей с суммами оказалось пустым\n" + nf.getMessage());
        }
        BigDecimal actualBalance,previousBalance,withdrawnAmount,expectedBalance;
        actualBalance = new BigDecimal(PageFactory.getWebDriver().findElement(By.id("topPanelWalletBalance")).getText());
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





}

