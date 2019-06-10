# language: ru
Функционал: API
  Предыстория:

    * сохраняем в память
      | DEVID  | randomNumber 4 |
  @enabledFeatures
  @api
  @getActiveSiteOptions
  Сценарий: Проверка запроса getActiveSiteOptions при выключенных опциях

    * редактируем некоторые активные опции сайта
      |fast_registration  | false  |

    * запрос к API "api/mobile/v8/getActiveSiteOptions" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "fast_registration", "state": 0 |


  @enabledFeatures
  @api
  @getActiveSiteOptions
  Сценарий: Проверка запроса getActiveSiteOptions при включенной fast_registration

    * редактируем некоторые активные опции сайта
      |fast_registration  | true  |

    * запрос к API "api/mobile/v8/getActiveSiteOptions" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "fast_registration", "state": 1 |
