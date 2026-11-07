import CardView from "../base/CardView";
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
      <CardView title="View Not View">
        {/*оператор возвращает первый ложный операнд, то есть тот же 0 ||| 0 - falsy => false => 0*/}
        {dd && <b>видно</b>}

        {/* 0 - falsy => false =>  не видно*/}
        {dd ? <b>видно</b> : <b>не видно</b>}
      </CardView>

      <CardView title="GreetingCreateElement">
        <GreetingCreateElement />
      </CardView>

      <CardView title="Card">
        <Card title="basic title">Test1</Card>
      </CardView>

      <CardView title="Card + GreetingCreateElement">
        <Card title="Title with Greeting">
          <GreetingCreateElement />
        </Card>
      </CardView>

      <CardView title="Greeting">
        <Greeting />
      </CardView>

      <CardView title="ListView NUMBER">
        <ListView
          items={[1, 2, 3]}
          renderItem={(number) => <span>{number * number}</span>}
        />
      </CardView>

      <CardView title="ListView STRING">
        <ListView
          items={["John", "Jane", "Bob"]}
          renderItem={(name) => <b>{name}</b>}
        />
      </CardView>
    </>
  );
}

export default DayOne;
