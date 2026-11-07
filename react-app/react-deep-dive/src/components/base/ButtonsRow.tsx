import styles from "./ButtonsRow.module.css";

import type { ReactNode } from "react";

type PanelProps = { children: ReactNode };

/** содержимое между открывающим и закрывающим тегом попадает в компонент как проп children */
function ButtonsRow({ children }: PanelProps) {
  return <div className={styles.row}>{children}</div>;
}

export default ButtonsRow;
