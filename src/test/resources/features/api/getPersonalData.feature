# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | USER  | Default |

    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | SOURCE | 16 |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID  |
      | email  | USER   |
      | password   | PASSWORD   |
      | source | SOURCE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

  @api
  @getPersonalData
  @correct
  Сценарий: 3_4 Запрос персональных данных. Позитивный кейс
    * запрос к API "api/mobile/v3/getPersonalData" и сохраняем в "RESPONCE_API":
      | devId     | 1         |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":0,"data":{"personalData": |
