package ru.gamble.pages.mainPages;

import cucumber.api.DataTable;
import org.hamcrest.MatcherAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.BUTTON;
import static ru.gamble.utility.Constants.DIRECTION;

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
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("проверяет фильтры по типу ставки с")
    public void checksFiltersByTypeOfBidWith(DataTable dataTable){
        String filterXpath = "//*[@class='input888wrpr']//custom-select";
        WebDriver driver = PageFactory.getWebDriver();
        List<String> data = dataTable.asList(String.class);

        LOG.info("Открываем фильтр по типу пари.");
        buttonFilterByTypeOfBid.click();
        new WebDriverWait(driver,2);
        String actual =  buttonFilterByTypeOfBid.getText().replaceAll("\n", " ").toLowerCase();
        for(String existed: data){
            assertThat(actual).as("Строка [" + actual + "] не соответсвует [" + existed + "]").contains(existed);
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
        for(int i = 0; i < table.size(); i++) {
            typeOfOutcome = table.get(i).get("Тип пари");
            betType = table.get(i).get("Выбранный исход");

            if(!filterByTypeOfBid.getAttribute("class").contains("expanded")){
                LOG.info("Открываем фильтр по типу пари.");
                filterByTypeOfBid.click();
            }

            LOG.info("Выбираем тип::" + typeOfOutcome);
            filterByTypeOfBid.findElement(By.xpath("//custom-select//div/span[contains(.,'" + typeOfOutcome + "')]")).click();
            workWithPreloader();

            List<WebElement> rows = PageFactory.getWebDriver().findElements(By.xpath("//tr[contains(@class,'showBetInfo ')]"))
                    .stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
            LOG.info("Всего пари::" + rows.size());
            LOG.info("Проверяем, что даты ставок попадают в диапазон");

            List<WebElement> subIvens;
            if(rows.size() > 0) {
                for (WebElement row : rows) {
                        selectedOutcome = row.findElement(By.xpath(".//tr[contains(@class,'table')]/td[2]")).getText();
                        if(betType.contains("!")){
                            assertThat(selectedOutcome).doesNotContain("Система");
                            assertThat(selectedOutcome).doesNotContain("Экспресс");
                        }else {
                            assertThat(selectedOutcome)
                                    .as("Ошибка! Текст [" + selectedOutcome + "] не соответсвует [" + betType + "]").contains(betType);
                        }
                        LOG.info("Текст [" + selectedOutcome + "] соответсвует [" + betType + "]");
                        if(selectedOutcome.contains("Экспресс") || selectedOutcome.contains("Система")){
                            LOG.info("Проверяем что в [" + selectedOutcome + "] больше одного события");
                            subIvens = row.findElements(By.xpath(".//div//tr[contains(@class,'table-inner__row')]"));
                           // if(subIvens.size() > 1){ subIvens.remove(0);} //Здесь удаляем мусорную строку
                            assertThat(subIvens.size())
                                    .as("Ошибка! В типе пари [" + typeOfOutcome + "] ==> " + subIvens.size() + " пари").isGreaterThan(1);
                            for(WebElement subIvent : subIvens){
                                LOG.info(subIvent.getText().replaceAll("\n", " "));
                            }
                        }
                }
            }else {
                LOG.info("Ни одного пари не найдено, нечего проверять.");
            }

            if(filterByTypeOfBid.getAttribute("class").contains("expanded")){
                LOG.info("Закрываем фильтр по типу пари.");
                filterByTypeOfBid.click();
            }
        }
    }

    @ActionTitle("проверяет сортировку 'Дата и время ставки' по возрастанию")
    public void checksSortingBidDateAndTime(){
        WebDriver driver = PageFactory.getWebDriver();
        String xpathBidDateAndTime = "//th[contains(@class,'my-stakes-date')]";
        String xpathRowBetsDatetime = "//tr[contains(@class,'showBetInfo')]/td[2]";
        String xpathPage = "//div[contains(@class,'pagination-page')]";
        List<WebElement> listOfBetsDateTime = null;
        Date lastRow = null;

        if(driver.findElements(By.xpath("//td[contains(.,'По заданным параметрам ставок не найдено')]")).size() > 0){
            LOG.info("По заданным параметрам ставок не найдено!");
        }else {
            LOG.info("Нажимаем на фильтр по дате-времени");
            driver.findElement(By.xpath(xpathBidDateAndTime)).click();

          List<WebElement> pages = driver.findElements(By.xpath(xpathPage)).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
          if(pages.size() != 0){
              for (int page = 0; page < pages.size(); page++) {
                  LOG.info("Переходим на страницу::" + (page + 1));
                  pages.get(page).click();
                  workWithPreloader();

                  listOfBetsDateTime = driver.findElements(By.xpath(xpathRowBetsDatetime)).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
                  lastRow = compareListRowsBetsByDateTime(lastRow, listOfBetsDateTime);
              }
          }else {
              listOfBetsDateTime = driver.findElements(By.xpath(xpathRowBetsDatetime)).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
              compareListRowsBetsByDateTime(lastRow, listOfBetsDateTime);
          }
        }
    }

    /**
     * Метод принимает Дату и список строк в Мои Пари для сравнивания двух соседних(расположенной выше и ниже)
     * по возрастанию Даты и возвращает последнюю дату, если будут ещё страницы и она понадобится для сравнения
     * @param lastRow последняя Дата, если она была или null
     * @param rows список строк текущей страницы
     * @return nextDate возвращаемая последняя Дата из списка rows
     */
    private Date compareListRowsBetsByDateTime(Date lastRow, List<WebElement> rows) {
        Date currentDate = null, nextDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar calendar = Calendar.getInstance();
        LOG.info("Проверяем что ставок > 1.");
        if(lastRow != null){
            currentDate = lastRow;
        }
            if (rows.size() < 2) {
                LOG.info("Меньше [" + rows.size() + "] ставки - невозможно стравнить.");
            } else {
                for (int i = 0; i < rows.size() - 1; i++) {
                    try {
                        if(lastRow == null) {
                            currentDate = formatter.parse(rows.get(i).getText());
                            nextDate = formatter.parse(rows.get(i + 1).getText());
                        }else {
                            currentDate = lastRow;
                            nextDate = formatter.parse(rows.get(i).getText());
                            lastRow = nextDate;
                        }

                        assertThat(nextDate.after(currentDate) || nextDate.equals(currentDate))
                                .as("Ошибка! Дата [" + currentDate.toString() + "] позже [" + nextDate.toString() + "]").isTrue();
                        LOG.info(" Дата [" + currentDate.toString() + "] раньше или равна [" + nextDate.toString() + "]");
                    } catch (ParseException e) {
                        LOG.error(e.getMessage());
                    }
                }
            }
        return nextDate;
    }

    @ActionTitle("выбирает из списка пари тип")
    public void selectsFromWagerTypeList(String typeOfOutcome){
        LOG.info("Открываем фильтр по типу пари.");
        filterByTypeOfBid.click();
        LOG.info("Выбираем тип::" + typeOfOutcome);
        filterByTypeOfBid.findElement(By.xpath("//custom-select//div/span[contains(.,'" + typeOfOutcome + "')]")).click();
        workWithPreloader();
    }

    @ActionTitle("проверяет сортировку 'Сумма' по возрастанию")
    public void checksSortAmount(){
        WebDriver driver = PageFactory.getWebDriver();

        String xpathRowBetsAmount = "//tr[contains(@class,'showBetInfo')]/td[3]";
        String xpathPage = "//div[contains(@class,'pagination-page')]";
        List<WebElement> listOfBetsAmount = null;
        BigDecimal lastAmount = null;

        if(driver.findElements(By.xpath("//td[contains(.,'По заданным параметрам ставок не найдено')]")).size() > 0){
            LOG.info("По заданным параметрам ставок не найдено!");
        }else {
        String xpathSum = "//th[contains(@class,'my-stakes-sum')]";
        WebElement filterSum = driver.findElement(By.xpath(xpathSum));
        LOG.info("Нажимаем на фильтр 'Cумма'");
        filterSum.click();

        List<WebElement> pages = driver.findElements(By.xpath(xpathPage)).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
        if(pages.size() != 0){
            for (int page = 0; page < pages.size(); page++) {
                LOG.info("Переходим на страницу::" + (pages.get(page).getText()));
                pages.get(page).click();
                workWithPreloader();

                listOfBetsAmount = driver.findElements(By.xpath(xpathRowBetsAmount)).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
                lastAmount = compareListRowsBetsByAmount(lastAmount, listOfBetsAmount);
                }
            }else {
            listOfBetsAmount = driver.findElements(By.xpath(xpathRowBetsAmount)).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
            compareListRowsBetsByAmount(lastAmount, listOfBetsAmount);
            }
        }

    }


    /**
     * Метод принимает сумму и список строк в Мои Пари для сравнивания двух соседних(расположенной выше и ниже)
     * по возрастанию Суммы и возвращает последнюю сумму, если будут ещё страницы и она понадобится для сравнения
     * @param lastAmount последняя сумма, если она была или null
     * @param rows список строк текущей страницы
     * @return nextAmount возвращаемая последняя сумма из списка rows
     */
    private BigDecimal compareListRowsBetsByAmount(BigDecimal lastAmount, List<WebElement> rows) {
        BigDecimal currentAmount = null, nextAmount = null;
        LOG.info("Проверяем что ставок > 1.");
        if(lastAmount != null){
            currentAmount = lastAmount;
        }
        if (rows.size() < 2) {
            LOG.info("Меньше [" + rows.size() + "] ставки - невозможно стравнить.");
        } else {
            for (int i = 0; i < rows.size() - 1; i++) {
                if(lastAmount == null) {
                    currentAmount = new BigDecimal(rows.get(i).getText().trim()).setScale(2,RoundingMode.UP);
                    nextAmount = new BigDecimal(rows.get(i + 1).getText().trim()).setScale(2,RoundingMode.UP);
                }else {
                    currentAmount = lastAmount;
                    nextAmount = new BigDecimal(rows.get(i).getText().trim()).setScale(2,RoundingMode.UP);;
                    lastAmount = nextAmount;
                }

                assertThat((nextAmount.compareTo(currentAmount) == 1) || (nextAmount.compareTo(currentAmount) == 0))
                        .as("Ошибка! Текущая сумма [" + currentAmount.toString() + "] больше последующей [" + nextAmount.toString() + "]").isTrue();
                LOG.info("Текущая сумма [" + currentAmount.toString() + "] меньше или равна последующей [" + nextAmount.toString() + "]");
            }
        }
        return nextAmount;
    }

    @ActionTitle("проверяет сортировку 'Исход' с")
    public void checksSortingOfOutcomeWith (DataTable dataTable){
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        WebDriver driver = PageFactory.getWebDriver();
        String xpathPage = "//div[contains(@class,'pagination-page')]";

        String outcomeName = null, expectedText = null, actualText = null;
        List<WebElement> listOfRowsWithOutcome = null;
        StringBuilder tmp = null;

        for(int i = 0; i < table.size(); i++) {
            tmp = new StringBuilder();
            outcomeName = table.get(i).get("Тип исхода");
            tmp.append(".*(").append(table.get(i).get("Присутствует").replaceAll(",","|").replaceAll(" ", "")).append(")+.*");
            expectedText = tmp.toString();

            selectTypeOfOutcome(outcomeName);

            if(driver.findElements(By.xpath("//td[contains(.,'По заданным параметрам ставок не найдено')]")).size() > 0){
                LOG.info("По заданным параметрам ставок не найдено!");
            }else {
                List<WebElement> pages = driver.findElements(By.xpath(xpathPage)).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
                if(pages.size() != 0){
                    for (int page = 0; page < pages.size() - 1; page++) {
                        LOG.info("Переходим на страницу::" + (page + 1));
                        pages.get(page).click();
                        workWithPreloader();

                        examinationOfOutcomeWithExpectedValue (outcomeName, expectedText);
                    }
                }else {
                    examinationOfOutcomeWithExpectedValue (outcomeName, expectedText);
                }
            }
        }
    }


    /**
     * Метод ищет строки с типом исхода на странице Мои Пари
     * и сравнивает фактическое название с ожидаемым
     * @param outcomeName локатор строки с исходом пари
     * @param expectedText ожидаемое название исхода
     */
    private void examinationOfOutcomeWithExpectedValue (String outcomeName, String expectedText){
        String actualText = null;
        WebDriver driver = PageFactory.getWebDriver();
        String xpathToColumnWithOutcome = "//tr[contains(@class,'showBetInfo')]/td[4]";
        List<WebElement> listOfRowsWithOutcome = listOfRowsWithOutcome = driver.findElements(By.xpath(xpathToColumnWithOutcome)).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
        LOG.info("С типом исхода [" + outcomeName + "] найдено [" + listOfRowsWithOutcome.size() + "] строк");
        for(WebElement lineOutcome: listOfRowsWithOutcome){
        actualText = lineOutcome.getText().replaceAll("\n"," ");
        assertThat(actualText.matches(expectedText)).as("В строке [" + actualText + "] не найдено [" + expectedText + "]").isTrue();
        LOG.info("В строке [" + actualText + "] содержится [" + expectedText + "]");
        }
    }

    @ActionTitle("выбирает тип исхода")
    public void selectTypeOfOutcome(String outcomeName){
        WebDriver driver = PageFactory.getWebDriver();
        String xpathToFilterOfTypeOfOutcome = "//th[contains(.,'Событие')]//div[contains(@class,'custom-select')]";

        LOG.info("Находим и нажимаем на фильтр исхода");
        WebElement filter = driver.findElement(By.xpath(xpathToFilterOfTypeOfOutcome));
        filter.click();

        LOG.info("Выбираем  [" + outcomeName + "]");
        filter.findElement(By.xpath("following-sibling::div/div/span[contains(.,'" + outcomeName + "')]")).click();
        workWithPreloader();
    }

}
