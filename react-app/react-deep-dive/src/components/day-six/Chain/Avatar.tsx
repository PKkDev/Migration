import ButtonsRow from "../../base/ButtonsRow";
import { makeRndStr } from "../../base/helper";
import { useAuth } from "../Contexts/AuthContext/useAuth";

function Avatar() {
  const authContext = useAuth();

  const name = authContext.user?.name;

  console.log("Avatar name: " + name);

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

export default Avatar;
