package kuding.petudio.service.etc.callback;

import java.io.IOException;

public interface IoExceptionResolveCallBack<T> {

    T call() throws IOException;
}
