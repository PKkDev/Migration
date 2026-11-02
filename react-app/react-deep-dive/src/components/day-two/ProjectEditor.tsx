import { useRef, useState } from "react";
import styles from "./ProjectEditor.module.css";

type Task = { id: number; title: string; done: boolean };
type Project = {
  name: string | null;
  owner: { name: string; email: string } | null;
  tasks: Task[];
};

function ProjectEditor() {
  const [project, setProject] = useState<Project>(() => ({
    name: null,
    owner: null,
    tasks: [],
  }));

  /** Неуправляемый компонент. Значение считывается напрямую из DOM-элемента */
  const inputRef = useRef<HTMLInputElement>(null);

  console.log("render data: ", project);

  function handleAddTask() {
    const input = inputRef.current;
    if (!input) return;
    const title = input.value.trim();
    if (!title) return;
    const id = Date.now();

    setProject((prev) => ({
      ...prev,
      tasks: [...prev.tasks, { id, title, done: false }],
    }));

    input.value = "";
  }

  function handleDoneTask(id: number) {
    setProject((prev) => {
      const newTasks = prev.tasks.map((task) =>
        task.id === id ? { ...task, done: !task.done } : task,
      );
      return { ...prev, tasks: newTasks };
    });
  }

  function handleRemoveTask(id: number) {
    setProject((prev) => {
      const newTasks = prev.tasks.filter((task) => task.id !== id);
      return { ...prev, tasks: newTasks };
    });
  }

  function updateOwnerName(value: string) {
    setProject((prev) => {
      const updatedOwner = prev.owner
        ? { ...prev.owner, name: value }
        : { name: value, email: "" };

      return { ...prev, owner: updatedOwner };
    });
  }

  function updateOwnerEmail(value: string) {
    setProject((prev) => {
      const updatedOwner = prev.owner
        ? { ...prev.owner, email: value }
        : { name: "", email: value };

      return { ...prev, owner: updatedOwner };
    });
  }

  return (
    <>
      <p>project: {JSON.stringify(project)}</p>

      <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
        <div className={styles.formGroup}>
          <label>Name:</label>
          <input
            type="text"
            value={project.name ?? ""}
            onChange={(e) => {
              const name = e.target.value;
              setProject((prev) => {
                return { ...prev, name: name };
              });
            }}
          />
        </div>

        <div className={styles.formGroup}>
          <label>Owner name:</label>
          <input
            type="text"
            value={project.owner?.name ?? ""}
            onChange={(e) => updateOwnerName(e.target.value)}
          />
        </div>

        <div className={styles.formGroup}>
          <label>Owner email:</label>
          <input
            type="email"
            value={project.owner?.email ?? ""}
            onChange={(e) => updateOwnerEmail(e.target.value)}
          />
        </div>

        <div className={styles.formGroup}>
          <label>Задача:</label>
          <input type="text" ref={inputRef} />
          <button onClick={handleAddTask}>Добавить</button>
        </div>

        <div>
          {project.tasks.map((task) => (
            <div key={task.id}>
              <input
                type="checkbox"
                checked={task.done}
                onChange={() => handleDoneTask(task.id)}
              />
              {task.title}
              <button onClick={() => handleRemoveTask(task.id)}>Удалить</button>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

export default ProjectEditor;
