import { Schema, type } from "@colyseus/schema";

class Fruit extends Schema {
    @type("string")
    key: string;

    @type("float32")
    x: number;

    @type("float32")
    y: number;

    @type("int32")
    color: number;
}

export default Fruit;