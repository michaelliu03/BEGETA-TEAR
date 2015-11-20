//import com.ning.http.client.AsyncHttpClient;

import io.netty.util.concurrent.Future;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import java.util.concurrent.ExecutionException;

/**
 * Created by kingson.wu on 2015/11/20.
 * {<a href='https://github.com/AsyncHttpClient/async-http-client'>@link</a>}
 */
public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        Future<Response> f = (Future<Response>) asyncHttpClient.prepareGet("http://baidu.com/").execute();
        Response r = f.get();

        System.out.println(r.getResponseBody());
    }
}
