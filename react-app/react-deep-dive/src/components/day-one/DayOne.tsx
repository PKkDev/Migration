import Card from "./Card";
import Greeting from "./Greeting";
import GreetingCreateElement from "./GreetingCreateElement";
import ListView from "./ListView";

/** содержимое между открывающим и закрывающим тегом попадает в компонент как проп children */
function DayOne() {
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

export default DayOne;
