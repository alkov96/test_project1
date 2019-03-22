# language: ru
  Функционал: 3_Ставки

    @regress
    @DayEventsCoupon
    @coupon

    Сценарий: Добавление события в купон на странице "События Дня". Сравнение коэфицента и суммы в купоне, и на банере и на странице. Проверка соответствия вида спорта и событий в нём.

      * разлогиниваем пользователя
      * открывается страница "Главная страница"

      * пользователь (нажимает кнопку) "Бургер"
      * открывается страница "Бургер"

      * пользователь (нажимает кнопку) "События дня"
      * открывается страница "События дня"
      * пользователь (очищает купон)

      * пользователь (добавляет событие с баннера в купон и сохраняет в память) "keyTeam1" "keyTeam2" "keyKoef"

      * пользователь (проверяет, совпадают ли названия событий на кнопках на баннере и сверху c) "keyTeam1" "keyTeam2"
      * пользователь (кликает на три вида спорта и проверяет, что игры соответвтвуют выбранному виду)

      * пользователь осуществляет переход в "Купон"
      * открывается страница "Купон"

      * пользователь (проверяет, добавилось ли событие в купон)
      * пользователь (проверяет, совпадают ли события в купоне с ожидаемыми из) "keyTeam1" "keyTeam2"

      * пользователь (сравнивает коэфиценты) "keyKoef"





