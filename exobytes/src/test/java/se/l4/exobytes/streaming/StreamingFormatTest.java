package se.l4.exobytes.streaming;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import se.l4.ylem.io.IOConsumer;
import se.l4.ylem.io.IOSupplier;

/**
 * Abstract base class for testing of a {@link StreamingFormat}. Will test
 * that written data can be read.
 */
public abstract class StreamingFormatTest
{
	protected abstract StreamingFormat format();

	protected IOSupplier<StreamingInput> write(IOConsumer<StreamingOutput> output)
		throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try(StreamingOutput out = format().createOutput(stream))
		{
			output.accept(out);
		}

		byte[] input = stream.toByteArray();
		return () -> format().createInput(new ByteArrayInputStream(input));
	}

	protected byte[] writeToBytes(IOConsumer<StreamingOutput> output)
		throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try(StreamingOutput out = format().createOutput(stream))
		{
			output.accept(out);
		}

		return stream.toByteArray();
	}

	protected StreamingInput toInput(String hex)
	{
		try
		{
			byte[] data = Hex.decodeHex(hex);
			return format().createInput(new ByteArrayInputStream(data));
		}
		catch(DecoderException | IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	protected String toHex(byte[] data)
	{
		return Hex.encodeHexString(data);
	}

	protected void assertBinary(byte[] data, String hex)
	{
		String encoded = Hex.encodeHexString(data);
		for(int i=0, n=Math.min(encoded.length(), hex.length()); i<n; i++)
		{
			if(encoded.charAt(i) != hex.charAt(i))
			{
				int idx = i / 2;
				throw new AssertionError(
					"Mismatch at index " + idx + ", expected "
					+ hex.substring(idx, idx+2)
					+ " but got "
					+ encoded.substring(idx, idx+2)
					+ "\n  Expected: " + hex
					+ "\n  Actual: " + encoded
				);
			}
		}

		if(encoded.length() != hex.length())
		{
			throw new AssertionError(
				"Different number of bytes returned"
				+ "\n  Expected: " + hex
				+ "\n  Actual: " + encoded
			);
		}
	}

	@Test
	public void testSymmetryNull()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeNull();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.NULL);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryByte127()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeByte((byte) 127);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readByte(), is((byte) 127));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryByteNegative128()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeByte((byte) -128);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readByte(), is((byte) -128));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryInt()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(12);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(12));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryIntNegative()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(-2829);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(-2829));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryLong()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(1029l);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(1029l));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryLongNegative()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(-1029l);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(-1029l));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryLongLarge()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(1324475548554l);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(1324475548554l));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryBooleanFalse()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeBoolean(false);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readBoolean(), is(false));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryBooleanTrue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeBoolean(true);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readBoolean(), is(true));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryFloat()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeFloat(3.14f);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readFloat(), is(3.14f));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryDouble()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeDouble(89765.0);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readDouble(), is(89765.0));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryString()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeString("string value");
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readString(), is("string value"));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryByteArray()
		throws IOException
	{
		byte[] data = new byte[] { 0, -28, 42, 100 };
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeByteArray(data);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readByteArray(), is(data));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectEmpty()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectValues()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeString("value1");
			out.writeString("key2");
			out.writeLong(12l);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("value1"));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(12l));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectValuesWithNull()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeNull();
			out.writeString("key2");
			out.writeInt(12);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.NULL);
			assertThat(in.readDynamic(), nullValue());
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(12));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectValuesWithNull2()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeString("value1");
			out.writeString("key2");
			out.writeNull();
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("value1"));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.NULL);
			assertThat(in.readDynamic(), nullValue());
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectBooleanBoolean()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeBoolean(false);
			out.writeString("key2");
			out.writeBoolean(true);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readBoolean(), is(false));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readBoolean(), is(true));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectByteByte()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeByte((byte) -20);
			out.writeString("key2");
			out.writeByte((byte) 20);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readByte(), is((byte) -20));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readByte(), is((byte) 20));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectShortShort()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeShort((short) -20);
			out.writeString("key2");
			out.writeShort((short) 20);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readShort(), is((short) -20));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readShort(), is((short) 20));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectCharChar()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeChar((char) 20);
			out.writeString("key2");
			out.writeChar((char) 220);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readChar(), is((char) 20));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readChar(), is((char) 220));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectIntInt()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeInt(-20);
			out.writeString("key2");
			out.writeInt(20);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(-20));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(20));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectLongLong()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeLong(-20);
			out.writeString("key2");
			out.writeLong(20);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(-20l));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(20l));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectFloatFloat()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeFloat(-22.2f);
			out.writeString("key2");
			out.writeFloat(22.2f);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readFloat(), is(-22.2f));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readFloat(), is(22.2f));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryObjectDoubleDouble()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeDouble(-22.2);
			out.writeString("key2");
			out.writeDouble(22.2);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readDouble(), is(-22.2));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readDouble(), is(22.2));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryListEmpty()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeListStart();
			out.writeListEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.LIST_START);
			in.next(Token.LIST_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryListWithSeveralValues()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeListStart();
			out.writeString("value");
			out.writeInt(74749);
			out.writeListEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.LIST_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("value"));
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(74749));
			in.next(Token.LIST_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryBytesAsInputStreamNoRead()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			try(OutputStream o = out.writeByteStream())
			{
				for(int i=0; i<100; i++)
				{
					o.write(i);
				}

				o.flush();

				for(int i=0; i<100; i++)
				{
					o.write(i);
				}
			}
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			try(InputStream i = in.readByteStream())
			{
			}
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSymmetryBytesAsInputStreamRead()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			try(OutputStream stream = out.writeByteStream())
			{
				for(int i=0; i<100; i++)
				{
					stream.write(i);
				}

				stream.flush();

				for(int i=100; i<200; i++)
				{
					stream.write(i);
				}
			}
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			try(InputStream stream = in.readByteStream())
			{
				/*
				 * This splits the read into 3 buffer reads and 2 reads that
				 * should force chunked streams to do a split read and load at
				 * some point.
				 */

				byte[] buf = new byte[66];

				stream.readNBytes(buf, 0, 66);
				for(int i=0; i<66; i++)
				{
					assertThat((int) buf[i], is(i));
				}

				assertThat(stream.read(), is(66));

				stream.readNBytes(buf, 0, 66);
				for(int i=0; i<66; i++)
				{
					assertThat((int) buf[i] & 0xff, is(67 + i));
				}

				stream.readNBytes(buf, 0, 66);
				for(int i=0; i<66; i++)
				{
					assertThat((int) buf[i] & 0xff, is(133 + i));
				}

				assertThat(stream.read(), is(199));
			}
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testConversionByteToChar()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeByte((byte) 127);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readChar(), is((char) 127));
		}
	}

	@Test
	public void testConversionByteToShort()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeByte((byte) 127);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readShort(), is((short) 127));
		}
	}

	@Test
	public void testConversionByteToInt()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeByte((byte) 127);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readInt(), is(127));
		}
	}

	@Test
	public void testConversionByteToLong()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeByte((byte) 127);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readLong(), is(127l));
		}
	}

	@Test
	public void testConversionShortToByte()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeShort((short) 94);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readByte(), is((byte) 94));
		}
	}

	@Test
	public void testConversionShortToByteOverflow()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeShort((short) (Byte.MAX_VALUE + 1));
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThrows(IOException.class, () -> {
				in.readByte();
			}, "Expected exception to be thrown due to overflowing byte");
		}
	}

	@Test
	public void testConversionShortToChar()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeShort((short) 127);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readChar(), is((char) 127));
		}
	}

	@Test
	public void testConversionShortToInt()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeShort((short) 127);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readInt(), is(127));
		}
	}

	@Test
	public void testConversionShortToLong()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeShort((short) 127);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readLong(), is(127l));
		}
	}

	@Test
	public void testConversionIntToByte()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(94);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readByte(), is((byte) 94));
		}
	}

	@Test
	public void testConversionIntToByteOverflow()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(Byte.MAX_VALUE + 1);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThrows(IOException.class, () -> {
				in.readByte();
			}, "Expected exception to be thrown due to overflowing byte");
		}
	}

	@Test
	public void testConversionIntToChar()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(94);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readChar(), is((char) 94));
		}
	}

	@Test
	public void testConversionIntToCharOverflow()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(Character.MAX_VALUE + 1);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThrows(IOException.class, () -> {
				in.readChar();
			}, "Expected exception to be thrown due to overflowing char");
		}
	}

	@Test
	public void testConversionIntToShort()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(94);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readShort(), is((short) 94));
		}
	}

	@Test
	public void testConversionIntToShortOverflow()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(Short.MAX_VALUE + 1);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThrows(IOException.class, () -> {
				in.readShort();
			}, "Expected exception to be thrown due to overflowing short");
		}
	}

	@Test
	public void testConversionIntToLong()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(94);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readLong(), is(94l));
		}
	}

	@Test
	public void testConversionLongToByte()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(Byte.MAX_VALUE);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readByte(), is(Byte.MAX_VALUE));
		}
	}

	@Test
	public void testConversionLongToByteOverflow()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(Byte.MAX_VALUE + 1l);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThrows(IOException.class, () -> {
				in.readByte();
			}, "Expected exception to be thrown due to overflowing byte");
		}
	}

	@Test
	public void testConversionLongToChar()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(Character.MAX_VALUE);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readChar(), is(Character.MAX_VALUE));
		}
	}

	@Test
	public void testConversionLongToCharOverflow()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(Character.MAX_VALUE + 1l);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThrows(IOException.class, () -> {
				in.readChar();
			}, "Expected exception to be thrown due to overflowing int");
		}
	}

	@Test
	public void testConversionLongToShort()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(Short.MAX_VALUE);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readShort(), is(Short.MAX_VALUE));
		}
	}

	@Test
	public void testConversionLongToShortOverflow()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(Short.MAX_VALUE + 1l);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThrows(IOException.class, () -> {
				in.readShort();
			}, "Expected exception to be thrown due to overflowing short");
		}
	}

	@Test
	public void testConversionLongToInt()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(Integer.MAX_VALUE);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readInt(), is(Integer.MAX_VALUE));
		}
	}

	@Test
	public void testConversionLongToIntOverflow()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(Integer.MAX_VALUE + 1l);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThrows(IOException.class, () -> {
				in.readInt();
			}, "Expected exception to be thrown due to overflowing int");
		}
	}

	@Test
	public void testConversionFloatToDouble()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeFloat(22f);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat(in.readDouble(), is(22.0));
		}
	}

	@Test
	public void testConversionDoubleToFloat()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeDouble(Float.MAX_VALUE);
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));

			in.next(Token.VALUE);
			assertThat((double) in.readFloat(), is((double) Float.MAX_VALUE));
		}
	}

	@Test
	public void testNextConsumesObjectStartAndEnd()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesListStartAndEnd()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeListStart();
			out.writeListEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.LIST_START);
			in.next(Token.LIST_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesNull()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeNull();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.NULL);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesStringValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeString("value");
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesByteValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeByte((byte) 10);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesCharValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeChar((char) 10);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesShortValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeShort((short) 10);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesIntValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeInt(10);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesLongValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeLong(10);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesBooleanValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeBoolean(false);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesFloatValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeFloat(22.2f);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesDoubleValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeDouble(22.2);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesByteArrayValue()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeByteArray(new byte[] { 0x01, 0x02, 0x03 });
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testNextConsumesObjectWithMultipleKeys()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeString("value");
			out.writeString("key2");
			out.writeInt(100);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			in.next(Token.VALUE);
			in.next(Token.VALUE);
			in.next(Token.VALUE);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testPeekAfterValueRead()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeString("value");
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));
			in.next(Token.VALUE);
			in.readString();
			assertThat(in.peek(), is(Token.END_OF_STREAM));
			assertThat(in.next(), is(Token.END_OF_STREAM));
		}
	}

	@Test
	public void testPeekWithoutReadOrSkipFails()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeString("value");
		});

		try(StreamingInput in = in0.get())
		{
			assertThat(in.peek(), is(Token.VALUE));
			in.next(Token.VALUE);
			assertThrows(IOException.class, () -> {
				in.peek();
			});
		}
	}

	@Test
	public void testPeekAfterObjectStart()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeString("value");
			out.writeString("key2");
			out.writeInt(100);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			assertThat(in.peek(), is(Token.VALUE));
			in.next(Token.VALUE);
			in.next(Token.VALUE);
			in.next(Token.VALUE);
			in.next(Token.VALUE);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testPeekAfterObjectFirstKey()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeString("value");
			out.writeString("key2");
			out.writeInt(100);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.next(Token.VALUE);
			in.skip();
			assertThat(in.peek(), is(Token.VALUE));
			in.next(Token.VALUE);
			in.next(Token.VALUE);
			in.next(Token.VALUE);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testSkipObject()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeString("value");
			out.writeString("key2");
			out.writeInt(100);
			out.writeObjectEnd();
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			in.skip();
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void testReadDynamicObject()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeString("value");
			out.writeString("key2");
			out.writeInt(100);
			out.writeObjectEnd();
		});

		Object value;
		try(StreamingInput in = in0.get())
		{
			in.next(Token.OBJECT_START);
			value = in.readDynamic();
			in.next(Token.END_OF_STREAM);
		}

		assertThat(value, instanceOf(Map.class));
		Map<String, Object> asMap = (Map<String, Object>) value;
		assertThat(asMap.get("key1"), is("value"));
		assertThat(((Number) asMap.get("key2")).longValue(), is(100l));
	}

	@Test
	public void testDynamicNull()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic(null));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.NULL);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicBoolean()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic(true));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readBoolean(), is(true));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicByte()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic((byte) 22));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readByte(), is((byte) 22));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicShort()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic((short) 400));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readShort(), is((short) 400));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicChar()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic((char) 400));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readChar(), is((char) 400));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicInt()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic(203040));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(203040));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicLong()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic(203040l));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(203040l));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicFloat()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic(4.2f));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readFloat(), is(4.2f));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicDouble()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic(45.0));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readDouble(), is(45.0));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicByteArray()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic(new byte[] { 0x01, 0x02, 0x03 }));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readByteArray(), is(new byte[] { 0x01, 0x02, 0x03 }));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testDynamicString()
		throws IOException
	{
		IOSupplier<StreamingInput> in0 = write(out -> out.writeDynamic("test"));

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readString(), is("test"));
			in.next(Token.END_OF_STREAM);
		}
	}
}
