import axios from "axios";
import { useEffect, useState } from "react";
import ButtonsRow from "../base/ButtonsRow";
import type { UserDto } from "./models/User";

function UserCard() {
  const [user, setUser] = useState<UserDto | null>(null);
  const [userId, setUserId] = useState<number>(1);
  // ошибка хранится вместе с id запроса: ошибка прежнего id не мешает текущему
  const [error, setError] = useState<{ id: number; message: string } | null>(
    null,
  );

  function loadUserData(controller: AbortController, id: number) {
    axios
      .get<UserDto>(`https://jsonplaceholder.typicode.com/users/${id}`, {
        signal: controller.signal,
      })
      .then((response) => {
        console.log("Get user response:" + JSON.stringify(response.data));
        setUser(response.data);
        setError(null);
      })
      .catch((e: Error) => {
        if (axios.isCancel(e)) return;

        const message = axios.isAxiosError(e) ? e.message : e.toString();
        setError({ id, message });
      });
  }

  useEffect(() => {
    console.log("UserCard setup");
    const controller = new AbortController();
    loadUserData(controller, userId);
    return () => {
      console.log("UserCard cleanup");
      controller.abort();
    };
  }, [userId]);

  // всё, что показываем, выводится из userId: данные и ошибка принимаются, только если они для текущего id
  const currentUser = user?.id === userId ? user : null;
  const currentError = error?.id === userId ? error : null;
  const isLoading = !currentUser && !currentError;

  return (
    <>
      {isLoading && <div>Загрузка данных...</div>}

      {currentError && <div>Ошибка: {currentError.message}</div>}

      {currentUser && (
        <div>
          <p>имя: {currentUser.name} </p>
          <p>email: {currentUser.email}</p>
          <p>город: {currentUser.address.city}</p>
        </div>
      )}

      <ButtonsRow>
        <button
          disabled={userId <= 1}
          onClick={() => setUserId((prev) => prev - 1)}
        >
          prev
        </button>
        <button
          disabled={userId >= 10}
          onClick={() => setUserId((prev) => prev + 1)}
        >
          next
        </button>
        <p>id: {userId}</p>
      </ButtonsRow>
    </>
  );
}

export default UserCard;
