module se.l4.exobytes {
	requires com.github.spotbugs.annotations;

	requires transitive se.l4.commons.types;
	requires transitive se.l4.commons.io;

	exports se.l4.exobytes;
	exports se.l4.exobytes.collections;
	exports se.l4.exobytes.enums;
	exports se.l4.exobytes.format;
	exports se.l4.exobytes.standard;
}
