# language: ru
Функционал: 6_Статические страницы

  @bad
  @regress
  @LandingAppImage_C1065
  Сценарий: Проверка блока "Все как вы любите", прогрузки картинок и лендинга.

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Бургер"
    * открывается страница "Бургер"

    * пользователь (нажимает кнопку) "Приложения для iOS и Android"

    * открывается страница "Приложения для iOS и Android"
    * (проверка блока %Все как любите)

    * (проверка того, что все нужные картинки прогрузились и есть футер)