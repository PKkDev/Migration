import type { ReactNode } from "react";

type PanelProps = { title: string; children: ReactNode };

/** содержимое между открывающим и закрывающим тегом попадает в компонент как проп children */
function Card({ title, children }: PanelProps) {
  return (
    <>
      <h1>{title}</h1>
      {children}
    </>
  );
}

export default Card;
