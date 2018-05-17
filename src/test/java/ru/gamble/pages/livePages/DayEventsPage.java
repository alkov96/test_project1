package ru.gamble.pages.livePages;

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
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.time.LocalTime;
import java.util.List;

/**
 * @author p.sivak.
 * @since 10.05.2018.
 */
@PageEntry(title = "События дня")
public class DayEventsPage extends AbstractPage {
    WebDriver driver = PageFactory.getDriver();
    WebDriverWait wait = new WebDriverWait(driver,10);

    private static final Logger LOG = LoggerFactory.getLogger(DayEventsPage.class);

    @FindBy(xpath = "//div[@class='event-widget-game-info']")
    private WebElement widget;


    public DayEventsPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(widget));
    }

    @ActionTitle("добавляет событие с баннера в купон")
    public void addEventToCoupon(){
        CommonStepDefs.workWithPreloader();
        WebDriver driver = PageFactory.getDriver();
        WebElement event = driver.findElement(By.xpath("//div[@class='event-widget-coef']/div[3]/span[2]"));
        LOG.info("Нажали на событие  "+ event.getText());
        event.click();//добавляем событие со страницы Событие дня с баннера(вторая команда)
        teamsOnBannerAndCoupon();
    }


    public void teamsOnBannerAndCoupon(){
        WebDriver driver = PageFactory.getDriver();
        String team2 = driver.findElement(By.xpath("//div[@class='event-widget-coef']/div[3]/span[1]")).getAttribute("title");//Сохраняем название команды2 на банере
        String team1 = driver.findElement(By.xpath("//div[@class='event-widget-coef']/div[1]/span[1]")).getAttribute("title");//Сохраняем название команды1 на банере
        float coef = Float.valueOf(driver.findElement(By.xpath("//div[@class='event-widget-coef__item' and contains(@ng-click,'P2')]/span[2]")).getText());
        Stash.put("team1key", team1);
        Stash.put("team2key", team2);
        Stash.put("ishodKey", team2);
        Stash.put("coefKey", coef);
    }

    @ActionTitle("проверяет, совпадают ли названия событий на кнопках на баннере и сверху")
    public void teamsOnBanner(){
        WebDriver driver = PageFactory.getDriver();
        String teamname1 = driver.findElement(By.xpath("//div[@class='event-widget-full-info']//div[contains(@ng-bind,'team1_name')]")).getText(); //Имя команды на банере в окошке
        String teamname2 = driver.findElement(By.xpath("//div[@class='event-widget-full-info']//div[contains(@ng-bind,'team2_name')]")).getText(); //Имя команды на банере в окошке
        String team1 = Stash.getValue("team1key");
        String team2 = Stash.getValue("team2key");
        if (CommonStepDefs.stringParse(teamname1+teamname2).equals(CommonStepDefs.stringParse(team1 + team2))) {
            LOG.info("Названия команд на баннере и названия команд на баннере на кнопках совпадают: " + teamname1+ " - "+ teamname2 + "=" + team1+ "-"+ team2);
        }
        else Assertions.fail("Названия команд на баннере и названия команд на баннере на кнопках не совпадают: "  + teamname1+ " - "+ teamname2 + "=" + team1+ "-"+ team2);

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
            wait.until(ExpectedConditions.attributeContains(sport, "class", "active"));
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
        String team1name = team1.get(3).getAttribute("title");
        Stash.put("team1nameKey", team1name);
        List<WebElement> team2 = driver.findElements(By.xpath("//td[@class='bets-widget-table__bets-item bets-widget-table__bets-item_who2']//span[contains(@class,'market-info-b market-name-j')]"));//название 2 команды в списке
        String team2name = team2.get(3).getAttribute("title");
        Stash.put("team2nameKey", team2name);
        LOG.info("Все иконки избранного на странице обнаружены");
        stars.get(3).click();
        LOG.info("Добавили в Избранное событие");
    }
}



