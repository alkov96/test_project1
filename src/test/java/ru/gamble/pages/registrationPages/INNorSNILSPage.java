package ru.gamble.pages.registrationPages;

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
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.Random;


@PageEntry(title = "ИНН или СНИЛС")
public class INNorSNILSPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(INNorSNILSPage.class);

    @FindBy(xpath = "//*[text()='ИНН или СНИЛС']")
    private WebElement pageTitle;

    @ElementTitle("ИНН")
    @FindBy(id = "innNumber")
    private WebElement innInput;

    @ElementTitle("СНИЛС")
    @FindBy(id = "snilsNumber")
    private WebElement snilsInput;

    @ElementTitle("Продолжить")
    @FindBy(id = "next_step")
    private WebElement continueButton;




    public INNorSNILSPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 30).until(ExpectedConditions.visibilityOf(pageTitle));
    }


    @ActionTitle("заполняет одно из двух полей")
    public void inputSNILSorINN(String keySNILS, String keyINN){
        int rnd = new Random().nextInt(2);
        if(rnd == 1){
            fillField(snilsInput,Stash.getValue(keySNILS));
        }else {
            fillField(innInput,Stash.getValue(keyINN));
        }
    }
}
