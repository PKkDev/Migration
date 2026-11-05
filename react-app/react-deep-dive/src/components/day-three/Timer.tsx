import { useRef, useState } from "react";

function Timer() {
  console.log("Timer render");

  const [seconds, setSeconds] = useState<number>(0);
  const intervalRef = useRef<number | null>(null);

  /**
   * Б. Состояние вместо ref
   * лишний рендер на Start и Stop, и setInterval внутри апдейтера, который StrictMode вызывает дважды
   */
  //const [intervalId, setIntervalId] = useState<number | null>(null);

  /**
   * А. Локальная переменная вместо ref.
   * Start - запускает новые интервалы, при этом старые не останавливаются
   * Stop - ничего не останавливает
   */
  // let intervalId: number | null = null;

  function startTimer() {
    const intervalId = intervalRef.current;

    if (intervalId != null) return;
    intervalRef.current = setInterval(() => {
      setSeconds((prev) => prev + 1);
      console.log("tick from " + intervalRef.current);
    }, 1000);
  }

  function stopTimer() {
    const intervalId = intervalRef.current;

    if (intervalId != null) {
      clearInterval(intervalId);
      intervalRef.current = null;
    }
  }

  function resetTimer() {
    stopTimer();
    setSeconds(0);
  }

  return (
    <>
      <p> {formatTimer(seconds)} </p>
      <button onClick={() => startTimer()}>Start</button>
      <button onClick={() => stopTimer()}>Stop</button>
      <button onClick={() => resetTimer()}>Reset</button>
    </>
  );
}

export default Timer;

function formatTimer(seconds: number) {
  const minutes = Math.floor(seconds / 60);
  const secondsStr = seconds % 60;
  return `${minutes.toString().padStart(2, "0")}:${secondsStr.toString().padStart(2, "0")}`;
}
