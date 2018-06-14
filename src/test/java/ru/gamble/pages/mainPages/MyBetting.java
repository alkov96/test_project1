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
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.BUTTON;
import static ru.gamble.utility.Constants.DIRECTION;

@PageEntry(title = "Мои пари")
public class MyBetting extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MyBetting.class);

    @FindBy(xpath = "//*[contains(@class,'subMenuArea')]//h4[contains(.,'Мои пари')]")
    private WebElement pageTitle;

    @ElementTitle("Фильтр по типу пари")
    @FindBy(xpath = "//*[@class='input888wrpr']//custom-select")
    private WebElement filterByTypeOfBid;

    @ElementTitle("Фильтр по дате пари")
    @FindBy(xpath = "//div[contains(@class,'input888wrpr')]")
    private WebElement filterByDateOfBid;

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
        filterByDateOfBid.click();
        new WebDriverWait(driver,2);
        String actual = filterByDateOfBid.getText().replaceAll("\n", " ").toLowerCase();
        for(String existed: data){
            assertThat(actual).as("Строка [" + actual + "] не соответсвует [" + existed + "]").contains(existed);
        }
        LOG.info("Закрываем фильтр по типу пари.");
        filterByDateOfBid.click();
    }

    @ActionTitle("проверяет попадание ставок в диапазон дат")
    public void checksBidsOnDateRange() {
        List<WebElement> dates = PageFactory.getWebDriver().findElements(By.xpath("//span[contains(@class,'datapicker__form-text')]"));
        Date firstDate, lastDate, bidDate;
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd.MM.yyyy");

        LOG.info("Нашли датапикеров::" + dates.size());

        SimpleDateFormat formatter2 = new SimpleDateFormat("dd.MM.yy HH:mm");
        List<WebElement> rows = PageFactory.getWebDriver().findElements(By.xpath("//tr[contains(@class,'showBetInfo ')]"));
        LOG.info("Всего ставок::" + rows.size());
        LOG.info("Проверяем, что даты ставок попадают в диапазон");

        try {
            firstDate = formatter1.parse(dates.get(0).getText());
            lastDate = formatter1.parse(dates.get(1).getText());
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

            LOG.info("Открываем фильтр по типу пари.");
            filterByTypeOfBid.click();
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
                        selectedOutcome = row.findElement(By.xpath("./td[5]/div")).getText();
                        assertThat(selectedOutcome)
                                .as("Ошибка! Текст [" + selectedOutcome + "] не соответсвует [" + betType + "]").contains(betType);
                        LOG.info("Текст [" + selectedOutcome + "] соответсвует [" + betType + "]");
                        if(selectedOutcome.contains("Экспресс") || selectedOutcome.contains("Система")){
                            subIvens = row.findElements(By.xpath("//tr[contains(@class,'table-inner__row')]"));
                            if(subIvens.size() > 1){ subIvens.remove(0);} //Здесь удаляем мусорную строку
                            assertThat(subIvens.size())
                                    .as("Ошибка! В типе пари [" + typeOfOutcome + "] меньше чем " + subIvens.size() + " пари").isGreaterThan(1);
                            for(WebElement subIvent : subIvens){
                                LOG.info(subIvent.getText().replaceAll("\n", " "));
                            }
                        }
                }
            }else {
                LOG.info("Ни одного пари не найдено, нечего проверять.");
            }

            LOG.info("Закрываем фильтр по типу пари.");
            filterByTypeOfBid.click();
        }



    }

}
