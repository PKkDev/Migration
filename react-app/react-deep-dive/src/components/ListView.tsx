import type { ReactNode } from "react";

type ListProps<T> = {
  items: T[];
  renderItem: (item: T) => ReactNode;
};

function ListView<T>({ items, renderItem }: ListProps<T>) {
  return (
    <>
      <h1>Generic List</h1>
      <ul>
        {items.map((item, index) => (
          <li key={index}>{renderItem(item)}</li>
        ))}
      </ul>
    </>
  );
}

export default ListView;
