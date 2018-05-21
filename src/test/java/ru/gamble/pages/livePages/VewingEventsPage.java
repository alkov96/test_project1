package ru.gamble.pages.livePages;

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
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.time.LocalTime;
import java.util.List;

import static org.openqa.selenium.By.xpath;

@PageEntry(title = "Лайв просмотр событий")
public class VewingEventsPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(VewingEventsPage.class);

    @FindBy(xpath = "//a[@class='ulTransBorder__link active']")
    private WebElement pageTitle;

    public VewingEventsPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }


    /**
     * Поиск игры в прематче, подходщей по фильтру по времени. И добавление найденно игры в избранное если есть такой параметр
     *
     * @param video   - искать игру с видео или без
     * @param adding   - добавлять ил игру в Избранное. если этот параметр = "и добавляет в избранное" - то добавляем в Избранное, в остальных случаях - нет
     */
    @ActionTitle("находит игру по фильтру видео")
    public void searchGameLiveVideo(String video, String adding) {
        boolean add = adding.equals("и добавляет в избранное");
        try {
            gameLiveVideo(video.equals("с видео"), adding.equals("и добавляет в избранное"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info("Игра " + video + " найдена: " + Stash.getValue("nameGameKey"));
    }

    public void gameLiveVideo(boolean withVideo, boolean adding) throws Exception {
        WebDriver driver = PageFactory.getDriver();
        LOG.info("Переходим в лайв");
        driver.findElement(By.id("live")).click();
        CommonStepDefs.workWithPreloader();

//если меню свернуто - разворачиваем

        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (!menu.getAttribute("class").contains("collapsed")) menu.click();

        LOG.info("Сворачиваем все виды спорта");
        driver.findElement(By.id("sports-toggler")).click();

        boolean gameIsAdding = false;
        String typeGame;
        if (withVideo) {
            typeGame = "LiveWithVideo";
            if (!driver.findElement(By.xpath("//div[@id='video-filter-toggler']")).getAttribute("class").contains("active")) {//включим ильтр видео если ищем игру с видео
                driver.findElement(By.xpath("//div[@id='video-filter-toggler']/i")).click();
                CommonStepDefs.workWithPreloader();
            }
        } else {
            typeGame = "LiveWithoutVideo";
            if (driver.findElement(By.xpath("//div[@id='video-filter-toggler']")).getAttribute("class").contains("active")) {//выключим фильтр по видео, если ищем игру без видео
                driver.findElement(By.xpath("//div[@id='video-filter-toggler']/i")).click();
                CommonStepDefs.workWithPreloader();
            }
        }


        int sportsInLiveCount = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[1]/li")).size();
        for (int sportCategory = 2; sportCategory <= sportsInLiveCount; sportCategory++) {
            LOG.info("Разворачиваем вид спорта");
            driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[1]/li[" + sportCategory + "]")).click();
            int gamesInSportCount = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[1]/li[" + sportCategory + "]/ul/li/div/div/ul/li")).size();
            LOG.info("Ищем игру у которой видео-трансляция " + withVideo);
            int gameNumber = hasVideo(sportCategory, gamesInSportCount, withVideo);
//если в этом спорте есть игра с видео и мы еще не добавляли в избранное - добавляем.
            if (gameNumber != -1 && !gameIsAdding) {
                String nameGamefull = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[1]/li[" + sportCategory + "]/ul/li/div/div/ul/li/div[1]/div[1]/div[1]")).get(gameNumber).findElement(By.xpath("../p")).getText();
                CommonStepDefs.addStash("nameGameKey",nameGamefull);
                CommonStepDefs.addStash("typeGameKey",typeGame);
                if (adding) {
                    LOG.info("Нужная игра найдена. Добавляем ее в Избранное.");
                    driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[1]/li[" + sportCategory + "]/ul[1]/li/div/div/ul/li/div[1]/div[1]/div[2]/div[1]")).get(gameNumber).click();
                }
                gameIsAdding = true;
            }
//сворачиваем снова все виды спорта, чтобы все они помещались на экран. иначе, если не видно элемента (не помещается) на странице он не найдется
            LOG.info("Сворачиваем все виды спорта.");
            if (!menu.getAttribute("class").contains("collapsed")) menu.click();
            driver.findElement(By.id("sports-toggler")).click();
            if (gameIsAdding) break;

        }
        if (!menu.getAttribute("class").contains("collapsed")) menu.click();
        if (driver.findElement(By.xpath("//div[@id='video-filter-toggler']")).getAttribute("class").contains("active")) { //прежде чем выйти из это функции вернем все к первоналчальному стостоянию
            driver.findElement(By.xpath("//div[@id='video-filter-toggler']/i")).click();//т.е. выключим на всякий лучай ильтр по видео
            CommonStepDefs.workWithPreloader();
        }

    }

    /**
     * Метод Ыычисления номер игры, для которой выполняется условие наличия видеотрансляции
     * @param sportNumber - порядковый номер спорта в списке левого меню
     * @param gameNumber - количество игр в спорте
     * @param withVideo - признак того что видео дя игры есть. Если этот параметр true то метод вернет номер игры с видео. Если этот параметр false, то метод вернет номер игры БЕЗ видео
     * @return метод возвращает либо номер игры, для которой выполняется условие withVideo, либо -1 - если игры удовлетворяющих условию не найдено
     */
    public int hasVideo(int sportNumber, int gameNumber, boolean withVideo) {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> allGameInSport = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[1]/li[" + sportNumber + "]/ul/li/div/div/ul/li/div[1]/div[1]/div[1]"));
        for (int count = 0; count < gameNumber; count++) {
            if (allGameInSport.get(count).isDisplayed() == withVideo) {
                return count;
            }
        }
        return -1;
    }

    /**
     * выставляет триггер по видео согласно параметру.
     * @param onoff если onoff="включен" то включаем выльтр. иначе - выключаем
     */
    @ActionTitle("выставляет фильтр по видео на")
    public void onTriggerVideo(String onoff){
        WebDriver driver = PageFactory.getDriver();
        String active = driver.findElement(By.xpath("//div[contains(@class,'left-menu-filters__item_video')]")).getAttribute("class");
        if (onoff.equals("включен")!=active.contains("active")) {
            driver.findElement(By.id("video-filter-toggler")).click();
            CommonStepDefs.workWithPreloader();
        }
    }


    /**
     * Проверка состояния страницы Лайв после перехода на игру (из Избранного или из поиска)
     * Проверят что нужная игра будет выделена в левом меню. Проверет что фильтр "с видео" в нужном состоянии
     *
     * @param filterVideo - ждем ли мы что фильтр "с видео" включен или выключен
     * @param team1Name   - название игры, на которую перешли
     */
    public static boolean pageLive(String team1Name, boolean filterVideo) {
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;

        //если меню свернуто - разворачиваем
        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (!menu.getAttribute("class").contains("collapsed")){
            menu.click();
            CommonStepDefs.workWithPreloader();
        }

        LOG.info("Проверка страницы Live, когда фильтр по видео = " + filterVideo);
        LOG.info("Смотрим что нужная игра выделена желтым в левом меню в Моих Играх (если эта игра есть в Избранном)");
        List<WebElement> favouriteGames = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[1]/ng-include[1]/li[1]/ul[1]/li")); // избранные игры, отображаемые в левом меню
        for (int count = 0; count < favouriteGames.size(); count++) {
            String nameFavouriteGame = favouriteGames.get(count).findElement(By.xpath("div[1]/div[1]/p[1]")).getAttribute("title");
            nameFavouriteGame = CommonStepDefs.stringParse(nameFavouriteGame);
            if (team1Name.equals(nameFavouriteGame)) {
                LOG.info("В лайве открыта игра " + nameFavouriteGame);
                if (!driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[1]/ng-include[1]/li[1]/ul[1]/li[" + (count + 1) + "]")).getAttribute("class").contains("active")) {
                    flag=false;
                    LOG.error("В лайв открытая игра из Избранного не выделена активной в левом меню");
                }
                break;
            }
        }

        LOG.info("Смотрим что нужная игра выделена желтым в левом меню в общем списке игр");
        String nameActiveGame = driver.findElement(xpath("//li[contains(@class,'left-menu__favorite-list-item') and contains(@class,'active')]//p[contains(@class,'left-menu__list-item-games-teams')]")).getText();////название активной игр
        if (!CommonStepDefs.stringParse(team1Name).equals(CommonStepDefs.stringParse(nameActiveGame))){
            flag=false;
            LOG.error("В ЛАЙВе игра на которую перешли не выделена активной в левом меню. Название активной игры:" + CommonStepDefs.stringParse(nameActiveGame) + ", а ожидалось" + CommonStepDefs.stringParse(team1Name));
        }


        LOG.info("Проверка что в центральной части окна открыта нужная игра");
        List<WebElement> team = driver.findElements(By.xpath("//div[@class='live-game-summary__game-content']/div[1]/ng-include[1]/div[1]/div/div/p"));
        String nameOnPage = CommonStepDefs.stringParse(team.get(0).getAttribute("title").trim() + " - " + team.get(1).getAttribute("title").trim());
        if (!CommonStepDefs.stringParse(team1Name).equals(CommonStepDefs.stringParse(nameOnPage))) {
            flag=false;
            LOG.error("В лайв открылась неправильная игра. " + nameOnPage + " вместо " + team1Name);
        }
        LOG.info("Проверка что фильтр  не сбросился");
        if (driver.findElement(By.id("video-filter-toggler")).getAttribute("class").contains("active") != filterVideo) {
            flag=false;
            LOG.error("Сменился фильтр 'с видео'");
        }
        return flag;
    }
}
