import { useState } from "react";
import CardView from "../base/CardView";
import ChatList from "./ChatList";
import PriceStates from "./price/PriceStates";
import PriceWithHook from "./price/PriceWithHook";
import Timer from "./Timer";

function DayThree() {
  /**
   * В. Утечка.
   * Если скрыть таймер,то счётчик продолжит идти т.к. мы его не уничтожаем, при уничтожении компонента
   */
  const [isTimerVisible, setIsTimerVisible] = useState<boolean>(true);

  return (
    <>
      <button onClick={() => setIsTimerVisible((prev) => !prev)}>
        Stopwatch
      </button>
      <CardView title="Timer">{isTimerVisible && <Timer />}</CardView>

      <CardView title="ChatList">
        <ChatList />
      </CardView>

      <CardView title="PriceStates">
        <PriceStates />
      </CardView>

      <CardView title="PriceWithHook">
        <PriceWithHook />
      </CardView>
    </>
  );
}

export default DayThree;
