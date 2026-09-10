package com.ivodam.finalpaper.edast.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.multipart.support.MultipartFilter;

@Configuration(proxyBeanMethods = false)
public class MultipartConfiguration {

  @Bean
  public FilterRegistrationBean<MultipartFilter> multipartFilterRegistration() {
    var multipartFilter = new MultipartFilter();
    multipartFilter.setMultipartResolverBeanName("multipartResolver");

    var registration = new FilterRegistrationBean<>(multipartFilter);

    registration.setName("multipartFilter");
    registration.addUrlPatterns("/*");
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

    return registration;
  }
}