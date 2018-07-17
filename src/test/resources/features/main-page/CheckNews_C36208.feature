# language: ru
Функционал: 4_Главная страница

  @regress
  @CheckNews_C36208
  Сценарий: Проверка раздела «Новости».
    * разлогиниваем пользователя

    * переходит на главную страницу
    * переходит на страницу 'https://888.ru'
    * открывается страница "Главная страница"

#    * пользователь (проверяет наличие обязательных разделов новостей с) данными
#      | НОВОСТИ | АНОНСЫ | ВСЕ |
    * пользователь (проверяет что дайджест новостей не пустой)

    * пользователь (проверяет смену цвета точек при нажатии на кнопку c) данными
    | НАПРАВЛЕНИЕ | КНОПКА         |
    | Вправо      | Стрелка-вправо |
    | Влево       | Стрелка-влево  |
    * пользователь (нажимает кнопку) "Новости"

    * открывается страница "Новости"
    * пользователь (проверяет наличие верхней линейки вкладок)

    * пользователь (проверяет наличие дайжеста новостей на имеющихся вкладках)
    * пользователь (нажимает кнопку) "На главную"

    * открывается страница "Главная страница"
    * пользователь (нажимает кнопку) "Анонсы"

    * пользователь (проверяет смену цвета точек при нажатии на кнопку c) данными
    | НАПРАВЛЕНИЕ | КНОПКА         |
    | Вправо      | Стрелка-вправо |
    | Влево       | Стрелка-влево  |
    * пользователь (нажимает кнопку) "Анонсы"

    * открывается страница "Анонсы"
    * пользователь (проверяет наличие анонсов)

    * (закрываем браузер)








