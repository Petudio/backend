package kuding.petudio.service.etc.callback;

import java.io.IOException;

public class ExceptionResolveTemplate {

    public <T> T execute(ExceptionResolveCallBack<T> exceptionResolveCallBack) {
        try {
            return exceptionResolveCallBack.call();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
