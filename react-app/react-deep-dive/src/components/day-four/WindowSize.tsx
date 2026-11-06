import { useEffect, useState } from "react";

function WindowSize() {
  const [width, setWidth] = useState(() => window.innerWidth);
  const [height, setHeight] = useState(() => window.innerHeight);

  console.log("WindowSize render width:" + width + " height: " + height);

  useEffect(() => {
    const handleResize = () => {
      setWidth(window.innerWidth);
      setHeight(window.innerHeight);
    };

    console.log("WindowSize setup");
    window.addEventListener("resize", handleResize);
    return () => {
      console.log("WindowSize cleanup");
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  return (
    <>
      <p>width: {width}</p>
      <p>height: {height}</p>
    </>
  );
}

export default WindowSize;
