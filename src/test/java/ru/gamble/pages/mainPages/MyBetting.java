package ru.gamble.pages.mainPages;

import cucumber.api.DataTable;
import org.apache.poi.ss.formula.functions.T;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.gamble.utility.BetFull;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.xpath;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

@PageEntry(title = "Мои пари")
public class MyBetting extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MyBetting.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//*[contains(@class,'subMenuArea')]//h4[contains(.,'Мои пари')]")
    private WebElement pageTitle;

    @ElementTitle("Фильтр по типу пари")
    @FindBy(xpath = "//*[@class='input888wrpr']//custom-select/div")
    private WebElement filterByTypeOfBid;

    @ElementTitle("поисковая строка")
    @FindBy(xpath = "//input[contains(@class,'input-search')]")
    private WebElement search_line;

    @FindBy(xpath = "//div[contains(@class,'input888wrpr')]")
    private WebElement buttonFilterByTypeOfBid;

    public MyBetting() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }



    @ActionTitle("проверяет попадание ставок в диапазон дат")
    public void checksBidsOnDateRange() {
        List<WebElement> dates = PageFactory.getWebDriver().findElements(xpath("//span[contains(@class,'datapicker__form-text')]"));
        Date firstDate, lastDate, bidDate;
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd.MM");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        LOG.info("Нашли датапикеров::" + dates.size());

        SimpleDateFormat formatter2 = new SimpleDateFormat("dd.MM.yy HH:mm");
        List<WebElement> rows = PageFactory.getWebDriver().findElements(xpath("//tr[contains(@class,'showBetInfo ')]"));
        LOG.info("Всего ставок::" + rows.size());
        LOG.info("Проверяем, что даты ставок попадают в диапазон");
        try {
            firstDate = formatter1.parse(dates.get(0).getAttribute("innerText"));
            lastDate = formatter1.parse(dates.get(1).getAttribute("innerText"));
            //***Преоборазуем дату из dd.MM.yy 00:00 в dd.MM.yy 23:59
            calendar.setTime(lastDate);
            calendar.add(Calendar.SECOND, 86399);
            calendar.set(Calendar.YEAR, year);
            lastDate = calendar.getTime();
            //***

            LOG.info("Дата на левом датапикере::" + firstDate.toString());
            LOG.info("Дата на правом датапикере::" + lastDate.toString());
            if(rows.size() > 0) {
                for (WebElement row : rows) {
                    try {
                        bidDate = formatter2.parse(row.findElement(xpath("./td[2]")).getAttribute("innerText"));
                        assertThat(bidDate.after(firstDate) && bidDate.before(lastDate))
                                .as("Дата не попадает в интервал::" + firstDate.toString() + "::" + bidDate + "::" + lastDate).isTrue();
                        LOG.info("Дата в интервале::" + firstDate.toString() + "::" + bidDate + "::" + lastDate);
                    } catch (ParseException e) {
                        LOG.error(e.getMessage());
                    }
                }
            }else {
                LOG.info("Ни одной ставки не найдено, нечего проверять.");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    public String rememberId() throws InterruptedException {
        List<WebElement> all_id = driver.findElements(xpath("//tr[contains(@class,'showBetInfo')]"));
        int value = new Random().nextInt(all_id.size());
        Thread.sleep(5000);

        new Actions(driver).moveToElement(all_id.get(value)).build().perform();
       // driver.findElements(xpath("//tr[contains(@class,'showBetInfo')]/td[1]")).get(0).getAttribute("innerText");

        String id = all_id.get(value).findElement(By.xpath("./td[1]")).getAttribute("innerText");
        return id;
    }

    @ActionTitle("проверяет поиск, вводит в поисковую строку: ")
    public void checkSearchById(String param) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(PageFactory.getWebDriver(), 15);
        String pattern;
        if (param.equals("id")){
            pattern = rememberId();
        }
        else {
            pattern = param;
        }
        pattern = pattern.replace("ID ","").trim();
//        ((JavascriptExecutor) driver).executeScript("arguments[0].value = "+pattern+"", search_line);
        search_line.clear();
        search_line.click();
        search_line.sendKeys(pattern);
        Thread.sleep(1000);
        search_line.sendKeys((Keys.BACK_SPACE));
        search_line.sendKeys(pattern.substring(pattern.length()-1));

//        driver.findElement(By.xpath("//input[contains(@class, 'input-search')]")).sendKeys(pattern);
        LOG.info("В строку поиска ввели: " + pattern);

        By by = xpath("//tr[contains(@class,'showBetInfo')]");

        if (param.equals("id")) {
            wait.until(ExpectedConditions.numberOfElementsToBe(by, 1));
            LOG.info("Ввели " + pattern + ". Результат: элемент найден" );
        } else {
            wait.until(ExpectedConditions.numberOfElementsToBeLessThan(by,1));
            LOG.info("Ввели " + pattern + ". Результат: элемент не найден" );
            }
        }


    @ActionTitle("проверяет сортировку по сумме")
    public void sortSumm() throws InterruptedException {
        boolean sort_list_summ;

        By button = xpath("//tr[@class='bets-table__row bets-table__row_head']/th[contains(@class,'table__cell_sum')]");
        By by = xpath("//tr[contains(@class,'showBetInfo')]/td[6]");

        driver.findElement(button).click();
        Thread.sleep(5000);

        List<Float> summList = driver.findElements(by).stream().filter(WebElement::isDisplayed).map(element -> Float.valueOf(element.getAttribute("innerText"))).collect(Collectors.toList());
        sort_list_summ = summList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()).equals(summList);
        assertTrue("Список не отсортировался по сумме!",sort_list_summ);

        driver.findElement(button).click();
        Thread.sleep(5000);

        summList.clear();

        sort_list_summ = summList.stream().sorted().collect(Collectors.toList()).equals(summList);
        assertTrue("Список не отсортировался по сумме!",sort_list_summ);

    }


    @ActionTitle("проверяет фильтр по типу исхода ставки")
    public void checkFilterInMyBetting(DataTable dataTable) throws InterruptedException {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String outcomeOfBet, selectedOutcomeOfBet, outcomeOfBetWeChoose;
        for (Map<String, String> aTable : table) {
            outcomeOfBet = aTable.get("Исход ставки");
            selectedOutcomeOfBet = aTable.get("Выбранный исход ставки");
            Thread.sleep(5000);
            WebElement button_choose_outcome = driver.findElement(xpath("//th[@class=' bets-table__cell bets-table__cell_head bets-table__cell_outcome']"));
            button_choose_outcome.click();
            LOG.info("Выбираем тип " + outcomeOfBet);
            Thread.sleep(3000);
            driver.findElement(xpath("//div[@class='custom-select-der scroll-contain ng-scope capitalize expanded']/div/span[contains(text(),'" + outcomeOfBet + "')]"))
                    .click();
            Thread.sleep(5000);
            List<WebElement> rows = PageFactory.getWebDriver().findElements(xpath("//tr[contains(@class,'showBetInfo')]"))
                    .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
            LOG.info("Всего пари::" + rows.size());
            if (rows.size()==0){
                LOG.info("нет ставок для " + outcomeOfBet);
                continue;
            }
            LOG.info("Проверяем, что для выбранного типа исхода, отображаются только ставки с этим типом:: " + outcomeOfBet);
            for (WebElement row : rows) {
                outcomeOfBetWeChoose = row.findElement(xpath("//td[10]"))
                        .getAttribute("innerText");
                if (selectedOutcomeOfBet.equals("Все")){
                    break;                }
                Assert.assertTrue("Для ставки с типом " + outcomeOfBet + " отображается тип " + outcomeOfBetWeChoose, outcomeOfBetWeChoose
                        .contains(selectedOutcomeOfBet));
            }
        }
    }


    @ActionTitle("проверяет фильтр по типу ставки")
    public void checksTypeOfOutcomeWithBetTypeWith(DataTable dataTable){
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String typeOfOutcome, betType, selectedOutcome;
        for (Map<String, String> aTable : table) {
            typeOfOutcome = aTable.get("Тип пари");
            betType = aTable.get("Выбранный исход");

            if (!filterByTypeOfBid.getAttribute("class").contains("expanded")) {
                LOG.info("Открываем фильтр по типу пари.");
                filterByTypeOfBid.click();
            }

            LOG.info("Выбираем тип::" + typeOfOutcome);
            filterByTypeOfBid.findElement(xpath("//custom-select//div/span[contains(.,'" + typeOfOutcome + "')]")).click();
            workWithPreloader();

            List<WebElement> rows = PageFactory.getWebDriver().findElements(xpath("//tr[contains(@class,'showBetInfo')]"));
            LOG.info("Всего пари::" + rows.size());
            LOG.info("Проверяем, что даты ставок попадают в диапазон");

            List<WebElement> subIvens;
            if (rows.size() > 0) {
                for (WebElement row : rows) {
                    selectedOutcome = row.findElement(xpath(".//td[4]")).getAttribute("innerText");
                    if (betType.contains("!")) {
                        assertThat(selectedOutcome).doesNotContain("Система");
                        assertThat(selectedOutcome).doesNotContain("Экспресс");
                    } else {
                        assertThat(selectedOutcome)
                                .as("Ошибка! Текст [" + selectedOutcome + "] не соответсвует [" + betType + "").contains(betType);
                    }
                    LOG.info("Текст [" + selectedOutcome + "] соответсвует [" + betType + "]");
                    if (selectedOutcome.contains("Экспресс") || selectedOutcome.contains("Система")) {
                        LOG.info("Проверяем что в [" + selectedOutcome + "] больше одного события");
                        row.click();
                        subIvens = driver.findElements(xpath("//tr[contains(@class,'showBetInfo') and not (contains(@class,'head'))]"));

//                        if (subIvens.size() > 1) {
//                            subIvens.remove(0);
//                        } //Здесь удаляем мусорную строку
                        assertThat(subIvens.size())
                                .as("Ошибка! В типе пари [" + typeOfOutcome + "] меньше чем " + subIvens.size() + " пари").isGreaterThan(1);
                        row.click();
//                        for (WebElement subIvent : subIvens) {
//                            LOG.info(subIvent.getAttribute("innerText").replaceAll("\n", " "));
//                        }
                    }
                }
            } else {
                LOG.info("Ни одного пари не найдено, нечего проверять.");
            }

            if (filterByTypeOfBid.getAttribute("class").contains("expanded")) {
                LOG.info("Закрываем фильтр по типу пари.");
                filterByTypeOfBid.click();
            }
        }
    }



////выставление даты начала ставок в МОИХ ПАРИ на самую раннюю из возможных
//    @ActionTitle("отматывает дату начала МОИХ ПАРИ на самую раннюю")
//    public void datapickerOnBegin(){
//        WebElement datapickerBegin = driver.findElement(By.xpath("//div[contains(@class,'datepicker__form') and position()=1]"));
//        if (!datapickerBegin.getAttribute("class").contains("active")){
//            datapickerBegin.click();
//            new WebDriverWait(driver,10)
//                    .withMessage("Дата начала не раскрылась")
//                    .until(ExpectedConditions.attributeContains(datapickerBegin,"class","active"));
//        }
//        LOG.info("Если доступно - нажимаем на стрелочку 'год назад'");
//        By BYarrowLeft;
//        for(int lineNumber=1;lineNumber<=2;lineNumber++){
//            BYarrowLeft= By.xpath("//div[@class='datepicker__line' and position()=" + lineNumber + "]//i[contains(@class,'arrow-left6')]");
//
//        while (!driver.findElement(BYarrowLeft).findElement(By.xpath("ancestor-or-self::div[contains(@class,'datepicker__btn')]")).getAttribute("class").contains("bound")){
//            driver.findElement(BYarrowLeft).click();
//        }
//        }
//        LOG.info("Год и месяц отщелкали на начало. Теперь день выбирем самый ранний");
//        driver.findElement(By.xpath("//div[contains(@class,'datepicker__day-btn') and not(contains(@class,'disabled'))]")).click();
//        CommonStepDefs.workWithPreloader();
//    }

    @ActionTitle("ищет и запоминает ожидаемые события по фильтру")
    public void remeberMyBets(String filter, String nameList) {
        int  cou = 3;
        LOG.info("Сначала включаем фильтр 'Ожидается'");
        new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(xpath("//table[@class='bets-table__table']//div[contains(@class,'custom-select__placeholder option')]/span")));
        driver.findElement(xpath("//table[@class='bets-table__table']//div[contains(@class,'custom-select__placeholder option')]/span")).click();
        driver.findElement(xpath("//table[@class='bets-table__table']//div[contains(@class,'custom-select-der')]//span[normalize-space(text())='Ожидается']")).click();
        CommonStepDefs.workWithPreloader();

//        LOG.info("Отматываем дату начала на самую раннюю");
//        datapickerOnBegin();

        LOG.info("Теперь включаем фильтр по типу ставки " + filter);
        new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(xpath("//div[@class='my-bets__filter']//div[contains(@class,'custom-select__placeholder option')]/span")));
        driver.findElement(xpath("//div[@class='my-bets__filter']//div[contains(@class,'custom-select__placeholder option')]/span")).click();
        driver.findElement(xpath("//div[@class='my-bets__filter']//div[contains(@class,'custom-select-der')]//span[normalize-space(text())='" + filter + "']")).click();
        CommonStepDefs.workWithPreloader();

//        WebDriverWait wait = new WebDriverWait(driver,10);
//        wait.withMessage("Нет записей в истории ожидаемых пари в купоне");
//        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//tr[contains(@class,'showBetInfo table__row')]"),0));

        List<WebElement> allBetsOnPage = driver.findElements(xpath("//tbody[contains(@class,'bets-table__bet')]"));

        if (allBetsOnPage.size()<cou){
            cou=allBetsOnPage.size();
        }

        List<BetFull> betsOnMyBets= new ArrayList<>();


        for (int i = 0; i<cou; i++) {

            betsOnMyBets.add(rememberBetOnMyBets(allBetsOnPage.get(i)));

            LOG.info("Запомнили строчку из Моих пари");
        }

        LOG.info("Первые " + cou + " ставок(ки) в моих пари это: ");
        for (int i=0;i<cou;i++){
            LOG.info(betsOnMyBets.get(i).getType() + "\n");
        }
        Stash.put(nameList,betsOnMyBets);

    }

    public BetFull rememberBetOnMyBets(WebElement element){
        StringBuilder helpString = new StringBuilder();
        int index;
        StringBuilder timeBet = new StringBuilder();
        List<String> coefs = new ArrayList<>();
        List<String> dateGames = new ArrayList<>();
        List<String> nameGames = new ArrayList<>();
        StringBuilder sum = new StringBuilder();
        BetFull bet = new BetFull();
        SimpleDateFormat formatNo = new SimpleDateFormat("dd.MM, hh:mm");
        SimpleDateFormat formatYes = new SimpleDateFormat("dd MMMM в k:mm(МСК)");

        helpString.append(element.findElement(xpath(".//tr[contains(@class,'showBetInfo bets-table__row')]/td[4]")).getAttribute("innerText"));
        String typeBet = (!helpString.toString().toLowerCase().contains("система") && !helpString.toString().toLowerCase().contains("экспресс")) ?
                "ординар" : helpString.toString().toLowerCase().split(" ")[0];
        bet.setType(typeBet);

        helpString.setLength(0);
        helpString.append(element.findElement(xpath(".//tr[contains(@class,'showBetInfo bets-table__row')]/td[3]")).getAttribute("innerText"));
        index = helpString.indexOf(":");
        try {
            timeBet.append(helpString.substring(index - 2, index + 3));
        }
        catch (StringIndexOutOfBoundsException e){
            LOG.info("Не получилось вытащить время из строчки "  + helpString);
        }
        bet.setTimeBet(timeBet.toString());

        if (!typeBet.equals("ординар")){
            element.click(); //для раскрытия системы  и экспресса
        }
        element.findElements(xpath(".//tr[contains(@class,'showBetInfo bets-table__row')]/td[contains(@class,'bets-table__cell bets-table__cell_coefficient')]"))
                .stream()
                .forEach(el -> coefs.add(el.getAttribute("innerText").replace(".00","").replace(".0","")));
        if (!typeBet.equals("ординар")){
           coefs.remove(0);
        }


        element.findElements(xpath(".//tr[contains(@class,'showBetInfo bets-table__row')]/td[contains(@class,'bets-table__cell bets-table__cell_event-datetime')]"))
                .stream()
                .map(WebElement::getText)
                .map(el -> CommonStepDefs.newFormatDate(formatNo, formatYes, el))
                .forEach(el -> dateGames.add(el));
        if (!typeBet.equals("ординар")){
            dateGames.remove(0);
        }
        element.findElements(xpath(".//tr[contains(@class,'showBetInfo bets-table__row')]/td[@class='bets-table__cell bets-table__cell_fade-overflow bets-table__cell_bet-name']"))
                .stream()
                .forEach(el -> nameGames.add(el.getAttribute("innerText")));

        bet.setCoefs(coefs);
        bet.setDates(dateGames);
        bet.setNames(nameGames);


        helpString.setLength(0);

//        sum.insert(0,  element.findElement(xpath(".//tr[contains(@class,'showBetInfo bets-table__row')]/td[@class='bets-table__cell bets-table__cell_sum']/span")).getAttribute("innerText") + " ");
        sum.insert(0,  element.findElement(xpath(".//tr[contains(@class,'showBetInfo bets-table__row')]/td[@class='bets-table__cell bets-table__cell_sum']/span")).getAttribute("innerText").trim());
        if (!sum.toString().contains("Б")) {sum.append(" Р");}
        bet.setSum(sum.toString().replace(".00","").replace(".0","").replaceAll(" ",""));
        if (!typeBet.equals("ординар")){
            element.findElement(xpath(".//tr[contains(@class,'showBetInfo bets-table__row')]/td[4]")).click();
        }

        return bet;
    }


    @ActionTitle("запоминает ID ставки и ее исход")
    public void rememberIDandResultBet(String keyMap,DataTable dataTable){
        Map<String,String> bets = new HashMap<>();
        String id;
        List<String> data = dataTable.asList(String.class);
        for (String state : data){
            selectReseltsBet(state);
            if (driver.findElements(By.xpath("//*[contains(@class,'showBetInfo table__row')]")).isEmpty()){
                continue;
            }
            id = driver.findElement(By.xpath("//*[contains(@class,'howBetInfo table__row')]/td")).getAttribute("innerText"); //у первой записи запоминаем id
            bets.put(state,id);
        }
        Stash.put(keyMap,bets);
    }

    @ActionTitle("выставляет фильтр исхода пари на")
    public void selectReseltsBet(String result){
        LOG.info("Сначала включаем фильтр '" + result + "'");
        new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(xpath("//table[@class='bets-table__table']//div[contains(@class,'custom-select__placeholder option')]/span")));
        driver.findElement(xpath("//table[@class='bets-table__table']//div[contains(@class,'custom-select__placeholder option')]/span")).click();
        driver.findElement(xpath("//table[@class='bets-table__table']//div[contains(@class,'custom-select-der')]//span[normalize-space(text())='" + result + "']")).click();
        CommonStepDefs.workWithPreloader();
    }
}
