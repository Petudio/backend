package kuding.petudio.service.etc.callback;

public interface ExceptionResolveCallBack<T> {

    T call() throws Exception;
}
