package ru.gamble.pages.mainPages;


import cucumber.api.java.ru.Когда;
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
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;


@PageEntry(title = "Главная страница")
public class MainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MainPage.class);

    @FindBy(xpath = "//div[contains(@class,'main-slider__wrapper')]")
    private WebElement pageTitle;

    @ElementTitle("Регистрация")
    @FindBy(id = "register")
    private WebElement registrationButton;

    @ElementTitle("Вход")
    @FindBy(id = "log-in")
    private WebElement enterButton;

    @ElementTitle("Прематч")
    @FindBy(id = "prematch")
    private WebElement prematchButton;

    @ElementTitle("Лайв")
    @FindBy(id = "live")
    private WebElement liveButton;

    public MainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(pageTitle,10);
        workWithPreloader();
        new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("переключение видов спорта")
    public void checkChangeSport(String widget) {
        String path;
        switch (widget) {
            case "Горячие ставки":
                path = "//div[@class='bets-widget lastMinutesBets']";
                break;
            default:
                path = "//div[@class='bets-widget nearestBroadcasts']";
                break;
        }

        //boolean flag = true;
        // DemoSingleton allError = DemoSingleton.getInstance();
        WebDriver driver = PageFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver,10);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {
            CommonStepDefs.waitOfPreloader();
        }catch (InterruptedException e2){
            LOG.error(e2.getMessage());
        }
        LOG.info("Смотрим что страницы в виджете переключаются и содержимое контейнера соответсвует выбранному виду спорта");
        String sportName;
        //    List<WebElement> allSport = driver.findElements(By.xpath("//div[contains(@class,'nearestBroadcasts')]//li[contains(@class,'sport-tabs__item') and not(contains(@class,'no-link'))]"));
        List<WebElement> allSport = driver.findElements(By.xpath(path + "//li[contains(@class,'sport-tabs__item') and not(contains(@class,'no-link'))]"));

        for (WebElement selectSport : allSport) {
            selectSport.click();
            sportName = selectSport.findElement(By.xpath("i")).getAttribute("class").replace("sport-tabs__icon sport-icon icon-", "");
            LOG.info(sportName);
            path.toString();
            wait.until(CommonStepDefs.attributeContainsLowerCase(
                    By.xpath(path + "//div[contains(@class,'bets-widget-table__inner')]"),"class",sportName));

            if (driver.findElements(By.xpath(path + "//div[contains(@class,'bets-widget-table__inner')]/table[1]/tbody/tr")).size() == 1) {
                LOG.error("В ближайших трансляциях есть вкладка спорта " + sportName + ", но список для него пустой");
            }
        }

    }

    /**
     * поиск на виджете Ближайшие трансляции игры с (без) кнопки Смотреть. Если игры по кнопк не найдено, то берется просто первая попавшаяся
     * @param param - параметр, указывающий ищем ли мы игру с кнопкой, или без
     * в Stash сохраняет найденную игру. ключ - "gameBT"
     */
    @ActionTitle("ищет игру на БТ")
    public  void searchVideoGameBT(String param) throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        boolean haveButton = param.equals("с кнопкой Смотреть")?true:false;
        String ngclick = new String();
        List<WebElement> games = new ArrayList<>();
        if (haveButton) {
            ngclick = "button";
        } else {
            {
                ngclick = "command";
            }
        }
        CommonStepDefs.waitOfPreloader();
        int x = driver.findElement(By.xpath("//div[contains(@class,'nearestBroadcasts')]//li[contains(@class,'sport-tabs__item')]/../li[last()]")).getLocation().getX();
        int y = driver.findElement(By.xpath("//div[contains(@class,'nearestBroadcasts')]//li[contains(@class,'sport-tabs__item')]/../li[last()]")).getLocation().getY();
        CommonStepDefs.scrollPage(x, y);
        List<WebElement> allSport = driver.findElements(By.xpath("//div[contains(@class,'nearestBroadcasts')]//li[contains(@class,'sport-tabs__item')]"));//все вид спортов на виджете БТ
        int number = 0;
        do {
            games = driver.findElements(By.xpath("//div[@class='bets-widget nearestBroadcasts']/div[2]/div[1]/table[1]/tbody/tr/td[position()=1 and contains(@ng-click,'" + ngclick + "')]"));
            if (!games.isEmpty()) {
                break;
            }
            allSport.get(number).click();

            CommonStepDefs.workWithPreloader();

            number++;
        } while (number <= allSport.size() - 1);
        if (games.isEmpty()){
            LOG.info("Подходящей игры не найдено. Придется брать просто первую попавшуюся из БТ");
            games = driver.findElements(By.xpath("//div[@class='bets-widget nearestBroadcasts']/div[2]/div[1]/table[1]/tbody/tr/td[position()=1 and @ng-click]"));
            haveButton = !haveButton;
        }else {
            LOG.info("Игра " + param + " найдена ");
        }
        Stash.put("gameBT",games.get(0).findElement(By.xpath("ancestor::tr")));
        Stash.put("haveButtonKey",haveButton);
    }

    //переходит на игру нажатием на название первой команды в виджете
    @ActionTitle("переходит на игру из виджета БТ")
    public void lala(){
        WebDriver driver = PageFactory.getDriver();
        WebElement selectGame = Stash.getValue("gameBT");
        //запоминаем названия команд
        String team1 = selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who1')]/div[1]")).getAttribute("title").trim();
        String team2 = selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who2')]/div[1]")).getAttribute("title").trim();
        String sportName = selectGame.findElement(By.xpath("ancestor::div[contains(@class,'bets-widget-table__inner active')]")).getAttribute("class").split("active-")[1].toLowerCase();
        LOG.info("Игра, на которой будем проверять переход из виджета БТ: " + team1 + " - " + team2 + ". Спорт - " + sportName);
        selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who1')]/div[1]")).click();
        Stash.put("team1BTkey",team1);
        Stash.put("team2BTkey",team2);
        Stash.put("sportKey",sportName);
    }


    @ActionTitle("проверяет что переход удался")
    public void openGame() throws Exception {
        CommonStepDefs commonStepDefs = new CommonStepDefs();
        commonStepDefs.checkLinkToGame();
    }

    //добавление коэфа победы первой команды в виджете БТ
    @ActionTitle("добавляет коэф с виджета в купон")
    public void addToCouponFromBT(String widget){
        String path;
        switch (widget) {
            case "Горячие ставки":
                path = "//div[contains(@class,'lastMinutesBets')]";
                break;
            default:
                path = "//div[contains(@class,'nearestBroadcasts')]";
                break;
        }
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> games = new ArrayList<>();
        List<WebElement> allSport = driver.findElements(By.xpath(path + "//li[contains(@class,'sport-tabs__item')]"));//все вид спортов на виджете
        int number = 0;
        do {
            games = driver.findElements(By.xpath(path + "/div[2]/div[1]/table[1]/tbody/tr/td[contains(@class,'bets-item_k1')]/div[not(contains(@class,'blocked'))]"));
            if (!games.isEmpty()) {
                break;
            }
            allSport.get(number).click();

            CommonStepDefs.workWithPreloader();

            number++;
        } while (number <= allSport.size() - 1);

        WebElement selectGame = games.get(0).findElement(By.xpath("ancestor::tr"));
        String team1 = selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who1')]/div[1]")).getAttribute("title").trim();
        String team2 = selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who2')]/div[1]")).getAttribute("title").trim();
        float p1 = Float.valueOf(selectGame.findElement(By.xpath("td[contains(@class,'bets-item_k1')]/div[1]/span")).getText());
        LOG.info("Игра, на которой будем проверять добавление в купон из виджета БТ: " + team1 + " - " + team2);
        LOG.info("Коэффициент победы первой команды = " + p1);
        selectGame.findElement(By.xpath("td[contains(@class,'bets-item_k1')]/div[1]/span")).click();
        new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(By.id("menu-toggler")));
        CommonStepDefs.workWithPreloader();
        Stash.put("team1key",team1);
        Stash.put("team2key",team2);
        Stash.put("ishodKey",team1);//мы выбирали победу первой команды, поэтому и в купоне название ихода должно совпадать с первой командой
        Stash.put("coefKey",p1);
    }

    @ActionTitle("осуществляет переход на страницу, проверяет, что открылась нужная страница")
    public void widgetsOnMain(){
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> attr = driver.findElements(By.xpath("//div[@class='benef__item']/a"));
        boolean flag = true; //flag, который говорит что все ок. в конце программы смотрим, если он false - значит были ошибки и их выводим
        for (WebElement element : attr) {
            String link = element.getAttribute("href");
            flag &= CommonStepDefs.goLink(element, link);
            LOG.info("Ссылка " + link + " открылась");
        }





    }

}
