# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | USER  | Default |

    * сохраняем в память
      | PASS  | Default |

    * сохраняем в память
      | SOURCE | 16 |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | USER    |
      | pass   | PASS    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/getVideoTranslationList" и сохраняем в "RESPONCE_API"

    * находим и сохраняем "DATA" из "RESPONCE_API"

    * достаём случайную видеотрансляцию из списка "DATA" и сохраняем в переменую "GAME_ID"

    * достаём параметр из "GAME_ID" и сохраняем в переменую:
      | Параметр      | Переменная     |
      | gameId        | GAME_ID        |
      | providerName  | PROVIDER_NAME  |
      | translationId | TRANSLATION_ID |


  @api
  @getVideoTranslationLink
  @correct
  Сценарий: 3_29 Получение ссылки на определенную видео трансляцию от IMG. Позитивный кейс

    * запрос к API "api/mobile/v3/getVideoTranslationLink" и сохраняем в "RESPONCE_API":
    | authToken     | AUTHTOKEN      |
    | devId         | DEVID          |
    | source        | SOURCE         |
    | gameId        | GAME_ID        |
    | providerName  | PROVIDER_NAME  |
    | translationId | TRANSLATION_ID |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":0,"data":{"videoTranslation": |

    * находим и сохраняем "VIDEOTRANSLATION" из "RESPONCE_API"

#    * сохраняем в память
#      | VIDEOTRANSLATION | https://stream.betconstruct.tv:7791/927249/index.m3u8?token=2cc3b0ab44698366991fd6f68fa068a0-1531218960-325-16 |

    * запрос к IMG "VIDEOTRANSLATION" и сохраняем в "RESPONCE_IMG"

#    * проверка вариантов ответа
#
#    * проверка полей и типов в ответе "RESPONCE_IMG":
#      | Параметр     | Тип     |
#      | eventId      | Integer |
#      | rtmpUrl      | String  |
#      | hlsUrl       | String  |
#      | thumbnailUrl | String  |
#      | statusCode   | Integer |
#
#
#    * находим и сохраняем "DATA" из "RESPONCE_API"
#
#    * проверка полей и типов в ответе "DATA":
#      | Параметр          | Тип    |
#      | videoTranslations | List   |
#      | providerName      | String |
#      | translationId     | String |