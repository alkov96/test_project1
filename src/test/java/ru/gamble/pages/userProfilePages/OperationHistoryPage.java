package ru.gamble.pages.userProfilePages;

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
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
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

    @FindBy(xpath = "//div[@class='history__table']")
    private WebElement historyTable;

    public OperationHistoryPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        workWithPreloader();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(historyTable));
    }


    /**
     * Метод возвращает true если содержимое страницы обновилось. и false если не обновилось
     * @param id - предыдущее наполнение страницы
     * @return
     */
    private boolean pageUpdate(List<String> id) throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        Thread.sleep(1000);
        List<WebElement> newList = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell']//span[@class='history__id']/span"));
        newList.forEach(element -> {
            if (id.contains(element.getText())) {
                id.add("idbad");
                Assert.fail("Такой id уже был, значит страница не обновилась + " + element.getText());
            }
        });
        return id.contains("idbad");
    }

    private boolean changePage(WebElement page) throws Exception {
        WebDriver driver = PageFactory.getDriver();
        Integer currentPage;
        Integer newPage;
        boolean result;

        List<String> id = new ArrayList<>();//список id - операций
        List<WebElement> operationsId;

        id.clear();
        operationsId = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell']//span[@class='history__id']/span"));
        operationsId.forEach(element -> id.add(element.getText()));
        currentPage = Integer.valueOf(driver.findElement(By.xpath("//div[@class='pagination']/div[contains(@class,'pagination-page ng-binding') and contains(@class,'active')]")).getText());
        page.click();

        newPage = Integer.valueOf(driver.findElement(By.xpath("//div[@class='pagination']/div[contains(@class,'pagination-page ng-binding') and contains(@class,'active')]")).getText());
        LOG.info("Перешли на страницу " + newPage);
        if (newPage.equals(currentPage)) {
            Assert.fail("Страница не перелистнулась!! была активной страница" + currentPage + " стала активно страница " + newPage);
            result = false;
        }
        result = pageUpdate(id);
        return result;
    }

    @ActionTitle("проверяет пролистывание страниц")
    public void pagesCheck() throws Exception {
        WebDriver driver = PageFactory.getDriver();

        List<WebElement> pages = driver.findElements(By.xpath("//div[@class='pagination']/div[contains(@class,'pagination-page ng-binding') and not(contains(@class,'active'))]"));//неактивные стрницы(без стрелок)
        LOG.info("Листаем на последнюю из видимых страниц");
        assertTrue(changePage(pages.get(pages.size() - 1)));
        LOG.info("Листаем влево");
        assertTrue(changePage(driver.findElement(By.xpath("//div[@class='pagination']//span[contains(@class,'arrow_left')]"))));
        LOG.info("Листаем вправо");
        assertTrue(changePage(driver.findElement(By.xpath("//div[@class='pagination']//span[contains(@class,'arrow_right')]"))));

    }

    @ActionTitle("проверяет сортировку по дате")
    public void sortDate() throws ParseException, InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        boolean sortList;

        Locale local = new Locale("ru", "RU");
        SimpleDateFormat format = new SimpleDateFormat("dd MMMMMMM yyyy, HH:mm", local);

        List<WebElement> elementsDate = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell ng-binding']"));//поле с датой для каждой операции в истории
        List<Date> operationsDate = new ArrayList<>();//список именно дат
        for (WebElement element : elementsDate) {
            operationsDate.add(format.parse(element.getText()));//берем каждое поле с датой и сохраняем дату в список
        }

        sortList = operationsDate.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()).equals(operationsDate);//проверка отсортирован ли список по убыванию
        assertThat(sortList).as("История не отсортирована по дате при открытии").isTrue();


        driver.findElement(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//th[contains(@class,'history__cell-date table__head-cell_sort')]")).click(); //сортируем по дате
        Thread.sleep(1000);
        elementsDate.clear();
        operationsDate.clear();
        elementsDate = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell ng-binding']"));
        for (WebElement element : elementsDate) {
            operationsDate.add(format.parse(element.getText()));
        }
        sortList = operationsDate.stream().sorted().collect(Collectors.toList()).equals(operationsDate); //проверка отсортирован ли список теперь по возрастанию
        assertThat(sortList).as("История не отсортировалась по дате после нажатия на сортировку").isTrue();
    }

    @ActionTitle("проверяет сортировку по балансу")
    public void sortBal() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        boolean sortList;

        String xpath = "//td[contains(@class,'table__body-cell history__cell-balance')]"; //путь до суммы баланса
        driver.findElement(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//th[contains(@class,'history__cell-balance table__head-cell_sort')]")).click(); //сортируем по балансу
        Thread.sleep(4000);
        List<WebElement> elementsBalance = driver.findElements(By.xpath(xpath)).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());//поле с суммой баланса
        List<Float> operationsBalance = new ArrayList<>();
        elementsBalance.forEach(element -> operationsBalance.add(Float.valueOf(element.getText())));
        sortList = operationsBalance.stream().sorted().collect(Collectors.toList()).equals(operationsBalance);
        assertThat(sortList).as("История не отсортировалась по сумме баланса").isFalse();

        driver.findElement(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//th[contains(@class,'history__cell-balance table__head-cell_sort')]")).click(); //сортируем по балансу
        Thread.sleep(1000);
        elementsBalance = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell history__cell-balance ng-binding']"));
        operationsBalance.clear();
        elementsBalance.forEach(element -> operationsBalance.add(Float.valueOf(element.getText())));
        sortList = operationsBalance.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()).equals(operationsBalance);
        assertThat(sortList).as("История не отсортировалась по сумме баланса в обратную сторону").isTrue();
    }

    @ActionTitle("проверяет поиск")
    public void checkSearch(){
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
        WebElement search = driver.findElement(By.xpath("//div[contains(@class,'input-search__wrapper_history')]/input")); //поле поиска
        search.clear();
        search.sendKeys("asd");//в поле поиска вводим буквы, по идее они не должны приниматься.
        if (!search.getAttribute("value").isEmpty()) //если поле поиск не пустое, значит буквы принялись, а это неправильно
        {
            flag = false;
            Assert.fail("В поле ввода можно ввести буквы, но там должны быть только цифры - поиск по ID");
        }
        search.clear();
        String randomID = String.valueOf((int) (1 + Math.random() * 998));
        search.sendKeys(randomID);
        List<WebElement> operationsID = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell']//span[@class='history__id']/span"));

        int count = 0;
        while (operationsID.isEmpty() && count != 60) { //пока поиск не сработает или пока не будет совершено 60 попыток нати что-либо
            if (count == 59) {
                flag = false;
                Assert.fail("60 раз пытались что-то ввести в поле поиска, и всегда ничего не находилось. Поиск не работает");
            }
            LOG.info("Не нашлось операций с ID " + randomID);
            randomID = String.valueOf((int) (1 + Math.random() * 998));
            search.clear();
            search.sendKeys(randomID);
            operationsID = driver.findElements(By.xpath("//div[@ng-controller='historyWalletCtrl']//div[@class='history__table']//tr[@class='repeated-item ng-scope']/td[@class='table__body-cell']//span[@class='history__id']/span"));

            count++;
        }
        for (WebElement element : operationsID) {
            if (!element.getText().contains(randomID)) {// indexof<0 только если символы не нашлись в строке. т.е. есди в поиск по подстроке попала запись без этой подстроки
                flag = false;
                Assert.fail("Поиск не сработал. Искали по подстроке " + randomID + ", но нашлась операция с номером " + element.getText());
            }
        }
    }
}
