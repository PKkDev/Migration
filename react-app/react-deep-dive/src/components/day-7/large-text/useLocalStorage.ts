import { useCallback, useState } from "react";

/**
 * используется для работы с localstorage
 * @param key ключ для хранилища
 * @param initialValue инициальное значение
 * @returns [значение, функция для установки значения]
 */
export function useLocalStorage<T>(
  key: string,
  initialValue: T,
): [T, (value: T) => void] {
  const [value, setValue] = useState<T>(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item !== null ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error("useLocalStorage storedValue", error);
      return initialValue;
    }
  });

  // const save = (value: T) => {
  //   setValue(value);
  //   try {
  //     window.localStorage.setItem(key, JSON.stringify(value));
  //   } catch (error) {
  //     console.error("useLocalStorage save", error);
  //   }
  // };

  const save = useCallback(
    (next: T) => {
      setValue(next);
      try {
        localStorage.setItem(key, JSON.stringify(next));
      } catch (error) {
        console.error("useLocalStorage useCallback(save)", error);
      }
    },
    [key],
  );

  return [value, save];
}
