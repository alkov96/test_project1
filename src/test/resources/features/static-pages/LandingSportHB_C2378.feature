# language: ru
Функционал: 6_Статические страницы

  @regress
  @LandingSportHB_C2378
  Сценарий: Проверка блока Горячие ставки на странице лендинга спорта

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (ищет подходящий спорт в Горячих ставках)
    * пользователь (переходит на лендинг вида спорта)
    * пользователь (проверяет наличие блока Горячие ставки и переходит на игру)

    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"

    * пользователь (проверяет, совпадают ли события в купоне с ожидаемыми)
    * пользователь (проверяет, совпадает ли исход в купоне с ожидаемым)
    * пользователь (сравнивает коэфиценты)

    * (закрываем браузер)