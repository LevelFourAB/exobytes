package se.l4.exobytes.benchmarks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

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
import se.l4.exobytes.streaming.StreamingOutput;

@Fork(value=1, warmups=1)
@Warmup(time=1, timeUnit=TimeUnit.SECONDS)
@Measurement(time=1, timeUnit=TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(1)
public class SerializationComparisonBenchmark
{
	private static ObjectA instance()
	{
		ObjectA a = new ObjectA();
		a.duration = 202134;
		a.value = "test test test \ud800\udd51";
		return a;
	}

	@Benchmark
	public void exobytesCBOR(ExobytesState state)
		throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try(StreamingOutput out = StreamingFormat.CBOR.createOutput(stream))
		{
			out.writeObject(state.serializer, instance());
		}
	}

	@Benchmark
	public void exobytesJSON(ExobytesState state)
		throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try(StreamingOutput out = StreamingFormat.JSON.createOutput(stream))
		{
			out.writeObject(state.serializer, instance());
		}
	}

	@Benchmark
	public void exobytesLegacyBinary(ExobytesState state)
		throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try(StreamingOutput out = StreamingFormat.LEGACY_BINARY.createOutput(stream))
		{
			out.writeObject(state.serializer, instance());
		}
	}

	@Benchmark
	public void jacksonCBOR(JacksonCBORState state)
		throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		state.mapper.writeValue(stream, instance());
	}

	@Benchmark
	public void jacksonJSON(JacksonJSONState state)
		throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		state.mapper.writeValue(stream, instance());
	}

	@Benchmark
	public void fastjson()
		throws IOException
	{
		String instance = JSON.toJSONString(instance());
	}

	public static void main(String[] args)
		throws Exception
	{
		Options opt = new OptionsBuilder()
			.include(SerializationComparisonBenchmark.class.getSimpleName())
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
