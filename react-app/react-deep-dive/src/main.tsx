import { createRoot } from "react-dom/client";
import { createBrowserRouter } from "react-router";
import { RouterProvider } from "react-router/dom";
import App from "./App.tsx";
import ErrorPage from "./components/base/ErrorPage.tsx";
import DaySeven from "./components/day-7/DaySeven.tsx";
import DayFive from "./components/day-five/DayFive.tsx";
import DayFour from "./components/day-four/DayFour.tsx";
import DayOne from "./components/day-one/DayOne.tsx";
import DaySix from "./components/day-six/DaySix.tsx";
import DayThree from "./components/day-three/DayThree.tsx";
import DayTwo from "./components/day-two/DayTwo.tsx";
import "./index.css";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      { index: true, element: <DaySeven /> }, // как бы текущий активный

      { path: "day-one", element: <DayOne /> },
      { path: "day-two", element: <DayTwo /> },
      { path: "day-three", element: <DayThree /> },
      { path: "day-four", element: <DayFour /> },
      { path: "day-five", element: <DayFive /> },
      { path: "day-six", element: <DaySix /> },
      { path: "day-seven", element: <DaySeven /> },
      { path: "*", element: <div>Page not found</div> },
    ],
    errorElement: <ErrorPage />,
  },
]);

createRoot(document.getElementById("root")!).render(
  // <StrictMode>
    <RouterProvider router={router} />
  // </StrictMode>,
);
