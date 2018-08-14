# language: ru
Функционал: 4_Главная страница

  @CheckAnnouncements_C36208
  Сценарий: Проверка раздела «Анонсы».

    * разлогиниваем пользователя

    * переходит на страницу 'https://888.ru'
    * открывается страница "Главная страница"
    * пользователь (нажимает кнопку) "Анонсы"

    * пользователь (проверяет смену цвета точек при нажатии на кнопку c) данными
      | НАПРАВЛЕНИЕ | КНОПКА         |
      | Вправо      | Стрелка-вправо |
      | Влево       | Стрелка-влево  |
    * пользователь (нажимает кнопку) "Анонсы"

    * открывается страница "Анонсы"
    * пользователь (проверяет наличие анонсов)

    #тест не запускается, так как нет блока "Анонсы"
