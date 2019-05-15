package ru.gamble.pages.livePages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cucumber.api.java.en_old.Ac;
import org.junit.Assert;
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

import javax.xml.ws.Action;
import java.util.ArrayList;
import java.util.List;

/**
 * @author p.sivak.
 * @since 10.05.2018.
 */
@PageEntry(title = "Мультимонитор")
public class MultimonitorPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MultimonitorPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//div[@class=' multiview-wrapper']")
    private WebElement multiviewWrapper;

    public MultimonitorPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(multiviewWrapper));
    }

    @ActionTitle("добавляет монитор для игры из избранного")
    public void addMonitorFav(String keyGame, String keyName){
        List<String> names = Stash.getValue("nameGameKey");
        List<String> types = Stash.getValue("typeGameKey");
        WebElement myGamesHeader = driver.findElement(By.xpath("//li[contains(@class,'left-menu__list-item-sport_favorite')]"));
        //разворачиваем список МОИ ИГРЫ
        if (!myGamesHeader.getAttribute("class").contains("active")){
            myGamesHeader.click();
            new WebDriverWait(driver,10)
                    .until(ExpectedConditions.attributeContains(myGamesHeader,"class","active"));
        }

        List<String> teams = new ArrayList<>();
        int index = types.indexOf(keyGame);
        Assert.assertFalse("Игры с таким типом в Избранном нет " + keyGame,index==-1);
        String name = names.get(index);
        LOG.info("В ЛМ в МОИХ ИГРАХ будем выбирать игру с названием " + name);
        Stash.put(keyName,name);
        List<WebElement> listInMyGames = driver.findElements(By.xpath("//ul[@class='left-menu__favorite-list']//div[contains(@class,'left-menu__list-item-games-names')]"));
        listInMyGames.forEach(str -> teams.add(CommonStepDefs.stringParse(str.getAttribute("innerText"))));
        index = teams.indexOf(CommonStepDefs.stringParse(name));
        Assert.assertFalse("Игры с таким названием в Избранном нет",index==-1);
        LOG.info("В ЛМ в МОИ ИГРЫ нужная гра находится под номером " + index);
        listInMyGames.get(index).click();
    }

    @ActionTitle("проверяет что количество мониторов сейчас равно")
    public void checkMonitorIsAdding(String expCount){
        By byMonitors = By.xpath("//div[contains(@class,'multiview-contain') and not(contains(@class,'no-games'))]");
        new WebDriverWait(driver,10)
                .withMessage("Количество мониторов не совпадает с ожидаемым. Их сейчас " + driver.findElements(byMonitors).size())
                .until(ExpectedConditions.numberOfElementsToBe(byMonitors,Integer.valueOf(expCount)));
        LOG.info("Количество мониторов совпадает с ожидаемым: " + expCount);
    }

    @ActionTitle("проверяет что на мониторах есть игра")
    public void checkMonitorsHaveGame(String keyName){
        List<WebElement> allMonitors = driver.findElements(By.xpath("//div[contains(@class,'multiview-contain') and not(contains(@class,'no-games'))]"));
        StringBuilder nameOnMonitor = new StringBuilder();
        String nameInMemory = keyName;
        if (keyName.matches("[A-Z]*")){
            nameInMemory = Stash.getValue(keyName);
        }
        nameInMemory = CommonStepDefs.stringParse(nameInMemory);
        for (WebElement monitor : allMonitors){
            nameOnMonitor.setLength(0);
            String[] nameM = monitor.findElement(By.xpath(".//*[contains(@class,'game-score__inner')]")).getAttribute("innerText").split("\n\n");
            nameOnMonitor.append(nameM.length==1?nameM[0]:nameM[0] + " - " + nameM[2]);
            LOG.info("На одном из мониторов игра:" + nameOnMonitor.toString());
            if (CommonStepDefs.stringParse(nameOnMonitor.toString()).equals(nameInMemory)){
                LOG.info("Нашлась нужная игра на мониторах. Проверка прошла успешно");
                return;
            }
        }
        Assert.fail("На мониторах не нашлась игра " + nameInMemory);
    }

    @ActionTitle("проверяет что появилась ошибка")
    public void checkErrors(String error){
        By byError = By.xpath("//div[contains(@class,'modal active')]");
        new WebDriverWait(driver,10)
                .withMessage("Нет попапа с ошибкой при добавлении монитора")
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(byError,0));
        LOG.info("Попап с ошибкой есть, теперь проверим текст");
        Assert.assertTrue("Ошибка на попапе не совпадает с ожидаемой : '" + driver.findElement(byError).getAttribute("innerText") + "'",
                driver.findElement(byError).getAttribute("innerText").contains(error));
        LOG.info("Текст ошибки совпадает с ожидаемым. Теперь закроем попап");
        driver.findElement(byError).findElement(By.xpath(".//div[@class='modal__closeBtn closeBtn']")).click();
    }



    @ActionTitle("добавляет на монитор игру")
    public void addGameWithVideo(String hasVideo, String keyName){
        boolean withVideo = hasVideo.equals("с видео");
      //  Stash.put("nameGameKey","");
        try {
            new VewingEventsPage().gameLiveVideo(withVideo, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> names = Stash.getValue("nameGameKey");
        String name = names.get(0).trim();
        String team1Split = name.split(" ")[0];
        String team2Split = name.split(" ")[(name.split(" ").length)-1];
        LOG.info("Игру нашли: " + name);

//        driver.findElement(By.id("multimonitor")).click();
//        CommonStepDefs.workWithPreloader();
//        driver.findElement(By.xpath("//div[@class='left-menu__list-item-games-names' and contains(text(),'" + name + "')]")).click();
        driver.findElement(By.xpath("//div[@class='left-menu__list-item-games-names']/*[contains(text(),'" + team1Split + "')]/following-sibling::*[contains(text(),'" + team2Split + "')]")).click();
        Stash.put("nameGameKey","");
    }

    @ActionTitle("добавляет на монитор игры, пока их не станет")
    public void addMonitorWithCheck(String howmuch,String keyListGames){
        int count = Integer.valueOf(howmuch);
        addGames(count,keyListGames,true);
    }

    /**
     * метод добавляет игры на мониторы
     * @param count - сколько игр нужно добавить
     * @param keyListGames - имя списка, в котором будут храниться названия игр
     * @param needCheck - нужна ли проверк что игра добавилась, или нужно просто кликнуть и все
     */
    private void addGames(int count,String keyListGames,boolean needCheck){
        LOG.info("В левом меню выберем одну игру, добавим ее на монитор");

        //если меню свернуто - разворачиваем
        setExpandCollapseMenusButton(true);

        LOG.info("Сворачиваем все виды спорта");
        closeSports();
        By xpathForSports = By.xpath("//li[contains(@id,'sport-') and not(contains(@class,'hide')) and not(contains(@id,'sport--'))]");
        int countNow=0;
        String pathToNameGame = ".//li[contains(@class,'left-menu__list-item-games')]";
        By xpathForRegion = By.xpath("./ancestor-or-self::div[contains(@class,'left-menu__list-item-region-competition')]/h4");
        List<String> nameGames = new ArrayList<>();
        int countI = 0;
        String name = new String();
        int sportsInLiveCount = driver.findElements(xpathForSports).size();
        for (int sportCategory = 0; sportCategory < sportsInLiveCount; sportCategory++) {
            LOG.info("Разворачиваем вид спорта");
            driver.findElements(xpathForSports).get(sportCategory).click();

            List<WebElement> gamesInSport = driver.findElements(xpathForSports).get(sportCategory).findElements(By.xpath(pathToNameGame));

            for (WebElement game:gamesInSport){
                //если игра еще не выведена на монитор - кликаем по ней
                if (!game.findElement(By.xpath("./div")).getAttribute("class").contains("active")){
                    name = game.findElement(By.xpath(".//div[@class='left-menu__list-item-games-names']")).getAttribute("innerText");
                    //если регион для это игры свернут - развернем его
                    if (!game.findElement(xpathForRegion).getAttribute("class").contains("competitionInLive")){
                        game.findElement(xpathForRegion).click();
                        new WebDriverWait(driver,10)
                                .withMessage("Регион для игры " + name + " не развернулся")
                                .until(ExpectedConditions.attributeContains(game.findElement(xpathForRegion),"class","competitionInLive"));
                    }
                    nameGames.add(name);
                    game.click();
                    countI++;
                }
                if (!needCheck && countI==count){
                    return;
                }
                if (needCheck) {
                    //если уже набрали нужное количество игры - выходим из цикла
                    if (driver.findElements(By.xpath("//div[contains(@class,'multiview-contain') and not(contains(@class,'no-games'))]")).size() == count) {
                        Stash.put(keyListGames, nameGames);
                        return;
                    }
                }
            }
        }
    }


    @ActionTitle("проверяет что все выделенные в ЛМ игры есть на мониторах")
    public void checkMonitorsHaveGames(){
        List<WebElement> allMonitors = driver.findElements(By.xpath("//div[contains(@class,'multiview-contain') and not(contains(@class,'no-games'))]"));
        List<WebElement> allActiveGames = driver.findElements(By.xpath("//div[contains(@class,'left-menu__list-item-games-row') and contains(@class,'active')]"));
        String name = new String();
        if (allActiveGames.size()!=allMonitors.size()){
            Assert.fail("Размер списка со всеми играми, выделенными в ЛМ(" + allActiveGames.size() + "), и списка вех мониторов - не свопадают!(" + allMonitors.size() + ")");
        }
        for (WebElement activeGame:allActiveGames){
            name = activeGame.findElement(By.xpath(".//div[contains(@class,'left-menu__list-item-games-names')]")).getAttribute("innerText");
            LOG.info("Проверим есть ли на мониторах игра " + name);
            checkMonitorsHaveGame(name);
        }
    }

    @ActionTitle("пытается добавить в мультимонитор еще игр в количестве")
    public void addMonitorWithoutCheck(String count){
        addGames(Integer.valueOf(count),"GAME",false);
    }

}
