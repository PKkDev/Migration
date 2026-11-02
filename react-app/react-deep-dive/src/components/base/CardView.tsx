import styles from "./CardView.module.css";

import type { ReactNode } from "react";

type PanelProps = { title: string; children: ReactNode };

/** содержимое между открывающим и закрывающим тегом попадает в компонент как проп children */
function CardView({ title, children }: PanelProps) {
  return (
    <div className={styles.card}>
      <h3 className={styles.title}>{title}</h3>
      {children}
    </div>
  );
}

export default CardView;
