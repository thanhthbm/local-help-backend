package vn.localhelp.core.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);    // Bật log nội dung body (JSON)
        filter.setMaxPayloadLength(10000); // Giới hạn độ dài body muốn log
        filter.setIncludeHeaders(true);    // Bật log toàn bộ Header (Để soi Token)
        filter.setIncludeClientInfo(true);
        return filter;
    }
}
