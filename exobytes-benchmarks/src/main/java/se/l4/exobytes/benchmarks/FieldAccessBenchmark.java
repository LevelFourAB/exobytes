package se.l4.exobytes.benchmarks;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Fork(value=1, warmups=1)
public class FieldAccessBenchmark
{
	@Benchmark
	public void reflectionSet(CurrentState state)
		throws Throwable
	{
		state.field.set(new TestClass(), "value");
	}

	@Benchmark
	public void varHandleSet(CurrentState state)
		throws Throwable
	{
		state.varHandle.set(new TestClass(), "value");
	}

	@Benchmark
	public void methodHandleSet(CurrentState state)
		throws Throwable
	{
		state.setter.invoke(new TestClass(), "value");
	}

	@Benchmark
	public void reflectionGet(CurrentState state)
		throws Throwable
	{
		state.field.get(new TestClass());
	}

	@Benchmark
	public void varHandleGet(CurrentState state)
		throws Throwable
	{
		state.varHandle.get(new TestClass());
	}

	@Benchmark
	public void methodHandleGet(CurrentState state)
		throws Throwable
	{
		state.getter.invoke(new TestClass());
	}

	public static class TestClass
	{
		public String name;
	}

	@State(Scope.Benchmark)
	public static class CurrentState
	{
		public Field field;
		VarHandle varHandle;
		MethodHandle getter;
		MethodHandle setter;

		@Setup(Level.Trial)
		public void setUp()
			throws Throwable
		{
			field = TestClass.class.getDeclaredField("name");
			field.setAccessible(true);

			varHandle = MethodHandles.privateLookupIn(TestClass.class, MethodHandles.lookup())
				.findVarHandle(TestClass.class, "name", String.class);
			getter = MethodHandles.lookup()
				.unreflectGetter(field);
			setter = MethodHandles.lookup()
				.unreflectSetter(field);
		}
	}

	public static void main(String[] args)
		throws Exception
	{
		Options opt = new OptionsBuilder()
			.include(FieldAccessBenchmark.class.getSimpleName())
			.build();

		new Runner(opt).run();
	}
}
