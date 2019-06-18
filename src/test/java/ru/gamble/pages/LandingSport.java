package ru.gamble.pages;

import cucumber.api.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Лэндинг спорта")
public class LandingSport extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(LandingSport.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//h1[contains(@class,'landing-sports-section__h')]")
    private WebElement header;

    public LandingSport() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(header));
    }


    @ActionTitle("проверяет наличие на станице лендинга блока Горячие ставки")
    public void checkHBandAdd() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        //смотрим что есть заголовок Горячие ставки
        String pathToTitleHB = "//ul[@class='landing-sports-wmenu']/li[contains(text(),'ставки')]";
        LOG.info("displayed "+driver.findElement(By.xpath(pathToTitleHB)).isDisplayed());
        LOG.info("enabled "+driver.findElement(By.xpath(pathToTitleHB)).isEnabled());

        wait.withMessage("На странице лендинга спорта нет блока горячих ставок".toUpperCase()+"\n");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(pathToTitleHB)));
        int x = driver.findElement(By.xpath(pathToTitleHB)).getLocation().getX();
        int y = driver.findElement(By.xpath(pathToTitleHB)).getLocation().getY();
        CommonStepDefs.scrollPage(x,y);
        LOG.info("На странице лендинга есть блок Горячие ставки");
        LOG.info("Кликаем на заголовок блока ГС");
        driver.findElement(By.xpath(pathToTitleHB)).click();//кликаем на заголовк блока Горячих ставок (на тот случай, если есть еще ближашие трансляции или просто соревнование и ГС неактивны
        wait.withMessage("Не удалось переключиться на блок Горячих ставок".toUpperCase()+"\n");
        wait.until(ExpectedConditions.attributeContains(driver.findElement(By.xpath(pathToTitleHB)), "class", "active"));
    }


    @ActionTitle("добавляет игру в купон со страницы лендинга, с блока Горячие ставки")
    public void AddBetToCoupon(DataTable dataTable){
        List<String> table = dataTable.asList(String.class);
        String team1key = table.get(0);
        String team2key = table.get(1);
        String ishodKey = table.get(2);
        String coefKey = table.get(3);
        WebElement hotBet = driver.findElements(By.xpath("//div[@class='bets-widget-table hot-bets']//tr[contains(@class,'bets-widget-table__bets')]")).get(0);
        String team1 = hotBet.findElement(By.xpath("td[contains(@class,'bets-item_who1')]/div")).getAttribute("title");
        String team2 = hotBet.findElement(By.xpath("td[contains(@class,'bets-item_who2')]/div")).getAttribute("title");
        float p1 = Float.valueOf(hotBet.findElement(By.xpath("td[contains(@class,'bets-item_k1')]/div/span")).getAttribute("innerText"));

        LOG.info("Нажимаем на коэффициент к1 - победа первой коаманды в Гоячих Ставках");
        hotBet.findElement(By.xpath("td[contains(@class,'bets-item_k1')]/div/span")).click();

        Stash.put(team1key,team1);
        Stash.put(team2key,team2);
        Stash.put(ishodKey,team1);//мы выбирали победу первой команды, поэтому и в купоне название ихода должно совпадать с первой командой
        Stash.put(coefKey,p1);
    }

    @ActionTitle("прощелкивает все виды спорта лэндинга")
    public void checkkAllSportsInLanding(){
        String nameSport;
        String hrefSport;
        WebDriverWait wait = new WebDriverWait(driver,10);
        List <WebElement> landingSports = driver.findElements(By.xpath("//div[@class='landing-sports-about__list']/*"));
        for (WebElement sport:landingSports){
            nameSport = sport.getAttribute("innerText").toLowerCase().replace("\n","");
            hrefSport = sport.getAttribute("href");
            LOG.info("Переходм на лэндинг " + nameSport);
            sport.click();
            wait
                    .withMessage("Не сменился URL, так и не перешли на лэндинг спорта " + hrefSport)
                    .until(ExpectedConditions.urlContains(hrefSport));
            LOG.info("URL сменился на нужный: " + driver.getCurrentUrl());

            wait
                    .withMessage("Вверху заголовок не содержит название выбранного спорта: " + driver.findElement(By.xpath("//h1[contains(@class,'landing-sports-section__h')]")).getAttribute("innerText"))
                    .until(ExpectedConditions.attributeContains(By.xpath("//h1[contains(@class,'landing-sports-section__h')]"),"innerText",nameSport));
            LOG.info("Вверху заголовок соответсвует ожидаемому, и содержит название выбранного спорта");

            wait
                    .withMessage("Внизу иконка выбранного спорта не выбралась")
                    .until(ExpectedConditions.attributeContains(sport,"class","active"));
            LOG.info("Внизу картинка выбранного спорта выбралась и подсвтилась");
        }

    }



}


