package net.plugins.serialization.codecs;

import net.plugins.serialization.Decoder;
import net.plugins.serialization.Encoder;

public interface Codec<R> extends Encoder<R>, Decoder<R> {
}
