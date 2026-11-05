import { useState } from "react";
import { usePrevious } from "./usePrevious";

function PriceWithHook() {
  const [price, setPrice] = useState(0);
  const [futurePrice, setFuturePrice] = useState(0);
  const [clicks, setClicks] = useState(0);
  const previous = usePrevious(price);

    console.log("PriceWithHook render " + price); 

  function addTen() {
    setPrice((prev) => prev + 10);

    setFuturePrice(() => price + 10);
  }
  function minTen() {
    setPrice((prev) => prev - 10);

    setFuturePrice(() => price - 10);
  }
  function addOne() {
    setClicks((prev) => prev + 1);
  }

  return (
    <>
      <>
        <p>price: {price}</p>
        <p>clicks: {clicks}</p>
        <p>
          было: {previous ?? "—"} стало: {futurePrice}
        </p>

        <button onClick={addTen}>+10</button>
        <button onClick={minTen}>-10</button>
        <button onClick={addOne}>+1</button>
      </>
    </>
  );
}

export default PriceWithHook;
