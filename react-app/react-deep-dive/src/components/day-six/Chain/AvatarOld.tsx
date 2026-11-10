import type { UserChainProps } from "./Page";

function AvatarOld({ data }: UserChainProps) {
  /** Или передаём через просы или через контекст */
  const name = data?.name;

  console.log("AvatarOld name: " + name);

  // После добавления AuthContext над <Outlet /> тут всегда будет контекст созданный в корне приложения,
  // а тот, что создаётся в page6 будет пересоздаваться

  return (
    <>
      <p>{name}</p>
    </>
  );
}

export default AvatarOld;
