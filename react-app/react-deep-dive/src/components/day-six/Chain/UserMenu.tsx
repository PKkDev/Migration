import AvatarOld from "./AvatarOld";
import type { UserChainProps } from "./Page";

function UserMenu({ data }: UserChainProps) {
  return (
    <>
      <AvatarOld data={data}></AvatarOld>
    </>
  );
}

export default UserMenu;
