import { useEffect, useRef } from "react";

export function usePrevious<T>(value: T): T | undefined {
  const ref = useRef<T | undefined>(undefined); // ref для актуального значения

  useEffect(() => {
    ref.current = value; // после рендера сохраняем в ref актуальное значение
  });

  // eslint-disable-next-line react-hooks/refs
  return ref.current; // возвращаем актуальное значение из ref (оно будет актуальным на предыдущий рендер)
}
