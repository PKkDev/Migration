import CardView from "../base/CardView";
import CounterSnapshot from "./CounterSnapshot";
import ProjectEditor from "./ProjectEditor";

function DayTwo() {
  return (
    <>
      <CardView title="CounterSnapshot">
        <CounterSnapshot />
      </CardView>

      <CardView title="ProjectEditor">
        <ProjectEditor />
      </CardView>
    </>
  );
}

export default DayTwo;
