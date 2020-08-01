package se.l4.exobytes.streaming;

import java.util.Collection;
import java.util.Map;

import se.l4.exobytes.internal.streaming.objects.MapInput;

public class ObjectStreaming
{
	private ObjectStreaming()
	{
	}

	/**
	 * Create a {@link StreamingInput} over an object. The object can have been
	 * acquired with {@link StreamingInput#readDynamic()} or it's a
	 * {@link Map}, {@link Collection}, primitive type or a {@link String}.
	 *
	 * @param value
	 * @return
	 */
	public static final StreamingInput createInput(Object value)
	{
		return MapInput.resolveInput(value);
	}
}
