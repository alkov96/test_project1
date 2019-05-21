package ru.gamble.pages.prematchPages;

import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument;
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
import ru.gamble.pages.CouponPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.openqa.selenium.By.xpath;
import static ru.gamble.stepdefs.CommonStepDefs.switchHandle;
import static ru.gamble.stepdefs.CommonStepDefs.waitOfPreloader;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.PERIOD;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;

@PageEntry(title = "Просмотр событий")
public class EventViewerPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(EventViewerPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @ElementTitle("Период времени")
    @FindBy(xpath = "//div[contains(@class,'periods__input')]")
    private WebElement selectPeriod;

    @ElementTitle("Кнопка МАКСБЕТ")
    @FindBy(xpath = "//label[contains(@class,'coupon-btn_max-bet')]/i[contains(@class,'icon-maxbet')]")
    private WebElement maxBet;

    private static By xpathForsportsPrematch = By.xpath("//*[@id='sports-list-container']//li[contains(@id,'sport') and not(contains(@id,'sport--'))]");
    private static By xpathSport = By.xpath("//li[contains(@class,'left-menu__list-item-sport') and not(contains(@id,'sport--')) and not(contains(@class,'favorite'))]");
    private static By xpathRegion = By.xpath(".//li[contains(@class,'left-menu__list-item-region')]");//путь, начиная от спорта. т.е. от li
    private static By xpathCompetition = By.xpath(".//div[contains(@class,'left-menu__list-item-region-compitition')]");//путь, начиная от спорта. т.е. от li


    public EventViewerPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//div[contains(@class,'menu-toggler')]"))));
        checkMenuIsOpen(true);
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

    /**
     * провекра что игры в первых 5 спортах удовлетворяют фильтру по времени
     * @param period
     */
    @ActionTitle("проверяет время игр")
    public void checkGamesWithPeriod(String period){
        String valuePeriod;
        WebElement sport;
        WebDriverWait wait = new WebDriverWait(driver,10);
        if(period.equals(PERIOD)){
            valuePeriod = Stash.getValue(period);
        }else{
            valuePeriod = period;
        }
        int countSports = driver.findElements(xpathSport).size(); //количество видов спорта
        countSports = Math.min(countSports,5);

        for (int i=0; i<countSports;i++){
            LOG.info("сворачиваем все виды спорта");
            closeSports();
            sport=driver.findElements(xpathSport).get(i);
            LOG.info("разворачиваем нужный спорт " + sport.getAttribute("innerText").split("\n")[0]);
            clickIfVisible(sport);
            wait
                .withMessage("Спорт " + sport.getAttribute("innerText").split("\n")[0] + " не раскрылся")
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(xpathRegion.toString().split(" ")[1].replace(".","")),0));
            checkRegionsInSport(i,valuePeriod);
        }
    }

    /**
     * проверка что все игры в первых 5 регионах спорта sport удовлетворяют периоду времени
     * @param sport
     * @param valuePeriod
     */
    private void checkRegionsInSport(int sport,String valuePeriod){
        WebDriverWait wait = new WebDriverWait(driver,10);
        int countRegions = driver.findElements(xpathSport).get(sport).findElements(xpathRegion).size(); //количество регионов
        countRegions = Math.min(countRegions,5);//будем смотреть толкьо первые 5 регионов
        WebElement region;
        for (int i=0;i<countRegions;i++){
            region = driver.findElements(xpathSport).get(sport).findElements(xpathRegion).get(i);
            if (!region.getAttribute("class").contains("active")) {
                LOG.info("разворачиваем регион " + region.getAttribute("innerText").split("\n")[0]);
                clickIfVisible(region);
                wait
                        .withMessage("Регион не раскрылся")
                        .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(xpathCompetition.toString().split(" ")[1].replace(".","")),0));
            }

            checkCompetitionsInRegion(sport,i,valuePeriod);
            checkMenuIsOpen(true);
            region = driver.findElements(xpathSport).get(sport).findElements(xpathRegion).get(i);
            if (region.getAttribute("class").contains("active")) {
                LOG.info("сворачиваем регион " + region.getAttribute("innerText").split("\n")[0]);
                clickIfVisible(region.findElement(By.xpath(".//div[contains(@class,'icon-arrow')]")));
            }
        }
    }

    /**
     * проверка что все игры в первых 5 соревнованиях выбранного региона удовлетворяют периоду времени
     * @param region
     * @param valuePeriod
     */
    private void checkCompetitionsInRegion(int sport, int region,String valuePeriod){
        WebElement competition;
        int countCompetit = driver.findElements(xpathSport).get(sport).findElements(xpathRegion).get(region).findElements(xpathCompetition).size(); //количество соревновани в выбранном регионе
        countCompetit = Math.min(countCompetit,5);
        for (int i=0;i<countCompetit;i++){
            checkMenuIsOpen(true);
            competition = driver.findElements(xpathSport).get(sport).findElements(xpathRegion).get(region).findElements(xpathCompetition).get(i);
            LOG.info("Выбираем сорвенование в этом регионе");
            clickIfVisible(competition);//кликнули на выбранное сорвенование в регионе. теперь в центральной части страниыв отображаются игры только этого соревнования
            setExpandCollapseMenusButton(false); //сворачиваем левое меню.чтобы ЦО была полностью видна
            checkGameOnCenter(valuePeriod);
        }
    }

    private void checkGameOnCenter(String valuePeriod){
        SimpleDateFormat formatterOnlyDate = new SimpleDateFormat("dd MMM yyyy", new Locale("ru"));
        Calendar today = Calendar.getInstance();
        String segodnya = formatterOnlyDate.format(today.getTime());
        today.add(Calendar.DAY_OF_YEAR,1);
        String zavtra = formatterOnlyDate.format(today.getTime());
        today.add(Calendar.DAY_OF_YEAR,1);
        String poslezavtra = formatterOnlyDate.format(today.getTime());
        By xpathToDate = By.xpath("../preceding-sibling::div[contains(@class,'prematch-competition__header')]/div[contains(@class,'prematch-competition__header-date')]");
        By xpathToTime = By.xpath(".//div[contains(@class,'bets-block__header-left-info')]");
//        LOG.info("Закроем ЛМ чтобы не мешалось");
//        checkMenuIsOpen(false);
        List <WebElement> games = driver.findElements(By.xpath("//div[@class='prematch-competition__games']/div[contains(@class,'bets-block_single-row')]"));
        LOG.info("В Центральной области страницы " + games.size() + " игр");
        List <String> dateGamesString = new ArrayList<>();
        List <String> nameGames = driver.findElements(By.xpath("//div[@class='bets-block__header-teams']")).stream().map(el->el.getAttribute("innerText")).collect(Collectors.toList());
        games.forEach(el->dateGamesString.add(
                el.findElement(xpathToDate).getAttribute("innerText")+
                " " + el.findElement(xpathToTime).getAttribute("innerText")));
        Calendar period = Calendar.getInstance();
        Calendar dateOneGame = Calendar.getInstance();
        int hoursPeriod = 0;
        if (!valuePeriod.equals("Выберите время")) {
            hoursPeriod = valuePeriod.contains("час") ? Integer.valueOf(valuePeriod.split(" ")[0]) : Integer.valueOf(valuePeriod.split(" ")[0]) * 24;
        }
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy k:mm");
        period.add(Calendar.HOUR,hoursPeriod);
        String dateGame=new String();
        for (int i=0;i<dateGamesString.size();i++){
            LOG.info("Проверяем игру " + nameGames.get(i));
            dateGame = dateGamesString.get(i);
            int ind = dateGame.indexOf(":");
            dateGame = dateGame.substring(0,ind-3);
            switch (dateGame){
                case "Сегодня":
                    dateGame=segodnya + " " + dateGamesString.get(i).substring(ind-2);
                    break;
                case "Завтра":
                    dateGame=zavtra + " " + dateGamesString.get(i).substring(ind-2);
                    break;
                case "Послезавтра":
                    dateGame=poslezavtra + " " + dateGamesString.get(i).substring(ind-2);
                    break;
                default:
                    dateGame = dateGamesString.get(i);
                    break;
            }
            try {
                dateOneGame.setTime(format.parse(dateGame));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Assert.assertTrue("Игра не соответсвтует выбранному периоду " + valuePeriod + ":: время игры " + dateGame,
                    dateOneGame.before(period));
            LOG.info("Игра соответсвует выбранному периоду");
            i++;
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
        List list = Stash.getValue("nameGameKey");
        LOG.info("Сейчас запомнено игр " + list);
        int nameInMemory = list==null?0:list.size();
        try {
            gamePrematchAtPeriod(period, inPeriod.equals("раньше"), adding.equals("и добавляет в избранное"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        list = Stash.getValue("nameGameKey");
        int nameInMemory2 = list==null?0:list.size();
        if (nameInMemory==nameInMemory2){
            Assertions.fail("Игра не найдена");
        }
        LOG.info("Игра по фильтру времени " + period + " (" + inPeriod + ") найдена: ");
        LOG.info(Stash.getValue("nameGameKey") + " время начала " + Stash.getValue("timeGameKey"));
    }

    public void gamePrematchAtPeriod(String period, boolean inPeriod, boolean adding) throws Exception {
        String nameGamefull;
        LOG.info("period: " + period + "\ninPeriod: " + inPeriod + "\nadding: " + adding);
        int sizeFavourite = driver.findElements(By.xpath("//ul[@class='left-menu__favorite-list']/li")).size();
        boolean gameIsAdding = false;
        int count;
        String typeGame;
        LOG.info("смотрим какие сейчас дата и время и прибавлем к этому времени период(потом будет учавствовать в Фильтре");
        Date dateGame;
        int hour = Integer.valueOf(period.substring(0, period.indexOf("час") - 1));
        Date periodDate = new Date(System.currentTimeMillis() + hour * 3600 * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("ru"));
        SimpleDateFormat formatterOnlyDate = new SimpleDateFormat("dd MMM yyyy", new Locale("ru"));
        Calendar today = Calendar.getInstance();
        String segodnya = formatterOnlyDate.format(today.getTime());
        today.add(Calendar.DAY_OF_YEAR,1);
        String zavtra = formatterOnlyDate.format(today.getTime());
        today.add(Calendar.DAY_OF_YEAR,1);
        String poslezavtra = formatterOnlyDate.format(today.getTime());

        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (!menu.getAttribute("class").contains("collapsed")) menu.click();
        if (inPeriod) {
            LOG.info("Включаем фильтр по времени на " + period);
            typeGame = "PrematchInPeriod";
            driver.findElement(By.xpath("//div[@class='periods']/div")).click();//включим фильтр по временеи чтоб не мучиться с поисками игры
            driver.findElement(By.xpath("//div[@class='periods']//li[contains(normalize-space(text()),'" + hour + "')]")).click();
            CommonStepDefs.workWithPreloader();
            if (!menu.getAttribute("class").contains("collapsed")) menu.click();
        } else {
            LOG.info("Выключаем фильтр по времени");
            typeGame = "PrematchVnePeriod";
            driver.findElement(By.xpath("//div[@class='periods']/div")).click();// если ищем игру вне периода то убедимся что фильтр выключен
            driver.findElement(By.xpath("//div[@class='periods']/ul/li[1]")).click();
            CommonStepDefs.workWithPreloader();
            if (!menu.getAttribute("class").contains("collapsed")) menu.click();
        }

        LOG.info("Сворачиваем все виды спорта");
        closeSports();
        WebDriverWait wait = new WebDriverWait(driver,10);
        By bypopular = By.xpath("//li[contains(@id,'sport--') and contains(@class,'active')]");
        int sportCount = driver.findElements(xpathForsportsPrematch).size();
        for (int sportN = 1; sportN < sportCount; sportN++) {
            LOG.info("Сначала свернем всякие допспорты(популярные соревнования, нулевая маржа)");
            if (!driver.findElements(bypopular).isEmpty()){
                wait.withMessage("вэйт1").until(ExpectedConditions.presenceOfElementLocated(bypopular));
                wait.withMessage("вэйт2").until(ExpectedConditions.visibilityOfElementLocated(bypopular));
                driver.findElements(bypopular).forEach(element -> element.findElement(By.xpath(".//*[contains(@class,'icon-arrow')]")).click());
            }
//популярные соревнования и нулевая маржа могут развернуться сами по себе, даже после того как нажали "свернуть все". поэтому нужные вид спорта уходят вниз за область экрана. потом нчего не рабоатет. чтобы этого избежать - нужно свернуть все эти доп.группы
            wait.withMessage("вэйт3").until(ExpectedConditions.numberOfElementsToBe(bypopular,0));
            //разворачиваем спорт(начнем с тенниса просто потому что не хочу футбол)
            LOG.info("Разворачиваем один спорт");
            driver.findElements(xpathForsportsPrematch).get(sportN).click();
            LOG.info(driver.findElements(xpathForsportsPrematch).get(sportN).findElement(By.xpath("./a")).getAttribute("title"));
            CommonStepDefs.workWithPreloader();
            //количество регионов в указанном спорте Например Мир,Европа и т.д.
            int regionsInSport = driver.findElements(xpathForsportsPrematch).get(sportN).findElements(By.xpath("./ul[1]/li")).size();
            count = 15;
            while (regionsInSport <= 1 && count > 0) {
                Thread.sleep(1000);
                regionsInSport = driver.findElements(xpathForsportsPrematch).get(sportN).findElements(By.xpath("./ul[1]/li")).size();
                count--;
                if (count == 0) {
                    LOG.info("Очень долго раскрывается спорт: за 15 секунд так и не развернулся");
                }
            }
            for (int region = 1; region <= regionsInSport; region++) {
                menu = driver.findElement(By.id("menu-toggler"));
                if (!menu.getAttribute("class").contains("collapsed")) {
                    LOG.info("Развернули ЛМ");
                    menu.click();
                }
                LOG.info("Разворачиваем регион");
                if (!driver.findElements(xpathForsportsPrematch).get(sportN).findElement(By.xpath("./ul[1]/li[" + region + "]")).getAttribute("class").contains("active")){
                    driver.findElements(xpathForsportsPrematch).get(sportN).findElement(By.xpath("./ul[1]/li[" + region + "]")).click();
                }
                CommonStepDefs.workWithPreloader();
                String pathToTours = "./ul[1]/li[" + region + "]//div[contains(@class,'left-menu__list-item-region-compitition')]";
                //В каждом регионе может быть несколько соревнований.
                int toursCount = driver.findElements(xpathForsportsPrematch).get(sportN).findElements(By.xpath(pathToTours)).size();

                for (int tour = 0; tour < toursCount; tour++) {
                    LOG.info("Выбираем соревнование");
                    menu = driver.findElement(By.id("menu-toggler"));
                    if (!menu.getAttribute("class").contains("collapsed")) menu.click();
                    driver.findElements(xpathForsportsPrematch).get(sportN).findElements(By.xpath(pathToTours)).get(tour).findElement(By.xpath("./h4[1]")).click();
                    CommonStepDefs.workWithPreloader();
                    //все даты в этом соревновании
                    List<WebElement> allDates = driver.findElements(By.xpath("//div[contains(@class,'prematch-competitions scroll-contain')]//div[contains(@class,'prematch-competition__header-date')]"));
                    LOG.info("Смотрим все игры в спорте, в соответствующем соревновании. Ищем игру подходящую по фильтру времени " + hour + inPeriod);
                    By byTime = By.xpath("./../following-sibling::div/div[contains(@class,'bets-block_single-row')]//div[contains(@class,'bets-block__header-left-info')]");
                    for (int index = 0; index < allDates.size(); index++) {
                        WebElement dateInTour = allDates.get(index);
                        String day = dateInTour.getAttribute("innerText");
                        switch (day){
                            case "Сегодня":
                                day=segodnya;
                                break;
                            case "Завтра":
                                day=zavtra;
                                break;
                            case "Послезавтра":
                                day=poslezavtra;
                                break;
                        }
                        for (WebElement game: allDates.get(index).findElements(byTime)){//для каждой даты несколько игр может быть на разное время. вот продемся по каждой из них
                            dateGame = formatter.parse(day + " " + game.getAttribute("innerText"));
                            if ((dateGame.getTime() <= periodDate.getTime()) == inPeriod) {//если игра подходит
                                nameGamefull = game.findElement(By.xpath("./following-sibling::div[contains(@class,'bets-block__header-teams')]")).getAttribute("innerText");
                                CommonStepDefs.addStash("nameGameKey",nameGamefull.replaceAll("\n"," "));
                                CommonStepDefs.addStash("timeGameKey",formatter.format(dateGame));
                                CommonStepDefs.addStash("typeGameKey",typeGame);
                                if (adding) {
                                    LOG.info("Нужную игру нашли. Добавляем ее в Избранное");
                                    game.findElement(By.xpath("./i")).click();//добавление в избранное
                                    new WebDriverWait(driver,10)
                                            .withMessage("Игра в избранное не добавилась!!")
                                            .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//ul[contains(@class,'left-menu__favorite-list')]/li"),sizeFavourite));
                                }
                                return;
                            }
                            //если игра не подошла идем дальше
                        }
                    }
                }
                if (!menu.getAttribute("class").contains("collapsed")) menu.click();
                driver.findElements(xpathForsportsPrematch).get(sportN).findElement(By.xpath("./ul[1]/li[" + region + "]/a[1]")).click();//свернули регион
                LOG.info("Нужной игры в этом регионе не было. Ищем в следующем");
                CommonStepDefs.workWithPreloader();
            }
            if (!menu.getAttribute("class").contains("collapsed")) menu.click();
            driver.findElements(xpathForsportsPrematch).get(sportN).findElement(By.xpath("./a[1]")).click();//свернули спорт
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
    public static void onTriggerPeriod(String period){
        //если меню свернуто - разворачиваем
        setExpandCollapseMenusButton(true);
        driver.findElement(By.xpath("//div[@class='periods']//div[contains(@class,'periods__input')]")).click();
        //включает фильтр по времени
        driver.findElement(By.xpath("//div[@class='periods']//ul[@class='periods__list']/li[contains(text(),'"+period+"')]")).click();
        CommonStepDefs.workWithPreloader();
        Stash.put("keyPeriod",period);
        setExpandCollapseMenusButton(true);
    }




    /**
     * проверка что страница Прметач соответсвует ожиданиям (открыта нужная игра и триггер по времени в правильном состоянии
     */
    public static boolean pagePrematch(String team1, String expectedPeriod, boolean isFavorit) {
        boolean flag = true;
        //если меню свернуто - разворачиваем
        WebElement menu = driver.findElement(By.id("menu-toggler"));
        if (!menu.getAttribute("class").contains("collapsed")) {
            menu.click();
            CommonStepDefs.workWithPreloader();
        }

        LOG.info("Проверка страницы прематч при включенном фильтре = " + expectedPeriod);
        new WebDriverWait(driver,10).until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class = 'left-menu__search left-menu-filters']"),0));
        String selectPeriod = driver.findElement(By.xpath("//div[@class = 'left-menu__search left-menu-filters']//div[contains(@class,'periods__input')]")).getAttribute("innerText");
        LOG.info("Теперь проверим что фильтр в нужном состоянии "+expectedPeriod);
        if (!selectPeriod.equals(expectedPeriod)) {
            flag=false;
            LOG.error("Фильтр по периоду не соответсвует ожидаемому. " + selectPeriod + " вместо " + expectedPeriod);
        }

        LOG.info("Проверим что нужная игра открыта по центарльной части страницы");
        //String nameOnPage = driver.findElement(By.xpath("//div[contains(@class,'header-teams')]//span[contains(@class,'game-center-container__inner-text')]")).getAttribute("innerText");
        StringBuilder nameOn = new StringBuilder();
        List <WebElement> names = driver.findElements(By.xpath("//p[contains(@class,'game-score_multiset__team-name-text')]"));
        if (names.size()==2){
//    nameOn.append(names.get(0).getAttribute("innerText"));
//    nameOn.append(names.get(1).getAttribute("innerText"));
            names.forEach(element -> nameOn.append(element.getAttribute("innerText")));
        }
        else{
            driver.findElements(By.xpath("//div[@class='game-score__inner']//p")).forEach(element -> nameOn.append(element.getAttribute("title")));
        }
        String nameOnPage = CommonStepDefs.stringParse(nameOn.toString());
        Assert.assertTrue(
                "В прематче открылась неправильная игра. Открылась игра: "+ nameOnPage + ", а ожидалось " + team1,
                CommonStepDefs.stringParse(team1).equals(nameOnPage));

        if (isFavorit){
            flag &= inLeftMenuGameYellow(team1);
        }
        onTriggerPeriod(expectedPeriod);//включаем обратнофильтр по времени
        return flag;
    }

    /**
     * Проверка что нужная игра есть в Избранном в левом меню и выделена там желтым
     */
    public static boolean inLeftMenuGameYellow(String team1){
        boolean flag = true;
        LOG.info("Проверяем что нужная игра активна и выделена желтым в левом меню в Моих Пари");
        List<WebElement> favouriteGames = driver.findElements(By.xpath("//li[contains(@class,'left-menu__favorite-list-item')]"));
        for (WebElement favourGame : favouriteGames) {
            String nameGameOnPage = favourGame.findElement(By.xpath(".//*[contains(@class,'left-menu__list-item-games-teams')]")).getAttribute("title");
            nameGameOnPage = CommonStepDefs.stringParse(nameGameOnPage);
            if (nameGameOnPage.contains(CommonStepDefs.stringParse(team1))) {
                if (!favourGame.getAttribute("class").contains("active")) {
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
        String xpathFavouriteGames = "//*[@id='sports-list-container']//li[contains(@class,'left-menu__favorite-list-item')]";
        int myGamesCount = driver.findElements(By.xpath(xpathFavouriteGames)).size();

        int sizeFavouriteUpdate;

        for (int count = myGamesCount; count >0; count--) {
            driver.findElement(By.xpath(xpathFavouriteGames + "//div[contains(@class,'fav-game-star')]")).click();//убираем игру из избранного в премтче
            new WebDriverWait(driver,10)
                    .withMessage("Игра не удалилась из избранного. в Избранном в ЛМ количество игр = " + driver.findElements(By.xpath("//*[@id='sports-list-container']//li[contains(@class,'left-menu__favorite-list-item')]")).size())
                    .until(ExpectedConditions.numberOfElementsToBeLessThan(By.xpath(xpathFavouriteGames),myGamesCount));

            myGamesCount = driver.findElements(By.xpath(xpathFavouriteGames)).size();
        }
    }

    @ActionTitle("многовыборный режим")
    public static void multiGamesOnOff(String onOrOff){
        WebDriverWait wait =  new WebDriverWait(driver,10);
        boolean turnOn = onOrOff.equalsIgnoreCase("включает")?true:false;
        By xpathMultiviewButton = By.xpath("//div[contains(@class,'left-menu-filters__item_multiview')]");

//        if (!driver.findElements(preloaderOnPage).isEmpty()){
//            LOG.info("Страница не прогрузилась. Обновим её");
//            driver.navigate().refresh();
//            CommonStepDefs.workWithPreloader();
//        }
        CommonStepDefs.workWithPreloader();
        LOG.info("\nОткроем левое меню");
        setExpandCollapseMenusButton(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wait.withMessage("Нет кнопки многовыборного режима");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[contains(@class,'left-menu-filters__item_multiview')]"),0));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'left-menu-filters__item_multiview')]")));

        LOG.info("Проверяем активность кнопки многовыборного режима");
        boolean isOn = driver.findElement(xpathMultiviewButton).getAttribute("class").contains("active");
        if (isOn!=turnOn){
            driver.findElement(xpathMultiviewButton).findElement(By.xpath("./i")).click();
            isOn = driver.findElement(xpathMultiviewButton).getAttribute("class").contains("active");
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
        WebDriverWait wait =  new WebDriverWait(driver,10);

        int index = Integer.valueOf(numberSportOnLM);

        setExpandCollapseMenusButton(true);
        LOG.info("Сворачиваем все виды спорта");
        closeSports();//сворачиваем все виды спорта

        List<WebElement> allSports = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li"));
        String xpathCurrentSport = "//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + (index+1) + "]";
        LOG.info("Разворачиваем первый спорт");
        allSports.get(index).click();//развернули спорт
        wait.withMessage("Спорт номер " + index + " не развернулся за 10 секунд");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath(xpathCurrentSport+"/ul[1]/li"),0));//проверяем что спорт развернулся

        if (!allSports.get(index).findElement(By.xpath("ul[1]/li[1]")).getAttribute("class").contains("active"))//если первый регион спорта не выбран то откроем его
        {
            LOG.info("Регион в выбранном спорте свернут - разворачиваем его");
            allSports.get(index).findElement(By.xpath("ul[1]/li[1]//div[contains(@class,'left-menu__list-item-arrow_region')]")).click();//развернули регион
            wait.withMessage("Регион " + allSports.get(index).findElement(By.xpath(".//div[@class='left-menu__item']/span[2]")).getAttribute("innerText") +
                    " в спорте номер " + index + " не развернулся!!")
                    .until(ExpectedConditions.numberOfElementsToBeMoreThan(
                            By.xpath(xpathCurrentSport + "/ul[1]/li//div[contains(@class,'left-menu__list-item-region-compitition')]"),0));
        }
        LOG.info("Добавляем первый турнир в регионе в контейнер многовыборного режима");
        allSports.get(index).findElement(By.xpath("./ul[1]/li[1]//label[contains(@for,'checkbox-competition')]")).click();//выбрали турнир
        LOG.info("Запоминаем название соревнования в ЛМ");
        String nameTour = allSports.get(index).findElement(By.xpath("ul[1]/li[1]//label[contains(@for,'checkbox-competition')]/i")).getAttribute("innerText");//название турнира в ЛМ
        setExpandCollapseMenusButton(false);
        Stash.put(keyNameTour,nameTour);
    }


    @ActionTitle("добавляет в многовыборный режим одну игру из спорта номер")
    public void addOneGameToMultiview(String numberSport, String keyNameGame){
        WebDriverWait wait =  new WebDriverWait(driver,10);

        int index = Integer.valueOf(numberSport);

        LOG.info("Добавляем одну игру из соревновани в контейнер");
        setExpandCollapseMenusButton(true);
        LOG.info("Сворачиваем все виды спорта");
        closeSports();//свернули все виды спорта
        LOG.info("Разворачиваем следующий спорт");
        List<WebElement> allSports = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li"));
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
        LOG.info("Наводим мышку на название соревнования, чтобы открлось всплывающее меню с перечнем игр");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        actions.moveToElement(tour).build().perform();//наводим мышку на турнир
        String nameGame = tour.findElements(By.xpath("../..//div[contains(@class,'poup-sports_prematch scroll-contain')]//label[contains(@for,'checkbox-game')]")).get(0).getAttribute("innerText");
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
        String nameTourInMulti = driver.findElements(By.xpath("//div[contains(@class,'prematch-competitions scroll-contain')]//div[contains(@class,'prematch-competition-name')]//div[contains(@class,'prematch-competition-name__inner-competition ellipsis-text')]")).get(0).getAttribute("innerText");

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
        WebDriverWait wait = new WebDriverWait(driver,10);
        LOG.info("Убираем одну игру из контейнера");
        setExpandCollapseMenusButton(true);

        int index = Integer.valueOf(numberSport);
        String xpathCurrentSport = "//*[@id='sports-list-container']/ul[2]/ng-include[2]/li[" + (index+1) + "]";
        List<WebElement> allSports = driver.findElements(By.xpath("//*[@id='sports-list-container']/ul[2]/ng-include[2]/li"));
        if (!allSports.get(index).getAttribute("class").contains("active")) {
            LOG.info("Спорт свернут - разворачиваем его");
            allSports.get(index).findElement(By.xpath(".//b[contains(@class,'left-menu__list-item-arrow_sport')]")).click();
            wait.withMessage("Спорт не развернулся за 10 секунд");
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                    By.xpath(xpathCurrentSport+"/ul[1]/li"),
                    0));//проверяем что спорт развернулся
        }
        if (!allSports.get(index).findElement(By.xpath("./ul[1]/li[1]")).getAttribute("class").contains("active"))
        {
            LOG.info("регион в спорте свернут - разворачиваем его");
            allSports.get(index).findElement(By.xpath("ul[1]/li[1]//div[contains(@class,'left-menu__list-item-arrow_region')]")).click();//развернули регион
            wait.withMessage("Регион не развернулся за 10 секунд");
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                    By.xpath(xpathCurrentSport + "/ul[1]/li[1]//label[contains(@for,'checkbox-competition')]"),
                    0));//проверяем что регион развернулся
        }
        WebElement tour = allSports.get(index).findElement(By.xpath("ul[1]/li[1]//label[contains(@for,'checkbox-competition')]"));
        Actions actions = new Actions(driver);
        LOG.info("Наводим мышку на название соревноания в ЛМ, чтобы появился всплывающий список игр");
        actions.moveToElement(tour).build().perform();//наводим мышку на турнир
        LOG.info("Запоминаем название игры, которая в контейнере");
        String nameGame =
                tour.findElements(By.xpath("../..//div[contains(@class,'poup-sports_prematch scroll-contain')]//input[contains(@id,'checkbox-game') and contains(@class,'ng-not-empty')]/../label")).get(0).getAttribute("innerText");
        LOG.info("Выбираем эту игру");
        WebElement game =
                tour.findElements(By.xpath("../..//div[contains(@class,'poup-sports_prematch scroll-contain')]//input[contains(@id,'checkbox-game') and contains(@class,'ng-not-empty')]")).get(0);
        LOG.info("И убираем игру из контейнера");
        game.findElement(By.xpath("../label")).click();
        Stash.put(keyNameDelettingGame,nameGame);
    }

    @ActionTitle("проеряет что удаленная игра не осталась в списке многовыборного режима")
    public void checkMultiviewHaveNotTheGame(String keyNameDeletingGame){
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

    @ActionTitle("режим мультирынков")
    public void onOffMultimarkets(String onOrOff){
        boolean needOn = onOrOff.contains("включает")?true:false;
        String filter = needOn?"Мультирынки":"Результат матча";
        WebElement filterHeader = driver.findElement(By.xpath("//div[contains(@class,'game-center-container__inner-header-filter-selected')]"));
        LOG.info("Если режим мультирынков не нужен, то включим вместо него 'Результат матча'");
        if (filterHeader.getAttribute("innerText").contains("Мультирынки")!=needOn){
            filterHeader.click();
            filterHeader.findElement(By.xpath("following-sibling::*/div[contains(@class,'inner-header-filter-list-item') and contains(text(),'" + filter + "')]"));
            CommonStepDefs.workWithPreloader();
            Assert.assertTrue(
                    "Режим мультирынков is " + !needOn + ", хотя на тумблер нажали",
                    filterHeader.getAttribute("innerText").contains("Мультирынки")==needOn);
        }
        LOG.info("Режим мультирынков is " + needOn);
    }


    @ActionTitle("проверяет совпадение названия маркета и размера коэффициента в центральной области и в контейнере мультирынка для активной игры")
    public void checkMarktandCoef(){
        String nameMarketMulti;
        String marketis;
        String coefOnPage;
        WebElement activeGameOnPage;

        WebElement activeGameMulti = driver.findElement(By.xpath("//div[contains(@class,'bets-block bets-block_single-row') and contains(@class,'bets-block_active')]"));
        List<WebElement> coefsInMulti = activeGameMulti.findElements(By.xpath(".//div[contains(@class,'bets-block__body_single-row')]/div[1]/div"));
        List<String> listMarketsInMulti = driver.findElements(By.xpath("//div[contains(@class,'header-event-narrow')]")).stream()
                .map(element->element.getAttribute("innerText").trim()).collect(Collectors.toList());
        //listMarketsInMulti.add(listMarketsInMulti.size(),listMarketsInMulti.get(listMarketsInMulti.size()-1) + " 2");
        for (int i=0; i<coefsInMulti.size();i++) {
            LOG.info("Проверка доабвлени ставки в купон из мультимаркета. Ставка " + listMarketsInMulti.get(i) + " " + coefsInMulti.get(i).getAttribute("innerText"));
            coefsInMulti.get(i).click();
//            activeGameOnPage = driver.findElement(By.xpath("//div[contains(@class,'inner game-center-container__live')]"));
            activeGameOnPage=driver.findElement(By.xpath("//div[contains(@class,'live-container')]//div[contains(@class,'bets-block__bet-cell_active')]"));
            coefOnPage = activeGameOnPage.findElement(By.xpath(".//span[contains(@class,'bet-cell-content-ratio')]")).getAttribute("innerText");
            marketis = activeGameOnPage.findElement(By.xpath("./ancestor-or-self::div[contains(@class,'bets-block__body')]/preceding-sibling::div[contains(@class,'bets-block__header')]")).getAttribute("innerText");

            switch (listMarketsInMulti.get(i).replaceAll("[^а-яА-Я]", "")) {
                case "П":
                    nameMarketMulti = "Исход Победитель матча";
                    break;
                default:
                    nameMarketMulti = listMarketsInMulti.get(i).replaceAll("[^а-яА-Я]", "");
                    break;
            }
            //рынки в мультирынке это Исход, Фора, Тотал.
            LOG.info("Првоерим что размер коэффииента и название маркета совпадаеют в контейнере мультимаркета и в центральной области страницы");
            Assert.assertTrue(
                    "Все плохо . коэф на странице и в контейнере мультимаркета не совпал. " + coefOnPage + " против " + coefsInMulti.get(i).getAttribute("innerText").trim(),
                    coefOnPage.equals(coefsInMulti.get(i).getAttribute("innerText").trim())
            );
            Assert.assertTrue(
                    "Маркет на страниуе и в контейнере мультимаркета не совпал. " + marketis + " против " + nameMarketMulti,
                    nameMarketMulti.contains(marketis.replaceAll("\n",""))
            );

            LOG.info("Убираем игру из купона нажатием на коэффициент в центральной области");
            activeGameOnPage.click();
            new WebDriverWait(driver,10)
                    .withMessage("Купон не очистился, хотя нажали на активную ставку еще раз - она должна была удалться из купона")
                    .until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[contains(@class,'coupon-bet') and not(contains(@class,'coupon-bet_offer'))]/ul"),0));
            new WebDriverWait(driver,10)
                    .withMessage("В контейнере мультирынков или в центральной области ставка осталась выделенной желтым")
                    .until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[contains(@class,'bets-block__bet-cell_active')]"),0));
        }

    }

    @ActionTitle("ищет ставку с маленьким значением maxbet")
    public void searchBetFromSuperbet(String betOk){
        WebDriverWait wait = new WebDriverWait(driver,10);
        Actions actions = new Actions(driver);
        int has = 0;
        LOG.info("Сворачиваем все виды спорта");
        closeSports();//свернули все виды спорта
        LOG.info("Разворачиваем футбол");
        WebElement football = driver.findElement(By.xpath("//*[@id='sports-list-container']//li[@id='sport-1']"));
        football.findElement(By.xpath(".//*[contains(@class,'left-menu__list-item-arrow_sport')]")).click();
        wait.withMessage("Футбол не развернулся спустя 10 секунд");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//ul[@class='left-menu__submenu']/li"),0));
        List<WebElement> regions = football.findElements(By.xpath(".//ul[@class='left-menu__submenu']/li"));
        if (regions.size()>3){//все регионы пролистывать - это очень долго. пусть будет всего 3 региона
            regions=regions.subList(0,3);
        }
        LOG.info("Ищем игру со ставкой Точный счёт, чей коэф > 50.0. Потмоуч о у ставок с меньшим коэффициентом вероятен большой максимум");
        for (WebElement region : regions){
            if(!region.getAttribute("class").contains("active")){
                region.findElement(By.xpath(".//*[contains(@class,'left-menu__list-item-arrow_region')]")).click();
                LOG.info("регион " + region.getAttribute("innerText"));
                wait.withMessage("Регион " + region.getAttribute("innerText") + " не развернулс за 10 секунл");
                wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[contains(@class,'left-menu__list-item-region-compitition')]"),0));
                List<WebElement> comps = region.findElements(By.xpath(".//div[contains(@class,'left-menu__list-item-region-compitition')]"));
                for (WebElement competition:comps){
                    competition.click();
                    workWithPreloader();
                    List<WebElement> games = driver.findElements(By.xpath("//div[contains(@class,'bets-block_single-row')]"));
                    for (WebElement game : games){
                        game.findElement(By.xpath(".//div[contains(@class,'bets-block__header-teams')]")).click();
                        workWithPreloader();
                        has = driver.findElements(By.xpath("//div[@class='game-container__bets-area-wrpr']//div[contains(@class,'bets-block')]//span[@class='bets-block__header-bet-name' and contains(@title,'Точный счет')]")).size();
                        if (has==0){
                            continue;
                        }
                        WebElement score = driver.findElement(By.xpath("//div[@class='game-container__bets-area-wrpr']//div[contains(@class,'bets-block')]//span[@class='bets-block__header-bet-name' and contains(@title,'Точный счет')]"));
                        //          List<WebElement> scores = score.findElement(By.xpath("ancestor::div[@class='bets-block__header']/following-sibling::div[contains(@class,'bets-block__body')]")).findElements(By.xpath("./div[contains(@class,'bets-block__bet-cell')]//span[position()=2]"));
                        List<WebElement> betsAll = score.findElement(By.xpath("ancestor::div[@class='bets-block__header']/following-sibling::div[contains(@class,'bets-block__body')]")).findElements(By.xpath("./div[contains(@class,'bets-block__bet-cell')]//span[position()=2]"));
                        List<WebElement> betsFilter = betsAll.stream().filter(e->Float.valueOf(e.getAttribute("innerText"))>50.0).collect(Collectors.toList());
                        if (betsFilter.size()==0){
                            LOG.info("У текущей игры нет ставок с коэффициентом больше 50.0, значит и maxBet будт большим, даже проверять не будем. Попрообуем на следующей игре");
                            continue;
                        }
                        LOG.info("Пройдемся по ставкам в этой игре, и поищем ту, у которой maxBet меньше " + betOk);
                        for (WebElement bet:betsFilter){
                            bet.click();
                            new CouponPage().checkListOfCoupon();
                            maxBet.click();
                            actions.moveToElement(driver.findElement(By.xpath("//*[@class='btn btn_full-width']")),0,-100).build().perform();
                            wait.withMessage("При нажатии на кнопку МаксБет поле с размером ставки не заполнилось");
                            wait.until(ExpectedConditions.attributeToBeNotEmpty(CouponPage.couponInputOrdinar,"value"));
                            String max = CouponPage.couponInputOrdinar.getAttribute("value");
                            if(Float.valueOf(max)<=Float.valueOf(betOk)){
                                LOG.info("Найденная игра и ставка: " + game.getAttribute("innerText").replaceAll("\n"," ") +
                                        " Ставка на точный счет " + bet.getAttribute("innerText"));
                                return;
                            }
                            LOG.info("max=" + max);
                            driver.findElement(By.xpath("//button[@class='btn btn_full-width']")).click();
                            wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[contains(@class,'coupon__bet-block')]/div"),0));
                        }
                        LOG.info("" + game.getAttribute("innerText"));
                        LOG.info("\nbetsFilter.size = " + betsFilter.size());
                    }
                }
                region.findElement(By.xpath(".//*[contains(@class,'left-menu__list-item-arrow_region')]")).click();//сворачиваем регион
            }
            LOG.info("клик");
        }
    }


    @ActionTitle("ищет ставку с маленьким значением maxbet2")
    public void searchBetFromSuperbet2(String betOk){
        WebDriverWait wait = new WebDriverWait(driver,10);
        Actions actions = new Actions(driver);
        LOG.info("Для супербета нужна ставка с маленьким значением max. Вероятность этого маленькая, поэтому нет смысла првоерять все виды спорта и все ставки. будем смотреть только футбол и специальные ставки");
        List<WebElement> sports = new ArrayList<>();
        String sportByTitle = "//li[contains(@class,'left-menu__list-item-sport')]/*[contains(@title,'%s')]/..";
        try {
            sports.add(driver.findElement(By.xpath(String.format(sportByTitle,"Специальные ставки"))));
            sports.add(driver.findElement(By.xpath(String.format(sportByTitle,"Футбол"))));
        }
        catch (NoSuchElementException e){
            LOG.info("Нет одного из спортов");
        }
        for (WebElement sport:sports){
            LOG.info("разворачиваем ЛМ и сворачиваем все виды спорта");
            setExpandCollapseMenusButton(true);
            closeSports();
            if (sport.getAttribute("class").contains("hide")){
                driver.findElement(By.id("sport--10")).click();
                wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(sport,"class","hide")));
            }
            LOG.info("Разворачиваем " + sport.findElement(By.xpath("./*")).getAttribute("title"));
            sport.click();
            int has = 0;

            List<WebElement> regions = sport.findElements(By.xpath(".//ul[@class='left-menu__submenu']/li"));
            if (regions.size()>3){//все регионы пролистывать - это очень долго. пусть будет всего 3 региона
                regions=regions.subList(0,3);
            }
            LOG.info("Ищем игру с коэф > 50.0. Потому что у ставок с меньшим коэффициентом вероятен большой максимум");
            for (WebElement region : regions){
                if(!region.getAttribute("class").contains("active")){
                    region.findElement(By.xpath(".//*[contains(@class,'left-menu__list-item-arrow_region')]")).click();
                    LOG.info("регион " + region.getAttribute("innerText"));
                    wait.withMessage("Регион " + region.getAttribute("innerText") + " не развернулся за 10 секунл");
                    wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[contains(@class,'left-menu__list-item-region-compitition')]"),0));
                    List<WebElement> comps = region.findElements(By.xpath(".//div[contains(@class,'left-menu__list-item-region-compitition')]"));
                    for (WebElement competition:comps){
                        competition.click();
                        workWithPreloader();
                        List<WebElement> games = driver.findElements(By.xpath("//div[contains(@class,'bets-block_single-row')]"));
                        for (WebElement game : games){
                            game.findElement(By.xpath(".//div[contains(@class,'bets-block__header-teams')]")).click();
                            workWithPreloader();

                            List<WebElement> betsAllEl = driver.findElements(By.xpath("//div[@class='game-container__bets-area-wrpr']//span[contains(@class,'bets-block__bet-cell-content-price')]"));

//                            List <Float> betsFloat = betsAllEl.stream().map(element -> Float.valueOf(element.getAttribute("innerText"))).collect(Collectors.toList());
////                            Collections.sort(betsFloat,Collections.reverseOrder());
////                            betsFloat.get(0);
////
////                            WebElement bet

                            betsAllEl.stream().map(element -> Float.valueOf(element.getAttribute("innerText"))).sorted(Comparator.comparing(Float::floatValue)).collect(Collectors.toList());

                            List<WebElement> betsFilter = betsAllEl.stream().filter(e->Float.valueOf(e.getAttribute("innerText"))>50.0).collect(Collectors.toList());
                            if (betsFilter.size()==0){
                                LOG.info("У текущей игры нет ставок с коэффициентом больше 50.0, значит и maxBet будет большим, даже проверять не будем. Попрообуем на следующей игре");
                                continue;
                            }
                            LOG.info("Пройдемся по ставкам в этой игре, и поищем ту, у которой maxBet меньше " + betOk);
                            for (WebElement bet:betsFilter){
                                bet.click();
                                new CouponPage().checkListOfCoupon();
                                maxBet.click();
                                actions.moveToElement(driver.findElement(By.xpath("//*[@class='btn btn_full-width']")),0,-100).build().perform();
                                wait.withMessage("При нажатии на кнопку МаксБет поле с размером ставки не заполнилось");
                                wait.until(ExpectedConditions.attributeToBeNotEmpty(CouponPage.couponInputOrdinar,"value"));
                                String max = CouponPage.couponInputOrdinar.getAttribute("value");
                                if(Float.valueOf(max)<=Float.valueOf(betOk)){
                                    LOG.info("Найденная игра и ставка: " + game.getAttribute("innerText").replaceAll("\n"," ") +
                                            " Ставка на точный счет " + bet.getAttribute("innerText"));
                                    return;
                                }
                                LOG.info("Не подходит: max=" + max);
                                driver.findElement(By.xpath("//button[@class='btn btn_full-width']")).click();
                                wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[contains(@class,'coupon__bet-block')]/div"),0));
                            }
                            LOG.info("" + game.getAttribute("innerText"));
                            LOG.info("\nbetsFilter.size = " + betsFilter.size());
                        }
                    }
                    region.findElement(By.xpath(".//*[contains(@class,'left-menu__list-item-arrow_region')]")).click();//сворачиваем регион
                }
            }
            LOG.info("нет подходящих ставок. Перейдем к следующему спорту");
        }


    }

    @ActionTitle("сравниваем количество игр по wss и в Прематче")
    public void compareSizeOfZeroMargin(){
        Integer games_in_prematch = Stash.getValue("by_size_zero_margin_key");
        Integer games_wss = Stash.getValue("key_size");
        Assert.assertTrue("Значения не совпадают! Количество игр по wss:" + games_wss + " Количество игр в Прематче: " + games_in_prematch,games_in_prematch.equals(games_wss));

    }

    @ActionTitle("проверяет нулевую маржу")
    public void checkZeroMargin(){
        WebDriverWait wait = new WebDriverWait(PageFactory.getWebDriver(),10);
        String by_games = "//div[contains(@class, 'bets-block__header bets-block__header_prematch')]";
        By by_competitions = xpath("//li[@id='sport--14']/ul[@class='left-menu__submenu']//div[contains(@class,'left-menu__list-item-region-compitition')]");
        closeSports();
        wait.until(CommonStepDefs.elementIsOnPage(By.id("sport--14"),"Нет нулевой маржи!"));
        driver.findElement(By.id("sport--14")).click();
        List<WebElement> competitions =driver.findElements(by_competitions);
        for (int i = 0; i < competitions.size(); i++) {
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(by_competitions, competitions.size()-1));
            wait.until(CommonStepDefs.elementIsOnPage((By.xpath(by_games)),"Не прогрузились игры"));
            driver.findElements(by_competitions).get(i).click();
            int size_games = driver.findElements(By.xpath(by_games)).size();
            int by_size_zero_margin = driver.findElements(xpath(by_games + "/div[contains(@class, 'bets-block__header-inner bets-block__header-inner_right')]/i[@title='Нулевая маржа']")).size();
            wait.withMessage("Количество игр и количество значков нулевой маржи не совпадают:" + by_size_zero_margin + " и " + size_games);
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(xpath(by_games + "/div[contains(@class, 'bets-block__header-inner bets-block__header-inner_right')]/i[@title='Нулевая маржа']"),size_games-1));
            LOG.info("В разделе Нулевая маржа у каждой игры есть значок нулевой маржи");
            Stash.put("by_size_zero_margin_key", by_size_zero_margin);
        }
    }

    @ActionTitle("добавляем рандомное событие из Нулевой маржи")
    public void addZeroMarginToCoupon(){
        WebDriverWait wait = new WebDriverWait(PageFactory.getWebDriver(),10);
        Random random = new Random();
        List<WebElement> competitions =driver.findElements(xpath("//li[@id='sport--14']/ul[@class='left-menu__submenu']//div[contains(@class,'left-menu__list-item-region-compitition')]"));
        competitions.get(0).click();
        wait.until(CommonStepDefs.elementIsOnPage((By.xpath( "//div[contains(@class, 'bets-block__header bets-block__header_prematch')]")),"Не прогрузились игры"));
        List<WebElement> coeffs = driver.findElements(xpath("//div[@class='bets-block prematch-competition-games__item']/div[contains(@class,'bets-block__body')]/div[contains(@class,'bets-block__bet-cell')]"));
        int num = random.nextInt(Math.abs(coeffs.size()-1));
        coeffs.get(num).click();
    }
}


