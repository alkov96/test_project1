package ru.gamble.pages.mobile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.gamble.utility.Constants.RANDOM;

@PageEntry(title = "Авторизованная мобильная главная страница")
public class MobileAuthorizedMainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileAuthorizedMainPage.class);

    @ElementTitle("Баланс")
    @FindBy(xpath = "//a[@href='/private/balance/deposit']")
    private WebElement deposit;

    @ElementTitle("Бонус")
    @FindBy(xpath = "//a[@href='/private/balance/bonus']")
    private WebElement bonus;

    @ElementTitle("Далее")
    @FindBy(xpath = "//button[contains(.,'Далее')]")
    protected WebElement nextButton;

    @ElementTitle("Сумма")
    @FindBy(xpath="//input[@class='form-input']")
    protected WebElement amountInput;

    @ElementTitle("VISA_МИР")
    @FindBy(xpath = "//span[@class='btn btn_default btn_flex btn_checkbox btn_large']")
    protected WebElement visaMirButton;

    @ElementTitle("ПРЕМАТЧ")
    @FindBy(xpath = "//div/span[contains(.,'Прематч')]")
    protected WebElement prematchTab;

    @ElementTitle("Популярные события")
    @FindBy(xpath = "//div/span[contains(.,'Популярные события')]")
    protected WebElement populateIventsTab;

    @ElementTitle("Купон-бар")
    @FindBy(xpath = "//a[@class='coupon-bar__control coupon-bar__control_left']")
    protected WebElement couponBar;


    public MobileAuthorizedMainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(driver, this);
        waitingForPreloaderToDisappear(30);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(deposit));
    }

    @ActionTitle("записываем значение баланса в")
    public void writeBalanceIn(String balanceKey){
        String value = deposit.getAttribute("innerText");
        Stash.put(balanceKey, value);
        LOG.info("Сохранили в память key [" + balanceKey + "] <== value [" + value + "]");
    }

    @ActionTitle("проверяет увеличение баланса")
    public void checksIncreaseBalanceOn(String keyOldBalance, String keySum){
        String oldBalance = Stash.getValue(keyOldBalance);
        String sum = Stash.getValue(keySum.trim());

        String newBalance = deposit.getAttribute("innerText").replaceAll("\\s","").trim();
        BigDecimal expectedAmount = new BigDecimal(oldBalance.replaceAll("\\s","").trim()).setScale(2, RoundingMode.HALF_UP)
                .add(new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP));
        BigDecimal actualAmount = new BigDecimal(newBalance).setScale(2, RoundingMode.HALF_UP);

        assertThat(expectedAmount.compareTo(actualAmount) == 0)
                .as("Ожидаемый баланс [" + expectedAmount.toString() + "] не соответсвует действительному [" + actualAmount.toString() + "]").isTrue();
        LOG.info("Ожидаемый баланс [" + expectedAmount.toString() + "] соответсвует действительному [" + actualAmount.toString() + "]");
    }

    @ActionTitle("записываем значение бонусов в")
    public void writeBonusIn(String bonusKey){
        String value = deposit.getAttribute("innerText");
        Stash.put(bonusKey, value);
        LOG.info("Сохранили в память key [" + bonusKey + "] <== value [" + value + "]");
    }

    @ActionTitle("проверяет уменьшение баланса")
    public void checksDecreaseBalanceOn(String keyOldBalance, String keySum){
        String oldBalance = Stash.getValue(keyOldBalance);
        String sum = Stash.getValue(keySum.trim());

        String newBalance = deposit.getAttribute("innerText").replaceAll("\\s","").trim();
        BigDecimal expectedAmount = new BigDecimal(oldBalance.replaceAll("\\s","").trim()).setScale(2, RoundingMode.HALF_UP)
                .subtract(new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP)).abs();
        BigDecimal actualAmount = new BigDecimal(newBalance).setScale(2, RoundingMode.HALF_UP);

        assertThat(expectedAmount.compareTo(actualAmount) == 0)
                .as("Ожидаемый баланс [" + expectedAmount.toString() + "] не соответсвует действительному [" + actualAmount.toString() + "]").isTrue();
        LOG.info("Ожидаемый баланс [" + expectedAmount.toString() + "] соответсвует действительному [" + actualAmount.toString() + "]");
    }

    @ActionTitle("выбирает случайное событие в виде спорта")
    public void selectTypeSportBy(String typeSport){
        WebDriver driver = PageFactory.getWebDriver();
        WebElement currentTypeOfSport, currentItem;
        Date currentDateTime = new Date(System.currentTimeMillis());
        int indexTypeOfSport, indexItem, indexBet;
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Date itemDate = null;
        List<WebElement> typesOfSport = driver.findElements(By.xpath("//div/span[@class='collapse-navigation__item-text']"));

        for(int i = 0; i < 3 ; i++) {
            if (typeSport.equals(RANDOM)) {
                indexTypeOfSport = new Random().nextInt(typesOfSport.size());
                currentTypeOfSport = typesOfSport.get(indexTypeOfSport);
                typesOfSport.remove(indexTypeOfSport);
            } else {
                currentTypeOfSport = typesOfSport.stream().filter(element -> element.getAttribute("innerText").contains(typeSport)).findFirst().get();
            }
            LOG.info("Выбрали тип спорта [" + currentTypeOfSport.getAttribute("innerText") + "] : попытка [" + i + "]");
            currentTypeOfSport.click();

            List<WebElement> items = driver.findElements(By.xpath("//li/div[@class='section__header']"));
            if(items.size() != 0) {
                for (int k = items.size() - 1; k >= 0; k--) {
                    indexItem = new Random().nextInt(items.size());
                    currentItem = items.get(indexItem);
                    LOG.info("Выбрали событие [" + currentItem.getAttribute("innerText").replaceAll("\n", "") + "]");
                    currentItem.click();

                    List<WebElement> itemsHeader = currentItem.findElements(By.xpath("//div[@class='game-item__match-item match-item']"));
                    if (itemsHeader.size() == 0) {
                        LOG.info("Событий [" + itemsHeader.size() + "]. Пробуем снова.");
                        continue;
                    } else {
                        for (WebElement itemHeader : itemsHeader) {
                            try {
                                itemDate = formatter.parse(itemHeader.findElement(By.xpath("div[@class='match-item__footer']"))
                                        .getAttribute("innerText").replaceAll("\n",""));
                                LOG.info("Смотрим на игру [" + itemHeader.getAttribute("innerText").replaceAll("\n", " ") + "]");
                            } catch (ParseException e) {
                                throw new AutotestError("Ошибка! Не сомогли разпознать дату события\n" + e.getMessage());
                            }
                            if (itemDate.after(currentDateTime)) {
                                LOG.info("Переходим в событие [" + itemHeader.getAttribute("innerText").replaceAll("\n", "") + "]");
                                driver.navigate().to(itemHeader.findElement(By.xpath("a")).getAttribute("href"));

                                List<WebElement> bets = driver.findElements(By.xpath("//div[@class='game-information__factor']"));
                                int betBegore = Integer.parseInt(couponBar.getAttribute("innerText").replaceAll("\n", " "));
                                LOG.info("Перед выбором фактора ставки в купоне было [" + betBegore + "] ставок");
                                if (bets.size() != 0) {
                                    indexBet = new Random().nextInt(bets.size());
                                    WebElement bet = bets.get(indexBet);
                                    LOG.info("Выбрали случайно фактор ставки [" + bet.getAttribute("innerText").replaceAll("\n", " ") + "]");
                                    bets.get(indexBet).click();

                                    int betAfter = Integer.parseInt(couponBar.getAttribute("innerText").replaceAll("\n", " "));
                                    assertThat(betAfter).as("Ожидаемое кол-во ставок [" + (betBegore + 1) + "] не соответсвует дейстивтельному [" + betAfter + "]").isEqualTo(betBegore + 1);
                                    LOG.info("Количество ставок в купоне стало [" + betAfter + "]");

                                    LOG.info("Нажимаем на купон");
                                    couponBar.click();

                                } else {
                                    throw new AutotestError("Ошибка! Не нашли ни одного фактора ставок");
                                }

                            }
                        }
                    }
                }

            }
        }
    }

}
