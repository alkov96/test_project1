package ru.gamble.pages.userProfilePages;

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
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

/**
 * @author p.sivak.
 * @since 21.05.2018.
 */
@PageEntry(title = "История операций")
public class OperationHistoryPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(OperationHistoryPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//div[@class='history__table']")
    private WebElement historyTable;

    @ElementTitle("Поле поиска")
    @FindBy(xpath = "//div[contains(@class,'input-search__wrapper_history')]/input")
    private WebElement searchInput;

    public OperationHistoryPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        workWithPreloader();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(historyTable));
    }


    /**
     * Метод возвращает true если содержимое страницы обновилось. и false если не обновилось
     * @param id - предыдущее наполнение страницы
     * @return
     */
    private boolean pageUpdate(List<String> id) throws InterruptedException {
        Thread.sleep(1000);
        List<WebElement> newList = driver.findElements(By.xpath("//span[@class='history__id']")).stream()
                .filter(element -> !element.findElement(By.xpath("preceding-sibling::span")).getAttribute("innerText").contains("Кэшаут"))
                .filter(element -> !element.findElement(By.xpath("preceding-sibling::span")).getAttribute("innerText").contains("Выигрыш пари"))
                .collect(Collectors.toList());
        //этот фильтр убирает операции кэшаута и выигрыша из списка. потмоу что у ставки и кэшаута/выигрыша будут одинаковые id. поэтому будем их просто не учитывать
        newList.forEach(element -> {
            if (id.contains(element.getAttribute("innerText"))) {
                id.add("idbad");
                Assert.fail("Такой id уже был, значит страница не обновилась + " + element.getAttribute("innerText"));
            }
        });
        return id.contains("idbad");
    }

    private boolean changePage(WebElement page) throws Exception {
        Integer currentPage;
        Integer newPage;
        boolean result;

        List<String> id = new ArrayList<>();//список id - операций
        List<WebElement> operationsId;

        id.clear();

//этот фильтр убирает операции кэшаута и выигрыша из списка. потмоу что у ставки и кэшаута/выигрыша будут одинаковые id. поэтому будем их просто не учитывать
        operationsId = driver.findElements(By.xpath("//span[@class='history__id']")).stream()
                .filter(element -> !element.findElement(By.xpath("preceding-sibling::span")).getAttribute("innerText").contains("Кэшаут"))
                .filter(element -> !element.findElement(By.xpath("preceding-sibling::span")).getAttribute("innerText").contains("Выигрыш пари"))
                .collect(Collectors.toList());
        operationsId.forEach(element -> id.add(element.getAttribute("innerText")));

        currentPage = Integer.valueOf(driver.findElement(By.xpath("//div[@class='pagination']/div[contains(@class,'pagination-page ng-binding') and contains(@class,'active')]")).getAttribute("innerText"));
        page.click();
        CommonStepDefs.workWithPreloader();

        newPage = Integer.valueOf(driver.findElement(By.xpath("//div[@class='pagination']/div[contains(@class,'pagination-page ng-binding') and contains(@class,'active')]")).getAttribute("innerText"));
        LOG.info("Перешли на страницу  [" + newPage + "]");
        if (newPage.equals(currentPage)) {
            Assert.fail("Страница не перелистнулась!! была активной страница" + currentPage + " стала активно страница " + newPage);
            result = false;
        }
        result = !pageUpdate(id);
        return result;
    }

    @ActionTitle("проверяет пролистывание страниц")
    public void pagesCheck() throws Exception {
        List<WebElement> pages = driver.findElements(By.xpath("//div[@class='pagination']/div[contains(@class,'pagination-page ng-binding') and not(contains(@class,'active'))]"));//неактивные стрницы(без стрелок)
        if (pages.size()<3){
            LOG.info("Нет страниц вообще, некуда листать.");
            return;
        }
        LOG.info("Листаем на последнюю из видимых страниц");
        assertTrue(changePage(pages.get(pages.size() - 1)));
        LOG.info("Листаем влево");
        assertTrue(changePage(driver.findElement(By.xpath("//div[@class='pagination']//span[contains(@class,'arrow_left')]"))));
        LOG.info("Листаем вправо");
        assertTrue(changePage(driver.findElement(By.xpath("//div[@class='pagination']//span[contains(@class,'arrow_right')]"))));

    }

    @ActionTitle("проверяет сортировку по дате")
    public void sortDate() throws ParseException, InterruptedException {
        boolean sortList;

        Locale local = new Locale("ru", "RU");
        SimpleDateFormat format = new SimpleDateFormat("dd MMMMMMM yyyy, HH:mm", local);

        List<WebElement> elementsDate = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell ng-binding']"));//поле с датой для каждой операции в истории
        List<Date> operationsDate = new ArrayList<>();//список именно дат
        for (WebElement element : elementsDate) {
            operationsDate.add(format.parse(element.getAttribute("innerText")));//берем каждое поле с датой и сохраняем дату в список
        }

        sortList = operationsDate.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()).equals(operationsDate);//проверка отсортирован ли список по убыванию
        assertThat(sortList).as("История не отсортирована по дате при открытии").isTrue();


        driver.findElement(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//th[contains(@class,'history__cell-date table__head-cell_sort')]")).click(); //сортируем по дате
        Thread.sleep(1000);
        elementsDate.clear();
        operationsDate.clear();
        elementsDate = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell ng-binding']"));
        for (WebElement element : elementsDate) {
            operationsDate.add(format.parse(element.getAttribute("innerText")));
        }
        sortList = operationsDate.stream().sorted().collect(Collectors.toList()).equals(operationsDate); //проверка отсортирован ли список теперь по возрастанию
        assertThat(sortList).as("История не отсортировалась по дате после нажатия на сортировку").isTrue();
    }


    @ActionTitle("проверяет сортировку по балансу")
    public void sortBal() throws InterruptedException {
        boolean sortList;

        String xpath = "//td[contains(@class,'history__cell-balance table__body-cell')]"; //путь до суммы баланса
        driver.findElement(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//th[contains(@class,'history__cell-balance table__head-cell_sort')]")).click(); //сортируем по балансу
        Thread.sleep(4000);
        List<WebElement> elementsBalance = driver.findElements(By.xpath(xpath)).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());//поле с суммой баланса
        List<Float> operationsBalance = new ArrayList<>();
        elementsBalance.forEach(element -> operationsBalance.add(Float.valueOf(element.getAttribute("innerText"))));
        sortList = operationsBalance.stream().sorted().collect(Collectors.toList()).equals(operationsBalance);
        assertThat(sortList).as("История не отсортировалась по сумме баланса").isTrue();

        driver.findElement(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//th[contains(@class,'history__cell-balance table__head-cell_sort')]")).click(); //сортируем по балансу
        Thread.sleep(1000);
        elementsBalance = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell history__cell-balance ng-binding']"));
        operationsBalance.clear();
        elementsBalance.forEach(element -> operationsBalance.add(Float.valueOf(element.getAttribute("innerText"))));
        sortList = operationsBalance.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()).equals(operationsBalance);
        assertThat(sortList).as("История не отсортировалась по сумме баланса в обратную сторону").isTrue();
    }

    @ActionTitle("проверяет поиск")
    public void checkSearch(){
        boolean flag = true;
//        WebElement search = driver.findElement(By.xpath("//div[contains(@class,'input-search__wrapper_history')]/input")); //поле поиска
        searchInput.clear();
        searchInput.sendKeys("asd");//в поле поиска вводим буквы, по идее они не должны приниматься.
        if (!searchInput.getAttribute("value").isEmpty()) //если поле поиск не пустое, значит буквы принялись, а это неправильно
        {
            flag = false;
            Assert.fail("В поле ввода можно ввести буквы, но там должны быть только цифры - поиск по ID");
        }
        searchInput.clear();
        String randomID = String.valueOf((int) (1 + Math.random() * 998));
        searchInput.sendKeys(randomID);
        List<WebElement> operationsID = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell']//span[@class='history__id']/span"));

        int count = 0;
        while (operationsID.isEmpty() && count != 60) { //пока поиск не сработает или пока не будет совершено 60 попыток нати что-либо
            if (count == 59) {
                flag = false;
                Assert.fail("60 раз пытались что-то ввести в поле поиска, и всегда ничего не находилось. Поиск не работает");
            }
            LOG.info("Не нашлось операций с ID " + randomID);
            randomID = String.valueOf((int) (1 + Math.random() * 998));
            searchInput.clear();
            searchInput.sendKeys(randomID);
            operationsID = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell']//span[@class='history__id']/span"));

            count++;
        }
        for (WebElement element : operationsID) {
            if (!element.getAttribute("innerText").contains(randomID)) {// indexof<0 только если символы не нашлись в строке. т.е. есди в поиск по подстроке попала запись без этой подстроки
                flag = false;
                Assert.fail("Поиск не сработал. Искали по подстроке " + randomID + ", но нашлась операция с номером " + element.getAttribute("innerText"));
            }
        }
    }

    @ActionTitle("вводит в поиск ID ставки и проверяет что баланс после нее изменился правильно")
    public void searchIdAndCheckBalance(String keyListBet, DataTable dataTable) throws InterruptedException {
        LOG.info("Достаем из памяти список ставок с разными результатами");
        Map<String,String> bets = Stash.getValue(keyListBet);
        Map<String, String> table = dataTable.asMap(String.class, String.class);
        String id;
        String changeBalance;
        String expectedBet;
        String resultOneBet;
        List<WebElement> lineInContainer;
        for (Map.Entry<String, String> entry : table.entrySet()) {

            resultOneBet = entry.getKey();
            id = bets.get(resultOneBet); // достаем из параметров результат ставки и ищем id для этого результата в bets
            if (id==null){
                LOG.info("В Моих пари на выбранные даты не было ставок с исходом = '" + resultOneBet + "'. Првоерки совпадени id не будет. \nИдем дальше");
                continue;
            }
            LOG.info("Вводим в поле поиска id = " + id.replace("\n",""));
            searchInput.clear();
            searchInput.sendKeys(id.toString().replace("\n",""));
            Thread.sleep(1500);
            expectedBet = resultOneBet.equals("Проигрыш")|| resultOneBet.equals("Ожидается")? "Заключение пари": resultOneBet;
            lineInContainer = driver.findElements(By.xpath("//tr[contains(@class,'repeated-item')]"));
            for (WebElement line:lineInContainer){
                if (line.getAttribute("innerText").contains(expectedBet)){
                    LOG.info("Проверим изменился баланс в нужную сторону или нет");
                    changeBalance = line.findElement(By.xpath(".//span[contains(@class,'history-summ')]")).getAttribute("innerText");
                    Assert.assertTrue("Изменение баланса не совпадает с ожидаемым. Для данного события ожидали " + entry.getKey() + ", а на самом деле " + changeBalance,
                            changeBalance.contains("-")==entry.getValue().equals("минус"));
                    LOG.info("Все верно. Идём дальше");

                    break;
                }
                if (lineInContainer.indexOf(line)==lineInContainer.size()-1){
                    Assert.fail("В истории операций есть записи по нужному id, но тип не тот. Ожидали что это будет " + expectedBet + " а на самом деле " + line.getAttribute("innerText"));
                }
            }
        }

    }
}
