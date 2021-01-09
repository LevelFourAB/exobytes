package se.l4.exobytes.streaming;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import se.l4.exobytes.internal.streaming.JsonInput;

/**
 * Test for {@link JsonInput}. Runs a set of JSON documents and makes sure
 * that we return the correct tokens.
 */
public class JsonInputTest
	extends StreamingFormatTest
{
	@Override
	protected StreamingFormat format()
	{
		return StreamingFormat.JSON;
	}

	protected StreamingInput createInput(String in)
		throws IOException
	{
		return new JsonInput(new StringReader(in));
	}

	@Test
	public void testReadString()
		throws IOException
	{
		String v = "\"value\"";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.VALUE);
			assertThat(in.readString(), is("value"));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadNull()
		throws IOException
	{
		String v = "null";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.NULL);
			assertThat(in.readDynamic(), nullValue());
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadNumber()
		throws IOException
	{
		String v = "28291";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.VALUE);
			assertThat(in.readInt(), is(28291));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadStringQuote1()
		throws IOException
	{
		String v = "\"va\\\"lue\"";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.VALUE);
			assertThat(in.readString(), is("va\"lue"));
		}
	}

	@Test
	public void testReadStringQuote2()
		throws IOException
	{
		String v = "\"va\\nlue\"";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.VALUE);
			assertThat(in.readString(), is("va\nlue"));
		}
	}

	@Test
	public void testReadStringQuote3()
		throws IOException
	{
		String v = "\"va\\u4580lue\"";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.VALUE);
			assertThat(in.readString(), is("va\u4580lue"));
		}
	}

	@Test
	public void testReadBinary()
		throws IOException
	{
		String v = "\"a2FrYQ==\"";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.VALUE);
			assertThat(in.readByteArray(), is("kaka".getBytes(StandardCharsets.UTF_8)));
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadObjectEmpty()
		throws IOException
	{
		String v = "{}";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.OBJECT_START);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadObjectEmptyWhitespace()
		throws IOException
	{
		String v = "{ }";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.OBJECT_START);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadObjectValue()
		throws IOException
	{
		String v = "{\"key\": \"value\"}";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.OBJECT_START);
				in.next(Token.VALUE);
				assertThat(in.readString(), is("key"));

				in.next(Token.VALUE);
				assertThat(in.readString(), is("value"));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadObjectValues()
		throws IOException
	{
		String v = "{ \"key1\": \"value1\", \"key2\": \"value2\" }";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.OBJECT_START);
				in.next(Token.VALUE);
				assertThat(in.readString(), is("key1"));

				in.next(Token.VALUE);
				assertThat(in.readString(), is("value1"));

				in.next(Token.VALUE);
				assertThat(in.readString(), is("key2"));

				in.next(Token.VALUE);
				assertThat(in.readString(), is("value2"));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadObjectNullValue()
		throws IOException
	{
		String v = "{\"key\": null}";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.OBJECT_START);
				in.next(Token.VALUE);
				assertThat(in.readString(), is("key"));

				in.next(Token.NULL);
				assertThat(in.readDynamic(), nullValue());
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadObjectComplex1()
		throws IOException
	{
		String v = "{ \"key1\": \"value1\", \"key2\": [ \"value2\" ] }";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.OBJECT_START);
				in.next(Token.VALUE);
				assertThat(in.readString(), is("key1"));

				in.next(Token.VALUE);
				assertThat(in.readString(), is("value1"));

				in.next(Token.VALUE);
				assertThat(in.readString(), is("key2"));

				in.next(Token.LIST_START);
					in.next(Token.VALUE);
					assertThat(in.readString(), is("value2"));
				in.next(Token.LIST_END);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadComplexObject2()
		throws IOException
	{
		String v = "{\"languages\": [],\"fields\": {\"test\": {}}}";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.OBJECT_START);
				in.next(Token.VALUE);
				assertThat(in.readString(), is("languages"));

				in.next(Token.LIST_START);
				in.next(Token.LIST_END);

				in.next(Token.VALUE);
				assertThat(in.readString(), is("fields"));

				in.next(Token.OBJECT_START);
					in.next(Token.VALUE);
					assertThat(in.readString(), is("test"));

					in.next(Token.OBJECT_START);
					in.next(Token.OBJECT_END);
				in.next(Token.OBJECT_END);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadComplexObject3()
		throws IOException
	{
		String v = "{\"languages\": [],\"fields\": {\"test\": {\"type\": \"token\",\"primary\": true}}}";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.OBJECT_START);
				in.next(Token.VALUE);
				assertThat(in.readString(), is("languages"));

				in.next(Token.LIST_START);
				in.next(Token.LIST_END);

				in.next(Token.VALUE);
				assertThat(in.readString(), is("fields"));

				in.next(Token.OBJECT_START);
					in.next(Token.VALUE);
					assertThat(in.readString(), is("test"));

					in.next(Token.OBJECT_START);
						in.next(Token.VALUE);
						assertThat(in.readString(), is("type"));

						in.next(Token.VALUE);
						assertThat(in.readString(), is("token"));

						in.next(Token.VALUE);
						assertThat(in.readString(), is("primary"));

						in.next(Token.VALUE);
						assertThat(in.readBoolean(), is(true));
					in.next(Token.OBJECT_END);
				in.next(Token.OBJECT_END);
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadObjectKeyAfterBoolean()
		throws IOException
	{
		String v = "{\"languages\": false,\"fields\": \"value\"}";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.OBJECT_START);
				in.next(Token.VALUE);
				assertThat(in.readString(), is("languages"));

				in.next(Token.VALUE);
				assertThat(in.readBoolean(), is(false));

				in.next(Token.VALUE);
				assertThat(in.readString(), is("fields"));

				in.next(Token.VALUE);
				assertThat(in.readString(), is("value"));
			in.next(Token.OBJECT_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadComplexSkip()
		throws IOException
	{
		String v = "{\"languages\": false,\"fields\": {\"test\": {\"type\": \"token\",\"primary\": true}}}";
		StreamingInput input = createInput(v);

		// Fast forward
		input.next(Token.OBJECT_START);
		input.next(Token.VALUE);
		input.next(Token.VALUE);

		// Read fields key
		input.next(Token.VALUE);
		input.next();
		input.skip();

		// Read Object end
		input.next(Token.OBJECT_END);

		if(input.peek() != Token.END_OF_STREAM)
		{
			fail("Found " + input.peek() + " in the stream, stream should be empty");
		}
	}

	@Test
	public void testReadComplexReading()
		throws IOException
	{
		String v = "{ \"key1\": \"value1\", \"key2\": [ \"value2\" ], \"key3\": \"value3\" }";
		StreamingInput input = createInput(v);

		boolean ended = false;
		input.next(Token.OBJECT_START);
		while(input.peek() != Token.END_OF_STREAM)
		{
			switch(input.next())
			{
				case OBJECT_END:
					ended = true;
					break;
				case VALUE:
					String key = input.readString();
					if(key.equals("key1"))
					{
						input.next();
						assertThat(input.readString(), is("value1"));
					}
					else if(key.equals("key3"))
					{
						input.next();
						assertThat(input.readString(), is("value3"));
					}
					else
					{
						input.next();
						input.skip();
					}
				default:
					// Do nothing
			}
		}

		if(! ended) fail("Did not read of the object");
	}

	@Test
	public void testReadListEmpty()
		throws IOException
	{
		try(StreamingInput in = createInput("[]"))
		{
			in.next(Token.LIST_START);
			in.next(Token.LIST_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadListValue()
		throws IOException
	{
		String v = "[ \"one\" ]";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.LIST_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("one"));
			in.next(Token.LIST_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadListValues()
		throws IOException
	{
		String v = "[ \"one\", \"two\" ]";
		try(StreamingInput in = createInput(v))
		{
			in.next(Token.LIST_START);
			in.next(Token.VALUE);
			assertThat(in.readString(), is("one"));
			in.next(Token.VALUE);
			assertThat(in.readString(), is("two"));
			in.next(Token.LIST_END);
			in.next(Token.END_OF_STREAM);
		}
	}

	@Test
	public void testReadLonger()
		throws IOException
	{
		String v = "{\"show_all_inline_media\":false,\"id\":21789286,\"default_profile\":false,\"profile_background_color\":\"161616\",\"profile_image_url\":\"http:\\/\\/a0.twimg.com\\/profile_images\\/1661276913\\/profile.3_normal.jpg\",\"following\":false,\"statuses_count\":514,\"followers_count\":70,\"utc_offset\":null,\"profile_background_image_url\":\"http:\\/\\/a0.twimg.com\\/profile_background_images\\/325347562\\/x24afe3def97fbe3508dfacb66c97493.png\",\"screen_name\":\"aholstenson\",\"name\":\"Andreas Holstenson\",\"profile_link_color\":\"037EC4\",\"profile_background_image_url_https\":\"https:\\/\\/si0.twimg.com\\/profile_background_images\\/325347562\\/x24afe3def97fbe3508dfacb66c97493.png\",\"listed_count\":1,\"url\":\"http:\\/\\/holstenson.se\",\"protected\":false,\"follow_request_sent\":false,\"created_at\":\"Tue Feb 24 19:52:33 +0000 2009\",\"profile_use_background_image\":true,\"verified\":false,\"profile_image_url_https\":\"https:\\/\\/si0.twimg.com\\/profile_images\\/1661276913\\/profile.3_normal.jpg\",\"is_translator\":false,\"profile_text_color\":\"C503C5\",\"description\":\"Tror p\\u00e5 att vi bara n\\u00e5r \",\"notifications\":false,\"time_zone\":null,\"id_str\":\"21789286\",\"default_profile_image\":false,\"location\":\"\",\"profile_sidebar_border_color\":\"D8D8D8\",\"favourites_count\":0,\"contributors_enabled\":false,\"lang\":\"en\",\"geo_enabled\":true,\"friends_count\":212,\"profile_background_tile\":true,\"profile_sidebar_fill_color\":\"FFFFFF\"}";
		StreamingInput input = createInput(v);
		input.next(Token.OBJECT_START);
		while(input.peek() != Token.OBJECT_END)
		{
			switch(input.next())
			{
				case VALUE:
					input.next();
					input.skip();
				default:
					// Do nothing
			}
		}
	}

	@Test
	public void testWriteEmptyObject()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeObjectEnd();
		});

		assertString(data, "{}");
	}

	@Test
	public void testWriteEmptyList()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();
			out.writeListEnd();
		});

		assertString(data, "[]");
	}

	@Test
	public void testWriteString()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeString("value");
		});

		assertString(data, "\"value\"");
	}

	@Test
	public void testWriteChar()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeChar('v');
		});

		assertString(data, "\"v\"");
	}

	@Test
	public void testWriteInt()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(12);
		});

		assertString(data, "12");
	}

	@Test
	public void testWriteNegativeInt()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeInt(-12);
		});

		assertString(data, "-12");
	}

	@Test
	public void testWriteLong()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeLong(12l);
		});

		assertString(data, "12");
	}

	@Test
	public void testWriteFloat()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeFloat(12.0f);
		});

		assertString(data, "12.0");
	}

	@Test
	public void testWriteFloatMax()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeFloat(Float.MAX_VALUE);
		});

		assertString(data, "3.4028235E38");
	}

	@Test
	public void testWriteDouble()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeDouble(12.2);
		});

		assertString(data, "12.2");
	}

	@Test
	public void testWriteShort()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeShort((short) 12);
		});

		assertString(data, "12");
	}

	@Test
	public void testWriteKeyValueString()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeString("key");
			out.writeString("value");
			out.writeObjectEnd();
		});

		assertString(data, "{\"key\":\"value\"}");
	}

	@Test
	public void testWriteKeyValueInt()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeString("key");
			out.writeInt(12);
			out.writeObjectEnd();
		});

		assertString(data, "{\"key\":12}");
	}

	@Test
	public void testWriteKeyValueLong()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeString("key");
			out.writeLong(12l);
			out.writeObjectEnd();
		});

		assertString(data, "{\"key\":12}");
	}

	@Test
	public void testWriteKeyValueShort()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeString("key");
			out.writeInt((short) 12);
			out.writeObjectEnd();
		});

		assertString(data, "{\"key\":12}");
	}

	@Test
	public void testWriteKeyValueFloat()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeString("key");
			out.writeFloat(3.14f);
			out.writeObjectEnd();
		});

		assertString(data, "{\"key\":3.14}");
	}

	@Test
	public void testWriteKeyValueDouble()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeString("key");
			out.writeDouble(3.14);
			out.writeObjectEnd();
		});

		assertString(data, "{\"key\":3.14}");
	}

	@Test
	public void testWriteKeyValueObject()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeString("key");
			out.writeObjectStart();
			out.writeObjectEnd();
			out.writeObjectEnd();
		});

		assertString(data, "{\"key\":{}}");
	}

	@Test
	public void testWriteKeyValueList()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeString("key");
			out.writeListStart();
			out.writeListEnd();
			out.writeObjectEnd();
		});

		assertString(data, "{\"key\":[]}");
	}

	@Test
	public void testObjectMultipleKeys()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeObjectStart();
			out.writeString("key1");
			out.writeString("value1");
			out.writeString("key2");
			out.writeLong(12l);
			out.writeObjectEnd();
		});

		assertString(data, "{\"key1\":\"value1\",\"key2\":12}");
	}

	@Test
	public void testWriteListString()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();
			out.writeString("value");
			out.writeListEnd();
		});

		assertString(data, "[\"value\"]");
	}

	@Test
	public void testWriteListInt()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();
			out.writeInt(12);
			out.writeListEnd();
		});

		assertString(data, "[12]");
	}

	@Test
	public void testWriteListLong()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();
			out.writeLong(12l);
			out.writeListEnd();
		});

		assertString(data, "[12]");
	}

	@Test
	public void testWriteListShort()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();
			out.writeShort((short) 12);
			out.writeListEnd();
		});

		assertString(data, "[12]");
	}

	@Test
	public void testWriteListFloat()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();
			out.writeFloat(3.14f);
			out.writeListEnd();
		});

		assertString(data, "[3.14]");
	}

	@Test
	public void testWriteListDouble()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();
			out.writeDouble(3.14);
			out.writeListEnd();
		});

		assertString(data, "[3.14]");
	}

	@Test
	public void testWriteListMixed()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeListStart();
			out.writeInt(12);
			out.writeString("value");
			out.writeListEnd();
		});

		assertString(data, "[12,\"value\"]");
	}

	@Test
	public void testWriteByteArray()
		throws IOException
	{
		byte[] data = writeToBytes(out -> {
			out.writeByteArray("kaka".getBytes(StandardCharsets.UTF_8));
		});

		assertString(data, "\"a2FrYQ==\"");
	}


	private void assertString(byte[] data, String value)
	{
		assertThat(
			new String(data, StandardCharsets.UTF_8),
			is(value)
		);
	}
}
