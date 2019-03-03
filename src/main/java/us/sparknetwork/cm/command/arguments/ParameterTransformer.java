package us.sparknetwork.cm.command.arguments;

public interface ParameterTransformer<T> {
    T transform(String param);
}
