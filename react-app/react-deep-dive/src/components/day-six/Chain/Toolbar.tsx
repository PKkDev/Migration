import type { UserChainProps } from "./Page";
import UserMenu from "./UserMenu";

function Toolbar(props: UserChainProps) {
  return (
    <>
      <UserMenu data={props.data}></UserMenu>
    </>
  );
}

export default Toolbar;
