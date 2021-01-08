package se.l4.exobytes.benchmarks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import se.l4.exobytes.AnnotationSerialization;
import se.l4.exobytes.Expose;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingFormat;
import se.l4.exobytes.streaming.StreamingInput;

@Fork(value=1, warmups=1)
@Warmup(time=1, timeUnit=TimeUnit.SECONDS)
@Measurement(time=1, timeUnit=TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(1)
public class DeserializationComparisonBenchmark
{
	private static final byte[] CBOR;
	private static final String JSON_STRING;
	private static final byte[] JSON_BINARY;

	static
	{
		try
		{
			CBOR = Hex.decodeHex("bf686475726174696f6e1a000315966576616c756573746573742074657374207465737420f0908591ff");
			JSON_STRING = "{\"duration\":202134,\"value\":\"test test test \uD800\uDD51\"}";
			JSON_BINARY = JSON_STRING.getBytes();
		}
		catch(DecoderException e)
		{
			throw new Error(e);
		}
	}

	@Benchmark
	public void exobytesCBOR(ExobytesState state)
		throws IOException
	{
		ByteArrayInputStream stream = new ByteArrayInputStream(CBOR);
		try(StreamingInput in = StreamingFormat.CBOR.createInput(stream))
		{
			in.readObject(state.serializer);
		}
	}

	@Benchmark
	public void exobytesJSON(ExobytesState state)
		throws IOException
	{
		ByteArrayInputStream stream = new ByteArrayInputStream(JSON_BINARY);
		try(StreamingInput in = StreamingFormat.JSON.createInput(stream))
		{
			in.readObject(state.serializer);
		}
	}

	@Benchmark
	public void jacksonCBOR(JacksonCBORState state)
		throws IOException
	{
		ByteArrayInputStream stream = new ByteArrayInputStream(CBOR);
		state.mapper.readValue(stream, ObjectA.class);
	}

	@Benchmark
	public void jacksonJSON(JacksonJSONState state)
		throws IOException
	{
		ByteArrayInputStream stream = new ByteArrayInputStream(JSON_BINARY);
		state.mapper.readValue(stream, ObjectA.class);
	}

	@Benchmark
	public void fastjson()
		throws IOException
	{
		JSON.parseObject(JSON_STRING, ObjectA.class);
	}

	public static void main(String[] args)
		throws Exception
	{
		Options opt = new OptionsBuilder()
			.include(DeserializationComparisonBenchmark.class.getSimpleName())
			.build();

		new Runner(opt).run();
	}

	@AnnotationSerialization
	public static class ObjectA
	{
		@Expose
		@JsonProperty
		@JSONField
		private String value;

		@Expose
		@JsonProperty
		@JSONField
		private int duration;

		public int getDuration()
		{
			return duration;
		}

		public String getValue()
		{
			return value;
		}
	}

	@State(Scope.Benchmark)
	public static class ExobytesState
	{
		public Serializer<ObjectA> serializer;

		@Setup(Level.Trial)
		public void setUp()
			throws Throwable
		{
			serializer = Serializers.create()
				.build()
				.get(ObjectA.class);
		}
	}

	@State(Scope.Benchmark)
	public static class JacksonCBORState
	{
		public ObjectMapper mapper;

		@Setup(Level.Trial)
		public void setUp()
			throws Throwable
		{
			mapper = new ObjectMapper(new CBORFactory());
		}
	}

	@State(Scope.Benchmark)
	public static class JacksonJSONState
	{
		public ObjectMapper mapper;

		@Setup(Level.Trial)
		public void setUp()
			throws Throwable
		{
			mapper = new ObjectMapper();
		}
	}
}
