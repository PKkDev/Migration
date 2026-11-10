import { useContext } from "react";
import { MissingContextError } from "../MissingContextError";
import { NotifContext, type NotifContextValue } from "./NotifContext";

export const useNotif = (): NotifContextValue => {
  const notifContext = useContext(NotifContext);
  if (notifContext === null) {
    throw new MissingContextError("useNotif", "NotifProvider");
  }
  return notifContext;
};
