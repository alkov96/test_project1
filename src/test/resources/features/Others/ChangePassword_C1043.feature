# language: ru
Функционал: Изменение пароля

  @smoke
  @ChangePassword_C1043
  Сценарий: ChangePassword_C1043
    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"

    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | bladekeeper@ya.ru |
      | Пароль | 1qwert2ASDF       |

    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"
    * пользователь (нажимает кнопку) "Личный кабинет"
    * пользователь осуществляет переход в "Профиль"
    * открывается страница "Профиль"
    * пользователь (меняет пароль в Личном кабинете)
    * пользователь (нажимает кнопку) "Иконка юзера"

    * открывается страница "Мини Личный Кабинет"
    * пользователь (нажимает кнопку) "Выйти"

    * открывается страница "Главная страница"
    * пользователь (нажимает кнопку) "Вход"

    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | bladekeeper@ya.ru |
      | Пароль | 1qwert2ASDF       |

    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"





