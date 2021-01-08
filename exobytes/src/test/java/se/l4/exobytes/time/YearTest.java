package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Year;
import java.time.ZoneOffset;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class YearTest
	extends SerializerTest
{
	protected Serializer<Year> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(Year.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteDefault()
		throws IOException
	{
		Serializer<Year> serializer = serializer();

		StreamingInput in = write(out -> serializer.write(Year.of(2011), out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2011));
	}

	@Test
	public void testReadDefault()
		throws IOException
	{
		Serializer<Year> serializer = serializer();

		StreamingInput in = write(out -> out.writeLong(2011)).get();

		Year value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(Year.of(2011)));
	}

	@Test
	public void testWriteTimestampDefault()
		throws IOException
	{
		Serializer<Year> serializer = serializer(TemporalAnnotations.timestamp());

		StreamingInput in = write(out -> serializer.write(Year.of(2011), out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(
			Year.of(2011)
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
		Serializer<Year> serializer = serializer(TemporalAnnotations.timestamp());

		StreamingInput in = write(out -> out.writeLong(1293840000000l)).get();

		Year value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(Year.of(2011)));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<Year> serializer = serializer(
			TemporalAnnotations.format()
		);

		Year dt = Year.of(2011);
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("2011"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<Year> serializer = serializer(
			TemporalAnnotations.format()
		);

		StreamingInput in = write(out -> out.writeString("2011")).get();
		Year value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(Year.of(2011)));
	}

	@Test
	public void testWriteFormatCustom()
		throws IOException
	{
		Serializer<Year> serializer = serializer(
			TemporalAnnotations.customFormat("uu")
		);

		Year dt = Year.of(2011);
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("11"));
	}

	@Test
	public void testReadFormatCustom()
		throws IOException
	{
		Serializer<Year> serializer = serializer(
			TemporalAnnotations.customFormat("uu")
		);

		StreamingInput in = write(out -> out.writeString("11")).get();
		Year value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(Year.of(2011)));
	}
}
