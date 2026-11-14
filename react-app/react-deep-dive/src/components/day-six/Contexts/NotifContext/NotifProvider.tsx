import { useCallback, useMemo, useState, type ReactNode } from "react";
import { NotifContext } from "./NotifContext";

type PanelProps = { children: ReactNode };

function NotifProvider({ children }: PanelProps) {
  const [notifications, setNotifications] = useState<number>(0);
  // function updateNotifications() {
  //   setNotifications((prev) => prev + 1);
  // }

  const updateNotifications = useCallback(() => {
    setNotifications((prev) => prev + 1);
  }, []);

  const value = useMemo(
    () => {
      // console.log("NotifProvider: value пересчитан");
      return { notifications, updateNotifications };
    }, // функция, которая вычисляет значение
    [notifications, updateNotifications], // зависимости
  );

  return (
    <>
      {/* <NotifContext
        value={{
          notifications: notifications,
          updateNotifications: updateNotifications,
        }}
      >
        {children}
      </NotifContext> */}

      <NotifContext value={value}>{children}</NotifContext>
    </>
  );
}

export default NotifProvider;
