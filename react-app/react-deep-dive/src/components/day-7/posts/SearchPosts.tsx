import styles from "./SearchPosts.module.css";

import axios from "axios";
import { useEffect, useState } from "react";
import { useDebounce } from "../large-text/useDebounce";
import { useLocalStorage } from "../large-text/useLocalStorage";
import type { PostDto } from "./PostDto";

function SearchPosts() {
  // Список постов
  const [posts, setPosts] = useState<PostDto[]>([]);
  // Текущий статус загрузки
  const [status, setStatus] = useState<string | null>(null);
  // Часть поискового запроса
  const [searchPart, setSearchPart] = useLocalStorage("searchPart", "");
  // Часть поискового запроса с задержкой
  const debounced = useDebounce(searchPart, 1000 * 2);

  useEffect(() => {
    const controller = new AbortController();

    function loadPosts(controller: AbortController, searchPart: string | null) {
      setStatus("загрузка...");
      const params = searchPart != null ? { q: searchPart } : {};
      axios
        .get<PostDto[]>("https://jsonplaceholder.typicode.com/posts", {
          params: params,
          signal: controller.signal,
        })
        .then((res) => {
          setPosts(res.data);
          setStatus(null);
        })
        .catch((e: Error) => {
          if (axios.isCancel(e)) return;

          const message = axios.isAxiosError(e) ? e.message : e.toString();
          setStatus(message);
          console.error(message);
        });
    }

    console.log("debounced:", debounced);
    loadPosts(controller, debounced ? debounced : null);
    return () => controller.abort();
  }, [debounced]);

  return (
    <>
      <input
        type="text"
        placeholder="Search"
        value={searchPart}
        onChange={(e) => setSearchPart(e.target.value)}
      />

      {status != null && <p>{status}</p>}

      {!status && (
        <div className={styles.postBoard}>
          {posts.map((post) => (
            <div key={post.id} className={styles.post}>
              <h2>{post.title}</h2>
              <p>{post.body}</p>
            </div>
          ))}
        </div>
      )}
    </>
  );
}

export default SearchPosts;
