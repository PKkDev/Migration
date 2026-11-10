import { useState } from "react";
import ButtonsRow from "../../base/ButtonsRow";
import CardView from "../../base/CardView";
import { makeRndStr } from "../../base/helper";
import type { PageUserType } from "../Contexts/AuthContext/PageUserType";
import Avatar from "./Avatar";
import NotifBadge from "./NotifBadge";
import Toolbar from "./Toolbar";
import UserName from "./UserName";

/** Единый тип пропсов для всех компонентов в цепочке */
export type UserChainProps = {
  data: PageUserType | null;
};

function Page() {
  const [user, setUser] = useState<PageUserType | null>(null);

  function handleUpdateName(): void {
    setUser(() => ({ name: makeRndStr(5) + " " + makeRndStr(5) }));
  }

  return (
    <>
      <CardView title="WithOut Context">
        <Toolbar data={user}></Toolbar>
        <ButtonsRow>
          <button onClick={handleUpdateName}>update name</button>
        </ButtonsRow>
      </CardView>

      <CardView title="With Context">
        <CardView title="Avatar">
          <Avatar></Avatar>
        </CardView>

        <CardView title="UserName">
          <UserName></UserName>
        </CardView>

        <CardView title="NotifBadge">
          <NotifBadge></NotifBadge>
        </CardView>
      </CardView>
    </>
  );
}

export default Page;
