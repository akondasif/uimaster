/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.page.cache;

import java.io.Serializable;
import java.util.List;

import org.shaolin.bmdp.datamodel.pagediagram.DisplayNodeType;
import org.shaolin.bmdp.datamodel.pagediagram.LogicNodeType;
import org.shaolin.bmdp.datamodel.pagediagram.NextType;
import org.shaolin.bmdp.datamodel.pagediagram.OutType;
import org.shaolin.bmdp.datamodel.pagediagram.PageNodeType;
import org.shaolin.bmdp.datamodel.pagediagram.WebNodeType;
import org.shaolin.bmdp.runtime.cache.CacheManager;
import org.shaolin.bmdp.runtime.cache.ICache;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.utils.StringUtil;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.flow.nodes.DisplayNode;
import org.shaolin.uimaster.page.flow.nodes.LogicNode;
import org.shaolin.uimaster.page.flow.nodes.UIPageNode;
import org.shaolin.uimaster.page.flow.nodes.WebChunk;
import org.shaolin.uimaster.page.flow.nodes.WebNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIFlowCacheManager implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final transient Logger logger = LoggerFactory.getLogger(UIFlowCacheManager.class);

    private static final String WEB_FLOW_CACHE = "__system_webflow_cache";

    private static final String DISPLAY_NODE_CACHE = "__system_webflow_pagenode_cache";

    /**
     * the map of the webchunk, the key:the chunk name, value:WebChunkType object local-cache the
     * webchunks
     */
    private static ICache<String, WebChunk> chunks;

    /**
     * the map of the UIPage Display Node with the source entity, the key:the name of the source
     * entity, value:WebNodeType object Using this map, we can find the Web Node with the source
     * entity Since the instance of this class is often read, and seldom modified, we use HashMap,
     * not Hashtable, for performance maybe can use FastHashMap in jakarta
     */
    private static ICache<String, WebNode> displayNodes;

    private static volatile long sessionObjectLimit = -1L;

    static
    {
        chunks = CacheManager.getInstance().getCache(
        		WEB_FLOW_CACHE, String.class, WebChunk.class);
        displayNodes = CacheManager.getInstance().getCache(
        		DISPLAY_NODE_CACHE, String.class, WebNode.class);
    }
    
    private final static UIFlowCacheManager INSTANCE = new UIFlowCacheManager();

    public static UIFlowCacheManager getInstance() {
    	return INSTANCE;
    }
    
    public static void addFlowCacheIfAbsent(String appName) {
    }
    
    public UIFlowCacheManager() {}
    
    /**
     * add chunk into map
     * 
     * @param webchunk
     * @return the webchunk that was added; if not added, returns existed chunk in chunk
     * @throws ParsingException 
     */
    private void addChunkIntoMap(org.shaolin.bmdp.datamodel.pagediagram.WebChunk webchunk) throws ParsingException
    {
        if (logger.isInfoEnabled()) {
            logger.info("Add uiflow: " + webchunk.getEntityName());
        }
        String entityname = webchunk.getEntityName();
        if (chunks.containsKey(entityname)) {
        	logger.debug("This Webchunk has already existed in cache, replace it: {}", entityname);
        }
        // add node
    	WebChunk chunk = new WebChunk(webchunk);
    	chunk.initChunk();
        // add new
    	chunks.put(entityname, chunk);

        // cache uipage displayNodes, only UIPageDisplayNode has SourceEntity
        List<WebNode> nodes = chunk.getWebNodes();
        for (WebNode node: nodes)
        {
            if (node instanceof UIPageNode)
            {
            	logger.info("Add page node: {}.{}", entityname, node.getName());
                String uipageEntity = ((UIPageNode)node).getSourceEntity();
                if (uipageEntity != null)
                {
                	displayNodes.putIfAbsent(uipageEntity, node);
                }
            }
        }
    }

    public static long getSessionObjectLimit()
    {
        return sessionObjectLimit;
    }

    public static void setSessionObjectLimit(String sizeStr)
    {
        sessionObjectLimit = StringUtil.parseSizeString(sizeStr);
    }

    /**
     * Put the webchunk value with the key into the map if webchunk existed, replace it
     * 
     * @param name the name of WebChunk
     * @param value the webchunk object
     * @throws ParsingException 
     */
    public void addChunk(org.shaolin.bmdp.datamodel.pagediagram.WebChunk chunkType) throws ParsingException
    {
        addChunkIntoMap(chunkType);
    }
    
    /**
     * remove the webchunk from the map
     * 
     * @param name the name of WebChunk
     */
    public org.shaolin.bmdp.datamodel.pagediagram.WebChunk removeAppChunk(String entityname)
    {
    	if (logger.isDebugEnabled())
    		logger.debug("Remove uiflow: " + entityname);
    	
        WebChunk webchunk = (WebChunk)chunks.remove(entityname);
        // remove uipagedisplay node
        if (webchunk != null)
        {
            List<WebNode> nodes = webchunk.getWebNodes();
            for (WebNode node: nodes) {
                if (node instanceof UIPageNode)
                {
                    String uipageEntity = ((UIPageNode)node).getSourceEntity();
                    if (uipageEntity != null)
                    {
                    	displayNodes.remove(uipageEntity);
                    }
                }
            }
            return webchunk.getType();
        }
        return null;
    }

    /**
     * Removes all mappings from the map.
     */
    public void removeAll()
    {
        chunks.clear();
        displayNodes.clear();
    }

    /**
     * reload all WebChunks
     * 
     * @param chunkname
     */
    public void reloadAll()
    {
        if (logger.isInfoEnabled())
            logger.info("reloadAll()");

        removeAll();
    }

    /**
     * Returns the webnode value to which the map maps the specified key
     * 
     * @param key key whose associated value is to be returned
     * @return the webnode value to which the map maps the specified key
     * @throws ParsingException 
     */
    public WebChunk get(String entityname) throws ParsingException
    {
    	if (this.chunks != null && this.chunks.containsKey(entityname)) {
			return this.chunks.get(entityname);
    	}
    	
        if (!chunks.containsKey(entityname))
        {
        	org.shaolin.bmdp.datamodel.pagediagram.WebChunk chunkType = 
        			IServerServiceManager.INSTANCE.getEntityManager().getEntity(entityname, 
        					org.shaolin.bmdp.datamodel.pagediagram.WebChunk.class);
            addChunkIntoMap(chunkType);
        }
        return chunks.get(entityname);
    }

    /**
     * Returns the number of key-value mappings in the map
     */
    public int size()
    {
        return chunks.size();
    }

    /**
     * add webnode into the specified chunk
     * @throws ParsingException 
     */
    public void addWebNode(String chunkName, WebNodeType node) throws ParsingException
    {
        if (node == null)
        {
            logger.error("node is null when addWebNode");

        }

        if (logger.isDebugEnabled())
            logger.debug("add WebNode: chunkname=" + chunkName + ",nodename=" + node.getName());

        WebChunk webchunk = get(chunkName);
        if (node instanceof PageNodeType) {
        	webchunk.getWebNodes().add(new UIPageNode((PageNodeType)node));
		} else if (node instanceof DisplayNodeType) {
			webchunk.getWebNodes().add(new DisplayNode((DisplayNodeType)node));
		} else if (node instanceof LogicNodeType) {
			webchunk.getWebNodes().add(new LogicNode((LogicNodeType)node));
		} else {
			throw new ParsingException("Unsupported web node: " + node.toString());
		}
    }

    /**
     * remove webnode from the specified chunk
     */
    public void removeWebNode(String chunkName, String nodename)
    {
		if (nodename == null) {
			logger.error("node is null when removeWebNode");
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("removeWebNode: chunkname=" + chunkName + ",nodename="
					+ nodename);
		}
		if (chunks.containsKey(chunkName)) {
			WebChunk chunk = chunks.get(chunkName);
			chunk.removeWebNode(nodename);
		}
    }

    /**
     * find the WebNode with the specified name in the specified chunk
     * 
     * @param chunkName the name of WebChunk
     * @param nodeName the name of WebNode
     * @return the WebNode object
     */
    public WebNode findWebNode(String chunkName, String nodeName)
    {
        if (chunkName == null || chunkName.equals(""))
        {
            logger.error("the chunkname is empty, can not find webnode {}.", nodeName);
            return null;
        }
        if (nodeName == null || nodeName.equals(""))
        {
            logger.error("the chunkname is {}, the nodeName is empty.", chunkName);
            return null;
        }
        
		if (chunks.containsKey(chunkName)) {
			WebChunk chunk = chunks.get(chunkName);
			//check for the newest update in development mode.
			return chunk.findWebNode(nodeName);
		} else {
			logger.error("web chunk: {} can't be found.", chunkName);
			return null;
		}
    }
    
    /**
     * find the WebNode with the specified source entity.
     * 
     * @param nodeName the entityname of the source entity
     * @return the WebNode object
     */
    public WebNode findWebNodeBySourceEntity(String entityName)
    {
        WebNode node = (WebNode)displayNodes.get(entityName);
        if (node == null)
            logger.error("findWebNodeBySourceEntity(): cannot find the webnode for page "
                    + entityName);
        return node;
    }

    /**
     * find the destination WebNode associated with the specified node and its out name, for tablib
     * link. first, find the Out node, if not found, find the InternalNode
     */
    public WebNode findNextWebNode(String chunkName, String nodename, String outname)
    {
        if (chunkName == null || nodename == null || outname == null)
        {
            logger.error("findNextWebNode(String, String, String) cannot find node: chunkname="
                    + chunkName + ", nodename=" + nodename + ", outname=" + outname);
            return null;
        }
        WebNode node = findWebNode(chunkName, nodename);
        if (node == null)
        {
            logger.error("findNextWebNode(String, String, String) cannot find node: chunkname="
                    + chunkName + ", nodename=" + nodename + "; the outname is " + outname);
            return null;
        }

        return findNextWebNode(node, outname);
    }

    /**
     * find the destination WebNode associated with the specified node and its out name, for tablib
     * link. first, find the Out node, if not found, find the InternalNode
     */
    public WebNode findNextWebNode(WebNode node, String outname)
    {
        if (node == null)
        {
            logger.error("findNextWebNode(WebNodeType, String):the node is null when find out "
                    + outname);
            return null;
        }
        if (outname == null || outname.equals(""))
        {
            logger.error("findNextWebNode(WebNodeType, String):the outname is null, the node "
                    + node.toString());
            return null;
        }

        // find the Out
        OutType lt = node.findOut(outname);
        NextType nt = null;
        if (lt == null)
        {
            logger.error("cannot find out(" + outname + ") of  node: " + node.toString());
            return null;
        }
        else
        {
            // logger.debug("find the Out!");
            nt = lt.getNext();
        }

        return findNextWebNode(node, nt);
    }

    /**
     * find the destination WebNode associated with the specified node and its out name, for tablib
     * link. first, find the Out node, if not found, find the InternalNode
     */
    public WebNode findNextWebNode(WebNode node, NextType nt)
    {
        if (node == null)
        {
            logger.error("findNextWebNode(WebNodeType, NextType):the node is null");
            return null;
        }

        if (nt == null)
        {
            logger.error("findNextWebNode(WebNodeType, NextType): node " + node.toString()
                    + ", NextType is null");
            return null;
        }
        if (logger.isTraceEnabled()) {
        	logger.trace("destChunkName:" + node.getChunk().getEntityName());
        }
        return findWebNode(node.getChunk().getEntityName(), nt.getDestNode());
    }

}
