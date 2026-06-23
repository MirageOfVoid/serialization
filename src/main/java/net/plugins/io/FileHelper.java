package net.plugins.io;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.serialization.codecs.Codec;
import net.plugins.serialization.codecs.MapCodec;
import net.plugins.util.Pair;
import net.plugins.util.Mapper;

import java.io.*;
import java.util.Scanner;

public class FileHelper<R, T> {
    private final Codec<R> codec;
    private final DynamicOps<T> ops;
    private final Mapper<T, String> mapper;

    protected boolean createFile = false;

    public FileHelper(Codec<R> codec, DynamicOps<T> ops, Mapper<T, String> mapper) {
        this.codec = codec;
        this.ops = ops;
        this.mapper = mapper;
    }

    public FileHelper(MapCodec<R> codec, DynamicOps<T> ops, Mapper<T, String> mapper) {
        this(codec.compress(), ops, mapper);
    }

    public void enableCreatingFile() {
        this.createFile = true;
    }

    protected DynamicOps<T> ops() {
        return ops;
    }

    protected Codec<R> codec() {
        return codec;
    }

    public DataResult<T> serialize(R r, File file) {
        if (!file.exists() && createFile) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                return DataResult.<T>error(e).flatMap(t -> codec.encodeStart(ops, r));
            }
        }

        return getContent(file).flatMap(content -> setContent(file, codec.encode(ops, r, content)));
    }

    public DataResult<Pair<R, T>> deserialize(File file) {
        return getContent(file).flatMap(content -> codec.decode(ops, content));
    }

    protected DataResult<T> getContent(File file) {
        try (Scanner scanner = new Scanner(file)) {
            StringBuilder result = new StringBuilder();
            while (scanner.hasNext()) result.append(scanner.nextLine());
            return mapper.applyFrom(result.toString());
        } catch (IOException e) {
            return DataResult.error(e);
        }
    }

    protected DataResult<String> createContent(DataResult<T> tResult) {
        return tResult.flatMap(mapper::applyTo);
    }
    
    protected DataResult<T> setContent(File file, DataResult<T> content) {
        return createContent(content).flatMap(s -> {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(s);
            } catch (IOException e) {
                return DataResult.error(e);
            }
            return content;
        });
    }
}
