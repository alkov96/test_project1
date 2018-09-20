package ru.gamble.pages.mobile;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;

@PageEntry(title = "Укажите паспортные данные")
public class MobileSpecifyPassportDataPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileSpecifyPassportDataPage.class);

    @FindBy(xpath = "//div/h2[contains(.,'Укажите паспортные')]")
    private WebElement pageTitle;

    @ElementTitle("Серия и номер")
    @FindBy(id = "passport")
    private WebElement serialAndNumberInput;

    @ElementTitle("Дата выдачи")
    @FindBy(xpath = "//div[@class='datepicker__overlay']")
    private WebElement birthDateInput;

    @ElementTitle("Кем выдан")
    @FindBy(name = "passport_issuer")
    private WebElement issuedByInput;

    @ElementTitle("Код подразделения")
    @FindBy(xpath = "passport_issuer_code")
    private WebElement unitCodeInput;

    @ElementTitle("Мужской")
    @FindBy(xpath = "//input[@value='MALE']/following-sibling::label")
    private WebElement maleButton;

    @ElementTitle("Женский")
    @FindBy(xpath = "//input[@value='FEMALE']/following-sibling::label")
    private WebElement femaleButton;


    @ElementTitle("Место рождения")
    @FindBy(id = "birth_place")
    private WebElement birthPlaceInput;

    @ElementTitle("Регион")
    @FindBy(xpath = "region")
    private WebElement regionInput;

    @ElementTitle("Населенный пункт")
    @FindBy(id = "city")
    private WebElement cityInput;

    @ElementTitle("Улица")
    @FindBy(id = "street")
    private WebElement streetInput;

    @ElementTitle("Дом/владение")
    @FindBy(id = "house")
    private WebElement houseInput;


    @ElementTitle("Ok")
    @FindBy(xpath = "//a[contains(.,'Ok')]")
    private WebElement datePickerOkButton;

    @ElementTitle("Отмена")
    @FindBy(xpath = "//a[contains(.,'Отмена')]")
    private WebElement datePickerCancelButton;

}
