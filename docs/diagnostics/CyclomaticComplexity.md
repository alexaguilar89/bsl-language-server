# Цикломатическая сложность (CyclomaticComplexity)

| Тип | Поддерживаются<br/>языки | Важность | Включена<br/>по умолчанию | Время на<br/>исправление (мин) | Тэги |
| :-: | :-: | :-: | :-: | :-: | :-: |
| `Дефект кода` | `BSL`<br/>`OS` | `Критичный` | `Да` | `25` | `brainoverload` |

## Параметры 

| Имя | Тип | Описание | Значение по умолчанию |
| :-: | :-: | :-- | :-: |
| `complexityThreshold` | `Целое` | ```Допустимая цикломатическая сложность метода``` | ```20``` |
| `checkModuleBody` | `Булево` | ```Проверять тело модуля``` | ```true``` |

<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики
<!-- Описание диагностики заполняется вручную. Необходимо понятным языком описать смысл и схему работу -->

Цикломатическая сложность программного кода является одной из наиболее старых метрик, впервые она была упомянута в 1976 году Томасом МакКэбом.  
Цикломатическая сложность показывает минимальное число необходимых тестов.
Наиболее эффективным способом снижения цикломатической сложности является декомпозиция кода, дробление методов на более простые, а также оптимизация логических выражений.

Цикломатическая сложность увеличивается на 1 за каждую конструкцию

- `Для ... По .. Цикл`
- `Для каждого ... Из ... Цикл`
- `Если ... Тогда`
- `ИначеЕсли ... Тогда`
- `Иначе`
- `Попытка ... Исключение ... КонецПопытки`
- `Перейти ~Метка`
- Бинарные операции `И ... ИЛИ`
- Тернарный оператор
- `Процедура`
- `Функция`

## Примеры
<!-- В данном разделе приводятся примеры, на которые диагностика срабатывает, а также можно привести пример, как можно исправить ситуацию -->

```bsl
Функция СерверныйМодульМенеджера(Имя)                                                   // 1
	ОбъектНайден = Ложь;                                                                // 0
                                                                                        // 0
	ЧастиИмени = СтрРазделить(Имя, ".");                                                // 0
	Если ЧастиИмени.Количество() = 2 Тогда                                              // 1
                                                                                        // 0
		ИмяВида = ВРег(ЧастиИмени[0]);                                                  // 0
		ИмяОбъекта = ЧастиИмени[1];                                                     // 0
                                                                                        // 0
		Если ИмяВида = ВРег("Константы") Тогда                                          // 1
			Если Метаданные.Константы.Найти(ИмяОбъекта) <> Неопределено Тогда           // 1
				ОбъектНайден = Истина;                                                  // 0
			КонецЕсли;                                                                  // 0
		ИначеЕсли ИмяВида = ВРег("РегистрыСведений") Тогда                              // 1
			Если Метаданные.РегистрыСведений.Найти(ИмяОбъекта) <> Неопределено Тогда    // 1
				ОбъектНайден = Истина;                                                  // 0
			КонецЕсли;                                                                  // 0
		Иначе                                                                           // 1
			ОбъектНайден = Ложь;                                                        // 0
		КонецЕсли;                                                                      // 0
	КонецЕсли;                                                                          // 0
                                                                                        // 0
	Если Не ОбъектНайден Тогда                                                          // 1
		ВызватьИсключение СтроковыеФункцииКлиентСервер.ПодставитьПараметрыВСтроку(      // 0
			НСтр("ru = 'Объект метаданных ""%1"" не найден,                             // 0
			           |либо для него не поддерживается получение модуля менеджера.'"), // 0
			Имя);                                                                       // 0
	КонецЕсли;                                                                          // 0
	УстановитьБезопасныйРежим(Истина);                                                  // 0
	Модуль = Вычислить(Имя);                                                            // 0
	F = ?(Условие, ИСТИНА, НЕОПРЕДЕЛЕНО);                                               // 1
	А = ?(Условие, ИСТИНА, ?(Условие2, ЛОЖЬ, НЕОПРЕДЕЛЕНО));                            // 2
	M = ИСТИНА ИЛИ 7;                                                                   // 1
	Возврат Модуль;                                                                     // 0
КонецФункции                                                                            // итог 12
```

## Источники
<!-- Необходимо указывать ссылки на все источники, из которых почерпнута информация для создания диагностики -->
<!-- Примеры источников

* Источник: [Стандарт: Тексты модулей](https://its.1c.ru/db/v8std#content:456:hdoc)
* Полезная информаця: [Отказ от использования модальных окон](https://its.1c.ru/db/metod8dev#content:5272:hdoc)
* Источник: [Cognitive complexity, ver. 1.4](https://www.sonarsource.com/docs/CognitiveComplexity.pdf) -->

* [Cyclomatic Complexity PHP](https://pdepend.org/documentation/software-metrics/cyclomatic-complexity.html)
* [Цикломатическая сложность](https://ru.wikipedia.org/wiki/%D0%A6%D0%B8%D0%BA%D0%BB%D0%BE%D0%BC%D0%B0%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F_%D1%81%D0%BB%D0%BE%D0%B6%D0%BD%D0%BE%D1%81%D1%82%D1%8C)

## Сниппеты

<!-- Блоки ниже заполняются автоматически, не трогать -->
### Экранирование кода

```bsl
// BSLLS:CyclomaticComplexity-off
// BSLLS:CyclomaticComplexity-on
```

### Параметр конфигурационного файла

```json
"CyclomaticComplexity": {
    "complexityThreshold": 20,
    "checkModuleBody": true
}
```