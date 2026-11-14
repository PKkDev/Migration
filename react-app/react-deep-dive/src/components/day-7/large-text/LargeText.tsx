import { useEffect, useState } from "react";
import CardView from "../../base/CardView";
import { useDebounce } from "./useDebounce";
import { useLocalStorage } from "./useLocalStorage";

function LargeText() {
  const [text, setText] = useState("");
  const debounced = useDebounce(text, 1000 * 2);

  const [storedValue, setStoredValue] = useLocalStorage("text", "");

  console.log("LargeText render: ", "text: " + text, "debounced: " + debounced);

  function handleChange(e: HTMLTextAreaElement) {
    setText(e.value);
    console.log(e.value);
  }

  useEffect(() => {
    console.log("debounced:", debounced);
    if (debounced) {
      setStoredValue(debounced);
    }
  }, [debounced, setStoredValue]);

  return (
    <>
      <textarea
        rows={10}
        cols={30}
        onChange={(e) => handleChange(e.target)}
      ></textarea>

      <CardView title="debounced">{debounced}</CardView>
      <CardView title="storedValue">{storedValue}</CardView>
    </>
  );
}

export default LargeText;

// Что в логах, не осталось ли таймера? Запишите в комментарии: что будет, если убрать clearTimeout?
// таймер очиститься
