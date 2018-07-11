package ru.gamble.stepdefs;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.DataTable;
import cucumber.api.java.it.Ma;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import cucumber.api.java.ru.*;
import net.minidev.json.JSONObject;
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
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.*;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.sbtqa.tag.stepdefs.GenericStepDefs;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.openqa.selenium.By.xpath;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.gamble.utility.Constants.*;


public class CommonStepDefs extends GenericStepDefs {
    private static final Logger LOG = LoggerFactory.getLogger(CommonStepDefs.class);

    @ActionTitle("нажимает на кнопку")
    public static void pressButton(String param) {
        Page page = null;
        WebElement button = null;
        try {
            page = PageFactory.getInstance().getCurrentPage();
            button = page.getElementByTitle(param);
            button.click();
            workWithPreloader();
        } catch (PageInitializationException e) {
            LOG.error(e.getMessage());
        } catch (PageException e1) {
            LOG.error(e1.getMessage());
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
            Integer day = (int) (Math.floor(1 + Math.random() * 27));
            Integer mon = (int) (Math.floor(1 + Math.random() * 11));
            Integer year = (int) (Math.floor(1950 + Math.random() * 49));
            String mons;
            if (mon < 10) {
                mons = "0" + mon.toString();
            } else
                mons = mon.toString();
            value = day.toString() + "." + mons + "." + year.toString();
        }

        if (value.equals(RANDOME_PHONE)) {
            value = "70" + Generators.randomNumber(9);
        }

        if(value.equals(RANDOME_SEX)){
            value = Generators.randomGender();
        }

        Stash.put(key, value);
        LOG.info("key:" + key + "| value::" + value);
    }

    // Метод ожидания появления и изчезновения прелоадера при методе click()
    public static void workWithPreloader() {
        String xpathPreloader = "//*[contains(@class,'preloader__container')]";
        waitShowElement(By.xpath(xpathPreloader));
    }

    // Ожидание появления элемента на странице
    public static void waitShowElement(By by) {
        WebDriver driver = PageFactory.getWebDriver();
        WebDriverWait driverWait = new WebDriverWait(driver, 3, 250);
        try {
            driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
            List<WebElement> preloaders = driver.findElements(by);
            LOG.info("Найдено прелоадеров::" + preloaders.size());
            driverWait.until(ExpectedConditions.invisibilityOfAllElements(preloaders));
            LOG.info("Прелоадеры закрылись");
        } catch (TimeoutException te) {
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
        cleanCookies();
        try {
        switch (siteUrl) {
            case "site":
                PageFactory.getWebDriver().get(JsonLoader.getData().get(STARTING_URL).get("mainUrl").getValue());
                break;
            case "admin":
                PageFactory.getWebDriver().get(JsonLoader.getData().get(STARTING_URL).get("adminUrl").getValue());
                break;
            case "registr":
                PageFactory.getWebDriver().get(JsonLoader.getData().get(STARTING_URL).get("registrationUrl").getValue());
            default:
                PageFactory.getWebDriver().get(siteUrl);
                break;
        }}catch (DataException e) {
            LOG.error(e.getMessage());
        }
        LOG.info("Перешли на страницу::" + PageFactory.getWebDriver().getCurrentUrl() + "\n");
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


    @Когда("^подтверждаем видеорегистрацию \"([^\"]*)\"$")
    public static void confirmVidochat(String param) {
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
            private String currentValue = null;

            @Override
            public Boolean apply(WebDriver driver) {
                return driver.findElement(locator).getAttribute(attribute).toLowerCase().contains(value.toLowerCase()) ? true : false;
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
            private String currentValue = null;

            @Override
            public Boolean apply(WebDriver driver) {
                return element.getAttribute(attribute).toLowerCase().contains(value.toLowerCase()) ? true : false;
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
     * @throws Exception
     */
    public void checkLinkToGame() throws Exception {
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
                ;
                //  if (driver.findElements(By.xpath("//div[@class='field-switcher']/div[contains(@class,'field-switcher__item_icon-video')]")).isEmpty()) {
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
            PageFactory.getWebDriver().manage().deleteAllCookies();
            LOG.info("Удаляем Cookies");
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * прелоадер должен обязательно появиться, если его не было - значит способ пополнения как бы и не выбран. поэтому эта ункция ждет чтобы прелоадер точно был,
     * но чтобы был не бесконечен
     *
     * @throws Exception
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
     *
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

    @Когда("^находим и сохраняем \"([^\"]*)\" из \"([^\"]*)\"$")
    public void fingingAndSave(String keyFingingParams, String sourceString) {
        String tmp = null;
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
        valueFingingParams = hashMapper(retMap, keyFingingParams);
        LOG.info("Достаем значение [" + keyFingingParams + "] и записываем в память::" + JSONValue.toJSONString(valueFingingParams));
        Stash.put(keyFingingParams, valueFingingParams);
    }

    /**
     * Метод пробегает по Map of Maps и ищет ключ
     * и возвращает либо значение по искомогу ключу или null
     *
     * @param finding - искомый ключ
     * @param json     - Object
     */
    private Object hashMapper(Object json, String finding) {
        String key;
        Object request = null, value;

        if (json instanceof Map) {
            ObjectMapper oMapper = new ObjectMapper();
            Map<String, Object> map = oMapper.convertValue(json, Map.class);

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
                if ((value instanceof String) || (value instanceof Integer) || (value instanceof Long) || (value instanceof Timestamp) || (value instanceof Boolean)) {
                    if (key.equalsIgnoreCase(finding)) {
                       // return request = String.valueOf(value);
                        return request = value;
                    }
                } else if (value instanceof Map) {
                    Map<String, Object> subMap = (Map<String, Object>) value;
                    if (key.equalsIgnoreCase(finding)) {
                        return request = value;
                    }else{
                    request = hashMapper(subMap, finding);
                    }
                } else if (value instanceof List) {
                    List list = (List) value;
                    if (key.equalsIgnoreCase(finding)) {
                        return request = ((List) list);
                    } else {
                        request = hashMapper((Object) list, finding);
                    }
                } else {
                    throw new IllegalArgumentException(String.valueOf(value));
                }
            }
        }else if(json instanceof List){
            for (int i = 0; i < ((List) json).size(); i++) {
                Object listItem = ((List) json).get(i);
                request = hashMapper(listItem, finding);
            }
        }
        return request;
    }

    private static String workWithDBgetResult(String sqlRequest, String param) {
        Connection con = DBUtils.getConnection();
        Statement stmt = null;
        PreparedStatement ps = null;
        ResultSet rs;
        String result = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlRequest);
            rs.last();
            result = rs.getString(param);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(con, ps, null);
        }
        return result;
    }

    /**
     * запрос на БД и сохранение всего ответа в map
     * @param sqlRequest
     * @return
     */
    private static void workWithDBresult(String sqlRequest) throws Exception{
        Connection con = DBUtils.getConnection();
        Statement stmt = null;
        PreparedStatement ps = null;
        ResultSet rs;
        StringBuilder keyNormal = new StringBuilder();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlRequest);
            rs.last();
            ResultSetMetaData allRows = rs.getMetaData();
            int count = allRows.getColumnCount();
            for (int i=1; i<=count; i++){
                String key = allRows.getColumnName(i);
                String value = rs.getString(key);
                for (String part: key.split("_")){
                    keyNormal.append(part);
                }
                LOG.info(keyNormal + "=" + value);
                Stash.put(keyNormal.toString().toUpperCase(),value);
                keyNormal.setLength(0);
            }
            } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(con, ps, null);
        }
    }



    @Когда("^получаем и сохраняем в память код подтверждения \\\"([^\\\"]*)\\\" телефона \\\"([^\\\"]*)\\\"$")
    public static void confirmPhone(String keyCode, String keyPhone) {
        String phone = Stash.getValue(keyPhone);
        String sqlRequest = "SELECT code FROM gamebet. `phoneconfirmationcode` WHERE phone='" + phone + "' ORDER BY creation_date";
        String code = workWithDBgetResult(sqlRequest, "code");
        Stash.put(keyCode, code);
        LOG.info("Получили код подтверждения телефона: " + code);
    }

    @Когда("^получаем и сохраняем в память код \\\"([^\\\"]*)\\\" подтверждения почты \\\"([^\\\"]*)\\\"$")
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
    public void добавляем_данные_в_JSON_объект_сохраняем_в_память(String keyJSONObject, DataTable dataTable) {

        Object jSONString = collectParametersInJSONString(dataTable);
        Stash.put(keyJSONObject, jSONString);
        LOG.info("Сохранили в память key::(" + keyJSONObject + ") |==> value::(" + String.valueOf(jSONString) + ")");

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
            jsonObject.put(key, JSONValue.parse(String.valueOf(value)));
        }
        params = jsonObject.toString();
        LOG.info(String.valueOf(params));
        return params;
    }

    @Когда("^определяем незанятый номер телефона и сохраняем в \\\"([^\\\"]*)\\\"$")
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
    @Когда("^ожидание \\\"([^\\\"]*)\\\" сек$")
    public static void justsleep(String sleep) throws InterruptedException {
        Long mcsleep = Long.parseLong(sleep) * 1000;
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
        String param, type, currentValue = "";
        Object json =  Stash.getValue(keyJSONObject);

        for(int i = 0; i < table.size(); i++) {
            param = table.get(i).get(PARAMETER);
            type = table.get(i).get(TYPE);
            currentValue = JSONValue.toJSONString(hashMapper(json, param));
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
    public static void searchUserStatus2(String status,String keyEmail) {
        String sqlRequest = "SELECT * FROM gamebet.`user` WHERE email LIKE 'testregistrator+7111%' AND registration_stage_id"+status + " AND offer_state=3 AND tsupis_status=3";

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
        String param, value, currentValue = null, tmp;
        Object json =  Stash.getValue(keyJSONObject);

        for(int i = 0; i < table.size(); i++) {
            param = table.get(i).get(PARAMETER);
            tmp = table.get(i).get(VALUE);
            if (tmp.matches("^[A-Z_]+$")) {
                value = JSONValue.toJSONString(Stash.getValue(tmp));
            } else {
                value = tmp;
            }
            currentValue = String.valueOf(hashMapper(json, param));
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
        RandomAccessFile fr = new RandomAccessFile("src\\test\\resources\\full_alt.txt", "r");
        String line;
        StringBuffer sbt=new StringBuffer("");
        String user = fr.readLine();
        String phone=user.trim().split("\t")[0];
        String birthDate = user.trim().split("\t")[1];
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
        FileWriter fw = new FileWriter("src\\test\\resources\\full_alt.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(sbt.toString());
        bw.flush();
        bw.close();
        fr.close();
        LOG.info(phone + " " +birthDate);
    }

    @Когда("^поиск пользователя проходившего ускоренную регистрацию \"([^\"]*)\"$")
    public static void searchUserNotPD(String keyEmail) {
        String sqlRequest = "SELECT * FROM gamebet.`user` WHERE tsupis_status IN (1,2) AND personal_data_state=1 AND email LIKE 'testregistrator+%@yandex.ru'";
        String email = workWithDBgetResult(sqlRequest, "email");
        Stash.put(keyEmail, email);
        LOG.info("Дата рождения: " + email);
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
        table.entrySet().forEach(el->
        {
            setter.append(el.getKey()+"="+el.getValue()+",");
        });
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
        String param, keyVariable, currentValue = "";
        Object json =  Stash.getValue(keyJSONObject);
        Object key = null, value;
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> map;
        Object selectedObject = null;
        map = oMapper.convertValue(json, Map.class);

        for(int i = 0; i < table.size(); i++) {
            param = table.get(i).get(PARAMETER);
            keyVariable = table.get(i).get(VARIABLE);

            for (Map.Entry<String, Object> entry1 : map.entrySet()) {
                if(param.equals("gameId")){
                    key = entry1.getKey();
                    Stash.put(keyVariable, key);
                    LOG.info("Cохранили [" + key.toString() + "]==>[" + keyVariable + "]");
                }else {
                    value = entry1.getValue();
                    currentValue = JSONValue.toJSONString(hashMapper(value, param));
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

    @Когда("^запрос к API \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public void requestToAPI(String path, String keyStash) {
        String fullPath = collectQueryString(path);
        requestByHTTPS(fullPath, keyStash,"GET",null);
    }

    @Когда("^запрос к IMG \"([^\"]*)\" и сохраняем в \"([^\"]*)\"$")
    public void запрос_к_IMG_и_сохраняем_в(String path, String keyStash) {
        String fullPath = (Stash.getValue(path)).toString().replaceAll("\\\\","");
        requestByHTTPS(fullPath, keyStash,"GET",null);
    }

    private String collectQueryString(String path){
        String requestUrl, requestPath = path, requestFull = "";
        LOG.info("Собираем строку запроса.");
        try {
            requestUrl = JsonLoader.getData().get("mobile-api").get("mainUrl").getValue();
            requestFull = requestUrl + "/" + requestPath;
        } catch (DataException e) {
            e.getMessage();
        }
        LOG.info("requestFull");
        return requestFull;
    }

    private void requestByHTTPS(String requestFull, String keyStash, String method, DataTable dataTable) {
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
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            //************

            url = new URL(requestFull);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod(method);
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
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
            LOG.info("Получаем ответ и записываем в память::" + jsonString.toString());
            LOG.info("");
            if (StringUtils.isNoneEmpty(jsonString)) {
                Stash.put(keyStash, JSONValue.parse(jsonString.toString()));
            } else {
                throw new AutotestError("ОШИБКА! Пустая строка JSON");
            }
        } catch (Exception e1) {
            LOG.error(e1.getMessage(), e1);
        }
    }

//    @Когда("^достаём видеотрансляцию провайдера \"([^\"]*)\" из списка \"([^\"]*)\" и сохраняем в переменую \"([^\"]*)\"$")
//    public void getVideoBroadcastProviderFromListAndSaveInVariable(String keyProvider, String keyListTranslation, String keyGameId) {
//        Map<String, Object> map;
//        String key;
//        Object value, selectedObject = null;
//        ObjectMapper oMapper = new ObjectMapper();
//        Object json =  Stash.getValue(keyListTranslation);
//        map = oMapper.convertValue(json, Map.class);
//
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            key = entry.getKey();
//            value = entry.getValue();
//            hashMapper(JSONValue.toJSONString(value), "providerName");
////            selectedObject = ((Map) value).entrySet().toArray()[new Random().nextInt(((Map) value).size())];
//            selectedObject = ((Map) value).entrySet().toArray()[new Random().nextInt(((Map) value).size())];
//        }
//        Stash.put(keyGameId, selectedObject);
//    }

}

