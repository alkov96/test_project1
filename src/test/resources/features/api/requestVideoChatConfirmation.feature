# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |
    * сохраняем в память
      | USER  | testregistrator+1534949443746@yandex.ru |
    * сохраняем в память
      | PASSWORD  | Parol123 |
    * сохраняем в память
      | SOURCE | 16 |

  @api
  @requestVideoChatConfirmation
  @correct
  Сценарий: Запрос на видеоидентификацию для пользователя ожидающего звонок

    * поиск акаунта со статуом регистрации "=17" "EMAIL"

#    * добавляем активную опцию сайта "video_identification_in_mobile_app"

    * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
      |video_identification_in_mobile_app|true|

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID    |
      | email  | EMAIL    |
      | pass   | PASSWORD |
      | source | SOURCE   |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v5/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * проверка полей и типов в ответе "DATA":
      | Параметр      | Тип    |
      | videochatLink | String |


  @api
  @requestVideoChatConfirmation
  @incorrect
  Сценарий: Запрос на видеоидентификацию для полностью зарегистрированного пользователя

    * поиск акаунта со статуом регистрации "=2" "EMAIL"

        * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
      |video_identification_in_mobile_app|true|

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | EMAIL    |
      | pass   | PASSWORD    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v5/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":12 |



  @api
  @requestVideoChatConfirmation
  @incorrect
  Сценарий: Запрос на видеоидентификацию с неправильного устройства

    * поиск акаунта со статуом регистрации "=17" "EMAIL"

        * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
      |video_identification_in_mobile_app|true|

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | EMAIL    |
      | pass   | PASSWORD    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v5/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | 42    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":45 |



  @api
  @requestVideoChatConfirmation
  @incorrect
  Сценарий: Запрос на видеоидентификацию при выключенной настройке

    * поиск акаунта со статуом регистрации "=17" "EMAIL"

        * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
      |video_identification_in_mobile_app|false|

    * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
    |video_identification_in_mobile_app|false|

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | EMAIL    |
      | pass   | PASSWORD    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v5/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | 42    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":44 |




