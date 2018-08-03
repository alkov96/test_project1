# language: ru
Функционал: 2_Пополнение и вывод

  @regress
  @DepositLimits_C47804
  Сценарий: Проверка лимитов пополнения.

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default |
      | Пароль | Default |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (запоминает значение баланса) "в рублях"

    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"

    * пользователь (нажимает кнопку) "Пополнение"
    * пользователь (смотрит, какие способы пополнения доступны)

    * пользователь (проверяет, что на попапе пополнения есть кнопки-ссылки сумм)
    * пользователь (проверяет смену допустимой макс.суммы при выборе пополнения с) данными
    | cupis_card         | 58000   |
    | cupis_wallet       | 100     |
    | cupis_mts          | 14999   |
    | cupis_megafon      | 5000    |
    | cupis_tele2        | 15000   |
    | cupis_beeline      | 5000    |
    | cupis_stoloto      | 15000   |
    | cupis_qiwi         | 25000   |
    | cupis_card_ok_fail | 5000    |
    | cupis_card_fail    | 5000    |
    | cupis_wallet_fail  | 5000    |
    | cupis_wallet_ok    | 8000    |
    | cupis_card_ok      | 2000    |
    | cupis_bpa_888_in   | 58000   |
    | cupis_ym           | 58000   |

    * пользователь (проверяет, что при выборе суммы с помощью кнопок эта сумма правильно отображается на кнопке и в поле ввода)
    * пользователь (проверяет поведение попапа пополнения при вводе суммы меньше минимально допустимой)

    * пользователь (проверяет поведение попапа пополнения при вводе суммы больше максимально допустимой)
    * пользователь (проверяет, что для разных способов пополнения, но при одинаковой сумме кнопка будет то активна, то заблокирована)

    * пользователь (открывает попап через кнопку 'Внести депозит')
    #* закрываем браузер


