# language: ru
Функционал: 4_Главная страница

  @regress
  @BannerOnMain_C1041
  Сценарий: Главная страница. Проверка пролистывания баннеров на главном слайдере.
    * разлогиниваем пользователя

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * открывается страница "Главный Баннер"
    * пользователь (запоминает состояние главного баннера)

    * пользователь (ждет некоторое время) "3"
    * пользователь (сравнивает текущее состояние баннера со старым)

    * (закрываем браузер)