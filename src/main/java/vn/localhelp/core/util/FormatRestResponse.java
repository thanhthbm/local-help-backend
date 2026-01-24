package vn.localhelp.core.util;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import vn.localhelp.core.domain.response.common.RestResponse;
import vn.localhelp.core.util.annotation.ApiMessage;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public @Nullable Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType,
      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {
    HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
    int status = servletResponse.getStatus();

    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(status);

    if (body instanceof String || body instanceof Resource) {
      return body;
    }

    if (status >= 400) {
      return body;
    } else {
      res.setData(body);
      ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
      res.setMessage(message != null ? message.value() : "CALL API SUCCESS");
    }

    return res;
  }
}
