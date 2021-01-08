package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.YearMonth;
import java.time.ZoneOffset;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class YearMonthTest
	extends SerializerTest
{
	protected Serializer<YearMonth> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(YearMonth.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteTimestampDefault()
		throws IOException
	{
		Serializer<YearMonth> serializer = serializer(TemporalAnnotations.timestamp());

		StreamingInput in = write(out -> serializer.write(YearMonth.of(2011, 12), out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(
			YearMonth.of(2011, 12)
				.atDay(1)
				.atStartOfDay()
				.toInstant(ZoneOffset.UTC)
				.toEpochMilli()
		));
	}

	@Test
	public void testReadTimestampDefault()
		throws IOException
	{
		Serializer<YearMonth> serializer = serializer(TemporalAnnotations.timestamp());

		StreamingInput in = write(out -> out.writeLong(1322697600000l)).get();

		YearMonth value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(YearMonth.of(2011, 12)));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<YearMonth> serializer = serializer(
			TemporalAnnotations.format()
		);

		YearMonth dt = YearMonth.of(2011, 12);
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("2011-12"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<YearMonth> serializer = serializer(
			TemporalAnnotations.format()
		);

		StreamingInput in = write(out -> out.writeString("2011-12")).get();
		YearMonth value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(YearMonth.of(2011, 12)));
	}

	@Test
	public void testWriteFormatCustom()
		throws IOException
	{
		Serializer<YearMonth> serializer = serializer(
			TemporalAnnotations.customFormat("uu-MM")
		);

		YearMonth dt = YearMonth.of(2011, 12);
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("11-12"));
	}

	@Test
	public void testReadFormatCustom()
		throws IOException
	{
		Serializer<YearMonth> serializer = serializer(
			TemporalAnnotations.customFormat("uu-MM")
		);

		StreamingInput in = write(out -> out.writeString("11-12")).get();
		YearMonth value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(YearMonth.of(2011, 12)));
	}
}
