package com.example.demo.filter;

import com.example.demo.utils.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/*"})
public class IPFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(IPFilter.class);

    //@Value("${jwt.config.failureTime}")
    //long failureTime;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse rep = (HttpServletResponse) response;


        rep.setCharacterEncoding("UTF-8");
        //rep.setContentType("application/json; charset=utf-8");
        String method = req.getMethod();
        String path = req.getContextPath();


        if (method.equals("OPTIONS")) {
            rep.setStatus(HttpServletResponse.SC_OK);
        } else {
            String ipInfo= IpUtil.getIpAddr(req);
            if(StringUtils.isBlank(ipInfo)||"unknown".equalsIgnoreCase(ipInfo)){
                logger.warn("ip地址获取失败：{}",ipInfo);
            }else {
                logger.info("ip地址获取成功，访问路径:{},访问来源ip：{}",path,ipInfo);
            }
        }
        chain.doFilter(request, response);
    }


    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        logger.info("初始化IPfilter");
    }
}
