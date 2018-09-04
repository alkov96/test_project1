# language: ru
Функционал: 2_Пополнение и вывод
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | USER  | Default |

    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | SOURCE | 16 |

    * сохраняем в память
      | RID | 15355431498522 |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId    | DEVID    |
      | email    | USER     |
      | pass     | PASSWORD |
      | source   | SOURCE   |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * добавляем данные в JSON объект "PARAMS" сохраняем в память:
      | auth_token | AUTHTOKEN |

    * запрос к WSS "wss://swarm-test.betfavorit.cf:8443/" и сохраняем в "RESPONCE_WSS":
    | command | restore_login |
    | params  | PARAMS        |
    | rid     | RID           |


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

    * пользователь (нажимает кнопку) "Внести депозит"
    * открывается страница "Пополнение счёта"

    * запрашиваем WSS

#    * пользователь (смотрит, какие способы пополнения доступны)
    * пользователь (проверяет, что на попапе пополнения есть кнопки-ссылки сумм)

    * запрашиваем параметры способов пополения и сохраняем в память как "DEPOSIT_METHODS_JSON"

    * пользователь (проверяет коректность смены макс.суммы при выборе пополнения с) "DEPOSIT_METHODS_JSON"


#    * пользователь (проверяет смену допустимой макс.суммы при выборе пополнения с) данными
#    | cupis_card         | 70000   |
#    | cupis_wallet       | 100     |
#    | cupis_mts          | 14999   |
#    | cupis_megafon      | 5000    |
#    | cupis_tele2        | 15000   |
#    | cupis_beeline      | 5000    |
#    | cupis_stoloto      | 15000   |
#    | cupis_qiwi         | 25000   |
#    | cupis_card_ok_fail | 5000    |
#    | cupis_card_fail    | 5000    |
#    | cupis_wallet_fail  | 5000    |
#    | cupis_wallet_ok    | 8000    |
#    | cupis_card_ok      | 60000    |
#    | cupis_bpa_888_in   | 550000   |
#    | cupis_ym           | 2500   |



    * пользователь (проверяет, что при выборе суммы с помощью кнопок эта сумма правильно отображается на кнопке и в поле ввода)
    * пользователь (проверяет поведение попапа пополнения при вводе суммы меньше минимально допустимой)

    * пользователь (проверяет поведение попапа пополнения при вводе суммы больше максимально допустимой)
    * пользователь (проверяет, что для разных способов пополнения, но при одинаковой сумме кнопка будет то активна, то заблокирована)

    * пользователь (открывает попап через кнопку 'Внести депозит')
    * закрываем браузер


