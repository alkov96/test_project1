# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | PHONE  | randomPhone |

    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | USER  | Default |


    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | USER  |
      | password   | PASSWORD  |
      | source | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data":{ |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "responceAPI":
      | devId | DEVID |
      | phone | PHONE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код подтверждения "CODE" телефона "PHONE" ""

  @confirmPhone
  @correct
  Сценарий: 3_10 Подтверждение телефона. Позитивный кейс

    * запрос к API "api/mobile/v3/confirmPhone" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | 16        |
      | сode      | CODE      |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data":{" |

  @fail
  Сценарий: 3_10 Подтверждение телефона. Негативный кейс
    * запрос к API "api/mobile/v3/confirmPhone" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | 16        |
      | сode      | 0         |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":9,"data":{"message":"code incorrect" |
