# language: ru
Функционал: API

  Предыстория:
    * сохраняем в память
      | DEVID  | randomNumber 4 |


  @api
  @isUpdateNeccessary
  @correct
  Сценарий: Проверка нужно ли обновление версии приложения на Android
    * обновим версию мобильного приложения для "Android" "TYPEOS" до "3.4" "3.0"
    * запрос к API "api/mobile/v3/isUpdateNeccessary" и сохраняем в "RESPONCE_API":
      | typeOS                  | TYPEOS        |
      | appVersion              | 3.5           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "updateStatus":0 |

    * запрос к API "api/mobile/v3/isUpdateNeccessary" и сохраняем в "RESPONCE_API":
      | typeOS                  | TYPEOS        |
      | appVersion              | 3.3         |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "updateStatus":1 |


    * запрос к API "api/mobile/v3/isUpdateNeccessary" и сохраняем в "RESPONCE_API":
      | typeOS                  | TYPEOS        |
      | appVersion              | 2.9           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "updateStatus":2 |


  @api
  @isUpdateNeccessary
  @correct
  Сценарий: Проверка нужно ли обновление версии приложения на IOS
    * обновим версию мобильного приложения для "IOS" "TYPEOS" до "3.4" "3.0"
    * запрос к API "api/mobile/v3/isUpdateNeccessary" и сохраняем в "RESPONCE_API":
      | typeOS                  | TYPEOS        |
      | appVersion              | 3.5           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "updateStatus":0 |

    * запрос к API "api/mobile/v3/isUpdateNeccessary" и сохраняем в "RESPONCE_API":
      | typeOS                  | TYPEOS        |
      | appVersion              | 3.3           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "updateStatus":1 |


    * запрос к API "api/mobile/v3/isUpdateNeccessary" и сохраняем в "RESPONCE_API":
      | typeOS                  | TYPEOS        |
      | appVersion              | 2.9           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "updateStatus":2 |
