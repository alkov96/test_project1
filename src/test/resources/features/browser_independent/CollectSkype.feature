# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | SOURCE | 16 |

  @browserIndependent
  Сценарий: COLLECT_SKYPE_AWAIT_DATA с двумя параметрами
    * формирум параметры и сохраняем в "PARAMS"
    | ts      | прошлый месяц       |
    | ts_end  | текущая дата-время  |
    * запрос типа COLLECT "api/get_skype_await_list" c параметрами "PARAMS" и сохраняем в "RESPONSE"
    * проверка что в ответе "RESPONSE" верные даты  "PARAMS":
    * проверка что ответ "RESPONSE" "не пустой"

    * формирум параметры и сохраняем в "PARAMS"
      | ts      | следующий месяц     |
      | ts_end  | текущая дата-время  |
    * запрос типа COLLECT "api/get_skype_await_list" c параметрами "PARAMS" и сохраняем в "RESPONSE"
    * проверка что ответ "RESPONSE" "пустой"

    * формирум параметры и сохраняем в "PARAMS"
      | ts      | прошлый месяц     |
    * запрос типа COLLECT "api/get_skype_await_list" c параметрами "PARAMS" и сохраняем в "RESPONSE"
    * проверка что ответ "RESPONSE" "не пустой"
    * проверка что в ответе "RESPONSE" верные даты  "PARAMS":