# Дорожная карта изучения React: от базы к продвинутой архитектуре

* **Формат:** 1.5–2 часа в день (через день, с правом на пропуск).
* **Структура занятия:** 45 минут — глубинная теория (internals), 45–60 минут — изолированная практика руками.
* **Источники:** у каждого дня есть блок «Источники» (в порядке чтения, с оценкой времени). Теория дня — это чтение этих материалов плюс эксперименты с `console.log` и DevTools, а не пересказ в чате. Все ссылки проверены на момент составления (2026-11); официальная документация React — [react.dev](https://react.dev).
* **Версии:** React 19.x, TypeScript в строгом режиме, Vite. Версии библиотек сверяйте с их документацией на момент прохождения: экосистема быстро меняется.
* **Связь с `java-sandbox`:** итоговый проект Фазы 2 (Дни 29–35) — клиент к «Enterprise Data API» из Java-карты. Контракт API (пагинация, сортировка, формат ошибок `ProblemDetails`, ответ 409) согласуйте заранее. Сначала работаем на моках (MSW), затем переключаемся на настоящий Java-бэкенд.

### Мост из Angular: на что опираться

| Angular | React |
|---|---|
| Компонент + шаблон | Функция, возвращающая JSX |
| Сервисы + DI | Кастомные хуки + Context |
| RxJS, `async` pipe | TanStack Query (серверные данные), события и `useEffect` (подписки) |
| Signals | `useState` / `useReducer` (сравнение по ссылке) |
| Change Detection (`OnPush`) | Reconciliation, `React.memo` |
| `ngOnInit` / `ngOnDestroy` | `useEffect` + cleanup (но это *синхронизация*, а не жизненный цикл) |
| Reactive Forms | React Hook Form + Zod |
| Router + Guards + Resolvers | React Router (loaders, protected routes) |
| NgRx | Zustand (или Redux Toolkit) |

---

## Фаза 1. Базовый переход и фундаментальная теория (Дни 1–14)

### Неделя 1: Ментальная модель, JSX и базовые хуки

- [x] **День 1: Архитектура React и TSX**
  - **Теория:** Компоненты пишутся в файлах `.tsx` (TypeScript + JSX). Трансляция разметки в вызовы функций (`React.createElement` / `_jsx` из `react/jsx-runtime`, опция `"jsx": "react-jsx"` в tsconfig): типы стираются сборщиком, проверку типов делает отдельно `tsc`. Virtual DOM как дерево легковесных JS-дескрипторов. Концепция $UI = f(State)$. Почему React — библиотека, а не фреймворк (что в Angular «из коробки», а здесь выбирается самому).
  - **Практика:** Развернуть проект (`npm create vite@latest react-deep-dive -- --template react-ts`). В `.tsx`-файле написать компонент без угловых скобок (только через `createElement`), затем переписать его на JSX в том же файле. Увидеть скомпилированный результат (`jsxDEV`) в браузере или Babel REPL. Реализовать передачу компонентов через `children` и render props с типизацией пропсов (`ReactNode`, обобщённый `List<T>`).
  - **Источники:**
    - [Thinking in React](https://react.dev/learn/thinking-in-react) (15 мин)
    - [Writing Markup with JSX](https://react.dev/learn/writing-markup-with-jsx) (8 мин)
    - [JavaScript in JSX with Curly Braces](https://react.dev/learn/javascript-in-jsx-with-curly-braces) (8 мин)
    - [Rendering Lists](https://react.dev/learn/rendering-lists) (8 мин)
    - [Introducing the New JSX Transform](https://legacy.reactjs.org/blog/2020/09/22/introducing-the-new-jsx-transform.html) (5 мин)

- [x] **День 2: `useState` и концепция Immutability**
  - **Теория:** Поверхностное сравнение ссылок (`Object.is`). Батчинг обновлений (Automatic Batching). Функциональный апдейтер: разница между `setCount(count + 1)` и `setCount(prev => prev + 1)`.
  - **Практика:** Создать компонент с глубоко вложенным стейтом (объект с массивами объектов). Реализовать обновление полей без мутаций через spread-оператор. Симулировать race condition в стейте через `setTimeout`.
  - **Источники:**
    - [State: A Component's Memory](https://react.dev/learn/state-a-components-memory) (10 мин)
    - [State as a Snapshot](https://react.dev/learn/state-as-a-snapshot) (10 мин)
    - [Queueing a Series of State Updates](https://react.dev/learn/queueing-a-series-of-state-updates) (10 мин)
    - [Updating Objects in State](https://react.dev/learn/updating-objects-in-state) (8 мин)
    - [Updating Arrays in State](https://react.dev/learn/updating-arrays-in-state) (8 мин)
    - [Keeping Components Pure](https://react.dev/learn/keeping-components-pure) (7 мин)

- [x] **День 3: `useRef` — выход за пределы цикла рендеринга**
  - **Теория:** Механика ref под капотом (стабильный объект `{ current: T }` между вызовами функции). `useState` против `useRef`. Прямой доступ к DOM-узлам.
  - **Практика:** Написать таймер с сохранением ID интервала в `useRef`. Сделать инпут с управляемым фокусом и плавным скроллом к элементу. Реализовать паттерн хранения предыдущего значения пропса через `useRef`.
  - **Источники:**
    - [Escape Hatches (обзор раздела)](https://react.dev/learn/escape-hatches) (3 мин)
    - [Referencing Values with Refs](https://react.dev/learn/referencing-values-with-refs) (10 мин)
    - [Manipulating the DOM with Refs](https://react.dev/learn/manipulating-the-dom-with-refs) (12 мин)
    - [useRef (справочник)](https://react.dev/reference/react/useRef) (8 мин)

- [x] **День 4: `useEffect` — модель синхронизации и StrictMode**
  - **Теория:** Почему `useEffect` — не аналог `ngOnInit`/`Form_Load`. Концепция синхронизации с внешними API/DOM. Механика функции очистки (cleanup function). `<StrictMode>`: в dev-режиме эффект намеренно запускается дважды (mount → unmount → mount), чтобы выявить отсутствующий cleanup. Статья «You Might Not Need an Effect»: что не нужно делать эффектом.
  - **Практика:** Реализовать подписку на `window.addEventListener('resize')` с обязательной отпиской в cleanup. Написать запрос к API через `fetch` с отменой через `AbortController`. Увидеть двойной вызов эффекта в StrictMode и убедиться, что cleanup делает его безвредным.
  - **Источники:**
    - [Synchronizing with Effects](https://react.dev/learn/synchronizing-with-effects) (15 мин)
    - [You Might Not Need an Effect](https://react.dev/learn/you-might-not-need-an-effect) (20 мин)
    - [Lifecycle of Reactive Effects](https://react.dev/learn/lifecycle-of-reactive-effects) (15 мин)
    - [useEffect (справочник)](https://react.dev/reference/react/useEffect) (8 мин)
    - [StrictMode (справочник)](https://react.dev/reference/react/StrictMode) (5 мин)

- [ ] **День 5: Ловушки `useEffect` и замыкания (Stale Closures)**
  - **Теория:** Механика замыканий в JS внутри функциональных компонентов. Устаревшие замыкания (Stale Closures) в таймерах и обработчиках. Роль массива зависимостей.
  - **Практика:** Искусственно воспроизвести баг устаревшего замыкания в `setInterval`. Исправить его двумя путями: через функциональный `setState` и через связку с `useRef`.
  - **Источники:**
    - [Замыкания (javascript.info)](https://javascript.info/closure) (15 мин)
    - [Removing Effect Dependencies](https://react.dev/learn/removing-effect-dependencies) (15 мин)
    - [Separating Events from Effects](https://react.dev/learn/separating-events-from-effects) (12 мин)
    - [Making setInterval Declarative with React Hooks (Dan Abramov)](https://overreacted.io/making-setinterval-declarative-with-react-hooks/) (20 мин)
    - [State as a Snapshot (повторить)](https://react.dev/learn/state-as-a-snapshot) (5 мин)

- [ ] **День 6: `useContext` — передача контекста через дерево**
  - **Теория:** Решение проблемы «prop drilling». Проблема каскадных ререндеров: почему изменение одного поля в контексте перерисовывает всех потребителей хука. Чтение контекста через `use(Context)` в React 19.
  - **Практика:** Создать контекст авторизации или темы оформления. Написать обёртку-хук с проверкой контекста на `null` с выбросом типизированной ошибки при вызове вне провайдера.
  - **Источники:**
    - [Passing Data Deeply with Context](https://react.dev/learn/passing-data-deeply-with-context) (12 мин)
    - [Scaling Up with Reducer and Context](https://react.dev/learn/scaling-up-with-reducer-and-context) (12 мин)
    - [useContext (справочник)](https://react.dev/reference/react/useContext) (8 мин)
    - [use (справочник)](https://react.dev/reference/react/use) (7 мин)

- [ ] **День 7: Практикум недели — Кастомные хуки**
  - **Теория:** Инкапсуляция логики состояния без разделения самого состояния. Правила хуков (Rules of Hooks).
  - **Практика:** Написать и протестировать кастомные хуки:
    - `useDebounce<T>(value: T, delay: number): T`
    - `useLocalStorage<T>(key: string, initialValue: T)`
    - Собрать компонент поиска с отложенным вводом.
  - **Источники:**
    - [Reusing Logic with Custom Hooks](https://react.dev/learn/reusing-logic-with-custom-hooks) (15 мин)
    - [Rules of Hooks](https://react.dev/reference/rules/rules-of-hooks) (7 мин)
    - [Rules of React (обзор правил)](https://react.dev/reference/rules) (5 мин)

---

### Неделя 2: Продвинутая механика рендера и внутренности React

- [ ] **День 8: Reconciliation, Fiber и конкурентный рендеринг**
  - **Теория:** Зачем создавался движок Fiber (прерываемый рендеринг, кооперативная многозадачность). Структура Fiber-ноды (`child`, `sibling`, `return`). Double Buffering (`current` vs `workInProgress`). Приоритеты обновлений: `useTransition` и `useDeferredValue` как способ пометить обновление «несрочным».
  - **Практика:** 15 минут: найти Fiber-ноду через Chrome DevTools (свойства `__reactFiber$...` на реальном DOM-элементе). Основное время: поле поиска по списку из 20 000 элементов, сначала без оптимизации (ввод «тормозит»), затем с `useTransition` / `useDeferredValue`.
  - **Источники:**
    - [React Fiber Architecture (Andrew Clark)](https://github.com/acdlite/react-fiber-architecture) (20 мин)
    - [Render and Commit](https://react.dev/learn/render-and-commit) (8 мин)
    - [useTransition (справочник)](https://react.dev/reference/react/useTransition) (10 мин)
    - [useDeferredValue (справочник)](https://react.dev/reference/react/useDeferredValue) (8 мин)
    - [A (Mostly) Complete Guide to React Rendering Behavior (Mark Erikson, по желанию)](https://blog.isquaredsoftware.com/2020/05/blogged-answers-a-mostly-complete-guide-to-react-rendering-behavior/) (40 мин)

- [ ] **День 9: Ключи (`key`) и идентификация состояния**
  - **Теория:** Как алгоритм диффинга использует `key`. Почему использование индекса массива `key={index}` ломает состояние компонентов при удалении/сортировке. Ключ должен быть стабильным: id из данных или значение, сгенерированное один раз при создании элемента (а не при каждом рендере).
  - **Практика:** Создать динамический список с инпутами с `key={index}`, удалить первый элемент и зафиксировать баг смещения значений. Исправить на стабильные id. Показать, что `key={crypto.randomUUID()}` прямо в JSX ломает состояние ещё сильнее. Применить изменение `key` для полного сброса формы.
  - **Источники:**
    - [Rendering Lists: keeping list items in order with key](https://react.dev/learn/rendering-lists#keeping-list-items-in-order-with-key) (5 мин)
    - [Preserving and Resetting State](https://react.dev/learn/preserving-and-resetting-state) (15 мин)
    - [Reconciliation: recursing on children](https://legacy.reactjs.org/docs/reconciliation.html#recursing-on-children) (8 мин)

- [ ] **День 10: Фазы Render и Commit, `useLayoutEffect`**
  - **Теория:** Render Phase (чистая, асинхронная, может перезапускаться) против Commit Phase (синхронная мутация реального DOM). Разница между `useEffect` и `useLayoutEffect`.
  - **Практика:** Воспроизвести мерцание интерфейса при вычислении координат тултипа в `useEffect`. Устранить проблему переходом на `useLayoutEffect`.
  - **Источники:**
    - [Render and Commit](https://react.dev/learn/render-and-commit) (8 мин)
    - [useLayoutEffect (справочник)](https://react.dev/reference/react/useLayoutEffect) (10 мин)
    - [Synchronizing with Effects (повторить)](https://react.dev/learn/synchronizing-with-effects) (5 мин)

- [ ] **День 11: Оптимизация вычислений — `useMemo`, `useCallback` и React Compiler**
  - **Теория:** Ссылочная целостность в JS. Назначение `useMemo` (кэширование результата) и `useCallback` (кэширование ссылки на функцию). Накладные расходы на мемоизацию. React Compiler: автоматическая мемоизация на этапе сборки, поэтому ручные `useMemo`/`useCallback` в новом коде нужны реже. Понимать их всё равно необходимо: для чтения существующего кода и для случаев, где компилятор не помогает.
  - **Практика:** Написать компонент с тяжелой фильтрацией массива (100 000 записей), замерить время через `console.time`. Оптимизировать через `useMemo`. Затем подключить React Compiler в Vite и сравнить результат в Profiler без ручной мемоизации.
  - **Источники:**
    - [useMemo (справочник)](https://react.dev/reference/react/useMemo) (12 мин)
    - [useCallback (справочник)](https://react.dev/reference/react/useCallback) (10 мин)
    - [React Compiler: введение](https://react.dev/learn/react-compiler) (10 мин)
    - [Why React Re-Renders (Josh Comeau)](https://www.joshwcomeau.com/react/why-react-re-renders/) (25 мин)

- [ ] **День 12: Мемоизация компонентов — `React.memo`**
  - **Теория:** Поверхностное сравнение пропсов (`shallow compare`). Почему инлайн-объекты `{}` и стрелочные функции ломают `React.memo`. Аналогия с `OnPush` из Angular.
  - **Практика:** Создать дочерний компонент с логированием рендера. Проверить потерю мемоизации при передаче немемоизированных коллбэков и восстановить её через `useCallback`. Повторить с включённым React Compiler и объяснить разницу.
  - **Источники:**
    - [memo (справочник)](https://react.dev/reference/react/memo) (12 мин)
    - [Before You memo() (Dan Abramov)](https://overreacted.io/before-you-memo/) (15 мин)
    - [Why React Re-Renders (Josh Comeau, повторить)](https://www.joshwcomeau.com/react/why-react-re-renders/) (10 мин)

- [ ] **День 13: Обработка ошибок — Error Boundaries**
  - **Теория:** Почему ошибки в фазе рендера размонтируют всё дерево приложения. Классовые методы жизненного цикла `componentDidCatch` и `getDerivedStateFromError`. Какие ошибки Error Boundary *не* ловит (обработчики событий, асинхронный код).
  - **Практика:** Написать переиспользуемый компонент `ErrorBoundary` на классе. Обернуть в него изолированный нестабильный виджет, оставив остальной UI рабочим. Затем сделать то же через `react-error-boundary` и сравнить: что библиотека добавляет (`resetKeys`, `useErrorBoundary`).
  - **Источники:**
    - [Component: catching rendering errors with an error boundary](https://react.dev/reference/react/Component#catching-rendering-errors-with-an-error-boundary) (12 мин)
    - [react-error-boundary (README)](https://github.com/bvaughn/react-error-boundary) (10 мин)

- [ ] **День 14: Итоговый проект первой фазы**
  - **Практика:** Собрать интерактивное файловое дерево (папки/файлы) с функциями создания, переименования, сворачивания и поиска. Использовать только чистый React, строгий TypeScript и кастомные хуки (без внешних библиотек).
  - **Источники:**
    - [Thinking in React (повторить)](https://react.dev/learn/thinking-in-react) (10 мин)
    - [Choosing the State Structure](https://react.dev/learn/choosing-the-state-structure) (12 мин)
    - [Sharing State Between Components](https://react.dev/learn/sharing-state-between-components) (10 мин)

---

## Фаза 2. Индустриальный стек (Дни 15–35)

### Неделя 3: Серверный кэш, клиентский стейт и роутинг

- [ ] **Дни 15–16: Разделение Server State и Client State**
  - **Теория:** Почему хранить данные API в глобальных сторах — антипаттерн. Понятие кэша удаленного состояния. Ключи запросов (`queryKey`) как зависимости.
  - **Практика:** Установить `@tanstack/react-query`. Реализовать выборку данных через `useQuery`. Настроить и протестировать параметры `staleTime` и `gcTime`.
  - **Источники:**
    - [TanStack Query: Overview](https://tanstack.com/query/latest/docs/framework/react/overview) (10 мин)
    - [Important Defaults](https://tanstack.com/query/latest/docs/framework/react/guides/important-defaults) (10 мин)
    - [Query Keys](https://tanstack.com/query/latest/docs/framework/react/guides/query-keys) (8 мин)
    - [Practical React Query (TkDodo)](https://tkdodo.eu/blog/practical-react-query) (20 мин)
    - [React Query as a State Manager (TkDodo)](https://tkdodo.eu/blog/react-query-as-a-state-manager) (15 мин)

- [ ] **Дни 17–18: Мутации и Optimistic Updates**
  - **Теория:** Изменение серверных данных через `useMutation`. Инвалидация ключей кэша (`invalidateQueries`). Механика оптимистичных обновлений с откатом (rollback). Сравнение с `useOptimistic` из React 19: когда достаточно его, а когда нужен кэш запросов.
  - **Практика:** Сделать переключатель статуса сущности с мгновенным обновлением UI и симуляцией сетевой ошибки с откатом к исходному значению.
  - **Источники:**
    - [Mutations](https://tanstack.com/query/latest/docs/framework/react/guides/mutations) (12 мин)
    - [Invalidations from Mutations](https://tanstack.com/query/latest/docs/framework/react/guides/invalidations-from-mutations) (5 мин)
    - [Optimistic Updates](https://tanstack.com/query/latest/docs/framework/react/guides/optimistic-updates) (12 мин)
    - [useOptimistic (справочник React 19)](https://react.dev/reference/react/useOptimistic) (8 мин)

- [ ] **День 19: Клиентский стейт с Zustand**
  - **Теория:** Архитектура Zustand: работа вне контекста React, интеграция через `useSyncExternalStore`. Селекторы стейта и предотвращение лишних ререндеров.
  - **Практика:** Создать глобальный стор для UI (сайдбары, модалки, активные фильтры). Настроить выборку через селекторы: `const open = useStore(s => s.open)`. Подключить middleware `persist` (`localStorage`) и `devtools`.
  - **Источники:**
    - [Zustand (README и документация)](https://github.com/pmndrs/zustand) (20 мин)
    - [useSyncExternalStore (справочник)](https://react.dev/reference/react/useSyncExternalStore) (12 мин)

- [ ] **Дни 20–21: Роутинг**
  - **Теория:** Клиентская маршрутизация: вложенные маршруты, `Outlet`, загрузчики данных (loaders) и когда они лучше `useQuery`. URL как источник истины для фильтров (`useSearchParams`). Защищённые маршруты (аналог Angular Guards). Выбрать одну библиотеку (React Router или TanStack Router) и держаться её.
  - **Практика:** Сделать приложение с тремя страницами: список с фильтрами в query-параметрах (перезагрузка страницы не сбрасывает фильтры), страница сущности по `/items/:id`, страница 404. Добавить защищённый маршрут с редиректом на `/login`.
  - **Источники:**
    - [React Router: документация](https://reactrouter.com/) (25 мин)
    - [TanStack Router (альтернатива)](https://tanstack.com/router) (10 мин)

---

### Неделя 4: Промышленные формы, UI-архитектура и тестирование

- [ ] **Дни 22–23: Неконтролируемые формы с React Hook Form**
  - **Теория:** Контролируемые (`value` + `onChange`) против неконтролируемых (`ref`) компонентов. Проблема просадки FPS при вводе в больших формах.
  - **Практика:** Реализовать форму через `useForm` и регистрацию `register`. Обернуть кастомный селект в `Controller` для работы со сторонними библиотеками.
  - **Источники:**
    - [React Hook Form: Get Started](https://react-hook-form.com/get-started) (12 мин)
    - [Controller](https://react-hook-form.com/docs/usecontroller/controller) (8 мин)
    - [Controlling an input with a state variable (React)](https://react.dev/reference/react-dom/components/input#controlling-an-input-with-a-state-variable) (8 мин)

- [ ] **Дни 24–25: Схемная валидация через Zod**
  - **Теория:** Type Inference: вывод TypeScript-типов из схемы валидации (`z.infer<typeof schema>`). Защита границ приложения (runtime validation). Валидация ответов API, а не только форм.
  - **Практика:** Описать строгую схему формы в Zod (числа, email, вложенные объекты, кастомная валидация зависимых полей через `.refine()`). Связать схему с формой через `@hookform/resolvers/zod`. Показать ошибки сервера (формат `ProblemDetails` с перечнем полей) в соответствующих полях через `setError`.
  - **Источники:**
    - [Zod: документация](https://zod.dev/) (20 мин)
    - [@hookform/resolvers](https://github.com/react-hook-form/resolvers) (8 мин)
    - [setError (React Hook Form)](https://react-hook-form.com/docs/useform/seterror) (5 мин)

- [ ] **День 26: UI-архитектура и Compound Components**
  - **Теория:** Паттерн составных компонентов (Compound Components). Инкапсуляция общего контекста группы элементов.
  - **Практика:** Разработать переиспользуемый компонент вкладок (`Tabs`, `Tabs.List`, `Tabs.Trigger`, `Tabs.Content`) на Compound-паттерне.
  - **Источники:**
    - [Compound Components with React Hooks (Kent C. Dodds)](https://kentcdodds.com/blog/compound-components-with-react-hooks) (20 мин)
    - [Passing Data Deeply with Context (повторить)](https://react.dev/learn/passing-data-deeply-with-context) (10 мин)

- [ ] **Дни 27–28: Тестирование компонентов**
  - **Теория:** Пирамида тестов на фронтенде. Vitest и React Testing Library: тестируем поведение, а не реализацию (запросы по роли и тексту, `userEvent`). MSW (Mock Service Worker) для подмены сети на уровне запросов. Тестирование хуков и компонентов с `QueryClientProvider`.
  - **Практика:** Покрыть тестами кастомный хук `useDebounce` (с fake timers), форму с валидацией (ошибки показываются и исчезают) и компонент списка, который грузит данные из мок-API через MSW (состояния Loading, Success, Error).
  - **Источники:**
    - [Vitest: Getting Started](https://vitest.dev/guide/) (15 мин)
    - [React Testing Library: Intro](https://testing-library.com/docs/react-testing-library/intro/) (10 мин)
    - [About Queries: приоритет запросов](https://testing-library.com/docs/queries/about/#priority) (8 мин)
    - [Common mistakes with React Testing Library (Kent C. Dodds)](https://kentcdodds.com/blog/common-mistakes-with-react-testing-library) (20 мин)
    - [MSW: документация](https://mswjs.io/docs/) (20 мин)
    - [Testing TanStack Query](https://tanstack.com/query/latest/docs/framework/react/guides/testing) (8 мин)

---

### Неделя 5: Инженерный проект (Enterprise Data Panel)

- [ ] **Дни 29–35: Разработка модуля управления данными**
  - **Стек:** Vite, React, TypeScript, React Router, TanStack Query, TanStack Table, Zustand, React Hook Form, Zod, Vitest + RTL + MSW.
  - **План:** Дни 29–31 работаем на моках (MSW) по согласованному контракту. Дни 32–35 переключаемся на настоящий Java-бэкенд («Enterprise Data API»): сгенерировать типизированный клиент из OpenAPI (`openapi-typescript` или `orval`), настроить прокси Vite и проверить CORS.
  - **Требования к функционалу:**
    - Таблица данных (TanStack Table в режиме `manual`) с пагинацией, серверной сортировкой и поиском.
    - Панель фильтров: стейт в Zustand, синхронизированный с URL Search Params.
    - Модалка создания/редактирования: валидация через Zod + React Hook Form, ошибки сервера отображаются в полях.
    - Оптимистичное удаление записи с возможностью отмены (на стороне Java: мягкое удаление + эндпоинт восстановления).
    - Обработка конфликта версий: ответ 409 показывается пользователю с предложением обновить данные.
    - Корректная обработка всех состояний: Loading (скелетоны), Error (retry), Empty.
    - Тесты основных сценариев (таблица, форма, удаление с отменой).
  - **Источники:**
    - [TanStack Table](https://tanstack.com/table/latest) (по ходу работы)
    - [openapi-typescript](https://openapi-ts.dev/) (по ходу работы)
    - [Orval (альтернатива)](https://orval.dev/) (по ходу работы)
    - [Vite: server.proxy](https://vite.dev/config/server-options#server-proxy) (5 мин)

---

## Фаза 3. Продвинутый уровень и масштабирование (Дни 36–50)

### Неделя 6: Профилирование и оптимизация производительности

- [ ] **Дни 36–38: Профилирование с помощью React DevTools**
  - **Теория:** Анализ Flamegraph и Ranked диаграмм. Поиск причин частых обновлений («Why did this render?»). Замена мемоизации композицией («Push State Down», передача через `children`). Проверка, что React Compiler действительно убрал лишние рендеры.
  - **Практика:** Создать искусственно замедленное приложение с тяжелым списком. Локализовать узкие места через React Profiler, устранить их реструктуризацией дерева компонентов. Сравнить результат с включённым и выключенным React Compiler.
  - **Источники:**
    - [Profiler (справочник)](https://react.dev/reference/react/Profiler) (8 мин)
    - [React Developer Tools](https://react.dev/learn/react-developer-tools) (5 мин)
    - [Before You memo() (Dan Abramov)](https://overreacted.io/before-you-memo/) (15 мин)
    - [Why React Re-Renders (Josh Comeau)](https://www.joshwcomeau.com/react/why-react-re-renders/) (25 мин)

- [ ] **Дни 39–40: Виртуализация списков**
  - **Теория:** Ограничения DOM при отрисовке больших объемов данных. Принцип скользящего окна (Windowing / Virtualization).
  - **Практика:** Подключить `@tanstack/react-virtual`. Реализовать плавный виртуализированный скролл таблицы на 20 000 строк.
  - **Источники:**
    - [TanStack Virtual](https://tanstack.com/virtual/latest) (20 мин)

- [ ] **Дни 41–42: Разделение кода (Code Splitting) и Suspense**
  - **Теория:** Механика разбиения бандла на чанки (Chunk Splitting). Динамические импорты `import()`. `Suspense` как общий механизм ожидания (код, данные) и хук `use(promise)`.
  - **Практика:** Реализовать ленивую загрузку маршрутов и тяжелых модальных окон через `React.lazy` и обертку `Suspense` со скелетоном. Проанализировать бандл (`rollup-plugin-visualizer`) до и после.
  - **Источники:**
    - [lazy (справочник)](https://react.dev/reference/react/lazy) (8 мин)
    - [Suspense (справочник)](https://react.dev/reference/react/Suspense) (15 мин)
    - [use (справочник)](https://react.dev/reference/react/use) (7 мин)
    - [Динамический import() (MDN)](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/import) (8 мин)
    - [rollup-plugin-visualizer](https://github.com/btd/rollup-plugin-visualizer) (5 мин)

---

### Неделя 7: Авторизация, e2e-тесты, доступность и обзор RSC

- [ ] **Дни 43–44: Авторизация на клиенте**
  - **Теория:** Поток JWT (access + refresh), где хранить токены (память, httpOnly cookie, `localStorage`) и чем это грозит (XSS, CSRF). Перехват ответов 401/403 и повторный запрос после обновления токена. Защищённые маршруты и ролевой доступ в UI.
  - **Практика:** Подключить логин к Spring Security из Java-карты: форма входа, хранение токена, автоматическое обновление, редирект на `/login` при 401, скрытие действий по ролям.
  - **Источники:**
    - [Session Management Cheat Sheet (OWASP)](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html) (20 мин)
    - [JSON Web Token Cheat Sheet (OWASP)](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_Cheat_Sheet.html) (15 мин)
    - [Cookies и HttpOnly (MDN)](https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cookies) (12 мин)

- [ ] **День 45: End-to-end тесты**
  - **Теория:** Чем e2e отличаются от компонентных тестов. Playwright: локаторы, авто-ожидание, изоляция состояния, трассировка.
  - **Практика:** Написать сквозной сценарий для Enterprise Data Panel: вход → фильтрация → создание записи → ошибка валидации → удаление и отмена.
  - **Источники:**
    - [Playwright: Getting Started](https://playwright.dev/docs/intro) (10 мин)
    - [Locators](https://playwright.dev/docs/locators) (12 мин)
    - [Best Practices](https://playwright.dev/docs/best-practices) (10 мин)
    - [Trace Viewer](https://playwright.dev/docs/trace-viewer-intro) (8 мин)

- [ ] **День 46: Доступность (a11y)**
  - **Теория:** Семантическая разметка, роли ARIA, управление фокусом в модалках, навигация с клавиатуры. Автоматические проверки (`axe`) и их пределы.
  - **Практика:** Прогнать панель через `axe`, исправить найденные проблемы. Добиться полного управления модалкой и таблицей только с клавиатуры.
  - **Источники:**
    - [ARIA Authoring Practices Guide](https://www.w3.org/WAI/ARIA/apg/) (25 мин)
    - [axe-core](https://github.com/dequelabs/axe-core) (8 мин)
    - [Accessibility (React)](https://legacy.reactjs.org/docs/accessibility.html) (12 мин)

- [ ] **Дни 47–48: Обзор: React Server Components и Server Actions (по желанию)**
  - **Теория:** Разделение среды исполнения: выполнение компонентов на сервере без отправки JS-кода клиенту. Разница между SSR и RSC. Границы директивы `'use client'`. Server Actions как RPC-вызовы из форм, их связь с Actions и `useActionState` в React 19. Почему для проектов со Spring-бэкендом чаще выбирают SPA, а RSC уместны в Next.js-проектах.
  - **Практика:** Развернуть проект на Next.js (App Router). Создать Server Component с чтением данных на сервере и форму с мутацией через Server Action и `revalidatePath`. Цель — понять модель, а не освоить Next.js полностью.
  - **Источники:**
    - [Server Components (справочник)](https://react.dev/reference/rsc/server-components) (12 мин)
    - ['use client'](https://react.dev/reference/rsc/use-client) (8 мин)
    - [Server Functions](https://react.dev/reference/rsc/server-functions) (10 мин)
    - [Next.js App Router](https://nextjs.org/docs/app) (по ходу работы)

- [ ] **Дни 49–50: Систематизация знаний и подготовка к интервью**
  - **Теория:** Повторение ключевых тем:
    - Модель согласования (Reconciliation), Fiber и конкурентный рендеринг.
    - Механика хуков, замыканий и StrictMode.
    - Client State vs Server State.
    - Оптимизация производительности: мемоизация, React Compiler и архитектурная композиция.
    - Роутинг, авторизация и тестирование.
    - RSC vs классический SSR.
