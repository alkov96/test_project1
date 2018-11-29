# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | PASSWORD  | Default |
    * сохраняем в память
      | DEVID  | randomNumber 4 |
    * сохраняем в память
      | STREET  | "Волгоградский проспект" |
    * сохраняем в память
      | FLAT  | randomNumber 3 |
    * сохраняем в память
      | HOUSE  | randomNumber 2 |
    * сохраняем в память
      | COMMENT  | random |


  @DD_identification
  Сценарий: Отправка заявки в Домашние Деньги и проверка статуса заявки

    * ищем пользователя с ограничениями "ALLROWS"

    * определяем дату завтрашнего дня "DATE"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID    |
      | email       | EMAIL    |
      | pass        | PASSWORD |
      | source      | 16       |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":8 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | street       | "Тверская ул." |
      | house        | HOUSE          |
      | building     |                |
      | housing      |                |
      | flat         | FLAT           |
      | phone        | PHONE     |
      | comment      | COMMENT        |
      | date         | DATE           |
      | time         | "10:00 - 17:00"|

    * запрос к API "api/mobile/v5/createDostavistaOrder " и сохраняем в "RESPONCE_API":
      | devId       | DEVID    |
      | authToken   | AUTHTOKEN|
      | source      | 16       |
      | data        | DATA     |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | identificationStatus":1 |

    * получаем и сохраняем в память все строки для достависты телефона "PHONE"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | recipient_id            | RECIPIENTID                     |
      | partner_order_id        | PARTNERORDERID                  |
      | delivery_address        | "Тверская ул."                   |
      | delivery_time_start     | "2018-11-15T20:00:00+03:00"      |
      | delivery_time_finish    | "2018-11-15T21:00:00+03:00"      |


    * запрос к esb "tasktype_endpoint/partner_notification" и сохраняем в "RESPONCE_API":

      | event_type   | "recipient_agreed"              |
      | event_date   | "2018-11-15T15:05:23+03:00"     |
      | data         | DATA                            |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * добавляем данные в JSON объект "COURIER" сохраняем в память:
      | phone                   | "88005553535"                     |
      | name                    | "Мистер Курьер"                   |

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | recipient_id            | RECIPIENTID                      |
      | partner_order_id        | PARTNERORDERID                   |
      | order_id                | 649326                           |
      | courier                 | COURIER                          |


    * запрос к esb "tasktype_endpoint/partner_notification" и сохраняем в "RESPONCE_API":

      | event_type   | "recipient_courier_assigned"    |
      | event_date   | "2018-11-15T15:05:23+03:00"     |
      | data         | DATA                            |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * получаем и сохраняем в память все строки для достависты телефона "PHONE"

    * смотрим изменился ли статус "EVENTTYPE" на "RECIPIENT_COURIER_ASSIGNED"


    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | recipient_id            | RECIPIENTID                      |
      | partner_order_id        | PARTNERORDERID                   |
      | order_id                | 649326                           |


    * запрос к esb "tasktype_endpoint/partner_notification" и сохраняем в "RESPONCE_API":

      | event_type   | "recipient_pack_verified_by_courier"    |
      | event_date   | "2018-11-15T15:05:23+03:00"             |
      | data         | DATA                                    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |


    * получаем и сохраняем в память все строки для достависты телефона "PHONE"

    * смотрим изменился ли статус "EVENTTYPE" на "RECIPIENT_PACK_VERIFIED_BY_COURIER"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | recipient_id            | RECIPIENTID                      |
      | partner_order_id        | PARTNERORDERID                   |


    * запрос к esb "tasktype_endpoint/partner_notification" и сохраняем в "RESPONCE_API":

      | event_type   | "recipient_completed"    |
      | event_date   | "2018-11-15T15:05:23+03:00"             |
      | data         | DATA                                    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |


    * получаем и сохраняем в память все строки для достависты телефона "PHONE"

    * смотрим изменился ли статус "EVENTTYPE" на "RECIPIENT_COMPLETED"

#    * запрос к API "api/mobile/v5/identificationDDStatus" и сохраняем в "RESPONCE_API":
#      | devId       | DEVID    |
#      | authToken   | AUTHTOKEN|
#      | source      | 16       |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | "code":0 |
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | identificationStatus":1 |
