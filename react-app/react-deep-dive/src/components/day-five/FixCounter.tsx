import { useEffect, useRef, useState } from "react";
import ButtonsRow from "../base/ButtonsRow";

function FixCounter() {
  const [count, setCount] = useState(0);
  const countRef = useRef<number>(0);

  console.log("FixCounter init render");

  function updateCounter() {
    setCount((prev) => prev + 10);
  }

  function viewWrongAlert() {
    // покажет значение без учёта updateCounter т.е. то значение в count которое было в контексте функции на момент времени вызова viewAlert
    setTimeout(() => alert(count), 3000);
  }

  function viewActualAlert() {
    // покажет актуальное значение т.к. мы берём его из useRef
    setTimeout(() => alert(countRef.current), 3000);
  }

  useEffect(() => {
    countRef.current = count;
  });

  useEffect(() => {
    console.log("FixCounter setInterval");
    const intervalId = setInterval(() => {
      console.log("count: ", countRef.current);
      setCount((prev) => prev + 1);
    }, 1000);

    return () => {
      console.log("FixCounter clearInterval");
      clearInterval(intervalId);
    };
  }, []);

  return (
    <>
      <p>Count: {count}</p>
      <ButtonsRow>
        <button onClick={updateCounter}>+10</button>
        <button onClick={viewWrongAlert}>Показать wrong через 3 сек</button>
        <button onClick={viewActualAlert}>Показать actual через 3 сек</button>
      </ButtonsRow>
    </>
  );
}

export default FixCounter;

/*

Что такое замыкание и почему у каждого рендера «свои» обработчики и колбэки?
весь компонент react - функция, при инициализации заданы значения по умолчанию, во время рерндеров они могут изменяться
и каждая функция имеет свой контекст исполнения в рамках текущего рендера т.е. если мы объявили функцию ссылающуюся на count в useSate,
то он будет ссылаться именно на состояния count в момент добавления функции в очередь на исполнение 

Почему setInterval(() => setCount(count + 1), 1000) в эффекте с [] останавливается на 1?
он запускается только 1 раз при инициализации компонента и если значение count при инициализации 0, то 0 +1 = 1 и так будет всегда
на ререндере при изменении count он не реагирует

Что обещает массив зависимостей и что происходит, если «соврать»? Что ловит exhaustive-deps?
обещает, что useEffect будет вызван только при изменении значений зависимостей, если соврать - лишние вызовы

Назовите три способа починки stale closure и цену каждого.
[], [count], useRef

Почему alert(count) через 3 секунды показывает старое число и является ли это багом?
нет т.к. вызов alert(count) через 3 секунды оперирует контекстом исполнения в момент добавления этой функции т.е. не учитывает изменения значений в эти 3 секунды

Когда функционального апдейтера мало (нужно прочитать значение) и что использовать вместо него?
можно использовать useEffect

*/
