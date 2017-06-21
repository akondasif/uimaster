package org.shaolin.uimaster.page.flow;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.shaolin.bmdp.utils.SerializeUtil;
import org.shaolin.bmdp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * System performance turning tool.
 * 1. all stored object in current session.
 * 2. the total weight of all objects.
 * 3. illegal objects such the sensitive information.
 * 
 * @author wushaol
 *
 */
public class SessionObjectServlet extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(SessionObjectServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
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
		HttpSession session = request.getSession();
		Enumeration<String> names = session.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			digObjects(sb, name, session.getAttribute(name));
		}
		
		names = session.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			try {
				long size = SerializeUtil.estimateObjectSize(session.getAttribute(name));
				totalSize += size;
				sb.append("<div style='color:red;'>").append(name).append(" Size: ");
				sb.append(StringUtil.getSizeString(size));
				sb.append("</div>");
			} catch (Exception e) {
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
			Set<Map.Entry> entries = ((Map)obj).entrySet();
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
