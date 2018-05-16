# language: ru
  Функционал: Добавление события в купон на странице События Дня. Сравнение коэфицента и суммы в купоне, и на банере и на странице. Проверка соответствия вида спорта и событий в нём.
    @smoke
    @DayEventsCoupon
      Сценарий: DayEventsCoupon
      * переходит на главную страницу
      * открывается страница "Главная страница"

      * пользователь (нажимает кнопку) "Бургер"
      * открывается страница "Бургер"

      * пользователь (нажимает кнопку) "События дня"
      * открывается страница "События дня"

      * пользователь (добавляет событие с баннера в купон)

      * пользователь (проверяет, совпадают ли названия событий на кнопках на баннере и сверху)
      * пользователь (кликает на три вида спорта и проверяет, что игры соответвтвуют выбранному виду)

      * пользователь осуществляет переход в "Купон"
      * открывается страница "Купон"

      * пользователь (проверяет, добавилось ли событие в купон)

      * пользователь (проверяет, совпадают ли события в купоне с ожидаемыми)
      * пользователь (проверяет, совпадает ли исход в купоне с ожидаемым)
      * пользователь (сравнивает коэфиценты)





