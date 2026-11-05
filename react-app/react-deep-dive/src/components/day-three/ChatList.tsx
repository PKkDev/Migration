import { useRef, useState } from "react";
import styles from "./ChatList.module.css";

type MessageType = { id: number; text: string };

function initializeMessages(): MessageType[] {
  const newMessages: MessageType[] = [];
  for (let i = 0; i < 100; i++) {
    newMessages.push({ id: i, text: "New message" + i });
  }
  return newMessages;
}

function ChatList() {
  const [messages, setMessages] = useState<MessageType[]>(() =>
    initializeMessages(),
  );

  const inputRef = useRef<HTMLInputElement>(null);
  function sendMessage(): void {
    const input = inputRef.current;
    if (!input) return;

    const title = input.value.trim();
    if (!title) return;

    setMessages((prev) => [...prev, { id: prev.length, text: title }]);
    input.value = "";
    input.focus();
  }

  function toLastMessage(): void {
    const lastMessage = messages[messages.length - 1];
    const map = getMap();
    const node = map.get(lastMessage.id);
    node?.scrollIntoView({
      behavior: "smooth",
      block: "nearest",
    });
  }

  const toMessageNumberRef = useRef<HTMLInputElement>(null);
  function toMessage(): void {
    const input = toMessageNumberRef.current;
    if (!input) return;

    const number = Number.parseInt(input.value.trim(), 10);
    if (isNaN(number)) return;

    const message = messages.find((x) => x.id === number);
    if (!message) return;
    const map = getMap();
    const node = map.get(message.id);
    node?.scrollIntoView({
      behavior: "smooth",
      block: "nearest",
    });
  }

  const itemsRef = useRef<Map<number, HTMLDivElement | null>>(null);
  function getMap(): Map<number, HTMLDivElement | null> {
    if (!itemsRef.current) {
      itemsRef.current = new Map();
    }
    return itemsRef.current;
  }

  return (
    <>
      <div>
        <button onClick={toMessage}>To message №</button>
        <input type="number" ref={toMessageNumberRef} />
      </div>

      <div>
        <button onClick={() => toLastMessage()}>To last Message</button>
      </div>

      <div>
        <input type="text" ref={inputRef} />
        <button onClick={() => sendMessage()}>Send</button>
      </div>

      <div className={styles.chatContainer}>
        {messages.map((message) => (
          <div
            className={styles.message}
            key={message.id}
            ref={(node) => {
              console.log(node);
              const map = getMap();
              map.set(message.id, node);
              return () => {
                map.delete(message.id);
              };
            }}
          >
            {message.text}
          </div>
        ))}
      </div>
    </>
  );
}

export default ChatList;
