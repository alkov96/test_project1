# language: ru
Функционал: 4_Главная страница

  @bad
  @regress
  @HotBetsCoupon_С1051
  Сценарий: Главная страница. Добавление ставки в купон с виджета "Горячие ставки".

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (добавляет коэф с виджета в купон) "Горячие ставки"
    * пользователь осуществляет переход в "Купон"

    * открывается страница "Купон"
    * пользователь (проверяет, совпадают ли события в купоне с ожидаемыми)

    * пользователь (проверяет, совпадает ли исход в купоне с ожидаемым)
    * пользователь (сравнивает коэфиценты)

    #* закрываем браузер

