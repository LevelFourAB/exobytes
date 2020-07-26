package se.l4.exobytes.streaming;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import se.l4.commons.io.IOConsumer;
import se.l4.commons.io.IOSupplier;

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
	public void testNull()
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
	public void testByte127()
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
	public void testByteNegative128()
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
	public void testInt()
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
	public void testIntNegative()
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
	public void testLong()
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
	public void testLongNegative()
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
	public void testLongLarge()
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
	public void testBooleanFalse()
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
	public void testBooleanTrue()
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
	public void testFloat()
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
	public void testDouble()
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
	public void testString()
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
	public void testByteArray()
		throws IOException
	{
		byte[] data = new byte[] { 0, -28, 42, 100 };
		IOSupplier<StreamingInput> in0 = write(out -> {
			out.writeBytes(data);
		});

		try(StreamingInput in = in0.get())
		{
			in.next(Token.VALUE);
			assertThat(in.readByteArray(), is(data));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testObjectEmpty()
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
	public void testObjectValues()
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
			in.next(Token.KEY);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("value1"));
			in.next(Token.KEY);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readLong(), is(12l));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testObjectValuesWithNull()
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
			in.next(Token.KEY);
			assertThat(in.readString(), is("key1"));
			in.next(Token.NULL);
			assertThat(in.readDynamic(), nullValue());
			in.next(Token.KEY);
			assertThat(in.readString(), is("key2"));
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(12));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testObjectValuesWithNull2()
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
			in.next(Token.KEY);
			assertThat(in.readString(), is("key1"));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("value1"));
			in.next(Token.KEY);
			assertThat(in.readString(), is("key2"));
			in.next(Token.NULL);
			assertThat(in.readDynamic(), nullValue());
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testListEmpty()
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
	public void testListWithSeveralValues()
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
}
