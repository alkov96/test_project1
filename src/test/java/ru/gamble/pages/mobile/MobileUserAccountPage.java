package ru.gamble.pages.mobile;

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
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

@PageEntry(title = "Учетная запись")
public class MobileUserAccountPage extends AbstractPage {

    private static final Logger LOG = LoggerFactory.getLogger(MobileUserAccountPage.class);

    @FindBy(xpath = "//div/h2[contains(.,'Учетная запись')]")
    private WebElement pageTitle;

    @ElementTitle("Дата рождения")
    @FindBy(xpath = "//div[contains(@class,'datepicker__main')]")
    private WebElement birthDateInput;

    @ElementTitle("Фамилия")
    @FindBy(id = "surname")
    private WebElement surnameInput;

    @ElementTitle("Имя")
    @FindBy(id = "first_name")
    private WebElement firstNameInput;

    @ElementTitle("Отчество")
    @FindBy(id = "patronymic")
    private WebElement patronymicInput;

    @ElementTitle("Эл. почта")
    @FindBy(id = "email")
    private WebElement emailInput;

    @ElementTitle("Укажите пароль")
    @FindBy(name = "password")
    private WebElement passwordInput;


    public MobileUserAccountPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("нажимает в поле ввода")
    public void clickInputField(String inputFieldName){
        pressButtonAP(inputFieldName);
    }
}
