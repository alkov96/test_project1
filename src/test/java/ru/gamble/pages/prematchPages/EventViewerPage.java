package ru.gamble.pages.prematchPages;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.attributeContains;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.PERIOD;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;

@PageEntry(title = "Просмотр событий")
public class EventViewerPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(EventViewerPage.class);

    @FindBy(xpath = "//div[contains(@class,'menu-toggler')]")
    private WebElement expandCollapseMenusButton;

    @ElementTitle("Период времени")
    @FindBy(xpath = "//div[contains(@class,'periods__input')]")
    private WebElement selectPeriod;


    public EventViewerPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(expandCollapseMenusButton));
        checkMenuIsOpen();
    }

    private void checkMenuIsOpen(){
        if(expandCollapseMenusButton.getAttribute("title").contains("Показать всё")){
            expandCollapseMenusButton.click();
            workWithPreloader();
        }
    }

    @ActionTitle("выбирает время")
    public void chooseTime(String key){
        String value;
        if(key.equals(PERIOD)){
            value = Stash.getValue(key);
        }else {value = key;}

        selectPeriod.click();
        selectPeriod.findElement(By.xpath("//*[contains(text(),'" + value + "')]")).click();
        workWithPreloader();

    }

    @ActionTitle("проверяет время игр")
    public void checkGamesWithPeriod(String period, String limit){
        String valuePeriod;
        if(period.equals(PERIOD)){
            valuePeriod = Stash.getValue(period);
        }else{
            valuePeriod = period;
        }
        int valueLimit = Integer.parseInt(Stash.getValue(limit));

        String xpathMainCategoriesOfEvents ="//li[not(contains(@id,'sport--'))]/a[@class='left-menu__list-item-sport-link ng-binding']";
        //"//a[@class='left-menu__list-item-sport-link ng-binding']";

        String xpathCountries;
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
                        .stream().filter(WebElement::isDisplayed).limit(valueLimit).collect(Collectors.toList());
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
                                .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
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
                            .stream().filter(WebElement::isDisplayed).limit(valueLimit).collect(Collectors.toList());

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
                    .stream().filter(WebElement::isDisplayed).limit(valueLimit).collect(Collectors.toList());
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
                .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
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

    /**
     * Метод проверяет входит ли время события в необходимый диапазон
     * @param diapason - диапазон, метка указывающая кол-во часов от текущей даты-веремени
     * @param currentGameDateTime - время события
     */
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
            assert gameDateTime != null;
//            assertThat(gameDateTime.after(currentDateTime))
//                    .as("Ошибка!!! Дата-время [" + gameDateTime.toString() + "] < [" + currentDateTime.toString() + "]");
            Assert.assertTrue(
                    "Ошибка!!! Дата-время [" + gameDateTime.toString() + "] < [" + currentDateTime.toString() + "]",
                    gameDateTime.after(currentDateTime));
            LOG.info("Дата-время [" + gameDateTime.toString() + "] > [" + currentDateTime.toString() + "]");

        }else {
            Date dateTimePlusPeriod = new Date(System.currentTimeMillis() + diapason * 3600 * 1000);

            assert gameDateTime != null;
            Assert.assertTrue(
                    "Ошибка!!! Дата-время [" + gameDateTime.toString() + "] вне диапазона [" + currentDateTime.toString() + " - " + dateTimePlusPeriod.toString() + "]",
                    gameDateTime.after(currentDateTime) && gameDateTime.before(dateTimePlusPeriod));
//            assertThat(gameDateTime.after(currentDateTime) && gameDateTime.before(dateTimePlusPeriod))
//                    .as("Ошибка!!! Дата-время [" + gameDateTime.toString() + "] вне диапазона [" + currentDateTime.toString() + " - " + dateTimePlusPeriod.toString() + "]");
            LOG.info("Дата-время [" + gameDateTime.toString() + "] соответствует [" + currentDateTime.toString() + " - " + dateTimePlusPeriod.toString() + "]");
        }
    }


    /**
     * Поиск игры в прематче, подходщей по фильтру по времени. И добавление найденно игры в избранное если есть такой параметр
     *
     * @param period   - в течении какого периода должна начаться игра
     * @param inPeriod - добавляем ли мы игру время начала которой входит в этот временной интервал, или не входит. если этот параметр = "включен" - то ищем игру внутри фильтра, в остальных случаях - вне фильтра
     * @param adding   - добавлять ил игру в Избранное. если этот параметр = "и добавляет в избранное" - то добавляем в Избранное, в остальных случаях - нет
     */
    @ActionTitle("находит игру по фильтру")
    public void searchGamePrematchAtPeriod(String period, String inPeriod, String adding) {
        boolean add = adding.equals("и добавляет в избранное");
        try {
            gamePrematchAtPeriod(period, inPeriod.equals("раньше"), adding.equals("и добавляет в избранное"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Stash.getValue("nameGameKey")==null){
            Assertions.fail("Игры не найдена");
        }
        LOG.info("Игра по фильтру времени " + period + " (" + inPeriod + ") найдена: ");
        LOG.info(Stash.getValue("nameGameKey") + " время начала " + Stash.getValue("timeGameKey"));
    }

    public void gamePrematchAtPeriod(String period, boolean inPeriod, boolean adding) throws Exception {
        WebDriver driver = PageFactory.getDriver();
        String nameGamefull;
        boolean gameIsAdding = false;
        int count;
        String typeGame;
        LOG.info("смотрим какие сейчас дата и время и прибавлем к этому времени период(потом будет учавствовать в Фильтре");
        Date dateGame;
        int hour = Integer.valueOf(period.substring(0, period.indexOf("час") - 1));
        Date Period = new Date(System.currentTimeMillis() + hour * 3600 * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - dd MMM yyyy", new Locale("ru"));


        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (!menu.getAttribute("class").contains("collapsed")) menu.click();
        if (inPeriod) {
            typeGame = "PrematchInPeriod";
            driver.findElement(By.xpath("//div[@class='periods']/div")).click();//включим фильтр по временеи чтоб не мучиться с поисками игры
            driver.findElement(By.xpath("//div[@class='periods']//li[contains(normalize-space(text()),'" + hour + "')]")).click();
            CommonStepDefs.workWithPreloader();
        } else {
            typeGame = "PrematchVnePeriod";
            driver.findElement(By.xpath("//div[@class='periods']/div")).click();// если ищем игру вне периода то убедимся что фильтр выключен
            driver.findElement(By.xpath("//div[@class='periods']/ul/li[1]")).click();
            CommonStepDefs.workWithPreloader();
        }

        LOG.info("Сворачиваем все виды спорта");
        closeSports();
        int sportCount = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li")).size();
        for (int sportN = 4; sportN <= sportCount; sportN++) {
            //разворачиваем спорт(начнем с тенниса просто потому что не хочу футбол)
            LOG.info("Разворачиваем один спорт");
            driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + sportN + "]")).click();
            LOG.info(driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + sportN + "]/a")).getAttribute("title"));
            CommonStepDefs.workWithPreloader();
            //количество регионов в указанном спорте Например Мир,Европа и т.д.
            int regionsInSport = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + sportN + "]/ul[1]/li")).size();
            count = 15;
            while (regionsInSport <= 1 && count > 0) {
                Thread.sleep(1000);
                regionsInSport = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + sportN + "]/ul[1]/li")).size();
                count--;
                if (count == 0) {
                    LOG.info("Очень долго раскрывается спорт: за 15 секунд так и не развернулся");
                }
            }
            for (int region = 1; region < regionsInSport; region++) {
                menu = driver.findElement(By.id("menu-toggler"));
                if (!menu.getAttribute("class").contains("collapsed")) {
                    LOG.info("Развернули ЛМ");
                    menu.click();
                }
                LOG.info("Разворачиваем регион");
                driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + sportN + "]/ul[1]/li[" + region + "]")).click();
                CommonStepDefs.workWithPreloader();
                //В каждом регионе может быть несколько соревнований.
                int toursCount = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + sportN + "]/ul[1]/li[1]/div")).size();

                for (int tour = 1; tour <= toursCount; tour++) {
                    LOG.info("Выбираем соревнование");
                    menu = driver.findElement(By.id("menu-toggler"));
                    if (!menu.getAttribute("class").contains("collapsed")) menu.click();
                    driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + sportN + "]/ul[1]/li[" + region + "]/div[1]/div[" + tour + "]/h4[1]")).click();
                    CommonStepDefs.workWithPreloader();
                    //все игры в этом соревновании
                    List<WebElement> allGames = driver.findElements(By.xpath("//div[@class = 'prematch-competitions scroll-contain ng-scope']/div[1]/div[1]/div/div[1]/div[1]/div[1]/div[1]/div[1]"));
                    LOG.info("Смотрим все игры в спорте, в соответствующем соревновании. Ищем игру подходящую по фильтру времени " + hour + inPeriod);
                    for (int GameInTour = 0; GameInTour < allGames.size(); GameInTour++) {
                        dateGame = formatter.parse(allGames.get(GameInTour).getText());

                        if (!gameIsAdding && (dateGame.getTime() <= Period.getTime()) == inPeriod) {

                            nameGamefull = driver.findElement(By.xpath("//div[@class = 'prematch-competitions scroll-contain ng-scope']/div[1]/div[1]/div[" + (GameInTour + 1) + "]/div[1]/div[1]/div[1]/div[2]")).getAttribute("title");
                            CommonStepDefs.addStash("nameGameKey",nameGamefull);
                            CommonStepDefs.addStash("timeGameKey",formatter.format(dateGame));
                            CommonStepDefs.addStash("typeGameKey",typeGame);
                            if (adding) {
                                LOG.info("Нужную игру нашли. Добавляем ее в Избранное");
                                driver.findElement(By.xpath("//div[@class = 'prematch-competitions scroll-contain ng-scope']/div[1]/div[1]/div[" + (GameInTour + 1) + "]/div[1]/div[1]/div[1]/div[1]/i[1]")).click();
                            }
                            gameIsAdding = true;
                        }

                        if (gameIsAdding) return;
                    }
                }
                if (!menu.getAttribute("class").contains("collapsed")) menu.click();
                driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + sportN + "]/ul[1]/li[" + region + "]/a[1]")).click();//свернули регион
                LOG.info("Нужной игры в этом регионе не было. Ищем в следующем");
                CommonStepDefs.workWithPreloader();
            }
            if (!menu.getAttribute("class").contains("collapsed")) menu.click();
            driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + sportN + "]/a[1]")).click();//свернули спорт
            LOG.info("Нужной игры в этом спорте не было. Ищем в следующем");
            CommonStepDefs.workWithPreloader();
        }
        //перед выходом из это функции выключим ильтр по времени
        if (!menu.getAttribute("class").contains("collapsed")) menu.click();
        driver.findElement(By.xpath("//div[@class='periods']/div")).click();// если ищем игру вне периода то убедимся что фильтр выключен
        driver.findElement(By.xpath("//div[@class='periods']/ul/li[1]")).click();
        CommonStepDefs.workWithPreloader();

    }

    @ActionTitle("включает фильтр по времени")
    public void onTriggerPeriod(String period){
        WebDriver driver = PageFactory.getDriver();
        driver.findElement(By.xpath("//div[@class='periods']//div[contains(@class,'periods__input')]")).click();
        driver.findElement(By.xpath("//div[@class='periods']//ul[@class='periods__list']/li[contains(text(),'"+period+"')]")).click();
        CommonStepDefs.workWithPreloader();
    }

    /**
     * проверка что страница Прметач соответсвует ожиданиям (открыта нужная игра и триггер по времени в правильном состоянии
     */
    public static boolean pagePrematch(String team1, String expectedPeriod, boolean isFavorit) {
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
        //если меню свернуто - разворачиваем
        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (!menu.getAttribute("class").contains("collapsed")) {
            menu.click();
            CommonStepDefs.workWithPreloader();
        }

        LOG.info("Проверка страницы прематч при включенном фильтре = " + expectedPeriod);

        String selectPeriod = driver.findElement(By.xpath("//div[@class = 'left-menu__search left-menu-filters']/ng-include[1]/div[1]/div[2]/div[1]/div[1]")).getText();
        LOG.info("Теперь проверим что фильтр в нужном состоянии "+expectedPeriod);
        if (!selectPeriod.equals(expectedPeriod)) {
            flag=false;
            LOG.error("Фильтр по периоду не соответсвует ожидаемому. " + selectPeriod + " вместо " + expectedPeriod);
        }

        LOG.info("Проверим что нужная игра открыта по центарльной части страницы");
        String nameOnPage = driver.findElement(By.xpath("//div[@ng-switch='openGame.sport.id']/div[1]/div[contains(@class,'teams-names')]/span[1]")).getAttribute("title");
        nameOnPage = CommonStepDefs.stringParse(nameOnPage);
        Assert.assertTrue(
                "В прематче открылась неправильная игра. Открылась игра: "+ nameOnPage + ", а ожидалось " + team1,
                CommonStepDefs.stringParse(team1).equals(nameOnPage));

        if (isFavorit){
            flag &= inLeftMenuGameYellow(team1);
        }
        return flag;
    }

    /**
     * Проверка что нужная игра есть в Избранном в левом меню и выделена там желтым
     */
    public static boolean inLeftMenuGameYellow(String team1){
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
        LOG.info("Проверяем что нужная игра активна и выделена желтым в левом меню в Моих Пари");
        List<WebElement> leftSidePage = driver.findElements(By.xpath("//div[@class='prematch-competition ng-scope']/div/div[1]/div[1]"));
        for (WebElement aLeftSidePage : leftSidePage) {
            String nameGameOnPage = aLeftSidePage.findElement(By.xpath("div[1]/div[2]")).getAttribute("title");
            nameGameOnPage = CommonStepDefs.stringParse(nameGameOnPage);
            if (nameGameOnPage.contains(CommonStepDefs.stringParse(team1))) {
                if (!aLeftSidePage.findElement(By.xpath("../div[1]")).getAttribute("class").contains("active")) {
                    flag = false;
                    LOG.error("В прематче открытая игра из Избранного не выделена в левой части меню");
                }
                break;
            }
        }
        return  flag;
    }


    @ActionTitle("очищает избранное через левое меню")
    public void clearFavouritePrematch(){
        WebDriver driver = PageFactory.getDriver();

//если меню свернуто - разворачиваем
        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (!menu.getAttribute("class").contains("collapsed"))
        {
            menu.click();
            CommonStepDefs.workWithPreloader();
        }

//сворачиваем все виды спорта, а затем разворачиваем 'мои игры'
        driver.findElement(By.id("sports-toggler")).click();
        driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[1]/ng-include[1]/li[1]")).click();
        CommonStepDefs.workWithPreloader();
        int myGamesCount = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[1]/ng-include[1]/li[1]/ul[1]/li")).size();

        int sizeFavouriteUpdate;

        for (int count = myGamesCount; count >0; count--) {
            driver.findElement(By.xpath("//*[@id='sports-list-container']/ul[1]/ng-include[1]/li[1]/ul[1]/li[1]/div[1]/div[1]/div[2]")).click();//убираем игру из избранного в премтче

            int countSec=0;
            while (countSec<5)
            {
                sizeFavouriteUpdate = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[1]/ng-include[1]/li[1]/ul[1]/li")).size();
                if (sizeFavouriteUpdate < myGamesCount) {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countSec++;
                if (countSec==5){
                    Assertions.fail("Не удалилась игра из избранного через левое меню. Было игр " + myGamesCount + ",осталось " + sizeFavouriteUpdate);
                }
            }

            myGamesCount = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[1]/ng-include[1]/li[1]/ul[1]/li")).size();
        }
    }

    @ActionTitle("многовыборный режим")
    public void multiGamesOnOff(String onOrOff){
        WebDriver driver = PageFactory.getDriver();
        WebDriverWait wait =  new WebDriverWait(driver,10);
        boolean turnOn = onOrOff.equalsIgnoreCase("включает")?true:false;

        WebElement multiviewButton = driver.findElement(By.xpath("//div[contains(@class,'left-menu-filters__item_multiview')]/i"));

        if (!driver.findElements(preloaderOnPage).isEmpty()){
            driver.navigate().refresh();
            CommonStepDefs.workWithPreloader();
        }

        setExpandCollapseMenusButton(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wait.withMessage("Нет кнопки многовыборного режима");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[contains(@class,'left-menu-filters__item_multiview')]"),0));

        LOG.info("Проверяем активность кнопки многовыборного режима");
        boolean isOn = multiviewButton.findElement(By.xpath("..")).getAttribute("class").contains("active");
        if (isOn!=turnOn){
            multiviewButton.click();
            isOn = multiviewButton.findElement(By.xpath("..")).getAttribute("class").contains("active");
        }
        Assert.assertTrue(
                "Не изменилось состояние многовыборного режима. Ожидалось " + turnOn + ", а на самом деле " + isOn,
                isOn==turnOn);

        //сворачиваем ЛМ чтобы было видно контейнер многовыборного режима
        setExpandCollapseMenusButton(false);

        LOG.info("Проверям включен ли многовыборный режим. Для этого смотрим заголовок в центральной части страницы");
        List<WebElement> innerHeader = driver.findElements(By.xpath("//div[contains(@class,'game-center-container__inner-header')]/div[contains(@class,'title-box')]/span[not(contains(@class,'js-hide'))]"));
        boolean multiveiwOn = !innerHeader.isEmpty();
        Assert.assertTrue(
                "Многововыборный режим не в том состоянии что ожидалось. Ожидалось " + turnOn + ", а на самом деле " + multiveiwOn,
                multiveiwOn==turnOn);
    }

    @ActionTitle("очищает список многовыборного режима")
    public void clearMultiviewContainer(){
        WebDriver driver = PageFactory.getDriver();

        setExpandCollapseMenusButton(false);
        List<WebElement> innerHeader = driver.findElements(By.xpath("//div[contains(@class,'game-center-container__inner-header')]/div[contains(@class,'title-box')]"));
        LOG.info("Очищаем список в многовыборном режиме через кнопку #Очистить все#");
        innerHeader.get(0).findElement(By.xpath("div[contains(@class,'clear-all btn')]")).click();
        if (!driver.findElements(preloaderOnPage).isEmpty()){
            driver.navigate().refresh();
            CommonStepDefs.workWithPreloader();
        }
        LOG.info("Смотрим не осталось ли игр в контейнере");
        List<WebElement> MultiViewContainer = driver.findElements(By.xpath("//div[contains(@class,'prematch-competitions scroll-contain')]//div[contains(@class,'prematch-competition-games')]"));
        Assert.assertTrue(
                "Не очистился контейнер в многовыборном режиме",
                MultiViewContainer.isEmpty());
    }

    @ActionTitle("добавляет в многовыборный режим целое соревнование из вида спорта номер")
    public void addCompetitionInMultiviewList(String numberSportOnLM, String keyNameTour){
        WebDriver driver = PageFactory.getDriver();
        WebDriverWait wait =  new WebDriverWait(driver,10);

        int index = Integer.valueOf(numberSportOnLM);

        setExpandCollapseMenusButton(true);
        LOG.info("Сворачиваем все виды спорта");
        closeSports();//сворачиваем все виды спорта

        List<WebElement> allSports = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li"));

        LOG.info("Разворачиваем первый спорт");
        allSports.get(index).click();//развернули спорт
        wait.withMessage("Спорт номер " + index + " не развернулся за 10 секунд");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + (index+1) + "]/ul[1]/li"),0));//проверяем что спорт развернулся

        if (!allSports.get(index).findElement(By.xpath("ul[1]/li[1]")).getAttribute("class").contains("active"))//если первый регион спорта не выбран то откроем его
        {
            LOG.info("Регион в выбранном спорте свернут - разворачиваем его");
            allSports.get(index).findElement(By.xpath("ul[1]/li[1]//div[contains(@class,'left-menu__list-item-arrow_region')]")).click();//развернули регион
        }
        LOG.info("Добавляем первый турнир в регионе в контейнер многовыборного режима");
        allSports.get(index).findElement(By.xpath("ul[1]/li[1]//label[contains(@for,'checkbox-competition')]")).click();//выбрали турнир
        LOG.info("Запоминаем название соревнования в ЛМ");
        String nameTour = allSports.get(index).findElement(By.xpath("ul[1]/li[1]//label[contains(@for,'checkbox-competition')]/i")).getText();//название турнира в ЛМ
        setExpandCollapseMenusButton(false);
        Stash.put(keyNameTour,nameTour);
    }


    @ActionTitle("добавляет в многовыборный режим одну игру из спорта номер")
    public void addOneGameToMultiview(String numberSport, String keyNameGame){
        WebDriver driver = PageFactory.getDriver();
        WebDriverWait wait =  new WebDriverWait(driver,10);
        List<WebElement> allSports = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li"));
        int index = Integer.valueOf(numberSport);

        LOG.info("Добавляем одну игру из соревновани в контейнер");
        setExpandCollapseMenusButton(true);
        LOG.info("Сворачиваем все виды спорта");
        closeSports();//свернули все виды спорта
        LOG.info("Разворачиваем следующий спорт");
        allSports.get(index).click();//развернули спорт
        wait.withMessage("Спорт номер " + index + " не развернулся за 10 секунд");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + (index+1) + "]/ul[1]/li"),0));//проверяем что спорт развернулся

        if (!allSports.get(index).findElement(By.xpath("ul[1]/li[1]")).getAttribute("class").contains("active"))
        {
            LOG.info("регион в спорте свернут - разворачиваем его");
            allSports.get(index).findElement(By.xpath("ul[1]/li[1]//div[contains(@class,'left-menu__list-item-arrow_region')]")).click();//развернули регион
            wait.withMessage("Регион не развернулся за 10 секунд");
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                    By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + (index+1) + "]/ul[1]/li[1]//label[contains(@for,'checkbox-competition')]"),
                    0));//проверяем что спорт развернулся
        }
        WebElement tour = allSports.get(index).findElement(By.xpath("ul[1]/li[1]//label[contains(@for,'checkbox-competition')]"));
        Actions actions = new Actions(driver);
        LOG.info("Наводим мышку на название соревнования, чтобы открлось всплывающее меню с переенм игр");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        actions.moveToElement(tour).build().perform();//наводим мышку на турнир
        String nameGame = tour.findElements(By.xpath("../..//div[contains(@class,'poup-sports_prematch scroll-contain')]//label[contains(@for,'checkbox-game')]")).get(0).getText();
        LOG.info("Выбираем игру из этого списка "+nameGame);
        WebElement game = tour.findElements(By.xpath("../..//div[contains(@class,'poup-sports_prematch scroll-contain')]//input[contains(@id,'checkbox-game')]")).get(0);
        LOG.info("Добавляем игру в контейнер многовыборного режима");
        game.findElement(By.xpath("../label")).click();
        Stash.put(keyNameGame,nameGame);
    }


    @ActionTitle("првоеряет наличие игр в контейнере многовыборного режима")
    public void checkListMultiview(String counterComp, String keyNameTour){
        checkListMultiview(counterComp,keyNameTour,"");
    }

    public void checkListMultiview(String counterComp, String keyNameTour, String keyNameGame){
        //т.к. добавляем из разных спортов, то точно долждно быть 2 разных турнира в контейнере. поэтому можно смотрить количетсво prematch-competition-nme
        WebDriver driver = PageFactory.getDriver();
        String nameGame = Stash.getValue(keyNameGame);
        String nameTour = Stash.getValue(keyNameTour);
        int countCompetition = Integer.valueOf(counterComp);

        setExpandCollapseMenusButton(false);
        LOG.info("Т.к. доабавллись игры из разн ивдов спорта, то они точно из разных соревнований. проверим содержитс ли в контейнере 2 разных соревновани");
        Assert.assertFalse(
                "Игра не добавилась в контейнер",
                driver.findElements(By.xpath("//div[contains(@class,'prematch-competitions scroll-contain')]//div[contains(@class,'prematch-competition-name')]")).size()<countCompetition
        );
        boolean gameAdd=false;

        //название турнира в контейнере
        LOG.info("Запоминаем название соревнования, которое в контейнере многовыборного режима");
        String nameTourInMulti = driver.findElements(By.xpath("//div[contains(@class,'prematch-competitions scroll-contain')]//div[contains(@class,'prematch-competition-name')]//div[contains(@class,'prematch-competition-name__inner-competition ellipsis-text')]")).get(0).getText();

        LOG.info("Сравниваем название соревновани в ЛМ и в контейнере");
        Assert.assertTrue(
                "В контейнере многовыбрного режима ожидались игры из соревнования " + nameTour + ", а вместо него " + nameTourInMulti,
                nameTourInMulti.trim().equals(nameTour.trim()));

        if (keyNameGame.isEmpty()) return;

        LOG.info("Теперь смотрим какие игры(а не соревнования) в контейнере");
        List<WebElement> MultiViewContainer = driver.findElements(By.xpath("//div[contains(@class,'prematch-competitions scroll-contain')]//div[contains(@class,'prematch-competition-games ')]//div[contains(@class,'bets-block__header-teams')]"));
        for (WebElement element:MultiViewContainer){
            if (CommonStepDefs.stringParse(element.getAttribute("title")).equals(CommonStepDefs.stringParse(nameGame.split("\n")[0]))) {
                LOG.info("Игра добавилась правильная");
                gameAdd=true;
                break;
            }
        }
        Assert.assertTrue(
                "Игра " + nameGame + " не добавилась в контейнер",
                gameAdd
        );
    }

    @ActionTitle("убирает одну игру из многовыборного режиме через Левое Меню")
    public void deleteOneGameMultiview(String numberSport,String keyNameDelettingGame){
        WebDriver driver = PageFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver,10);
        LOG.info("Убираем одну игру из контейнера");
        setExpandCollapseMenusButton(true);

        int index = Integer.valueOf(numberSport);
        List<WebElement> allSports = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li"));
        if (!allSports.get(index).findElement(By.xpath("ul[1]/li[1]")).getAttribute("class").contains("active"))
        {
            LOG.info("регион в спорте свернут - разворачиваем его");
            allSports.get(index).findElement(By.xpath("ul[1]/li[1]//div[contains(@class,'left-menu__list-item-arrow_region')]")).click();//развернули регион
            wait.withMessage("Регион не развернулся за 10 секунд");
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                    By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + (index+1) + "]/ul[1]/li[1]//label[contains(@for,'checkbox-competition')]"),
                    0));//проверяем что спорт развернулся
        }
        WebElement tour = allSports.get(index).findElement(By.xpath("ul[1]/li[1]//label[contains(@for,'checkbox-competition')]"));
        Actions actions = new Actions(driver);
        LOG.info("Наводим мышку на название соревноания в ЛМ, чтобы появился всплывающий список игр");
        actions.moveToElement(tour).build().perform();//наводим мышку на турнир
        LOG.info("Запоминаем название игры, которая в контейнере");
        String nameGame = //tour.findElement(By.xpath("../div[1]/ul[1]/li[1]/div[1]/label")).getText();ng-valid ng-not-empty ng-dirty ng-valid-parse ng-touched
                //tour.findElements(By.xpath("../..//div[contains(@class,'poup-sports_prematch scroll-contain')]//label[contains(@for,'checkbox-game')]")).get(0).getText();
                tour.findElements(By.xpath("../..//div[contains(@class,'poup-sports_prematch scroll-contain')]//input[contains(@id,'checkbox-game') and contains(@class,'ng-not-empty')]/../label")).get(0).getText();
        LOG.info("Выбираем эту игру");
        WebElement game = //tour.findElement(By.xpath("../div[1]/ul[1]/li[1]/div[1]/input[contains(@id,'checkbox-game')]"));//игра из списка турнира.
                //  tour.findElements(By.xpath("../..//div[contains(@class,'poup-sports_prematch scroll-contain')]//input[contains(@id,'checkbox-game')]")).get(0);
                tour.findElements(By.xpath("../..//div[contains(@class,'poup-sports_prematch scroll-contain')]//input[contains(@id,'checkbox-game') and contains(@class,'ng-not-empty')]")).get(0);
        LOG.info("И убираем игру из контейнера");
        game.findElement(By.xpath("../label")).click();
        Stash.put(keyNameDelettingGame,nameGame);
    }

    @ActionTitle("проеряет что удаленная игра не осталась в списке многовыборного режима")
    public void checkMultiviewHaveNotTheGame(String keyNameDeletingGame){
        WebDriver driver = PageFactory.getDriver();
        String nameGame = Stash.getValue(keyNameDeletingGame);
        setExpandCollapseMenusButton(false);
        LOG.info("Проверяем не осталась ли удаленая игра в контейнере.");
        List<WebElement> MultiViewContainer = driver.findElements(By.xpath("//div[contains(@class,'prematch-competitions scroll-contain')]//div[contains(@class,'prematch-competition-games ')]//div[contains(@class,'bets-block__header-teams')]"));
        for (WebElement element : MultiViewContainer) {
            //   log.info("вот контейнер " + element.getAttribute("title"));
            if (CommonStepDefs.stringParse(element.getAttribute("title")).equals(CommonStepDefs.stringParse(nameGame.split("\n")[0]))) {
                Assert.fail("Не получилось убрать игру из контейнера через левое меню. " + nameGame);
            }
        }
    }

    @ActionTitle("очищает контейнер через кнопку 'очистить все'")
    public void cleanMultiview(){
        WebDriver driver = PageFactory.getDriver();
        LOG.info("Проверка очистки контейнера многовыборного режима");
        setExpandCollapseMenusButton(false);
        LOG.info("Нажимаем на кнопку #очистить все#");
        List<WebElement> innerHeader = driver.findElements(By.xpath("//div[contains(@class,'game-center-container__inner-header')]/div[contains(@class,'title-box')]"));
        innerHeader.get(0).findElement(By.xpath("div[contains(@class,'clear-all btn')]")).click();
        CommonStepDefs.workWithPreloader();
        LOG.info("Проверяем не осталось ли игр в контейнере");
        List<WebElement> MultiViewContainer = driver.findElements(By.xpath("//div[contains(@class,'prematch-competitions scroll-contain')]/div[1]/div/div"));
        new WebDriverWait(driver,10).
                withMessage("Контейнер многовыборного режима не очистился по кнопке 'очистить'").
                until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[contains(@class,'prematch-competitions scroll-contain')]/div[1]/div/div"),0));
    }
}

