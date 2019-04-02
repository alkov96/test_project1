package ru.gamble.pages.livePages;

import cucumber.api.DataTable;
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
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.xpath;

@PageEntry(title = "Лайв просмотр событий")
public class VewingEventsPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(VewingEventsPage.class);

    @FindBy(xpath = "//a[@class='ulTransBorder__link active']")
    private WebElement pageTitle;

    private static By xpathForSports = By.xpath("//li[contains(@id,'sport-') and not(contains(@class,'hide')) and not(contains(@id,'sport--'))]");

    public VewingEventsPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
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

    public void gameLiveVideo(boolean withVideo, boolean adding){
        WebDriver driver = PageFactory.getDriver();
        int sizeFavourite = driver.findElements(By.xpath("//ul[@class='left-menu__favorite-list']/li")).size();
        LOG.info("Переходим в лайв");
        driver.findElement(By.id("live")).click();
        CommonStepDefs.workWithPreloader();

//если меню свернуто - разворачиваем

        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (!menu.getAttribute("class").contains("collapsed")) menu.click();

        LOG.info("Сворачиваем все виды спорта");
        closeSports();

        boolean gameIsAdding = false;
        String typeGame;LOG.info("Выставлем фильтр по видео на " + withVideo);

        if (withVideo) {
            typeGame = "LiveWithVideo";
            if (!driver.findElement(By.xpath("//div[@id='video-filter-toggler']")).getAttribute("class").contains("active")) {//включим ильтр видео если ищем игру с видео
                driver.findElement(By.xpath("//div[@id='video-filter-toggler']")).click();
                CommonStepDefs.workWithPreloader();
            }
        } else {
            typeGame = "LiveWithoutVideo";
            if (driver.findElement(By.xpath("//div[@id='video-filter-toggler']")).getAttribute("class").contains("active")) {//выключим фильтр по видео, если ищем игру без видео
                driver.findElement(By.xpath("//div[@id='video-filter-toggler']")).click();
                CommonStepDefs.workWithPreloader();
            }
        }


        int sportsInLiveCount = driver.findElements(xpathForSports).size();
        for (int sportCategory = 0; sportCategory < sportsInLiveCount; sportCategory++) {
            LOG.info("Разворачиваем вид спорта");
            driver.findElements(xpathForSports).get(sportCategory).click();
            String pathToNameGame = ".//*[contains(@class,'left-menu__list-item-games-teams')]";
            String pathToStarGame = ".//*[contains(@class,'item-games-fav-game-star')]";
            int gamesInSportCount = driver.findElements(xpathForSports).get(sportCategory).findElements(By.xpath(pathToNameGame)).size();
            LOG.info("Ищем игру у которой видео-трансляция " + withVideo);
            int gameNumber = hasVideo(sportCategory, gamesInSportCount, withVideo);
            //если в этом спорте есть игра с видео и мы еще не добавляли в избранное - добавляем.
            if (gameNumber != -1 && !gameIsAdding) {
                String nameGamefull = driver.findElements(xpathForSports).get(sportCategory).findElements(By.xpath(pathToNameGame)).get(gameNumber).getAttribute("innerText");
                CommonStepDefs.addStash("nameGameKey",nameGamefull);
                CommonStepDefs.addStash("typeGameKey",typeGame);
                if (adding) {
                    LOG.info("Нужная игра найдена. Добавляем ее в Избранное.");
                    if (!driver.findElements(xpathForSports).get(sportCategory).findElements(By.xpath(pathToStarGame)).get(gameNumber).isDisplayed()){
                        driver.findElements(xpathForSports).get(sportCategory).findElements(By.xpath(pathToStarGame)).get(gameNumber).findElement(By.xpath("./ancestor::div[contains(@class,'poup-sports')]/preceding-sibling::h4")).click();
                    }
                    driver.findElements(xpathForSports).get(sportCategory).findElements(By.xpath(pathToStarGame)).get(gameNumber).click();
                    new WebDriverWait(driver,10)
                            .withMessage("Игра в избранное не добавилась!!")
                            .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//ul[@class='left-menu__favorite-list']/li"),sizeFavourite));
                }
                gameIsAdding = true;
            }
            //сворачиваем снова все виды спорта, чтобы все они помещались на экран. иначе, если не видно элемента (не помещается) на странице он не найдется
            LOG.info("Сворачиваем все виды спорта.");
            if (!menu.getAttribute("class").contains("collapsed")) menu.click();
            closeSports();
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
        List<WebElement> allGameInSport = driver.findElements(xpathForSports).get(sportNumber).findElements(By.xpath(".//div[contains(@class,'icon-video')]"));
        for (int count = 0; count < gameNumber; count++) {
            if (allGameInSport.get(count).getAttribute("class").contains("hide")!=withVideo) {
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
    public static boolean pageLive(String team1Name, boolean filterVideo, boolean isFavorit) {
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;

        //если меню свернуто - разворачиваем
        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (!menu.getAttribute("class").contains("collapsed")){
            menu.click();
            CommonStepDefs.workWithPreloader();
        }

        LOG.info("Проверка страницы Live, когда фильтр по видео = " + filterVideo);
        if (isFavorit){
            flag = inLeftMenuGameSelected(team1Name);
        }
        else {
            String nameOnLeftMenu =
                    driver.findElement(xpath("//li[contains(@class,'left-menu__list-item-games') and contains(@class,'active')]" +
                            "//p[contains(@class,'left-menu__list-item-games-teams')]")).getAttribute("innerText");
            Assert.assertTrue(
                    "В левом меню выделена желтым неправильная игра. Вместо " + team1Name + " выделена " +nameOnLeftMenu,
                    CommonStepDefs.stringParse(nameOnLeftMenu).equals(team1Name));
        }

        LOG.info("Проверка что в центральной части окна открыта нужная игра");
        List<WebElement> team = driver.findElements(By.xpath("//div[@class='live-game-summary__game-content']/div[1]/ng-include[1]/div[1]/div/div/p"));
        String nameOnPage = CommonStepDefs.stringParse(team.get(0).getAttribute("title").trim() + " - " + team.get(1).getAttribute("title").trim());
        if (!CommonStepDefs.stringParse(team1Name).equals(CommonStepDefs.stringParse(nameOnPage))) {
            flag=false;
            LOG.error("В лайв открылась неправильная игра. " + nameOnPage + " вместо " + team1Name);
        }
        LOG.info("Проверка что фильтр 'С видео' в правильном состоянии " + filterVideo);
        if (driver.findElement(By.id("video-filter-toggler")).getAttribute("class").contains("active") != filterVideo) {
            flag=false;
            LOG.error("Фильтр 'с видео' не в том состоянии, что ожидалось." + !filterVideo + ", вместо " + filterVideo);
        }
        return flag;
    }


    /**
     * Проверка что нужная игра есть в Избранном в левом меню и выделена там желтым
     */
    public static boolean inLeftMenuGameSelected(String team1Name){

        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
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
        String nameActiveGame = driver.findElement(xpath("//li[contains(@class,'left-menu__favorite-list-item') and contains(@class,'active')]//p[contains(@class,'left-menu__list-item-games-teams')]")).getAttribute("innerText");////название активной игр
        if (!CommonStepDefs.stringParse(team1Name).equals(CommonStepDefs.stringParse(nameActiveGame))){
            flag=false;
            LOG.error("В ЛАЙВе игра на которую перешли не выделена активной в левом меню. Название активной игры:" + CommonStepDefs.stringParse(nameActiveGame) + ", а ожидалось" + CommonStepDefs.stringParse(team1Name));
        }
        return flag;
    }


    /**
     * Проверка что при свёрнутом левом меню Лайв отображаются иконки видов спорта больше чем указанное число
     * Проверят что нужная игра будет выделена в левом меню. Проверет что фильтр "с видео" в нужном состоянии
     * @param number - минимальное число иконок
     */
    @ActionTitle("проверяет в свёрнутом левом меню иконок видов спорта больше")
    public void checksMinimizedLeftMenuPresenceIconsSports (String number){
        WebDriver driver = PageFactory.getWebDriver();
        String xpathLeftMenu = "//div[contains(@class,'menu-toggler')]";
        String xpathTypeOfSports = "//a[contains(@class,'list-item-sport-link')]";
        WebElement leftMenu = driver.findElement(By.xpath(xpathLeftMenu));
        setExpandCollapseMenusButton(false);
        List<WebElement> list = driver.findElements(By.xpath(xpathTypeOfSports)).stream().filter(e -> (e.isDisplayed() && (!e.getAttribute("title").isEmpty()))).collect(Collectors.toList());
        for (WebElement el: list) {
            LOG.info("Найден::" + el.getAttribute("title"));
        }
        assertThat(list.size() > Integer.parseInt(number)).as("Ошибка! Иконок видов спорта [" + list.size() + "] меньше [" + number + "]").isTrue();
        LOG.info("Иконок видов спорта [" + list.size() + "] > [" + number + "]");
    }

    /**
     * Проверка что при развёрнутом левом меню Лайв отображаются строгое число постоянных элементов
     * поле ввода 'Турнир или команда'
     * иконка 'Группировка по регионам'
     * иконка 'С видео'
     * Проверят что нужная игра будет выделена в левом меню. Проверет что фильтр "с видео" в нужном состоянии
     * @param listItems - список постоянных элементов
     */
    @ActionTitle("проверяет, что при развёрнутом левом меню есть элементы с")
    public void checksThatWhenLeftMenuIsExpandedThereAreItemsWith (DataTable listItems){
        WebDriver driver = PageFactory.getWebDriver();
        WebElement menuToggler = driver.findElement(By.id("menu-toggler"));
        if(menuToggler.getAttribute("title").contains("Показать всё")){
            LOG.info("Левое меню оказалось свёрнутым. Разворачиваем.");
            menuToggler.click();
        }
        List<String> list = listItems.asList(String.class);
        for (String aList : list) {
            try {
                driver.findElement(By.xpath("//*[contains(@title,'" + aList + "')]"));
                LOG.info("Найден обязательный элемент [" + aList + "]");
            } catch (Exception e) {
                throw new AutotestError("Ошибка! Обязательный элемент [" + aList + "] не найден.");
            }
        }
    }

    @ActionTitle("проверяет, что при активном фильтре 'Группировка по регионам' появлятся родителький класс региона внутри которого есть игры")
    public void checkGroupingFilterByRegion(){
//        String xpathMainCategoriesOfEvents = "//li[contains (@id,'sport')]";
//        String xpathFlagsWithoutFilter = ".//h4[contains(@class,'left-menu')]//span[contains(@class, 'flag')]";
//        String xpathRegionFilter = "//div[contains(@class,'left-menu-filters__item_regions')]";
//        String xpathRegions = "//a[contains(@class,'left-menu__list-item-region-link')]";
//        String xpathRegionInnerCompitition = ".//div[contains(@class,'compitition')]";
//
//        LOG.info("Ищем категории вида спорта.");
//        List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath(xpathMainCategoriesOfEvents));
//        if(list.size() > 0){
//            LOG.info("Найдено видов спорта::" + list.size());
//            for(int i = 0; i < (list.size() < 3 ? list.size() : 3); i++){
//                if(!list.get(i).getAttribute("class").contains("active")){
//                    LOG.info("Меню спорта было свёрнуто. Раскрываем.");
//                    list.get(i).click();
//                }
//                List<WebElement> listFlags = list.get(i).findElements(By.xpath(xpathFlagsWithoutFilter)).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
//                LOG.info("Найдено флагов без фильтра 'Группировка регионов'::" + listFlags.size() );
//
//                WebElement regionFilter = PageFactory.getWebDriver().findElement(By.xpath(xpathRegionFilter));
//                if(!regionFilter.getAttribute("class").contains("active")){
//                    LOG.info("Включаем фильтр");
//                    regionFilter.click();
//                }
//
//                LOG.info("Ищем появились ли регионы");
//                List<WebElement> listRegions = PageFactory.getWebDriver().findElements(By.xpath(xpathRegions))
//                        .stream().filter(e -> (e.isDisplayed() && !e.getText().isEmpty())).collect(Collectors.toList());
//                LOG.info("Найдено регионов после включения фильтра 'Группировка регионов'::" + list.size() );
//                if(listRegions.size()>0){
//                    for (WebElement el: listRegions) {
//                        LOG.info("Раскрываем регион::" + el.getText());
//                        el.click();
//                        List<WebElement> innerGames = el.findElements(By.xpath(xpathRegionInnerCompitition))
//                                .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
//                        if(innerGames.size() > 0){
//                            LOG.info("Нашли игр внутри региона::" + innerGames.size());
//                            for (WebElement game:innerGames) {
//                                LOG.info(game.getText().replaceAll("\n","]-["));
//                            }
//                        }else {
//                            throw new AutotestError("Ошибка! Фильтр не сработал");
//                        }
//                        LOG.info("Закрываем регион::" + el.getText());
//                        el.click();
//                    }
//                }
//                LOG.info("Выключаем фильтр");
//                regionFilter.click();
//            }
//        }else {
//            LOG.info("Нет ни одной строки с видом спорта!");
//        }
        WebDriver driver =  PageFactory.getWebDriver();
        LOG.info("Сначала убедимся что в Лайве вообще есть игры");
        List<WebElement> games = driver.findElements(By.xpath("//div[@class='left-menu__list-item-games-row']"));
        Assert.assertFalse("Нет игр в ЛАЙВЕ", games.isEmpty());
        LOG.info("Включаем группировку по регионам(если не было вкючено)");
        setExpandCollapseMenusButton(true);
        WebElement regionFilter =driver.findElement(By.xpath("//div[contains(@class,'left-menu-filters__item_regions')]"));
        if (!regionFilter.getAttribute("class").contains("active")){
            regionFilter.click();
            new WebDriverWait(driver,10)
                    .withMessage("Триггер 'Группировка по регионам' не включился в Левом меню")
                    .until(ExpectedConditions.attributeContains(regionFilter,"class","active"));
        }

        LOG.info("Смотрим появились ли регионы в ЛМ");
        List<WebElement> regions =
                driver.findElements(By.xpath("//a[contains(@class,'left-menu__list-item-region-link') and not(contains(@class,'hide'))]"));
        Assert.assertFalse(
                "Нет регионов в ЛМ, значит группировка не сработала",
                regions.isEmpty()
        );

        LOG.info("Выключаем группировку по регионам и смотрим что теперь их(регионов) нет в ЛМ");
        regionFilter.click();

        new WebDriverWait(driver,10)
                .withMessage("Триггер 'Группировка по регионам' не включился в Левом меню")
                .until(ExpectedConditions.not(ExpectedConditions.attributeContains(regionFilter,"class","active")));


        regions.clear();
        regions =
                driver.findElements(By.xpath("//a[contains(@class,'left-menu__list-item-region-link') and not(contains(@class,'hide'))]"));
        Assert.assertTrue(
                "Есть регионов в ЛМ, значит группировка не отменилась",
                regions.isEmpty()
        );

    }

    @ActionTitle("проверяет что при активном фильтре 'С видео' у игр есть иконка в виде монитора со треугольником внутри")
    public void checkWorkFilterWithVideo(){
        WebDriver driver = PageFactory.getWebDriver();
        String xpathFilter = "//div[contains(@class,'left-menu-filters__item_video')]";
        String xpathMainCategoriesOfEvents = "//li[contains (@id,'sport')]";
        String xpathGamesWithVideo = "//li[contains(@class,'left-menu__list-item-games')]";
        WebElement filterGameWithVideo = driver.findElement(By.xpath(xpathFilter));
        LOG.info("Включаем фильтр");
        if(!filterGameWithVideo.getAttribute("class").contains("active")) {
            filterGameWithVideo.click();
        }

        LOG.info("Ищем категории вида спорта.");
        List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath(xpathMainCategoriesOfEvents));
        if(list.size() > 0) {
            LOG.info("Найдено видов спорта::" + list.size());
            for (int i = 0; i < (list.size() < 3 ? list.size() : 3); i++) {
                if (!list.get(i).getAttribute("class").contains("active")) {
                    LOG.info("Меню спорта было свёрнуто. Раскрываем.");
                    list.get(i).click();
                }

                List<WebElement> gameList = driver.findElements(By.xpath(xpathGamesWithVideo)).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
                LOG.info("Надено игр::" + gameList.size());
                if (gameList.size() > 0) {
                    for (WebElement game : gameList) {
                        game.findElement(By.xpath("//div[contains(@class, 'icon icon-video-tv')]"));
                        LOG.info(game.getAttribute("innerText").replaceAll("\n","]-["));
                    }
                }


            }
        }
    }

}
