package kuding.petudio.service.etc.callback;

import java.io.IOException;

public class IoExceptionResolveTemplate {

    public <T> T execute(IoExceptionResolveCallBack<T> ioExceptionResolveCallBack) {
        try {
            T result = ioExceptionResolveCallBack.call();
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
