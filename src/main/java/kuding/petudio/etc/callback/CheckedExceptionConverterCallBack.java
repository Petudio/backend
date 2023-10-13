package kuding.petudio.etc.callback;

public interface CheckedExceptionConverterCallBack<T> {

    T call() throws Exception;
}
