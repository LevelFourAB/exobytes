module se.l4.exobytes {
	requires com.github.spotbugs.annotations;

	requires transitive se.l4.ylem.types.reflect;
	requires transitive se.l4.ylem.types.mapping;
	requires transitive se.l4.ylem.types.instances;

	requires org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	exports se.l4.exobytes;
	exports se.l4.exobytes.array;
	exports se.l4.exobytes.collections;
	exports se.l4.exobytes.enums;
	exports se.l4.exobytes.standard;
	exports se.l4.exobytes.streaming;
	exports se.l4.exobytes.time;

	uses se.l4.exobytes.SerializersModule;
}
