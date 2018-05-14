package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Generators {
    private static final Logger LOG = LoggerFactory.getLogger(Generators.class);

    /**
     * Генератор
     *
     * @param len - максимальная длина строки.
     * @return возвращает получившуюся строку
     */
    public static String randomNumber(int len) {

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            result.append(new Random().nextInt(10));
        }
        return result.toString();
    }

    /**
     * Генератор даты рождения
     *
     * @param key - ключ по которому сохраняем дату в памяти.
     */
    @ActionTitle("генерит дату рождения в")
    public static void generateBirthday(String key){

        Calendar calendar = Calendar.getInstance();

        DateFormat df = new SimpleDateFormat("dd MMMM yyyy");


        String value = "testregistrator+" + System.currentTimeMillis() + "@yandex.ru";
        LOG.info("Сохраняем в память (ключ):" + key + ":(значение)::" + value);
        Stash.put(key,value);
    }

    /**
     * Случайная строка русских символов (и большие и маленькие буквы сразу)
     *
     * @param len - максимальная длина строки. но может быть и меньше
     * @return возвращает получившуюся строку
     */
    public static String randomString(int len) {

        StringBuilder result = new StringBuilder();
        int count = (int) (1 + Math.random() * len);
        for (int i = 0; i <= count; i++) {
            result.append((char) ('А' + new Random().nextInt(64)));
        }
        return result.toString();
    }

    /**
     * @return Возвращает случайно строку с полом Мужским или Женским
     */
    public static String randomSex(){
        return Constants.sex.get(new Random().nextInt(Constants.sex.size()));
    }

    /**
     * @return Возвращает случайно строку с городами
     */
    public static String randomCity(){
        return Constants.cities.get(new Random().nextInt(Constants.cities.size()));
    }

    /**
     * Генератор e-mail
     *
     * @param key - ключ по которому сохраняем е-mail в памяти.
     */
    @ActionTitle("генерит email в")
    public void generateEmailAndSave(String key){
        String value = "testregistrator+" + System.currentTimeMillis() + "@yandex.ru";
        LOG.info("Сохраняем в память (ключ):" + key + ":(значение)::" + value);
        Stash.put(key,value);
    }

}
