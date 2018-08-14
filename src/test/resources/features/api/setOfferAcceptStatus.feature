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
      | devId  | DEVID    |
      | email  | USER     |
      | password   | PASSWORD     |
      | source | SOURCE   |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

  @api
  @setOfferAcceptStatus
  @correct
  Сценарий: 3_3	Подтверждение оферты

    * запрос к API "api/mobile/v3/setOfferAcceptStatus" и сохраняем в "RESPONCE_API":
      | devId             | DEVID     |
      | authToken         | AUTHTOKEN |
      | source            | SOURCE    |
      | offerAcceptStatus | 2         |

    * проверка ответа API из "RESPONCE_API":
      | exepted  |  "code":0  |