# language: ru
Функционал: Проверка лимитов пополнения

  @smoke
  @DepositLimits_C47804
  Сценарий: DepositLimits_C47804
    * переходит на главную страницу 'https://dev-bk-bet-site1.tsed.orglot.office'
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default |
      | Пароль | Default |
    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"
    * пользователь (запоминает значение баланса) "в рублях"

    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"

    * пользователь (нажимает кнопку) "Пополнение"
    * пользователь (смотрит, какие способы пополнения доступны)

    * пользователь (проверяет, что на попапе пополнения есть кнопки-ссылки сумм)
    * пользователь (проверяет смену допустимой макс.суммы при выборе пополнения с) данными
    | cupis_card    | 550000 |
    | cupis_wallet  | 550000 |
    | cupis_mts     | 14999  |
    | cupis_megafon | 15000  |
    | cupis_tele2   | 15000  |
    | cupis_beeline | 5000   |
    | cupis_stoloto | 550000 |


    * пользователь (проверяет, что при выборе суммы с помощью кнопок эта сумма правильно отображается на кнопке и в поле ввода)
    * пользователь (проверяет поведение попапа пополнения при вводе суммы меньше минимально допустимой)

    * пользователь (проверяет, что для разных способов пополнения, но при одинаковой сумме кнопка будет то активна, то заблокирована)
    * пользователь (открывает попап через кнопку 'Внести депозит')
    * (закрываем браузер)


