import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter } from "react-router";
import { RouterProvider } from "react-router/dom";
import App from "./App.tsx";
import DayFive from "./components/day-five/DayFive.tsx";
import DayFour from "./components/day-four/DayFour.tsx";
import DayOne from "./components/day-one/DayOne.tsx";
import DayThree from "./components/day-three/DayThree.tsx";
import DayTwo from "./components/day-two/DayTwo.tsx";
import "./index.css";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      { index: true, element: <DayFive /> }, // как бы текущий активный

      { path: "day-one", element: <DayOne /> },
      { path: "day-two", element: <DayTwo /> },
      { path: "day-three", element: <DayThree /> },
      { path: "day-four", element: <DayFour /> },
      { path: "day-five", element: <DayFive /> },
      { path: "*", element: <div>Page not found</div> },
    ],
    errorElement: <div>Routing Error</div>,
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>,
);
