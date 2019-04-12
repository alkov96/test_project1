#language: ru
Функционал: изменение базовых параметров через личный кабинет
  Предыстория:
    * сохраняем в память
      | PASSWORD  | Default |

    * поиск акаунта для проверки изменений базовых параметров "EMAIL"

    * генерим email в "NEWEMAIL"
    * поиск акаунта со статуом регистрации ">=9" "OLDEMAIL"

    * редактируем некоторые активные опции сайта
      |change_basic_parameter |true|

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | EMAIL  |
      | Пароль | PASSWORD  |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Иконка юзера"

    * открывается страница "Мини Личный Кабинет"
    * пользователь (переходит по ссылке) "Профиль"

  @regress
  @ChangeBasicParameter
  Сценарий: Проверка смены EMAIL через EMAIL

    * открывается страница "Профиль"
    * пользователь (нажимает кнопку) "Изменить email"
    * пользователь (подтверждает пароль) "PASSWORD"

    * открывается страница "Подтверждение прав пользователя"
    * пользователь (выбирает способ подтверждения наличие доступа) "EMAIL"
    * пользователь (проверяет что страница находится на шаге) "2"
    * пользователь (проверяет что кнопка 'отправить ещё раз') "заблокирована"
    * пользователь (открывает ссылку из письма) "EMAIL"

    * пользователь (проверяет что страница находится на шаге) "3"
    * пользователь (вводит в поле почты адрес) "OLDEMAIL"
    * пользователь (проверяет есть ли сообщение об ошибке) "да"
    * пользователь (вводит в поле почты адрес) "NEWEMAIL"
    * пользователь (проверяет есть ли сообщение об ошибке) "нет"

    * пользователь (открывает ссылку из письма) "NEWEMAIL"
    * пользователь (проверяет на странице наличие текста) "NEWEMAIL"
    * пользователь (проверяет на странице наличие текста) "Поздравляем"
    * пользователь (нажимает кнопку) "В личный кабинет"

    * открывается страница "Профиль"
    * пользователь (проверяет что поле содержит значение) "Электронная почта" "NEWEMAIL"


  @regress
  @ChangeBasicParameter
  Сценарий: Проверка смены EMAIL через PHONE

    * запоминаем значение "PHONE" для пользователя с "EMAIL"

    * открывается страница "Профиль"
    * пользователь (нажимает кнопку) "Изменить email"
    * пользователь (подтверждает пароль) "PASSWORD"

    * открывается страница "Подтверждение прав пользователя"
    * пользователь (выбирает способ подтверждения наличие доступа) "PHONE"
    * пользователь (проверяет что страница находится на шаге) "2"
    * пользователь (проверяет что кнопка 'отправить ещё раз') "заблокирована"
    * пользователь (вводит код из смс) "PHONE" "неправильный"
    * пользователь (жмёт важную розово-голубую кнопку) "Применить"
    * пользователь (проверяет есть ли сообщение об ошибке) "да"
    * пользователь (ждет пока не закончится таймер и кнопка 'попробовать снова' станет активной)
    * пользователь (жмёт важную розово-голубую кнопку) "Отправить еще раз"
    * пользователь (вводит код из смс) "PHONE" "правильный"
    * пользователь (жмёт важную розово-голубую кнопку) "Применить"

    * пользователь (проверяет что страница находится на шаге) "3"
    * пользователь (вводит в поле почты адрес) "OLDEMAIL"
    * пользователь (проверяет есть ли сообщение об ошибке) "да"
    * пользователь (вводит в поле почты адрес) "NEWEMAIL"
    * пользователь (проверяет есть ли сообщение об ошибке) "нет"

    * пользователь (открывает ссылку из письма) "NEWEMAIL"
    * пользователь (проверяет на странице наличие текста) "NEWEMAIL"
    * пользователь (проверяет на странице наличие текста) "Поздравляем"
    * пользователь (нажимает кнопку) "В личный кабинет"

    * открывается страница "Профиль"
    * пользователь (проверяет что поле содержит значение) "Электронная почта" "NEWEMAIL"

