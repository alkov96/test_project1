# language: ru
Функционал: API

  @api
  @getVideoTranslationList
  @correct
  Сценарий: 3_28 Получение списка доступных видео трансляций. Позитивный кейс

    * запрос к API "api/mobile/v4/getVideoTranslationList" и сохраняем в "RESPONCE_API"

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":0,"data":{"videoTranslations": |

    * находим и сохраняем "DATA" из "RESPONCE_API"

    * проверка полей и типов в ответе "DATA":
      | Параметр          | Тип    |
      | videoTranslations | List   |
      | providerName      | String |
      | translationId     | String |