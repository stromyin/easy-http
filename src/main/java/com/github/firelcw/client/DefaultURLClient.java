package com.github.firelcw.client;

import com.github.firelcw.exception.EasyHttpException;
import com.github.firelcw.model.HttpMethod;
import com.github.firelcw.model.HttpRequest;
import com.github.firelcw.model.HttpRequestConfig;
import com.github.firelcw.model.HttpResponse;
import com.github.firelcw.util.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import static com.github.firelcw.util.Utils.*;
import static java.lang.String.format;

/**
 * @author liaochongwei
 * @date 2020/8/26 14:28
 */
public class DefaultURLClient extends AbstractClient{

    public static DefaultURLClient getInstance() {
        return DefaultURLClient.ClientInstance.INSTANCE;

    }
    public static class ClientInstance {
        private ClientInstance() {
        }
        private static final DefaultURLClient INSTANCE = new DefaultURLClient();
    }


    @Override
    public HttpResponse request(HttpRequest param) {
        HttpRequestConfig config = super.getConfig();

        Entity entity = new Entity();
        entity.init(param);

        this.addDefaultHeaders(param);

        final URL url;
        final HttpURLConnection connection;
        try {
            url = new URL(entity.url);
            connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(config.getConnectTimeout());
            connection.setReadTimeout(config.getRequestTimeout());
            connection.setAllowUserInteraction(false);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(param.getMethod().name());


            for (String field : param.getHeaders().keySet()) {
                String values = param.getHeaders().get(field);
                if (field.equals(CONTENT_TYPE)) {
                    //values = values +
                }
                connection.addRequestProperty(field, values);
            }

            if (entity.body != null) {
                connection.setChunkedStreamingMode(8196);
                connection.setDoOutput(true);
                try (OutputStream out = connection.getOutputStream()) {
                    out.write(entity.body);
                }
            }
            connection.connect();
            return this.convertResponse(connection);

        } catch (Exception e) {
            throw new EasyHttpException("URL request error: " + e.getMessage());
        }

    }

    /**
     * 转化为HttpResponse
     * @param connection
     * @return HttpResponse
     * @throws IOException
     */
    private HttpResponse convertResponse(HttpURLConnection connection) throws IOException {

        HttpResponse response = new HttpResponse();
        int status = connection.getResponseCode();
        response.setStatusCode(status);
        response.setMessage(connection.getResponseMessage());

        if (status < 0) {
            throw new IOException(format("Invalid status(%s) executing %s %s", status,
                    connection.getRequestMethod(), connection.getURL()));
        }

        Charset charset = getCharset(connection.getHeaderField(CONTENT_TYPE));
        response.setContentLength(connection.getContentLength());
        if (status >= 400) {
            response.setBody(Utils.toString(connection.getErrorStream(), charset));
        } else {
           response.setBody(Utils.toString(connection.getInputStream(), charset));
        }

        return response;
    }

    /**
     * 添加默认
     * @param request
     */
    private void addDefaultHeaders(HttpRequest request) {

        if (request.getContentType() != null) {
            request.addHeader(CONTENT_TYPE, request.getContentType());
        }
        if (request.getHeaders().get(ACCEPT) == null) {
            request.addHeader(ACCEPT,"*/*");
        }
    }

    public static class Entity {
        private byte[] body;
        private String url;

        public void init(HttpRequest request) {
            url = request.getUrl();
            if (HttpMethod.GET.equals(request.getMethod()) ||
                    HttpMethod.DELETE.equals(request.getMethod()) ) {
                this.handleUrl(request.getQueryParams());
                return;
            }
            if (isForm(request.getContentType())) {
                this.addBody(asUrlEncoded(request.getQueryParams()));
            }else {
                this.addBody(request.getBody());
                this.handleUrl(request.getQueryParams());
            }


        }

        private void addBody(String content) {
            if (content != null) {
                this.body = content.getBytes(UTF_8);
            }
        }

        private void handleUrl(Map<String,String> params) {
            String urlParams = asUrlEncoded(params, UTF_8.name());
            if (urlParams == null) {
                return;
            }
            if (url.contains("?")) {
                url = url + "&" + urlParams;
            }else {
                url = url + "?" + urlParams;
            }
        }
    }
}
