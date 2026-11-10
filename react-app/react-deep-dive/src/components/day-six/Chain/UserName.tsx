import ButtonsRow from "../../base/ButtonsRow";
import { makeRndStr } from "../../base/helper";
import { useAuth } from "../Contexts/AuthContext/useAuth";

function UserName() {
  const authContext = useAuth();

  const name = authContext.user?.name;

  console.log("UserName name: " + name);

  return (
    <>
      <p>{name}</p>

      <ButtonsRow>
        <button
          onClick={() => authContext.login(makeRndStr(5) + " " + makeRndStr(5))}
        >
          login
        </button>
        <button onClick={() => authContext.logout()}>logout</button>
      </ButtonsRow>
    </>
  );
}

export default UserName;
