# День 1. Архитектура React и TSX

## JSX/TSX: это синтаксический сахар над вызовом функции

Файл с разметкой в проекте называется `.tsx` (TypeScript + JSX). Браузер не понимает ни JSX, ни TypeScript, поэтому
перед запуском бандлер (Vite, под капотом esbuild) делает две независимые вещи:

1. **Стирает типы TypeScript.** Проверку типов он не делает, этим занимается `tsc` (в проекте `tsc -b` в скрипте `build` и линтер/IDE).
2. **Превращает JSX в вызовы функции.** `<Greeting name="Anna" />` превращается в обычный JavaScript.

Как именно, задаёт `tsconfig`: `"jsx": "react-jsx"` (современный режим) подставляет вызов `_jsx` / `jsxDEV` и сам
добавляет нужный `import`, поэтому `import React from "react"` в каждом файле не нужен.

Старая форма записи, которая всё ещё работает, тот же самый компонент:

```tsx
// JSX
<Card title="Привет"><p>Текст</p></Card>

// то же самое без угловых скобок
createElement(Card, { title: "Привет" }, createElement("p", null, "Текст"));
```

Правила перевода, которые стоит знать:

| JSX | во что превращается |
|---|---|
| `<div className="a">` | `createElement("div", { className: "a" })` |
| `<Card />` | `createElement(Card, null)` (идентификатор, а не строка) |
| `<Card>текст</Card>` | дети идут третьим и последующими аргументами |
| `{expr}` | любое JS-выражение, вставляется как значение |
| `<>…</>` | `createElement(Fragment, null, …)` (группа без обёртки в DOM) |

**Регистр имени важен.** Тег с маленькой буквы (`<greeting />`) React считает HTML-элементом и ищет такой в DOM
(TypeScript сообщит, что такого элемента нет в `JSX.IntrinsicElements`; если ошибку обойти, в DOM появится неизвестный
тег `<greeting>`, а не ваш компонент). С большой (`<Greeting />`) это ссылка на функцию-компонент.

Отличия от HTML в атрибутах: `className` вместо `class`, `htmlFor` вместо `for`, `style={{ gap: "10px" }}` принимает объект.

## Virtual DOM

`createElement` не создаёт настоящий DOM-узел. Он возвращает **обычный объект-описание** (React-элемент) примерно такого вида:

```js
{ type: "h1", props: { children: "Hello" }, key: null }
```

Дерево таких объектов и есть Virtual DOM. При обновлении React строит новое дерево, сравнивает со старым и вносит в
реальный DOM минимум правок. Важное уточнение, которое часто упускают: Virtual DOM не быстрее прямой работы с DOM.
Его ценность в декларативной модели: вы описываете, как UI должен выглядеть, а не как его менять шаг за шагом.
Время платите за сравнение деревьев.

## $UI = f(State)$

Компонент это чистая функция от состояния и пропсов. Когда состояние изменилось, React вызывает функцию заново и
получает новое описание UI. Отсюда следствия, которые развернёт День 2:

1. локальные переменные внутри компонента сбрасываются при каждом вызове;
2. нельзя менять DOM вручную поверх React;
3. тело компонента не должно иметь побочных эффектов.

## Что можно вставить в `{}` и что рендерится

В фигурных скобках допустимо любое JS-выражение (но не оператор: `if`, `for` нельзя, используйте тернарный
оператор, `&&`, `map`).

| значение | что на экране |
|---|---|
| строка, число (в том числе `0`) | текст |
| `true`, `false`, `null`, `undefined` | **ничего** |
| массив | каждый элемент по очереди |
| объект (кроме React-элемента) | ошибка рендера |

Из этого вытекает классическая ловушка:

```tsx
{items.length && <List items={items} />}   // при пустом списке на экране появится "0"
{items.length > 0 && <List items={items} />} // правильно: слева boolean
```

`0 && ...` возвращает `0`, а число рендерится. `false` и `null` не рендерятся. Поэтому в условии слева лучше иметь
явный `boolean`, либо использовать тернарный оператор.

## Списки и `key`

Массив элементов рендерится целиком, но каждому элементу нужен стабильный `key` среди соседей:

```tsx
{tasks.map((t) => <li key={t.id}>{t.title}</li>)}
```

`key` помогает React при сравнении деревьев понять, какой элемент остался тем же, а какой добавлен, удалён или
перемещён. Требования: уникален среди соседей и стабилен (не `Math.random()` и не индекс, если список меняется).
Без ключа или с нестабильным ключом сбрасывается локальное состояние дочерних компонентов и теряется фокус в полях ввода.

Дети, переданные массивом без ключей, дают предупреждение `Each child in a list should have a unique "key" prop`.

## Проецирование контента (`children`)

Содержимое между открывающим и закрывающим тегом попадает в компонент как проп `children`.
Тип такого содержимого `ReactNode` (строка, число, элемент, массив, `null` и т. д.):

```tsx
type PanelProps = { title: string; children: ReactNode };

function Card({ title, children }: PanelProps) {
  return (
    <>
      <h1>{title}</h1>
      {children}
    </>
  );
}
```

Аналог в Angular это `<ng-content>`, но здесь содержимое обычное значение, а не специальная директива.

## Render props: передача функции вместо содержимого

Если компоненту нужно самому решить, **как** отрисовать элемент, вы передаёте функцию (render prop). Типизируется она обобщением,
похожим на `List<T>` в C#:

```tsx
type ListProps<T> = {
  items: T[];
  renderItem: (item: T) => ReactNode;
  getKey: (item: T) => string | number;
};

function List<T>({ items, renderItem, getKey }: ListProps<T>) {
  return (
    <ul>
      {items.map((item) => (
        <li key={getKey(item)}>{renderItem(item)}</li>
      ))}
    </ul>
  );
}

<List items={users} getKey={(u) => u.id} renderItem={(u) => <b>{u.name}</b>} />
```

`T` выводится из `items`, поэтому в `renderItem` вы получаете типизированного `u` без явных аннотаций.

## Экспорты и импорты компонентов

- `export default Greeting` импортируется как `import Greeting from "./Greeting"` (имя любое);
- `export function Greeting` импортируется как `import { Greeting } from "./Greeting"` (имя фиксировано).

Если в файле `export default`, а импортируете в фигурных скобках, Vite выдаст `Missing export`. Выберите один способ на
проект, чтобы не путаться.

## Стили: глобальные и CSS Modules

Обычный `.css`, подключённый через `import "./CardView.css"`, **глобальный**: селекторы действуют на всю страницу,
изоляции (как emulated encapsulation в Angular) нет. Изоляция достигается файлом `*.module.css`: бандлер подменяет имена
классов на уникальные (хешированные):

```tsx
import styles from "./ProjectEditor.module.css";
<div className={styles.formGroup} />
```

## StrictMode

В dev-режиме `<StrictMode>` намеренно вызывает функцию компонента дважды, чтобы выявить побочные эффекты в теле
компонента. Поэтому `console.log` в теле компонента печатается дважды. В production-сборке этого нет.

## Источники

- [Writing Markup with JSX](https://react.dev/learn/writing-markup-with-jsx)
- [JavaScript in JSX with Curly Braces](https://react.dev/learn/javascript-in-jsx-with-curly-braces)
- [Rendering Lists](https://react.dev/learn/rendering-lists)
- [Keeping Components Pure](https://react.dev/learn/keeping-components-pure)
- [Introducing the New JSX Transform](https://legacy.reactjs.org/blog/2020/09/22/introducing-the-new-jsx-transform.html)
