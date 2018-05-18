package ru.gamble.pages.prematchPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.pages.CouponPage;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.gamble.utility.Constants.PERIOD;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
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
            workWithPreloader();
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
        workWithPreloader();

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

        String xpathMainCategoriesOfEvents = "//a[@class='left-menu__list-item-sport-link ng-binding']";

        String xpathCountries = "";
        WebElement isOpenMenu;

        LOG.info("Ищем главные категории событий.");

        // Ожидание появения хотя-бы одного события
        List<WebElement> listMainCategoriesOfEvents = getWebDriver().findElements(By.xpath(xpathMainCategoriesOfEvents))
                .stream().filter(e -> e.isDisplayed() && !(e.getText().isEmpty())).limit(valueLimit).collect(Collectors.toList());
        LOG.info("Найдено видов категорий событий::" + listMainCategoriesOfEvents.size());

        if(listMainCategoriesOfEvents.size()>0) {
            for (WebElement event : listMainCategoriesOfEvents) {
                LOG.info(event.getText());
                boolean populate = false;
                boolean nullMarge = false;

                // Если главные категории не открыты, то открываем
                isOpenMenu = event.findElement(By.xpath(".."));
                if (!isOpenMenu.getAttribute("class").contains("active")) {
                    event.click();
                    workWithPreloader();
                }

                String match = event.getText();

                if (match.contains("ПОПУЛЯРНЫЕ СОРЕВНОВАНИЯ")) {
                    xpathCountries = "../ul/li/div";
                    populate = true;
                } else if (match.contains("НУЛЕВАЯ МАРЖА")) {
                    xpathCountries = "../ul/li/ul";
                    nullMarge = true;
                } else {
                    xpathCountries = "..//*[@class='left-menu__list-item-region-link']";
                }

                List<WebElement> listSubIvents = event.findElements(By.xpath(xpathCountries))
                        .stream().filter(e -> e.isDisplayed()).limit(valueLimit).collect(Collectors.toList());
                LOG.info("Найдено подсобытий::" + listSubIvents.size());

                // Для популряных соревнований
                if (populate) {
                    clickCompetitionsAndCheckGamesDateTime(listSubIvents, valuePeriod, valueLimit);
                    // Для нулевой маржи
                } else if (nullMarge) {
                    String xpathCountry = "..//*[@class='left-menu__list-item-region ng-scope']/a";

                    for (WebElement sport : listSubIvents) {
                        isOpenMenu = sport.findElement(By.xpath("..//*[contains(@class,'left-menu__list-item-sport ng-scope')]"));
                        if (!isOpenMenu.getAttribute("class").contains("active")) {
                            sport.click();
                        }

                        List<WebElement> listCountries = sport.findElements(By.xpath(xpathCountry))
                                .stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
                        if (listCountries.size() > 0) {
                            clickCompetitionsAndCheckGamesDateTime(listCountries, valuePeriod, valueLimit);
                        }
                    }
                    // Для всего остального
                } else {
                    if (!event.findElement(By.xpath("..")).getAttribute("class").contains("active")) {
                        event.click();
                    }

                    List<WebElement> listCountries = event.findElements(By.xpath(xpathCountries))
                            .stream().filter(el -> el.isDisplayed()).limit(valueLimit).collect(Collectors.toList());

                    if (listCountries.size() > 0) {
                        clickCompetitionsAndCheckGamesDateTime(listCountries, valuePeriod, valueLimit);
                    }
                }
            }
        }else {
            LOG.error("Не загрузилось меню списка событий!::" + listMainCategoriesOfEvents.size());
            throw new AutotestError("Не загрузилось меню списка событий!::"+ String.valueOf(listMainCategoriesOfEvents.size()));
        }
    }

    private void clickCompetitionsAndCheckGamesDateTime(List<WebElement> listCompetitions, String valuePeriod, int valueLimit){
        String xpathGames = "..//div[@class='left-menu__list-item-region-compitition compitition-b ng-scope ng-isolate-scope']";
        String xpathDateTimeGames = "//div[@class='prematch-competition-games__item-date ng-binding']";
        for (WebElement country: listCompetitions) {
            LOG.info(country.getText());
            // Если меню страны не открыта, то открываем
            if(!country.findElement(By.xpath("..")).getAttribute("class").contains("active")){
                country.click();
                workWithPreloader();
            }
            // Ищем список игровых событий в данной стране
            List<WebElement> listGames = country.findElements(By.xpath(xpathGames))
                    .stream().filter(el -> el.isDisplayed()).limit(valueLimit).collect(Collectors.toList());
            if (listGames.size() > 0) {
                for (WebElement gameItem : listGames) {
                    LOG.info(gameItem.getText());
                    gameItem.click();
                    workWithPreloader();
                    checkGames(country, valuePeriod, valueLimit);
                }
            }else {
                checkGames(country, valuePeriod, valueLimit);
            }
        }
    }

    private void checkGames(WebElement country, String valuePeriod, int valueLimit){
        String xpathDateTimeGames = "//div[@class='prematch-competition-games__item-date ng-binding']";
        String rowDateTime;
        // Ожидание появения хотя-бы одной игры в меню стран
        new WebDriverWait(getWebDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathDateTimeGames)));

        List<WebElement> listRow = country.findElements(By.xpath(xpathDateTimeGames))
                .stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
        // Здесть обрезаем большой список до последних строк valueLimit

        List<WebElement> listDateTime;
        if (listRow.size() > valueLimit) {
            listDateTime = listRow.stream().skip(listRow.size() - valueLimit).collect(Collectors.toList());
        } else {
            listDateTime = listRow;
        }

        if (listDateTime.size() > 0) {
            for (WebElement game : listDateTime) {
                LOG.info(game.findElement(By.xpath("../../../*")).getText().replaceAll("\n", " "));
                rowDateTime = game.getText();
                int period;
                if (valuePeriod.contains("Любое время")) {
                    period = 0;
                } else {
                    period = Integer.parseInt(valuePeriod.replaceAll("[\\D]+", ""));
                }
                checkDateTime(period, rowDateTime);
            }
        }else {
            LOG.error("Не надено время игры!!!");
            throw new AutotestError("Не надено время игры!!!");}
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
                    .as("Ошибка!!! Дата-время [" + gameDateTime.toString() + "] < [" + currentDateTime.toString() + "]");
            LOG.info("Дата-время [" + gameDateTime.toString() + "] > [" + currentDateTime.toString() + "]");

        }else {
            Date dateTimePlusPeriod = new Date(System.currentTimeMillis() + diapason * 3600 * 1000);

            assertThat(gameDateTime.after(currentDateTime) && gameDateTime.before(dateTimePlusPeriod))
                    .as("Ошибка!!! Дата-время [" + gameDateTime.toString() + "] вне диапазона [" + currentDateTime.toString() + " - " + dateTimePlusPeriod.toString() + "]");
            LOG.info("Дата-время [" + gameDateTime.toString() + "] соответствует [" + currentDateTime.toString() + " - " + dateTimePlusPeriod.toString() + "]");
        }
    }

}
