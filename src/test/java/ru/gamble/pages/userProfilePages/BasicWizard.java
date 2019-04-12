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
import ru.gamble.utility.YandexPostman;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.Locale;

import static ru.gamble.stepdefs.CommonStepDefs.getSMSCode;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

@PageEntry(title = "Подтверждение прав пользователя")
public class BasicWizard extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(BonusesPage.class);
    WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//basics-wizard")
    private WebElement areaBasicWizard;

    @ElementTitle("В личный кабинет")
    @FindBy(xpath = "//*[contains(@class,'btn_important_blue-purple') and contains(@href,'private/user')]")
    private WebElement buttonLinkPrivate;

    public BasicWizard() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        workWithPreloader();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(areaBasicWizard));
    }

    @ActionTitle("выбирает способ подтверждения наличие доступа")
    public void chooseMethod(String keyForMethod){
        WebDriver driver = PageFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver,15);
        String value = Stash.getValue(keyForMethod).toString().replace("+","");
        LOG.info("Открывает список возможных способов подвтерждения прав пользователя");
        driver.findElement(By.xpath("//div[@class='bw-pane__select']//div[contains(@class,'custom-select__placeholder option')]")).click();
        WebElement scrollMethods = driver.findElement(By.xpath("//div[@class='bw-pane__select']//div[contains(@class,'custom-select-der scroll-contain')]"));
        wait.withMessage("Нажали на заголовок выпадающего списка, но список не раскрылся")
                .until(ExpectedConditions.attributeContains(scrollMethods,"class","expanded"));
        LOG.info("В выпавшем списке выбираем строчку = " + value);
        scrollMethods.findElement(By.xpath(".//span[translate(text(),'[ -+]*','')='" + value + "']")).click();
//        wait.withMessage("Выбрали нужную строчку в выпадающем списке, но выбранный способ подвтерждения прав = ")
//                .until(ExpectedConditions.attributeContains(scrollMethods.findElement(By.xpath("./preceding-sibling::div")),"innerText",value));

        Assert.assertTrue("Выбрали нужную строчку в выпадающем списке, но выбранный способ подвтерждения прав = " + scrollMethods.findElement(By.xpath("./preceding-sibling::div")).getAttribute("innerText"),
                scrollMethods.findElement(By.xpath("./preceding-sibling::div")).getAttribute("innerText").replaceAll("[ -]*[+]*","").equals(value));


        LOG.info("Нажимаем на кнопку 'ВЫСЛАТЬ ССЫЛКУ'");
        driver.findElement(By.xpath("//div[contains(@class,'btn_important_blue-purple')]")).click();
    }

    @ActionTitle("проверяет что страница находится на шаге")
    public void testStepPage(String step){
        By activeStepBy = By.xpath("//span[contains(@class,'regTopRowActiveStep')]");

        new WebDriverWait(driver,10)
                .until(ExpectedConditions.numberOfElementsToBe(activeStepBy,1));
        new WebDriverWait(driver,10)
                .withMessage("Ожидалось что страница находится на шаге " + step + ", а на самом деле на шаге " + driver.findElement(activeStepBy).getAttribute("innerText"))
                .until(ExpectedConditions.attributeContains(activeStepBy,"innerText",step));
    }

    @ActionTitle("проверяет что кнопка 'отправить ещё раз'")
    public void checkButtonAgainSend(String state){
        Boolean buttonIsActive = state.equals("активна");
        WebElement buttonSendAgain = driver.findElement(By.xpath("//div[contains(@class,'bw-pane__controls')]/button"));
        Assert.assertTrue("Кнопка 'Отправить ещё раз' в неправильном состоянии. Ожидалось, что она будет " + state + ", но это не так",
                buttonSendAgain.isEnabled()==buttonIsActive);
        if (!buttonIsActive){
            Assert.assertFalse("Кнопка не активна, как и ожидалось, но нет счётчика рядом, показывающим сколько секунд осталось до повторной попытки",
                    driver.findElements(By.xpath("//div[contains(@class,'bw-pane__controls')]/span")).isEmpty());
        }
    }

    @ActionTitle("открывает ссылку из письма")
    public void openLink(String keyEmail) throws InterruptedException {
        String email = Stash.getValue(keyEmail);
        String link = new String();
        try {
            link = YandexPostman.getLinkForAuthentication(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutotestError("Ошибка! Не смогли получить ссылку для аутентификации.");
        }
        driver.get(Stash.getValue("fullLink"));
    }

    @ActionTitle("вводит код из смс")
    public void inputSMSCode(String keyPhone,String correctOrNot){
        String code = getSMSCode(Stash.getValue(keyPhone));
        WebElement inputCode = driver.findElement(By.xpath("//input[contains(@class,'input_sms-code')]"));
        if (!correctOrNot.equals("правильный")){
            code = code.substring(0,3) + (Integer.valueOf(code.substring(3,4)) + 1);
        }
        inputCode.clear();
        inputCode.sendKeys(code);
    }

    @ActionTitle("ждет пока не закончится таймер и кнопка 'попробовать снова' станет активной")
    public void waitAndTryAgain(){
        LOG.info("До повторной попытки осталось : ");
        String sec = driver.findElement(By.xpath("//span[contains(@class,'pane__text_timer')]")).getAttribute("innerText");
        LOG.info( sec + "секунд");
        int time = Integer.valueOf(sec.split(":")[0]);
        time = time*60 + Integer.valueOf(sec.split(":")[1]);
        try {
            Thread.sleep((time+1)*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ActionTitle("вводит в поле почты адрес")
    public void inputNewEmail(String keyEmail){
        String email = Stash.getValue(keyEmail);
        WebElement inputEmail = driver.findElement(By.id("email"));
        inputEmail.clear();
        inputEmail.sendKeys(email);
        driver.findElement(By.xpath("//div[@class='bw-pane__controls']/button")).click();
    }

    @ActionTitle("проверяет есть ли сообщение об ошибке")
    public void checkErrorMessage(String hasError){
        Boolean has = hasError.equalsIgnoreCase("да");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<WebElement> allMessages = driver.findElements(By.xpath("//div[contains(@class,'bw-pane__errors')]/*"));
        for (WebElement mess: allMessages){
            LOG.info(":" + mess.getAttribute("innerText"));
        }
        Assert.assertFalse("Неправильно! Ждали наличие ошибок? - " + hasError,
                allMessages.isEmpty()==has);
    }


    @ActionTitle("проверяет на странице наличие текста")
    public void checkTextOnPage(String text){
        if (text.matches("[A-Z]*")){
            text = Stash.getValue(text);
        }
        new WebDriverWait(driver,10)
                .withMessage("На странице нет искомого текста : " + text)
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[contains(text(),'" + text + "')]"),0));
    }

    @ActionTitle("жмёт важную розово-голубую кнопку")
    public void cleckOnButtonWithtext(String text){
        driver.findElement(By.xpath("//button[contains(@class,'btn_important_blue-purple') and normalize-space(text())='" + text + "']")).click();
    }

}
