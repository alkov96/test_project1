package ru.gamble.pages.mainPages;

import cucumber.api.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

@PageEntry(title = "Мои пари")
public class MyBetting extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MyBetting.class);

    @FindBy(xpath = "//*[contains(@class,'subMenuArea')]//h4[contains(.,'Мои пари')]")
    private WebElement pageTitle;

    @ElementTitle("Фильтр по типу пари")
    @FindBy(xpath = "//*[@class='input888wrpr']//custom-select/div")
    private WebElement filterByTypeOfBid;


    @FindBy(xpath = "//div[contains(@class,'input888wrpr')]")
    private WebElement buttonFilterByTypeOfBid;

    public MyBetting() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(driver, this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("проверяет фильтры по типу ставки с")
    public void checksFiltersByTypeOfBidWith(DataTable dataTable) throws InterruptedException {
        String filterXpath = "//*[@class='input888wrpr']//custom-select";
        WebDriver driver = PageFactory.getWebDriver();
        List<String> data = dataTable.asList(String.class);

        LOG.info("Открываем фильтр по типу пари.");
        buttonFilterByTypeOfBid.click();
        Thread.sleep(2000);
        String actual =  buttonFilterByTypeOfBid.getText().replaceAll("\n", " ").toLowerCase();
        for(String existed: data){
            assertTrue("Строка [" + actual + "] не соответсвует [" + existed + "]",actual.contains(existed));
        }
        LOG.info("Закрываем фильтр по типу пари.");
        buttonFilterByTypeOfBid.click();
    }

    @ActionTitle("проверяет попадание ставок в диапазон дат")
    public void checksBidsOnDateRange() {
        List<WebElement> dates = PageFactory.getWebDriver().findElements(By.xpath("//span[contains(@class,'datapicker__form-text')]"));
        Date firstDate, lastDate, bidDate;
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();

        LOG.info("Нашли датапикеров::" + dates.size());

        SimpleDateFormat formatter2 = new SimpleDateFormat("dd.MM.yy HH:mm");
        List<WebElement> rows = PageFactory.getWebDriver().findElements(By.xpath("//tr[contains(@class,'showBetInfo ')]"));
        LOG.info("Всего ставок::" + rows.size());
        LOG.info("Проверяем, что даты ставок попадают в диапазон");
        try {
            firstDate = formatter1.parse(dates.get(0).getText());
            lastDate = formatter1.parse(dates.get(1).getText());
            //***Преоборазуем дату из dd.MM.yy 00:00 в dd.MM.yy 23:59
            calendar.setTime(lastDate);
            calendar.add(Calendar.SECOND, 86399);
            lastDate = calendar.getTime();
            //***

            LOG.info("Дата на левом датапикере::" + firstDate.toString());
            LOG.info("Дата на правом датапикере::" + lastDate.toString());
            if(rows.size() > 0) {
                for (WebElement row : rows) {
                    try {
                        bidDate = formatter2.parse(row.findElement(By.xpath("./td[2]/div")).getText());
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

    @ActionTitle("проверяет тип исхода с типом пари с")
    public void checksTypeOfOutcomeWithBetTypeWith(DataTable dataTable){
        WebDriver driver = PageFactory.getWebDriver();
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
            filterByTypeOfBid.findElement(By.xpath("//custom-select//div/span[contains(.,'" + typeOfOutcome + "')]")).click();
            workWithPreloader();

            List<WebElement> rows = PageFactory.getWebDriver().findElements(By.xpath("//tr[contains(@class,'showBetInfo ')]"))
                    .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
            LOG.info("Всего пари::" + rows.size());
            LOG.info("Проверяем, что даты ставок попадают в диапазон");

            List<WebElement> subIvens;
            if (rows.size() > 0) {
                for (WebElement row : rows) {
                    selectedOutcome = row.findElement(By.xpath(".//tr[contains(@class,'table')]/td[2]")).getText();
                    if (betType.contains("!")) {
                        assertThat(selectedOutcome).doesNotContain("Система");
                        assertThat(selectedOutcome).doesNotContain("Экспресс");
                    } else {
                        assertThat(selectedOutcome)
                                .as("Ошибка! Текст [" + selectedOutcome + "] не соответсвует [" + betType + "]").contains(betType);
                    }
                    LOG.info("Текст [" + selectedOutcome + "] соответсвует [" + betType + "]");
                    if (selectedOutcome.contains("Экспресс") || selectedOutcome.contains("Система")) {
                        LOG.info("Проверяем что в [" + selectedOutcome + "] больше одного события");
                        subIvens = row.findElements(By.xpath(".//div//tr[contains(@class,'table-inner__row')]"));
                        if (subIvens.size() > 1) {
                            subIvens.remove(0);
                        } //Здесь удаляем мусорную строку
                        assertThat(subIvens.size())
                                .as("Ошибка! В типе пари [" + typeOfOutcome + "] меньше чем " + subIvens.size() + " пари").isGreaterThan(1);
                        for (WebElement subIvent : subIvens) {
                            LOG.info(subIvent.getText().replaceAll("\n", " "));
                        }
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

    @ActionTitle("ищет и запоминает ожидаемые события по фильтру")
    public void remeberMyBets(String filter, String nameList) {
        WebDriver driver = PageFactory.getWebDriver();
        int  cou = 3;
        LOG.info("Сначала включаем фильтр 'Ожидается'");
        driver.findElement(By.xpath("//table[@class='table-inner']//div[contains(@class,'custom-select__placeholder option')]/span")).click();
        driver.findElement(By.xpath("//table[@class='table-inner']//div[contains(@class,'custom-select-der')]//span[normalize-space(text())='Ожидается']")).click();
        CommonStepDefs.workWithPreloader();

        LOG.info("Теперь включаем фильтр по типу ставки " + filter);
        driver.findElement(By.xpath("//div[@class='my-stakes__filter-grid_M']//div[contains(@class,'custom-select__placeholder option')]/span")).click();
        driver.findElement(By.xpath("//div[@class='my-stakes__filter-grid_M']//div[contains(@class,'custom-select-der')]//span[normalize-space(text())='" + filter + "']")).click();
        CommonStepDefs.workWithPreloader();

        WebDriverWait wait = new WebDriverWait(driver,10);
        wait.withMessage("Нет записей в истории ожидаемых пари в купоне");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//tr[contains(@class,'showBetInfo table__row')]"),1));

        List<WebElement> allBetsOnPage = driver.findElements(By.xpath("//tr[contains(@class,'showBetInfo table__row')]"));

        if (allBetsOnPage.size()<cou){
            cou=allBetsOnPage.size();
        }

        List<BetFull> betsOnMyBets= new ArrayList<>();


        for (int i = 0; i<cou; i++) {

            betsOnMyBets.add(rememberBetOnMyBets(allBetsOnPage.get(i)));

            LOG.info("Запомнили строчку из Моих пари");
        }

        LOG.info("Первые " + cou + " ставок(ки) в моих пари это: " +
        betsOnMyBets.get(0).getType() + "\n" + betsOnMyBets.get(1).getType() + "\n" + betsOnMyBets.get(2).getType());
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
        SimpleDateFormat formatNo = new SimpleDateFormat("dd.MM.yy");
        SimpleDateFormat formatYes = new SimpleDateFormat("dd.MM");

        helpString.append(element.findElement(By.xpath(".//tr[contains(@class,'table')]/td[2]")).getText());
        String typeBet = (!helpString.toString().toLowerCase().contains("система") && !helpString.toString().toLowerCase().contains("экспресс")) ?
                "ординар" : helpString.toString().toLowerCase();
        bet.setType(typeBet);

        helpString.setLength(0);
        helpString.append(element.findElement(By.xpath("td[2]/div")).getText());
        index = helpString.indexOf(":");
        try {
            timeBet.append(helpString.substring(index - 2, index + 3));
        }
        catch (StringIndexOutOfBoundsException e){
            LOG.info("Не получилось вытащить время из строчки "  + helpString);
        }
        bet.setTimeBet(timeBet.toString());


        element.findElements(By.xpath(".//td[contains(@class,'table__head-cell_my-stakes-kef')]/div"))
                .stream()
                .forEach(el -> coefs.add(el.getText()));

        element.findElements(By.xpath(".//td[contains(@class,'table__head-cell_my-stakes-event')]/span[position()=1]"))
                .stream()
                .map(WebElement::getText)
                .map(el -> CommonStepDefs.newFormatDate(formatNo, formatYes, el))
                .forEach(el -> dateGames.add(el));

        element.findElements(By.xpath(".//td[contains(@class,'table__head-cell_my-stakes-event')]/span[position()=2]"))
                .stream()
                .forEach(el -> nameGames.add(el.getText()));

        bet.setCoefs(coefs);
        bet.setDates(dateGames);
        bet.setNames(nameGames);


        helpString.setLength(0);
        helpString.append(element.findElement(By.xpath(".//div[contains(@class,'showBetInfo__money-str')]/span[2]")).getAttribute("class"));

        sum.append(helpString.toString().contains("hide") ? "Б" : "Р");
        sum.insert(0, element.findElement(By.xpath(".//div[contains(@class,'showBetInfo__money-str')]/span[1]")).getText() + " ");
        bet.setSum(sum.toString());

        return bet;
    }
}
