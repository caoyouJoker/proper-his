package com.javahis.web.filter ;

import java.io.* ;
import javax.servlet.* ;

public class EncodingFilter implements Filter
{
	public void init(FilterConfig filterConfig)
          throws ServletException
	{
		 System.out.println("** 过滤器初始化...") ;
	}
	public void doFilter(ServletRequest request,
                     ServletResponse response,
                     FilterChain chain)
              throws IOException,
                     ServletException
	{
		try
		{
			request.setCharacterEncoding("UTF-8");
		}
		catch (Exception e)
		{
		}
		chain.doFilter(request,response) ;
	}
	public void destroy()
	{
		 System.out.println("** 过滤器销毁...") ;
	}
};
/*
  <filter>
	<filter-name>encoding</filter-name>
	<filter-class>web.emr.EncodingFilter</filter-class>
  </filter>
  <filter-mapping>
	<filter-name>encoding</filter-name>
	<url-pattern>/*</url-pattern>
  </filter-mapping>
*/