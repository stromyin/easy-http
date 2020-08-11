package com.github.firelcw.hander;

import com.github.firelcw.codec.Decoder;
import com.github.firelcw.exception.HttpInterceptorException;
import com.github.firelcw.interceptor.HttpInterceptor;
import com.github.firelcw.model.HttpMethod;
import com.github.firelcw.model.HttpRequest;
import com.github.firelcw.model.HttpRequestConfig;
import com.github.firelcw.model.HttpResponse;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * http处理器
 * @author liaochongwei
 * @date 2020/7/31 14:26
 */
public class HttpHandler {

    /**
     * 拦截器
     */
    private List<HttpInterceptor> interceptors;

    public HttpHandler() {
        this.interceptors = Collections.emptyList();
    }

    public Object handle(RequestHandler requestHandler, Decoder decoder, Type returnType) {
        // 处理拦截器 排除，排序
        this.exclude(requestHandler.getRequest().getUrl(),requestHandler.getRequest().getMethod());
        this.ordered();

        // 执行前置拦截
        this.doPreInterceptors(requestHandler.getRequest(),requestHandler.getConfig());
        HttpResponse response = requestHandler.handle();
        // 执行后置拦截
        this.doPostInterceptors(requestHandler.getRequest(),response);
        return new DecodeHandler(response, decoder, returnType).handle();
    }

    /**
     * 执行前置拦截
     * @param request
     * @param config
     */
    private void doPreInterceptors(HttpRequest request, HttpRequestConfig config) {
        interceptors.forEach(e-> {
            if (!e.preHandle(request, config)) {
                throw new HttpInterceptorException( "The '" + e.getClass().getSimpleName() +"' failed");
            }
        });
    }

    /**
     * 执行后置拦截
     * @param request
     * @param response
     */
    private void doPostInterceptors(HttpRequest request, HttpResponse response) {
        interceptors.forEach(e->e.postHandle(request, response));
    }

    /**
     * 拦截器排除
     */
    private void exclude(String url, HttpMethod method) {
        if (CollectionUtils.isEmpty(interceptors)) {
            return;
        }
        interceptors.removeIf(e-> e.excludes().stream().anyMatch( n -> n.match(url, method)));
    }
    /**
     * 拦截器排序
     */
    private void ordered() {
        if (CollectionUtils.isEmpty(interceptors)) {
            return;
        }
        this.interceptors = this.interceptors.stream()
                .sorted(Comparator.comparing(HttpInterceptor::order))
                .collect(Collectors.toList());
    }



    public List<HttpInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<HttpInterceptor> interceptors) {
       this.interceptors = interceptors;
    }
}
