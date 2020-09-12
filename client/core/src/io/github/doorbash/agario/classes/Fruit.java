//
// THIS FILE HAS BEEN GENERATED AUTOMATICALLY
// DO NOT CHANGE IT MANUALLY UNLESS YOU KNOW WHAT YOU'RE DOING
// 
// GENERATED USING @colyseus/schema 0.4.41
// 

package io.github.doorbash.agario.classes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.colyseus.annotations.SchemaClass;
import io.colyseus.annotations.SchemaField;
import io.colyseus.serializer.schema.Schema;

@SchemaClass
public class Fruit extends Schema {
	@SchemaField("0/float32")
	public float x = 0;

	@SchemaField("1/float32")	
	public float y = 0;

	@SchemaField("2/int32")	
	public int color = 0;

	public Vector2 position = new Vector2();
	public Color _color;
}

