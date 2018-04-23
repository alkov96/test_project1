package ru.gamble.pages;

import gherkin.lexer.Pa;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.gamble.pages.utility.Constants.PERIOD;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;

@PageEntry(title = "Просмотр событий")
public class EventViewerPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(EventViewerPage.class);

    @FindBy(xpath = "//div[contains(@class,'menu-toggler')]")
    private WebElement expandСollapseMenusButton;

    @ElementTitle("Период времени")
    @FindBy(xpath = "//div[contains(@class,'periods__input')]")
    private WebElement selectPeriod;


    public EventViewerPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
            new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(expandСollapseMenusButton));
            checkMenuIsOpen();
    }

    private void checkMenuIsOpen(){
        if(expandСollapseMenusButton.getAttribute("title").contains("Показать всё")){
            expandСollapseMenusButton.click();
        }
    }

    @ActionTitle("выбирает время")
    public void chooseTime(String key){
        String value = "";
        if(key.equals(PERIOD)){
            value = Stash.getValue(key);
        }else {value = key;}

        selectPeriod.click();
        selectPeriod.findElement(By.xpath("//*[contains(text(),'" + value + "')]")).click();
    }

    @ActionTitle("проверяет время игр")
    public void checkGamesWithPeriod(String period, String limit){
        String valuePeriod = "";
        if(period.equals(PERIOD)){
            valuePeriod = Stash.getValue(period);
        }else{
            valuePeriod = period;
        }
        int valueLimit = Integer.parseInt(Stash.getValue(limit));


        String xpathOfSports = "//*[contains(@class,'left-menu__list-item-sport ng-scope')]";
        String xpathCountries = "";

        LOG.info("Ищем виды спорта.");
        List<WebElement> listOfSports = getWebDriver().findElements(By.xpath(xpathOfSports))
                .stream().filter(e -> e.isDisplayed()).limit(valueLimit).collect(Collectors.toList());
        LOG.info("Найдено видов спорта::" + listOfSports.size());
        for (WebElement info:listOfSports) {

            LOG.info(info.findElement(By.xpath("./*")).getText());
        }

        for(WebElement sport: listOfSports){
            boolean populate = false;
           if(!sport.getAttribute("class").contains("active")){ sport.findElement(By.xpath("./a")).click(); }

            LOG.info("Ищем страны.");
           if(sport.findElement(By.xpath("./*")).getText().contains("ПОПУЛЯРНЫЕ СОРЕВНОВАНИЯ")){
               xpathCountries = "./ul/li/div";
               populate = true;
           }else { xpathCountries = "ul/li";}

            List<WebElement> listCountries = sport.findElements(By.xpath(xpathCountries))
                    .stream().filter(e -> e.isDisplayed()).limit(valueLimit).collect(Collectors.toList());
            LOG.info("Найдено стран::" + listCountries.size());

            if(populate){
                clickGameAndCheckDateTime(listCountries,valuePeriod,valueLimit);
            }else {
                for (WebElement country : listCountries) {
                    if (!country.getAttribute("class").contains("active")) {
                        country.findElement(By.xpath("./a")).click();
                    }
                    LOG.info(country.toString());
                    clickGameAndCheck(country, valuePeriod, valueLimit);
                }
            }
        }
    }

    private void clickGameAndCheckDateTime(List<WebElement> listCountries, String valuePeriod, int valueLimit){
        String xpathDateTimeGames = "//div[@class='prematch-competition-games__item-date ng-binding']";
        String rowDateTime;
        for (WebElement game: listCountries) {
                //Нажимаем на ссылку каждой игры и проверяем время
                LOG.info("::" + game.getText());
                game.click();
                List<WebElement> listDateTime = PageFactory.getWebDriver().findElements(By.xpath(xpathDateTimeGames))
                        .stream().filter(e -> e.isDisplayed()).limit(valueLimit).collect(Collectors.toList());
                //Проверяем все строки с датой и временем
                for (WebElement row : listDateTime) {
                    rowDateTime = row.getText();
                    int period;
                    if(valuePeriod.contains("Любое время")){
                        period = 0;
                    }else {
                        period = Integer.parseInt(valuePeriod.replaceAll("[\\D]+", ""));
                    }
                    checkDateTime(period, rowDateTime);
                }
        }
    }


    private void clickGameAndCheck(WebElement country,String valuePeriod, int valueLimit){
        String xpathGames = "./following-sibling::*[1]/*";
        String xpathDateTimeGames = "//div[@class='prematch-competition-games__item-date ng-binding']";
        String rowDateTime;

        List<WebElement> listGames = country.findElements(By.xpath(xpathGames))
                .stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
        //Нажимаем на ссылку каждой игры и проверяем время
        for (WebElement game:listGames){
            LOG.info(country.getText() + "::" + game.getText());
            game.click();
            List<WebElement> listDateTime = PageFactory.getWebDriver().findElements(By.xpath(xpathDateTimeGames))
                    .stream().filter(e -> e.isDisplayed()).limit(valueLimit).collect(Collectors.toList());
            //Проверяем все строки с датой и временем
            for (WebElement row:listDateTime){
                rowDateTime = row.getText();
                checkDateTime(Integer.parseInt(valuePeriod.replaceAll("[\\D]+","")),rowDateTime);
            }
        }
    }


    private void checkDateTime(int diapason, String currentGameDateTime){
        Date currentDateTime = new Date(System.currentTimeMillis());

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - dd MMM yyyy");
        Date gameDateTime = null;

        try {
            gameDateTime = formatter.parse(currentGameDateTime);
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }

        if(diapason == 0){
            assertThat(gameDateTime.after(currentDateTime))
                    .as("Ошибка!!! Дата-время [" + gameDateTime.toString() + "] не позже [" + currentDateTime.toString() + "]");
            LOG.info("Дата-время [" + gameDateTime.toString() + "] больше [" + currentDateTime.toString() + "]");

        }else {
            Date dateTimePlusPeriod = new Date(System.currentTimeMillis() + diapason * 3600 * 1000);

            assertThat(gameDateTime.after(currentDateTime) && gameDateTime.before(dateTimePlusPeriod))
                    .as("Ошибка!!! Дата-время [" + gameDateTime.toString() + "] вне диапазона [" + currentDateTime.toString() + " - " + dateTimePlusPeriod.toString() + "]");
            LOG.info("Дата-время [" + gameDateTime.toString() + "] соответствует [" + currentDateTime.toString() + " - " + dateTimePlusPeriod.toString() + "]");
        }
    }


    private void openArrowAndCheckGameTime(List<WebElement> list, String expectedValue){
        String rowDateTame = "";
            for (WebElement openArrow:list) {
                List<WebElement> countriesList = openArrow.findElements(By.xpath("//left-menu__list-item-region ng-scope"));
                if(countriesList.size()>0){
                    for (WebElement country: countriesList){
                        country.click();
                        List<WebElement> gamesList = getWebDriver().findElements(By.className("ng-binding"));
                        if (gamesList.size()>0){
                            for (WebElement game:gamesList) {
                                game.click();
                                rowDateTame = getWebDriver().findElement(By.xpath("//div[@class='prematch-competition-games__item-date ng-binding']")).getText();
                            }
                        }
                    }
                }
            }
    }
}
