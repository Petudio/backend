package kuding.petudio.service.etc.callback;

public interface CheckedExceptionConverterCallBack<T> {

    T call() throws Exception;
}
