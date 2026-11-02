import { useState } from "react";
import { flushSync } from "react-dom";

function CounterSnapshot() {
  /**
   * https://habr.com/ru/companies/otus/articles/667706/
   *
   * Состояние хранит React (внутри своей структуры для компонента), а не переменная функции.
   * Функция компонента вызывается при каждом рендере заново, и count каждый раз получает значение из хранилища React.
   * Поэтому count внутри одного рендера константа: это снимок (snapshot)
   *
   * setCount не меняет переменную, а просит React отрисовать компонент заново с новым значением
   *
   * Автоматический батчинг.
   *
   * React не рендерит сразу при каждом setState. Он ждёт, пока закончится текущий синхронный блок кода, и потом делает один проход по накопленным очередям.
   *
   * Как это устроено:
   * 1) Первый setState помечает компонент «нужен рендер» и планирует его.
   * 2) Следующие setState в том же блоке видят, что рендер уже запланирован. Они только добавляют записи в очередь.
   * 3)Когда блок закончился, происходит один рендер, и все очереди применяются разом.
   */
  const [count, setCount] = useState(0);
  const [countOther, setCountOther] = useState(0);

  console.log(`render data: count = ${count}`);

  /** итог: +1 (во всех трёх count один и тот же снимок) */
  function handleIncrementOne() {
    setCount(count + 1);
    setCount(count + 1);
    setCount(count + 1);
    console.log("handleIncrementOne: ", count); // будет выведено не новое значение, а старое, так как ререндер ещё не произошёл
  }

  /**
   * итог: +3 (каждый апдейтер получает результат предыдущего)
   * если новое состояние опирается на предыдущее состояние, используйте функцию.
   * функция вычисляет состояние из предыдущего состояния
   */
  function handleIncrementTwo() {
    setCount((prev) => prev + 1);
    setCount((prev) => prev + 1);
    setCount((prev) => prev + 1);
    console.log("handleIncrementTwo: ", count); // будет выведено не новое значение, а старое, так как ререндер ещё не произошёл
  }

  /**
   * три отдельных таймера на 2000 мс это три отдельные задачи в цикле событий.
   * Склеит ли их React в один рендер или сделает два-три, зависит от тайминга, строгой гарантии нет.
   * Гарантия есть для одного синхронного блока. Итоговое значение (+1) от этого не зависит
   */
  function handleIncrementAsync() {
    // итог: +1 (во всех трёх count один и тот же снимок)
    setTimeout(() => setCount(count + 1), 2000);
    setTimeout(() => setCount(count + 1), 2000);
    setTimeout(() => setCount(count + 1), 2000);

    // итог: +3 (каждый апдейтер получает результат предыдущего)
    // setTimeout(() => setCount((prev) => prev + 1), 2000);
    // setTimeout(() => setCount((prev) => prev + 1), 2000);
    // setTimeout(() => setCount((prev) => prev + 1), 2000);
  }

  function handleIncrementOtherOne() {
    // handler end(console.log) => render(setCountOther)
    // setCount((c) => c + 1);
    // setCountOther((o) => o + 1);
    // console.log("handler end");

    // render(flushSync) => handler end(console.log) => render(setCountOther)
    flushSync(() => setCount((c) => c + 1)); // рендер происходит сразу
    setCountOther((o) => o + 1); // второй рендер в конце обработчика
    console.log("handler end");
  }

  return (
    <>
      <p>Count: {count}</p>
      <button onClick={handleIncrementOne}>+1 (неправильно)</button>
      <button onClick={handleIncrementTwo}>+1 (правильно)</button>
      <button onClick={handleIncrementAsync}>+1 (async)</button>

      <p>countOther: {countOther}</p>
      <button onClick={handleIncrementOtherOne}>+1 (other)</button>
    </>
  );
}

export default CounterSnapshot;
