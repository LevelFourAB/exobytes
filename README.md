# Exobytes

Exobytes is a serialization library for Java 9+ supporting JSON, CBOR and
other formats. Exobytes is designed to give detailed control over the serialized
format and is built around mapping types to `Serializer` instances either by
manual implementation or via explicit annotations on a type.

## License

This project is licensed under the [MIT license](https://opensource.org/licenses/MIT),
see the file `LICENSE.md` for details.

## Features

* Low-level streaming input and output
* `Serializer` interface for encapsulation of how a specific type is written and read
* Fine-grained control over serialization
  * Annotation-based serializers
  * Custom `Serializer` implementation
* Support for injection via `InstanceFactory`

## Usage via Maven

```xml
<dependency>
  <groupId>se.l4.exobytes</groupId>
  <artifactId>exobytes</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Creating `Serializers`

The main entry point of the library is the type `Serializers`. It is recommended
to build a shared `Serializers` instance for your application and pass it
around:

```java
// Build an instance with all standard serializers and modules
Serializers serializers = Serializers.create()
  .build();
```

It's possible to set a custom instance factory to enable injection when using
annotation-based serializers:

```java
// Build an instance with all standard serializers and modules
Serializers serializers = Serializers.create()
  .withInstanceFactory(instanceFactoryHere)
  .build();
```

## Serializing and deserializing

Serializers are commonly looked up via their class:

```java
Serializer<String> serializer = serializers.get(String.class);
```

This serializer can then be used to write something to a `StreamingOutput`:

```java
try(StreamingOutput out = StreamingFormat.CBOR.createOutput(outputStream)) {
  out.writeObject(serializer, "value to write");
}
```

Or read via `StreamingInput`:

```java
try(StreamingInput in = StreamingFormat.CBOR.createInput(inputStream)) {
  String value = in.readObject(serializer);
}
```

## Annotation and reflection based serializers

In Exobytes classes are not serializable by default but instead require 
an explicit mapping. The most common mapping for types is using annotations and
reflection.

Annotate a class with `@AnnotationSerialization` and the library will resolve
a serializer by looking for `@Expose` annotations:

```java
@AnnotationSerialization
public class Employee {
  @Expose
  private long id;

  @Expose
  private String name;

  @Expose
  @TemporalHints.Timestamp
  private LocalDate hired;
}
```

## Custom serializer via `Use`

Types can also be made serializable in Exobytes by placing a `Use` annotation 
on them specifying a `Serializer` or `SerializerResolver` class that will be
used for the type.

Example of a class that holds a string but skips reflection-based serialization:

```java
@Use(StringHolder.SerializerImpl.class)
public class StringHolder {
  private final String value;

  public StringHolder(String value) {
    this.value = value;
  }
  
  /**
   * Serializer for StringHolder that reads and writes the class as a single
   * string.
   */
  public static class SerializerImpl implements Serializer<StringHolder> {
    
    public StringHolder read(StreamingInput in) throws IOException {
      // Consume the next token - and make sure it's a value
      in.next(Token.VALUE);
      
      // Read the string
      String value = in.readString();
      
      // Return the result that should be deserialized
      return new StringHolder(value);
    }

    public void write(StringHolder object, StreamingOutput out) throws IOException {
      // Simply write the value of the holder
      out.writeString(object.value);
    }
  
  }
}
```

## Dates and times

Most types from `java.time` are supported with detailed control of the format
via `Temporal`-annotations.

```java
@Expose
@TemporalHints.Precision(ChronoUnit.SECONDS)
@TemporalHints.Timestamp
public Instant created;

@Expose
@TemporalHints.Format
public Instant created;

@Expose
@TemporalHints.Format(TemporalHints.StandardFormat.ISO_WEEK_DATE)
public Instant created;

@Expose
@TemporalHints.CustomFormat("yyyy-MM-dd")
public ZonedDateTime time;
```

## Composing annotations

Exobytes supports meta annotations, where you can create an annotation that
in turns is annotated with Exobytes annotations. This functionality makes it
easy to create standards, such as if you always

```java
@TemporalHints.Precision(ChronoUnit.MILLISECONDS)
@TemporalHints.Timestamp
public @interface StorableTimestamp {
}
```
