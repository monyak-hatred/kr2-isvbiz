# DECISIONS

## 1. Полиморфизм вместо цепочки if; фабрика только выбирает
Каждый способ доставки — класс, реализующий `DeliveryPolicy` (`supports`, `calculateCost`, `describeLimits`).
`DeliveryService.calculate` не знает ни одного способа: берёт политику у `DeliveryPolicyFactory`, проверяет
`supports` и вызывает `calculateCost` полиморфно. Фабрика содержит только таблицу «тип -> реализация» без формул.
Новый способ (в этой работе — постамат `LockerPolicy`) добавляется новым классом и одной строкой регистрации,
расчёты `PickupPolicy`, `StandardCourierPolicy` и `ExpressPolicy` не затрагиваются.

## 2. Минимальная стоимость (вариант 5) — одна константа и один метод
`DeliveryRules.MIN_COST = 30` и `DeliveryRules.applyMinimum` — единственное место правила; платные политики
вызывают его, самовывоз (бесплатный) — нет. Невозможность доставки выражена `DeliveryNotPossibleException`
с описанием ограничений способа.
