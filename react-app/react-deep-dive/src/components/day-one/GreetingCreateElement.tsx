import { createElement } from "react";

/** Показать разметка это вызовы функции createElement(тип, пропсы, ...дочерние) */
function GreetingCreateElement() {
  return createElement(
    "div",
    null,
    "GreetingCreateElement",
    createElement("h1", null, "GreetingCreateElement"),
    createElement(
      "ul",
      null,
      createElement("li", null, "первый"),
      createElement("li", null, "второй"),
      createElement("li", null, "третий"),
    ),
  );
}

export default GreetingCreateElement;
