import "./App.css";
import Card from "./components/Card";
import Greeting from "./components/Greeting";
import GreetingCreateElement from "./components/GreetingCreateElement";
import ListView from "./components/ListView";

function App() {
  console.log("GreetingCreateElement: ", GreetingCreateElement());
  const dd = 0;

  return (
    <>
      <h1>Hello</h1>

      {/*оператор возвращает первый ложный операнд, то есть тот же 0 ||| 0 - falsy => false => 0*/}
      {dd && <b>видно</b>}

      {/* 0 - falsy => false =>  не видно*/}
      {dd ? <b>видно</b> : <b>не видно</b>}

      <GreetingCreateElement />

      <Card title="basic title">Test1</Card>

      <Card title="Title with Greeting">
        <GreetingCreateElement />
      </Card>

      <Greeting />

      <ListView
        items={[1, 2, 3]}
        renderItem={(number) => <span>{number * number}</span>}
      />

      <ListView
        items={["John", "Jane", "Bob"]}
        renderItem={(name) => <b>{name}</b>}
      />
    </>
  );
}

export default App;
