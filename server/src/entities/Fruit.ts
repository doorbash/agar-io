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

    angle = Math.PI * (Math.random() * 2 - 1);
    init_x: number
    init_y: number
}

export default Fruit;