package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
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
     * от '1997-10-01' до текущей даты в зависимости от текущего возраста пользователя
     */
    public static String generatePassportIssueDate(String birthDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Random random = new Random();
        Calendar cal = Calendar.getInstance();
        LocalDate randomBirthDate = null;

        int minDay;

        //Преобразуем текущую дату в LocalDate
        LocalDate currentDate = cal.getTime().toInstant().atZone(defaultZoneId).toLocalDate();

        //Преобразуем дату рождения в LocalDate
        LocalDate birthday = dateFormat.parse(birthDate).toInstant().atZone(defaultZoneId).toLocalDate();

        //Устанавливаем сколько полных лет человеку на текущую дату
        int fullYearsOld = Period.between(birthday, currentDate).getYears();

        //Преобразуем первый день перехода на современный Российский паспорт в LocalDate
        LocalDate firstDateRussinPassport = dateFormat.parse("1997-10-01").toInstant().atZone(defaultZoneId).toLocalDate();


        // Если ему 45 и более
        if(fullYearsOld >= 45){
            // Вычисляем дату 45-летия равную или позже 1997 года

            cal.set(birthday.getYear(),birthday.getMonthValue(),birthday.getDayOfMonth());
            cal.add(Calendar.DAY_OF_YEAR, +45);
            LocalDate dirthdayPlus45 = dateFormat.parse(dateFormat.format(cal.getTime())).toInstant().atZone(defaultZoneId).toLocalDate();

            minDay = (dirthdayPlus45.isBefore(firstDateRussinPassport))
                    ? (int) firstDateRussinPassport.toEpochDay()
                    : (int) dirthdayPlus45.toEpochDay();

            // Если ему от 20 до 45
        }else if(fullYearsOld < 45 && fullYearsOld >= 20){
            // Вычисляем дату 20-летия равную и позже 1997 года
            cal.set(birthday.getYear(),birthday.getMonthValue(),birthday.getDayOfMonth());
            cal.add(Calendar.DAY_OF_YEAR, +20);
            LocalDate dirthdayPlus20 = dateFormat.parse(dateFormat.format(cal.getTime())).toInstant().atZone(defaultZoneId).toLocalDate();

            minDay = (dirthdayPlus20.isBefore(firstDateRussinPassport))
                    ? (int) firstDateRussinPassport.toEpochDay()
                    : (int) dirthdayPlus20.toEpochDay();

        }else {
            // Вычисляем дату 14-летия
            cal.set(birthday.getYear(),birthday.getMonthValue(),birthday.getDayOfMonth());
            cal.add(Calendar.DAY_OF_YEAR, +14);
            LocalDate dirthdayPlus14 = dateFormat.parse(dateFormat.format(cal.getTime())).toInstant().atZone(defaultZoneId).toLocalDate();

            minDay = (int) dirthdayPlus14.toEpochDay();
        }
            int maxDay = (int) currentDate.toEpochDay();
            int randomRange = minDay + random.nextInt(maxDay - minDay);
            randomBirthDate = LocalDate.ofEpochDay(randomRange);

        return randomBirthDate.toString();
    }

    public static String generateCardNumber(){
        List<String> numberTypeCard = Arrays.asList("4","5","6");
        String startNumber = numberTypeCard.get(new Random().nextInt(numberTypeCard.size()));
        String numberWithoutLastDigest = startNumber + randomNumber(14);
        String cardNumber = "";
        int[] digits = new int[16];
        for(int i = 0; i < 10; i++){
            cardNumber = numberWithoutLastDigest + i;
            digits = cardNumber.chars().map(c -> c -= '0').toArray();
            if(check(digits)){break; };
        }
       return cardNumber;
    }

    private static boolean check(int[] digits) {
        int sum = 0;
        int length = digits.length;

        for (int i = 0; i < digits.length; i++) {

            // get digits in reverse order
            int digit = digits[length - i - 1];

            // every 2nd number multiply by 2
            if (i % 2 == 1) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit;
        }
        return sum % 10 == 0;
    }

    /**
     * Метод возвращает случайную строку даты в вормате "MM/yy"
     * от 1 до 97 лет вперёд
     * для использования как даты действия карты
     */
    public static String generateDateForGard(){
        DateFormat dateFormat = new SimpleDateFormat("MM/yy");
        Calendar cal = Calendar.getInstance();
        int randomRange = 366 + (int) (Math.random() * 35040);
        cal.add(Calendar.DAY_OF_YEAR, + randomRange);
        LOG.info(dateFormat.format(cal.getTime()));
        return dateFormat.format(cal.getTime());
    }

    /**
     * Случайная строка латинских больших букв
     *
     * @param len - максимальная длина строки. но может быть и меньше
     * @return возвращает получившуюся строку
     */
    public static String randomBigLatinString(int len) {

        StringBuilder result = new StringBuilder();
        int count = (int) (1 + Math.random() * len);
        for (int i = 0; i <= count; i++) {
            result.append((char) ('A' + new Random().nextInt(26)));
        }
        return result.toString();
    }
}
