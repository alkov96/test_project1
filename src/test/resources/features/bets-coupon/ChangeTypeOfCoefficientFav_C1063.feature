# language: ru
Функционал: Изменение типа отображения коэффицентов в избранном
  @smoke
  @bets-coupon
  @ChangeTypeOfCoefficientFav_C1066
  Сценарий: ChangeTypeOfCoefficientFav_C1066
    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"

    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"

    * пользователь (нажимает кнопку) "Бургер"
    * открывается страница "Бургер"

    * пользователь (нажимает кнопку) "События дня"
    * открывается страница "События дня"

    * пользователь (добавляет событие в избранное)

    * пользователь (открывает Избранное)
    * открывается страница "Избранное"

    * пользователь (переходит в настройки и меняет коэффицент в избранном)