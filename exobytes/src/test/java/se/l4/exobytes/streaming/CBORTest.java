package se.l4.exobytes.streaming;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import se.l4.exobytes.internal.cbor.CBOROutput;

public class CBORTest
	extends StreamingFormatTest
{
	@Override
	protected StreamingFormat format()
	{
		return StreamingFormat.CBOR;
	}

	@Test
	public void testWriteInt0()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(0);
		});

		assertThat(data[0], is((byte) 0x00));
	}

	@Test
	public void testWriteInt1()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(1);
		});

		assertThat(data[0], is((byte) 0x01));
	}

	@Test
	public void testWriteInt10()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(10);
		});

		assertThat(data[0], is((byte) 0x0a));
	}

	@Test
	public void testWriteInt23()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(23);
		});

		assertThat(data[0], is((byte) 0x17));
	}

	@Test
	public void testWriteInt24()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(24);
		});

		assertThat(data[0], is((byte) 0x18));
		assertThat(data[1], is((byte) 0x18));
	}

	@Test
	public void testWriteInt25()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(25);
		});

		assertThat(data[0], is((byte) 0x18));
		assertThat(data[1], is((byte) 0x19));
	}

	@Test
	public void testWriteInt100()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(100);
		});

		assertThat(data[0], is((byte) 0x18));
		assertThat(data[1], is((byte) 0x64));
	}

	@Test
	public void testWriteInt500()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(500);
		});

		assertThat(data[0], is((byte) 0x19));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0xf4));
	}

	@Test
	public void testWriteInt1000()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(1000);
		});

		assertThat(data[0], is((byte) 0x19));
		assertThat(data[1], is((byte) 0x03));
		assertThat(data[2], is((byte) 0xe8));
	}

	@Test
	public void testWriteInt1000000()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(1000000);
		});

		assertThat(data[0], is((byte) 0x1a));
		assertThat(data[1], is((byte) 0x00));
		assertThat(data[2], is((byte) 0x0f));
		assertThat(data[3], is((byte) 0x42));
		assertThat(data[4], is((byte) 0x40));
	}

	@Test
	public void testWriteInt1000000000000()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeLong(1000000000000l);
		});

		assertThat(data[0], is((byte) 0x1b));
		assertThat(data[1], is((byte) 0x00));
		assertThat(data[2], is((byte) 0x00));
		assertThat(data[3], is((byte) 0x00));
		assertThat(data[4], is((byte) 0xe8));
		assertThat(data[5], is((byte) 0xd4));
		assertThat(data[6], is((byte) 0xa5));
		assertThat(data[7], is((byte) 0x10));
		assertThat(data[8], is((byte) 0x00));
	}

	/*
	@Test
	public void testWriteInt18446744073709551615()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeBigInteger(new BigInteger("18446744073709551615"));
		});

		assertThat(data[0], is((byte) 0x1b));
		assertThat(data[1], is((byte) 0xff));
		assertThat(data[2], is((byte) 0xff));
		assertThat(data[3], is((byte) 0xff));
		assertThat(data[4], is((byte) 0xff));
		assertThat(data[5], is((byte) 0xff));
		assertThat(data[6], is((byte) 0xff));
		assertThat(data[7], is((byte) 0xff));
		assertThat(data[8], is((byte) 0xff));
	}

	@Test
	public void testWriteInt18446744073709551616()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeBigInteger(new BigInteger("18446744073709551616"));
		});

		assertThat(data[0], is((byte) 0xc2));
		assertThat(data[1], is((byte) 0x49));
		assertThat(data[2], is((byte) 0x01));
		assertThat(data[3], is((byte) 0x00));
		assertThat(data[4], is((byte) 0x00));
		assertThat(data[5], is((byte) 0x00));
		assertThat(data[6], is((byte) 0x00));
		assertThat(data[7], is((byte) 0x00));
		assertThat(data[8], is((byte) 0x00));
		assertThat(data[9], is((byte) 0x00));
		assertThat(data[10], is((byte) 0x00));
	}
	*/

	@Test
	public void testWriteIntNegative1()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(-1);
		});

		assertThat(data[0], is((byte) 0x20));
	}

	@Test
	public void testWriteIntNegative10()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(-10);
		});

		assertThat(data[0], is((byte) 0x29));
	}

	@Test
	public void testWriteIntNegative100()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(-100);
		});

		assertThat(data[0], is((byte) 0x38));
		assertThat(data[1], is((byte) 0x63));
	}

	@Test
	public void testWriteIntNegative500()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(-500);
		});

		assertThat(data[0], is((byte) 0x39));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0xf3));
	}

	@Test
	public void testWriteIntNegative1000()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(-1000);
		});

		assertThat(data[0], is((byte) 0x39));
		assertThat(data[1], is((byte) 0x03));
		assertThat(data[2], is((byte) 0xe7));
	}

	/*
	@Test
	public void testWriteIntNegative18446744073709551616()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeBigInteger(new BigInteger("-18446744073709551616"));
		});

		assertThat(data[0], is((byte) 0xc3));
		assertThat(data[1], is((byte) 0x49));
		assertThat(data[2], is((byte) 0x01));
		assertThat(data[3], is((byte) 0x00));
		assertThat(data[4], is((byte) 0x00));
		assertThat(data[5], is((byte) 0x00));
		assertThat(data[6], is((byte) 0x00));
		assertThat(data[7], is((byte) 0x00));
		assertThat(data[8], is((byte) 0x00));
		assertThat(data[9], is((byte) 0x00));
		assertThat(data[10], is((byte) 0x00));
	}

	@Test
	public void testWriteHalf0_0()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeHalf(0.0f);
		});

		assertThat(data[0], is((byte) 0xf9));
		assertThat(data[1], is((byte) 0x00));
		assertThat(data[2], is((byte) 0x00));
	}

	@Test
	public void testWriteHalfNegative0_0()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeHalf(-0.0f);
		});

		assertThat(data[0], is((byte) 0xf9));
		assertThat(data[1], is((byte) 0x80));
		assertThat(data[2], is((byte) 0x00));
	}
	*/

	@Test
	public void testWriteFloat100000_0()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeFloat(100000.0f);
		});

		assertThat(data[0], is((byte) 0xfa));
		assertThat(data[1], is((byte) 0x47));
		assertThat(data[2], is((byte) 0xc3));
		assertThat(data[3], is((byte) 0x50));
		assertThat(data[4], is((byte) 0x00));
	}

	@Test
	public void testWriteFloat3_4028234663852886e38()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeFloat(3.4028234663852886e+38f);
		});

		assertThat(data[0], is((byte) 0xfa));
		assertThat(data[1], is((byte) 0x7f));
		assertThat(data[2], is((byte) 0x7f));
		assertThat(data[3], is((byte) 0xff));
		assertThat(data[4], is((byte) 0xff));
	}

	@Test
	public void testWriteFloatInfinity()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeFloat(Float.POSITIVE_INFINITY);
		});

		assertThat(data[0], is((byte) 0xfa));
		assertThat(data[1], is((byte) 0x7f));
		assertThat(data[2], is((byte) 0x80));
		assertThat(data[3], is((byte) 0x00));
		assertThat(data[4], is((byte) 0x00));
	}

	@Test
	public void testWriteFloatNegativeInfinity()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeFloat(Float.NEGATIVE_INFINITY);
		});

		assertThat(data[0], is((byte) 0xfa));
		assertThat(data[1], is((byte) 0xff));
		assertThat(data[2], is((byte) 0x80));
		assertThat(data[3], is((byte) 0x00));
		assertThat(data[4], is((byte) 0x00));
	}

	@Test
	public void testWriteFloatNaN()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeFloat(Float.NaN);
		});

		assertThat(data[0], is((byte) 0xfa));
		assertThat(data[1], is((byte) 0x7f));
		assertThat(data[2], is((byte) 0xc0));
		assertThat(data[3], is((byte) 0x00));
		assertThat(data[4], is((byte) 0x00));
	}

	@Test
	public void testWriteDouble1_0e300()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeDouble(1.0e+300);
		});

		assertThat(data[0], is((byte) 0xfb));
		assertThat(data[1], is((byte) 0x7e));
		assertThat(data[2], is((byte) 0x37));
		assertThat(data[3], is((byte) 0xe4));
		assertThat(data[4], is((byte) 0x3c));
		assertThat(data[5], is((byte) 0x88));
		assertThat(data[6], is((byte) 0x00));
		assertThat(data[7], is((byte) 0x75));
		assertThat(data[8], is((byte) 0x9c));
	}

	@Test
	public void testWriteDoubleNegative4_1()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeDouble(-4.1);
		});

		assertThat(data[0], is((byte) 0xfb));
		assertThat(data[1], is((byte) 0xc0));
		assertThat(data[2], is((byte) 0x10));
		assertThat(data[3], is((byte) 0x66));
		assertThat(data[4], is((byte) 0x66));
		assertThat(data[5], is((byte) 0x66));
		assertThat(data[6], is((byte) 0x66));
		assertThat(data[7], is((byte) 0x66));
		assertThat(data[8], is((byte) 0x66));
	}

	@Test
	public void testWriteDoubleInfinity()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeDouble(Double.POSITIVE_INFINITY);
		});

		assertThat(data[0], is((byte) 0xfb));
		assertThat(data[1], is((byte) 0x7f));
		assertThat(data[2], is((byte) 0xf0));
		assertThat(data[3], is((byte) 0x00));
		assertThat(data[4], is((byte) 0x00));
		assertThat(data[5], is((byte) 0x00));
		assertThat(data[6], is((byte) 0x00));
		assertThat(data[7], is((byte) 0x00));
		assertThat(data[8], is((byte) 0x00));
	}

	@Test
	public void testWriteDoubleNegativeInfinity()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeDouble(Double.NEGATIVE_INFINITY);
		});

		assertThat(data[0], is((byte) 0xfb));
		assertThat(data[1], is((byte) 0xff));
		assertThat(data[2], is((byte) 0xf0));
		assertThat(data[3], is((byte) 0x00));
		assertThat(data[4], is((byte) 0x00));
		assertThat(data[5], is((byte) 0x00));
		assertThat(data[6], is((byte) 0x00));
		assertThat(data[7], is((byte) 0x00));
		assertThat(data[8], is((byte) 0x00));
	}

	@Test
	public void testWriteDoubleNaN()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeDouble(Double.NaN);
		});

		assertThat(data[0], is((byte) 0xfb));
		assertThat(data[1], is((byte) 0x7f));
		assertThat(data[2], is((byte) 0xf8));
		assertThat(data[3], is((byte) 0x00));
		assertThat(data[4], is((byte) 0x00));
		assertThat(data[5], is((byte) 0x00));
		assertThat(data[6], is((byte) 0x00));
		assertThat(data[7], is((byte) 0x00));
		assertThat(data[8], is((byte) 0x00));
	}

	@Test
	public void testWriteNull()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeNull();
		});

		assertThat(data[0], is((byte) 0xf6));
	}

	@Test
	public void testWriteBooleanTrue()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeBoolean(true);
		});

		assertThat(data[0], is((byte) 0xf5));
	}

	@Test
	public void testWriteBooleanFalse()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeBoolean(false);
		});

		assertThat(data[0], is((byte) 0xf4));
	}

	@Test
	public void testWriteStringEmpty()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeString("");
		});

		assertThat(data[0], is((byte) 0x60));
	}

	@Test
	public void testWriteStringA()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeString("a");
		});

		assertThat(data[0], is((byte) 0x61));
		assertThat(data[1], is((byte) 0x61));
	}

	@Test
	public void testWriteStringIETF()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeString("IETF");
		});

		assertThat(data[0], is((byte) 0x64));
		assertThat(data[1], is((byte) 0x49));
		assertThat(data[2], is((byte) 0x45));
		assertThat(data[3], is((byte) 0x54));
		assertThat(data[4], is((byte) 0x46));
	}

	@Test
	public void testWriteStringQuoteAndBackslash()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeString("\"\\");
		});

		assertThat(data[0], is((byte) 0x62));
		assertThat(data[1], is((byte) 0x22));
		assertThat(data[2], is((byte) 0x5c));
	}

	@Test
	public void testWriteStringUnicode_00fc()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeString("\u00fc");
		});

		assertThat(data[0], is((byte) 0x62));
		assertThat(data[1], is((byte) 0xc3));
		assertThat(data[2], is((byte) 0xbc));
	}

	@Test
	public void testWriteStringUnicode_6c34()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeString("\u6c34");
		});

		assertThat(data[0], is((byte) 0x63));
		assertThat(data[1], is((byte) 0xe6));
		assertThat(data[2], is((byte) 0xb0));
		assertThat(data[3], is((byte) 0xb4));
	}

	@Test
	public void testWriteStringUnicode_d800_dd51()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeString("\ud800\udd51");
		});

		assertThat(data[0], is((byte) 0x64));
		assertThat(data[1], is((byte) 0xf0));
		assertThat(data[2], is((byte) 0x90));
		assertThat(data[3], is((byte) 0x85));
		assertThat(data[4], is((byte) 0x91));
	}

	@Test
	public void testWriteListZeroLength()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart(0);
			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x80));
	}

	@Test
	public void testWriteListFixedLength_1_2_3()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart(3);
				out.writeInt(1);
				out.writeInt(2);
				out.writeInt(3);
			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x83));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0x02));
		assertThat(data[3], is((byte) 0x03));
	}

	@Test
	public void testWriteListFixedLengthSub_1__2_3___4_5_()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart(3);

				out.writeInt(1);

				out.writeListStart(2);
					out.writeInt(2);
					out.writeInt(3);
				out.writeListEnd();

				out.writeListStart(2);
					out.writeInt(4);
					out.writeInt(5);
				out.writeListEnd();

			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x83));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0x82));
		assertThat(data[3], is((byte) 0x02));
		assertThat(data[4], is((byte) 0x03));
		assertThat(data[5], is((byte) 0x82));
		assertThat(data[6], is((byte) 0x04));
		assertThat(data[7], is((byte) 0x05));
	}

	@Test
	public void testWriteListFixedLengthWithObjectFixedLength()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart(2);
				out.writeString("a");

				out.writeObjectStart(1);
					out.writeString("b");
					out.writeString("c");
				out.writeObjectEnd();
			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x82));
		assertThat(data[1], is((byte) 0x61));
		assertThat(data[2], is((byte) 0x61));
		assertThat(data[3], is((byte) 0xa1));
		assertThat(data[4], is((byte) 0x61));
		assertThat(data[5], is((byte) 0x62));
		assertThat(data[6], is((byte) 0x61));
		assertThat(data[7], is((byte) 0x63));
	}

	@Test
	public void testWriteListFixedLength_1_to_25()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart(25);
				for(int i=1; i<=25; i++)
				{
					out.writeInt(i);
				}
			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x98));
		assertThat(data[1], is((byte) 0x19));
		assertThat(data[2], is((byte) 0x01));
		assertThat(data[3], is((byte) 0x02));
		assertThat(data[4], is((byte) 0x03));
		assertThat(data[5], is((byte) 0x04));
		assertThat(data[6], is((byte) 0x05));
		assertThat(data[7], is((byte) 0x06));
		assertThat(data[8], is((byte) 0x07));
		assertThat(data[9], is((byte) 0x08));
		assertThat(data[10], is((byte) 0x09));
		assertThat(data[11], is((byte) 0x0a));
		assertThat(data[12], is((byte) 0x0b));
		assertThat(data[13], is((byte) 0x0c));
		assertThat(data[14], is((byte) 0x0d));
		assertThat(data[15], is((byte) 0x0e));
		assertThat(data[16], is((byte) 0x0f));
		assertThat(data[17], is((byte) 0x10));
		assertThat(data[18], is((byte) 0x11));
		assertThat(data[19], is((byte) 0x12));
		assertThat(data[20], is((byte) 0x13));
		assertThat(data[21], is((byte) 0x14));
		assertThat(data[22], is((byte) 0x15));
		assertThat(data[23], is((byte) 0x16));
		assertThat(data[24], is((byte) 0x17));
		assertThat(data[25], is((byte) 0x18));
		assertThat(data[26], is((byte) 0x18));
		assertThat(data[27], is((byte) 0x18));
		assertThat(data[28], is((byte) 0x19));
	}

	@Test
	public void testWriteListIndeterminateLengthEmpty()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();
			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x9f));
		assertThat(data[1], is((byte) 0xff));
	}

	@Test
	public void testWriteListMixedLengths1()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();

				out.writeInt(1);

				out.writeListStart(2);
					out.writeInt(2);
					out.writeInt(3);
				out.writeListEnd();

				out.writeListStart();
					out.writeInt(4);
					out.writeInt(5);
				out.writeListEnd();

			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x9f));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0x82));
		assertThat(data[3], is((byte) 0x02));
		assertThat(data[4], is((byte) 0x03));
		assertThat(data[5], is((byte) 0x9f));
		assertThat(data[6], is((byte) 0x04));
		assertThat(data[7], is((byte) 0x05));
		assertThat(data[8], is((byte) 0xff));
		assertThat(data[9], is((byte) 0xff));
	}

	@Test
	public void testWriteListMixedLengths2()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();

				out.writeInt(1);

				out.writeListStart(2);
					out.writeInt(2);
					out.writeInt(3);
				out.writeListEnd();

				out.writeListStart(2);
					out.writeInt(4);
					out.writeInt(5);
				out.writeListEnd();

			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x9f));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0x82));
		assertThat(data[3], is((byte) 0x02));
		assertThat(data[4], is((byte) 0x03));
		assertThat(data[5], is((byte) 0x82));
		assertThat(data[6], is((byte) 0x04));
		assertThat(data[7], is((byte) 0x05));
		assertThat(data[8], is((byte) 0xff));
	}

	@Test
	public void testWriteListMixedLengths3()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart(3);

				out.writeInt(1);

				out.writeListStart(2);
					out.writeInt(2);
					out.writeInt(3);
				out.writeListEnd();

				out.writeListStart();
					out.writeInt(4);
					out.writeInt(5);
				out.writeListEnd();

			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x83));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0x82));
		assertThat(data[3], is((byte) 0x02));
		assertThat(data[4], is((byte) 0x03));
		assertThat(data[5], is((byte) 0x9f));
		assertThat(data[6], is((byte) 0x04));
		assertThat(data[7], is((byte) 0x05));
		assertThat(data[8], is((byte) 0xff));
	}

	@Test
	public void testWriteListMixedLengths4()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart(3);

				out.writeInt(1);

				out.writeListStart();
					out.writeInt(2);
					out.writeInt(3);
				out.writeListEnd();

				out.writeListStart(2);
					out.writeInt(4);
					out.writeInt(5);
				out.writeListEnd();

			out.writeListEnd();
		});

		assertThat(data[0], is((byte) 0x83));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0x9f));
		assertThat(data[3], is((byte) 0x02));
		assertThat(data[4], is((byte) 0x03));
		assertThat(data[5], is((byte) 0xff));
		assertThat(data[6], is((byte) 0x82));
		assertThat(data[7], is((byte) 0x04));
		assertThat(data[8], is((byte) 0x05));
	}

	@Test
	public void testWriteObjectZeroLength()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart(0);
			out.writeObjectEnd();
		});

		assertThat(data[0], is((byte) 0xa0));
	}

	@Test
	public void testWriteObjectFixedLength_1_2_3_4()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart(2);
				out.writeInt(1);
				out.writeInt(2);

				out.writeInt(3);
				out.writeInt(4);
			out.writeObjectEnd();
		});

		assertThat(data[0], is((byte) 0xa2));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0x02));
		assertThat(data[3], is((byte) 0x03));
		assertThat(data[4], is((byte) 0x04));
	}

	@Test
	public void testWriteObjectFixedLengthWithSubArray()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart(2);
				out.writeString("a");
				out.writeInt(1);

				out.writeString("b");
				out.writeListStart(2);
					out.writeInt(2);
					out.writeInt(3);
				out.writeListEnd();
			out.writeObjectEnd();
		});

		assertThat(data[0], is((byte) 0xa2));
		assertThat(data[1], is((byte) 0x61));
		assertThat(data[2], is((byte) 0x61));
		assertThat(data[3], is((byte) 0x01));
		assertThat(data[4], is((byte) 0x61));
		assertThat(data[5], is((byte) 0x62));
		assertThat(data[6], is((byte) 0x82));
		assertThat(data[7], is((byte) 0x02));
		assertThat(data[8], is((byte) 0x03));
	}

	@Test
	public void testWriteBytesZeroLength()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeByteArray(new byte[0]);
		});

		assertThat(data[0], is((byte) 0x40));
	}

	@Test
	public void testWriteBytesFixedLength()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeByteArray(new byte[] { 0x01, 0x02, 0x03, 0x04 });
		});

		assertThat(data[0], is((byte) 0x44));
		assertThat(data[1], is((byte) 0x01));
		assertThat(data[2], is((byte) 0x02));
		assertThat(data[3], is((byte) 0x03));
		assertThat(data[4], is((byte) 0x04));
	}

	@Test
	public void testWriteBytesIndeterminateLengthManualChunk()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			try(OutputStream stream = out.writeByteStream())
			{
				stream.write(0x01);
				stream.write(0x02);
				stream.flush();
				stream.write(0x03);
				stream.write(0x04);
				stream.write(0x05);
			}
		});

		assertThat(data[0], is((byte) 0x5f));
		assertThat(data[1], is((byte) 0x42));
		assertThat(data[2], is((byte) 0x01));
		assertThat(data[3], is((byte) 0x02));
		assertThat(data[4], is((byte) 0x43));
		assertThat(data[5], is((byte) 0x03));
		assertThat(data[6], is((byte) 0x04));
		assertThat(data[7], is((byte) 0x05));
		assertThat(data[8], is((byte) 0xff));
	}

	@Test
	public void testWriteTaggedInt()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			((CBOROutput) out).writeTag(1);

			out.writeInt(1363896240);
		});

		assertBinary(data, "c11a514b67b0");
	}

	@Test
	public void testReadNull()
		throws IOException
	{
		StreamingInput in = toInput("f6");
		assertThat(in.peek(), is(Token.NULL));
		assertThat(in.next(), is(Token.NULL));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadBooleanTrue()
		throws IOException
	{
		StreamingInput in = toInput("f5");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readBoolean(), is(true));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadBooleanFalse()
		throws IOException
	{
		StreamingInput in = toInput("f4");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readBoolean(), is(false));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadInt0()
		throws IOException
	{
		StreamingInput in = toInput("00");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(0));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadInt1()
		throws IOException
	{
		StreamingInput in = toInput("01");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadInt10()
		throws IOException
	{
		StreamingInput in = toInput("0a");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(10));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadInt23()
		throws IOException
	{
		StreamingInput in = toInput("17");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(23));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadInt24()
		throws IOException
	{
		StreamingInput in = toInput("1818");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(24));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadInt25()
		throws IOException
	{
		StreamingInput in = toInput("1819");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(25));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadInt100()
		throws IOException
	{
		StreamingInput in = toInput("1864");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(100));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadInt1000()
		throws IOException
	{
		StreamingInput in = toInput("1903e8");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1000));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadInt1000000()
		throws IOException
	{
		StreamingInput in = toInput("1a000f4240");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1000000));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadLong1000000()
		throws IOException
	{
		StreamingInput in = toInput("1b000000e8d4a51000");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(1000000000000l));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadIntNegative1()
		throws IOException
	{
		StreamingInput in = toInput("20");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(-1));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadIntNegative10()
		throws IOException
	{
		StreamingInput in = toInput("29");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(-10));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadIntNegative100()
		throws IOException
	{
		StreamingInput in = toInput("3863");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(-100));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadIntNegative1000()
		throws IOException
	{
		StreamingInput in = toInput("3903e7");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(-1000));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	/*
	@Test
	public void testReadHalf0_0()
		throws IOException
	{
		StreamingInput in = toInput("f90000");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readHalf(), is(0.0));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadHalfNegative0_0()
		throws IOException
	{
		StreamingInput in = toInput("f98000");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readHalf(), is(-0.0));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}
	*/

	@Test
	public void testReadDouble1_1()
		throws IOException
	{
		StreamingInput in = toInput("fb3ff199999999999a");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readDouble(), is(1.1));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadDouble1_0e300()
		throws IOException
	{
		StreamingInput in = toInput("fb7e37e43c8800759c");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readDouble(), is(1.0e300));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadFloat100000_0()
		throws IOException
	{
		StreamingInput in = toInput("fa47c35000");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readDouble(), is(100000.0));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadFloat3_4028234663852886e38()
		throws IOException
	{
		StreamingInput in = toInput("fa7f7fffff");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readDouble(), is(3.4028234663852886e+38));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadStringEmpty()
		throws IOException
	{
		StreamingInput in = toInput("60");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is(""));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadStringA()
		throws IOException
	{
		StreamingInput in = toInput("6161");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("a"));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadStringIETF()
		throws IOException
	{
		StreamingInput in = toInput("6449455446");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("IETF"));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadStringQuoteAndBackslash()
		throws IOException
	{
		StreamingInput in = toInput("62225c");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("\"\\"));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadString_00fc()
		throws IOException
	{
		StreamingInput in = toInput("62c3bc");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("\u00fc"));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadString_6c34()
		throws IOException
	{
		StreamingInput in = toInput("63e6b0b4");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("\u6c34"));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadString_d800_dd51()
		throws IOException
	{
		StreamingInput in = toInput("64f0908591");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("\ud800\udd51"));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadStringStreaming()
		throws IOException
	{
		StreamingInput in = toInput("7f657374726561646d696e67ff");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("streaming"));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadByteArrayEmpty()
		throws IOException
	{
		StreamingInput in = toInput("40");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readByteArray(), is(new byte[0]));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadByteArray_01020304()
		throws IOException
	{
		StreamingInput in = toInput("4401020304");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readByteArray(), is(new byte[] { 0x01, 0x02, 0x03, 0x04 }));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadByteArrayStreaming()
		throws IOException
	{
		StreamingInput in = toInput("5f42010243030405ff");
		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readByteArray(), is(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArrayEmpty()
		throws IOException
	{
		StreamingInput in = toInput("80");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));
		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArray_1_2_3()
		throws IOException
	{
		StreamingInput in = toInput("83010203");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(3));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArray_1__2_3__4_5__()
		throws IOException
	{
		StreamingInput in = toInput("8301820203820405");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(3));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(4));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(5));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArray_1_to_25()
		throws IOException
	{
		StreamingInput in = toInput("98190102030405060708090a0b0c0d0e0f101112131415161718181819");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		for(int i=1, n=25; i<=n; i++)
		{
			assertThat(in.peek(), is(Token.VALUE));
			assertThat(in.next(), is(Token.VALUE));
			assertThat(in.readInt(), is(i));
		}

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArrayDynamicLengthEmpty()
		throws IOException
	{
		StreamingInput in = toInput("9fff");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));
		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArrayDynamicMixedLength1()
		throws IOException
	{
		StreamingInput in = toInput("9f018202039f0405ffff");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(3));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(4));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(5));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArrayDynamicMixedLength2()
		throws IOException
	{
		StreamingInput in = toInput("9f01820203820405ff");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(3));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(4));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(5));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArrayDynamicMixedLength3()
		throws IOException
	{
		StreamingInput in = toInput("83018202039f0405ff");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(3));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(4));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(5));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArrayDynamicMixedLength4()
		throws IOException
	{
		StreamingInput in = toInput("83019f0203ff820405");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(3));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(4));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(5));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadArrayDynamicLength_1_to_25()
		throws IOException
	{
		StreamingInput in = toInput("9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		for(int i=1, n=25; i<=n; i++)
		{
			assertThat(in.peek(), is(Token.VALUE));
			assertThat(in.next(), is(Token.VALUE));
			assertThat(in.readInt(), is(i));
		}

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadObject()
		throws IOException
	{
		StreamingInput in = toInput("a0");
		assertThat(in.peek(), is(Token.OBJECT_START));
		assertThat(in.next(), is(Token.OBJECT_START));
		assertThat(in.peek(), is(Token.OBJECT_END));
		assertThat(in.next(), is(Token.OBJECT_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadObject_1_2_3_4()
		throws IOException
	{
		StreamingInput in = toInput("a201020304");
		assertThat(in.peek(), is(Token.OBJECT_START));
		assertThat(in.next(), is(Token.OBJECT_START));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readInt(), is(1));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readInt(), is(3));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(4));

		assertThat(in.peek(), is(Token.OBJECT_END));
		assertThat(in.next(), is(Token.OBJECT_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadObjectWithArray()
		throws IOException
	{
		StreamingInput in = toInput("a26161016162820203");
		assertThat(in.peek(), is(Token.OBJECT_START));
		assertThat(in.next(), is(Token.OBJECT_START));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readString(), is("a"));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readString(), is("b"));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(3));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.OBJECT_END));
		assertThat(in.next(), is(Token.OBJECT_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadObjectWithIndeterminateList()
		throws IOException
	{
		StreamingInput in = toInput("bf61610161629f0203ffff");
		assertThat(in.peek(), is(Token.OBJECT_START));
		assertThat(in.next(), is(Token.OBJECT_START));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readString(), is("a"));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readString(), is("b"));

		assertThat(in.peek(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.LIST_START));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(2));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(3));

		assertThat(in.peek(), is(Token.LIST_END));
		assertThat(in.next(), is(Token.LIST_END));

		assertThat(in.peek(), is(Token.OBJECT_END));
		assertThat(in.next(), is(Token.OBJECT_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadObjectIndeterminateLength()
		throws IOException
	{
		StreamingInput in = toInput("bf6346756ef563416d7421ff");
		assertThat(in.peek(), is(Token.OBJECT_START));
		assertThat(in.next(), is(Token.OBJECT_START));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readString(), is("Fun"));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readBoolean(), is(true));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readString(), is("Amt"));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(-2));

		assertThat(in.peek(), is(Token.OBJECT_END));
		assertThat(in.next(), is(Token.OBJECT_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadObjectIndeterminateLength2()
		throws IOException
	{
		StreamingInput in = toInput("bf6379756df66568656c6c6f66636f6f6b6965ff");
		assertThat(in.peek(), is(Token.OBJECT_START));
		assertThat(in.next(), is(Token.OBJECT_START));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readString(), is("yum"));

		assertThat(in.peek(), is(Token.NULL));
		assertThat(in.next(), is(Token.NULL));

		assertThat(in.peek(), is(Token.KEY));
		assertThat(in.next(), is(Token.KEY));
		assertThat(in.readString(), is("hello"));

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("cookie"));

		assertThat(in.peek(), is(Token.OBJECT_END));
		assertThat(in.next(), is(Token.OBJECT_END));
		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadTagged()
		throws IOException
	{
		StreamingInput in = toInput("c11a514b67b0");

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1363896240));

		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testReadTagged2()
		throws IOException
	{
		StreamingInput in = toInput("c1fb41d452d9ec200000");

		assertThat(in.peek(), is(Token.VALUE));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readFloat(), is(1363896240.5f));

		assertThat(in.peek(), is(Token.END_OF_STREAM));
	}
}
