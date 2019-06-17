package ru.gamble.utility;

import ru.sbtqa.tag.qautils.properties.Props;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String LOGIN = "Логин";
    public static final String PASSWORD = "Пароль";
    public static final String DEFAULT = "Default";
    public static final String PERIOD = "Период";
    public static final String RANDOM = "random";
    public static final String RANDOMHEX = "randomHex";
    public static final String RANDOMDATE = "randomDate";
    public static final String SERIAL = "Серия";
    public static final String NUMBER = "Номер";
    public static final String DATEISSUE = "Дата выдачи";
    public static final String ISSUEDBY = "Кем выдан";
    public static final String UNITCODE = "Код подразделения";
    public static final String SEX = "Пол";
    public static final String PLACEOFBIRTH = "Место рождения";
    public static final String REGION = "Регион";
    public static final String LOCALITY = "Нас. пункт";
    public static final String STREET = "Улица";
    public static final String HOUSE = "Дом/владение";
    public static final String FLAT = "Квартира";
    public static final String DATEOFBIRTH = "Дата рождения";
    public static final String EMAIL = "E-mail";
    public static final String NUMBERPHONE = "Номер телефона";
    public static final String TRUE = "true";
    public static final String LINK = "ССЫЛКА";
    public static final String TEXT = "ТЕКСТ";
    public static final String ELEMENT = "ЭЛЕМЕНТ";
    public static final String DIRECTION = "НАПРАВЛЕНИЕ";
    public static final String BUTTON = "КНОПКА";
    public static final String RANDOME_PHONE = "randomPhone";
    public static final String RANDOME_EMAIL = "randomEmail";
    public static final String RANDOME_NUMBER = "randomNumber";
    public static final String RANDOME_SEX = "randomSex";
    public static final String STARTING_URL = Props.get("webdriver.necessary.url");
    public static final String PARAMETER = "Параметр";
    public static final String TYPE = "Тип";
    public static final String VALUE = "Значение";
    public static final String VARIABLE = "Переменная";
    public static final String PHONE = "Телефон";
    public static final String INPUT_FIELD = "Поле ввода";
    public static final String SAVE_VALUE = "Переменная сохранения";
    public static final String NAME = "Имя";
    public static final String LASTNAME = "Фамилия";
    public static final String PATERNALNAME = "Отчество";
    public static final String AMOUNT = "СУММА";
    public static final String PAYMENT_METHOD = "Способ";
    public static final String PHONE_PATTERN = "7933";

    public static final List<String> cities = Arrays.asList("Москва","Санкт-Петербург","Новосибирск","Екатеринбург","Нижний Новгород","Казань","Челябинск","Омск","Самара","Ростов-на-Дону","Уфа","Красноярск","Пермь","Воронеж","Волгоград");
    public static final List<String> sex = Arrays.asList("Мужской","Женский");
    public static final List<String> gender = Arrays.asList("MALE","FEMALE");

}

