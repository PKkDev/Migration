import ButtonsRow from "../../base/ButtonsRow";
import { useNotif } from "../Contexts/NotifContext/useNotif";

function NotifBadge() {
  const notifContext = useNotif();

  const notif = notifContext.notifications;

  console.log("NotifBadge notif: " + notif);

  return (
    <>
      <p>{notif}</p>

      <ButtonsRow>
        <button
          onClick={() => {
            notifContext.updateNotifications();
          }}
        >
          +1 notif
        </button>
      </ButtonsRow>
    </>
  );
}

export default NotifBadge;
