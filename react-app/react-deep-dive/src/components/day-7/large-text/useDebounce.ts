import { useEffect, useState } from "react";

/**
 * используется для задержки обновления быстро меняющегося значения
 * @param value само значение, за которым нужно следить
 * @param delay время задержки в миллисекундах
 * @returns  новое значение только тогда, когда пользователь перестанет его менять в течение указанного времени
 */
export function useDebounce<T>(value: T, delay: number): T {
  console.log("useDebounce render: ", "value: " + value, "delay: " + delay);

  const [debounced, setDebounced] = useState(value);

  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(id); // новое значение или размонтирование: старый таймер отменяется
  }, [value, delay]);

  return debounced;
}
