package ru.gamble.pages.livePages;

import cucumber.api.DataTable;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.pages.userProfilePages.FavouritePage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.openqa.selenium.By.xpath;

/**
 * @author p.sivak.
 * @since 10.05.2018.
 */
@PageEntry(title = "События дня")
public class DayEventsPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(DayEventsPage.class);

    @FindBy(xpath = "//div[@class='event-widget-game-info']")
    private WebElement widget;


    public DayEventsPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(widget));
    }

    @ActionTitle("добавляет событие с баннера в купон и сохраняет в память")
    public void addEventToCouponFromBanner(String keyTeam1, String keyTeam2, String keyKoef){
        WebDriver driver = PageFactory.getDriver();
        CommonStepDefs.workWithPreloader();
        String xpathEvent = "//div[@class='event-widget-coef']/div[3]/span[2]";
        CommonStepDefs.workWithPreloader();
        LOG.info("Ищем событие.");
        List <WebElement> events = driver.findElements(By.xpath(xpathEvent))
                .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
        if(events.size() > 0) {
            LOG.info("Нажали на событие  " + events.get(0).getText());
            events.get(0).click();//добавляем событие со страницы Событие дня с баннера(вторая команда)
            String team1 = driver.findElement(By.xpath("//div[@class='event-widget-coef']/div[1]/span[1]")).getAttribute("title");//Сохраняем название команды1 на банере
            String team2 = driver.findElement(By.xpath("//div[@class='event-widget-coef']/div[3]/span[1]")).getAttribute("title");//Сохраняем название команды2 на банере
            String coef = driver.findElement(By.xpath("//div[@class='event-widget-coef__item' and contains(@ng-click,'W2')]/span[2]")).getText();
            driver.findElements(By.xpath("//span[contains (@class,'event-widget-coef')]"));
            Stash.put(keyTeam1, team1);
            Stash.put(keyTeam2, team2);
            //Stash.put(keyOutcome, team2);
            Stash.put(keyKoef, coef);
            LOG.info("Сохранили в памяти key [" + keyKoef + "] <== value [" + coef + "]");
        } else {
            throw new AutotestError("Ошибка! Ни один баннер не найден.");
        }
    }

    @ActionTitle("проверяет, совпадают ли названия событий на кнопках на баннере и сверху c")
    public void teamsOnBanner(String keyTeam1, String keyTeam2){
        WebDriver driver = PageFactory.getDriver();
        String teamname1 = driver.findElement(By.xpath("//div[@class='event-widget-full-info']//div[contains(@ng-bind,'team1_name')]")).getText(); //Имя команды на банере в окошке
        String teamname2 = driver.findElement(By.xpath("//div[@class='event-widget-full-info']//div[contains(@ng-bind,'team2_name')]")).getText(); //Имя команды на банере в окошке
        String team1 = Stash.getValue(keyTeam1);
        String team2 = Stash.getValue(keyTeam2);
        if (CommonStepDefs.stringParse(teamname1+teamname2).equals(CommonStepDefs.stringParse(team1 + team2))) {
            LOG.info("Названия команд на баннере и названия команд на баннере на кнопках совпадают: [" + teamname1 + "] - [" + teamname2 + "] = [" + team1 + "] - [" + team2 + "]");
        }
        else Assertions.fail("Названия команд на баннере и названия команд на баннере на кнопках не совпадают: ["  + teamname1 + "] - [" + teamname2 + "] = [" + team1 + "] - ["+ team2 + "]");
    }

    @ActionTitle("кликает на три вида спорта и проверяет, что игры соответвтвуют выбранному виду")
    public void typesSportClick(){
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> typesSport = driver.findElements(By.xpath("//div[@class='bets-widget__sports']//ul[contains(@class,'bets-widget__sports-list ng-scope')]//li"));//создаём список все видов спорта в события дня
        int counter = 0;
        String iconSport;
        String activeSport;
        for (WebElement sport : typesSport) { //каждый вэб-элемент спорт это элемент из списка тайпсспорт
            sport.click();//нажала на первую иконку
            new WebDriverWait(driver,10).until(ExpectedConditions.attributeContains(sport, "class", "active"));
            iconSport = sport.findElement(By.xpath("i")).getAttribute("class").replace("sport-icon icon-", "").toLowerCase();//реплейс - замена подстроки.+привели сроки к низкому регистру
            LOG.info("Нажала на иконку " + iconSport);
            activeSport = driver.findElement(By.xpath("//div[@class='bets-widget bets-widget_wide liveNow']//div[@class='bets-widget-table']/div[1]")).getAttribute("class");
            activeSport = activeSport.replace("bets-widget-table__inner active-", "").toLowerCase();
            if (!activeSport.equals(iconSport)) {
                Assertions.fail("игры в списке не соответствуют выбраному виду спорта.");
            }
            counter++;
            if (counter >= 4) break;
        }
    }

    @ActionTitle("добавляет событие в избранное")
    public void addEventToFavourite() throws Exception {
        WebDriver driver = PageFactory.getDriver();
        FavouritePage.clearFavouriteGames();
        List<WebElement> stars = driver.findElements(By.xpath("//tr[@class='bets-widget-table__bets ng-scope']/td[10]//span[contains(@class,'favorite-icon-dashboard')]"));
        List<WebElement> team1 = driver.findElements(By.xpath("//td[@class='bets-widget-table__bets-item bets-widget-table__bets-item_who1']//span[contains(@class,'market-info-b market-name-j')]"));//название 1 команды в списке
        if(team1.size() > 0 && stars.size() > 0) {
            String team1name = team1.get(3).getAttribute("title");
            Stash.put("team1nameKey", team1name);
            List<WebElement> team2 = driver.findElements(By.xpath("//td[@class='bets-widget-table__bets-item bets-widget-table__bets-item_who2']//span[contains(@class,'market-info-b market-name-j')]"));//название 2 команды в списке
            String team2name = team2.get(3).getAttribute("title");
            Stash.put("team2nameKey", team2name);
            LOG.info("Все иконки избранного на странице обнаружены");
            stars.get(3).click();
            LOG.info("Добавили в Избранное событие");
        }else {
            throw new AutotestError("Ошибка! В Лайв в События дня не нашли ни одного события.");
        }
    }

    @ActionTitle("проверяет, добавилось ли событие в избранное")
    public void checkIsEventAddToFav() throws Exception {
        WebDriver driver = PageFactory.getDriver();
        driver.findElement(By.id("elected")).click();
        Thread.sleep(5000);
        if (!driver.findElement(By.xpath("//div[contains(@class,'elected__teams ellipsis-text')]")).isDisplayed()){
            LOG.info("Событие не добавилось в избранное, попробуем ещё раз.");
            addEventToFavourite();
        }
        else LOG.info("События добавились в Избранное");
        driver.findElement(By.id("elected")).click();
    }

    public static void addEventsToCouponF () throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        int counter = 0;
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        List<WebElement> allEvents = driver.findElements(xpath("//div[@class='bets-widget bets-widget_wide liveNow']//table[@class='full_width bets-widget-table']//tr/td[5]/div/span")).
                stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
        if(allEvents.size() > 0){
            for (WebElement event : allEvents) {
                Thread.sleep(1000);
                event.click();
                event.getText();
                LOG.info("Событие добавилось в купон");
                counter++;
                if (counter >= 6) break;
            }
        }else {
            throw new AutotestError("Ошибка! Лайв->События дня:: Ни одного события не найдено.");
        }
    }

    @ActionTitle("добавляет несколько событий в купон")
    public void addEventsToCoupon() throws InterruptedException {
        addEventsToCouponF();
    }
}



