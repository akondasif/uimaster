package org.shaolin.uimaster.page.flow;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.shaolin.bmdp.utils.SerializeUtil;
import org.shaolin.bmdp.utils.StringUtil;
import org.shaolin.uimaster.page.AjaxContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * System performance turning tool.
 * 1. all stored object in current session.
 * 2. the total weight of all objects.
 * 3. illegal objects such the sensitive information.
 * 
 * @author wushaol
 *
 */
@RestController
public class SessionPrinterController {

	private static Logger logger = LoggerFactory.getLogger(SessionPrinterController.class);
	
	@RequestMapping("/ssobjects")
	public void getSessionObject(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		if (request.getProtocol().compareTo("HTTP/1.0") == 0) 
		{
			response.setHeader("Pragma", "no-cache");
		} 
		else if (request.getProtocol().compareTo("HTTP/1.1") == 0) 
		{
			response.setHeader("Cache-Control", "no-cache");
		}
		response.setDateHeader("Expires", 0);
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<body>");
		
		long totalSize = 0;
		HttpSession session = request.getSession(false);
		Enumeration<String> names = session.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			if (name.equals(AjaxContext.AJAX_COMP_MAP)) {
				continue;
			}
			try {
				digObjects(sb, name, session.getAttribute(name));
			} catch (Throwable e) {
				logger.warn("Unable to show data of item: " + name, e);
			}
		}
		sb.append("<div><br/><span>UI Ajax Widgets</span>");
		digObjects(sb, AjaxContext.AJAX_COMP_MAP, session.getAttribute(AjaxContext.AJAX_COMP_MAP));
		sb.append("</div>");
		
		names = session.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			try {
				long size = SerializeUtil.estimateObjectSize(session.getAttribute(name));
				totalSize += size;
				sb.append("<div style='color:red;'>").append(name).append(" Size: ");
				sb.append(StringUtil.getSizeString(size));
				sb.append("</div>");
			} catch (Throwable e) {
				logger.warn("Unable to serialize session item: " + name, e);
			}
		}
		sb.append("<div style='color:red;'>Total Size: ");
		sb.append(StringUtil.getSizeString(totalSize));
		sb.append("</div>");
		sb.append("</body>");
		sb.append("</html>");
		
		PrintWriter out = response.getWriter();
		out.print(sb.toString());
	}
	
	private void digObjects(StringBuffer sb, Object attrName, Object obj) {
		if (obj instanceof Map) {
			sb.append("<ul>").append(attrName).append(" Map Elements: ");
			Set<Map.Entry> entries = new TreeMap(((Map)obj)).entrySet();
			for (Map.Entry e : entries) {
				if (!(obj instanceof Map || obj instanceof List)) {
					sb.append("<li>");
					sb.append(e.getClass().getCanonicalName()).append(": ");
					sb.append(e);
					sb.append("</li>");
				} else {
					digObjects(sb, e.getKey(), e.getValue());
				}
			}
			sb.append("</ul>");
		} else if (obj instanceof List) {
			sb.append("<ul>").append(attrName).append(" List Elements: ");
			List elements = ((List)obj);
			Collections.sort(elements,new Comparator(){  
	            public int compare(Object arg0, Object arg1) {  
	                return (arg0.hashCode() > arg1.hashCode()) ? 1 : -1;  
	            }  
	        });  
			for (Object e : elements) {
				if (obj instanceof Map || obj instanceof List) {
					digObjects(sb, "list internal element", e);
				} else {
					sb.append("<li>");
					sb.append(e.getClass().getCanonicalName()).append(": ");
					sb.append(e);
					sb.append("</li>");
				}
			}
			sb.append("</ul>");
		} else {
			sb.append("<li>");
			sb.append(attrName).append("(").append((obj != null)? obj.getClass().getCanonicalName(): null).append("): ");
			sb.append(obj);
			sb.append("</li>");
		}
	}
	
}
