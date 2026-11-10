import { useRouteError } from "react-router";

function ErrorPage() {
  const error = useRouteError() as Error;
  return <div>{error.message}</div>;
}

export default ErrorPage;
