import { createContext } from "react";

export type NotifContextValue = {
  notifications: number;
  updateNotifications(): void;
};

export const NotifContext = createContext<NotifContextValue | null>(null);
