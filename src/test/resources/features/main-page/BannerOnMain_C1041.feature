# language: ru
Функционал: 4.Главная страница.

  @smoke
  @BannerOnMain_C1041
  Сценарий: Главная страница. Проверка пролистывания баннеров на главном слайдере.

    * переходит на главную страницу 'https://dev-bk-bet-site1.tsed.orglot.office'
    * открывается страница "Главная страница"

    * открывается страница "Главный Баннер"
    * пользователь (запоминает состояние главного баннера)

    * пользователь (ждет некоторое время) "3"
    * пользователь (сравнивает текущее состояние баннера со старым)

    * (закрываем браузер)