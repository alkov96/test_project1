package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
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

    /**
     * @return Возвращает случайно строку с полом Мужским или Женским
     */
    public static String randomGender(){
        return Constants.gender.get(new Random().nextInt(Constants.gender.size()));
    }

    /**
     * Метод возвращает случайную строку даты в вормате "yyyy-MM-dd"
     * от 18 до 100 лет назад
     * для использования как даты рождения
     */
    public static String generateDateInRequiredRange(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        int randomRange = 6571 + (int) (Math.random() * 26280);
        cal.add(Calendar.DAY_OF_YEAR, - randomRange);
        LOG.info(dateFormat.format(cal.getTime()));
        return dateFormat.format(cal.getTime());
    }

    /**
     * Метод возвращает случайную строку даты в вормате "yyyy-MM-dd"
     * от вчерашней даты до 1995 года если дата рождения на 1995 год была более 14 лет
     * и oт вчерашней даты до 14 лет от даты рождения если на 1995 год человеку было менее 14 лет
     */
    public static String generatePassportIssueDate(String birthDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Random random = new Random();
        Calendar cal = Calendar.getInstance();
        //Сохраняем текущую дату
        Date currentDate = cal.getTime();

        //Сохраняем нижнюю границу выдачи паспорта в России
        Date lowerBorderDate = dateFormat.parse("1995-01-01");

        //Cохраняем дату рождения
        Date date = dateFormat.parse(birthDate);

        //Устанавливаем и сохраняем дату рождения + 14 лет
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR,+14);
        Date birthDatePlusForthteen = dateFormat.parse(dateFormat.format(cal.getTime()));

        int minDay = (birthDatePlusForthteen.before(lowerBorderDate))
                ? (int) lowerBorderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
                : (int) birthDatePlusForthteen.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay();

        int maxDay = (int) currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay();
        int randomRange = minDay + random.nextInt(maxDay - minDay);
        LocalDate randomBirthDate = LocalDate.ofEpochDay(randomRange);

        return randomBirthDate.toString();
    }

}
