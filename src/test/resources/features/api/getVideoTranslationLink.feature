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

    * находим и сохраняем "VIDEOTRANSLATIONS" из "RESPONCE_API"

    * достаём из "VIDEOTRANSLATION" параметр и сохраняем в переменую:
    | Параметр      | Переменная      |
    | key           | GAME_ID         |
    | providerName  | PROVIDER_NAME   |
    | translationId | TRANSLATION_ID  |



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
      | exepted | {"code":0,"data":{"videoTranslations": |

    * находим и сохраняем "DATA" из "RESPONCE_API"

    * проверка полей и типов в ответе "DATA":
      | Параметр          | Тип    |
      | videoTranslations | List   |
      | providerName      | String |
      | translationId     | String |