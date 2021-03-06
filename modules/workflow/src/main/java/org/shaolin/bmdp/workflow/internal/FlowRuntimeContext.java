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
package org.shaolin.bmdp.workflow.internal;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.shaolin.bmdp.datamodel.common.NameExpressionType;
import org.shaolin.bmdp.json.JSONObject;
import org.shaolin.bmdp.persistence.HibernateUtil;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.be.ITaskEntity;
import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.runtime.spi.FlowEvent;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.bmdp.utils.SerializeUtil;
import org.shaolin.bmdp.workflow.be.ITask;
import org.shaolin.bmdp.workflow.be.TaskImpl;
import org.shaolin.bmdp.workflow.dao.CoordinatorModel;
import org.shaolin.bmdp.workflow.exception.WorkflowVarException;
import org.shaolin.bmdp.workflow.internal.DefaultFlowSessionService.DefaultWorkflowSession;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;
import org.shaolin.bmdp.workflow.spi.IWorkflowService;
import org.shaolin.bmdp.workflow.spi.WorkflowSession;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.TransOpsExecuteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow runtime context, store everything related to flow state.
 * 
 */
public final class FlowRuntimeContext extends TransOpsExecuteContext implements FlowVariableContext, Serializable {

	private static final long serialVersionUID = -6150417408203161229L;

    private static final Logger logger = LoggerFactory.getLogger(FlowRuntimeContext.class);
    
    private Event event;
    
    final FlowEngine engine;
    private final Event requestEvent;
    private FlowContextImpl flowContextInfo;

    private List<String> globalVarNames;
    private Set<String> globalVarNamesSet;
    private final DefaultEvaluationContext globalVariables = new DefaultEvaluationContext();
    private final DefaultEvaluationContext localVariables = new DefaultEvaluationContext();

    private WorkflowSession session;
    private boolean sessionDestroyed;

    private Deque<FlowState> stacks;
    
    private Object transactionState;
    private boolean recoverable;

    private volatile boolean isClosed = false;

    private NodeInfo currentNode;
    private boolean waitResponse;
    private boolean responseBack;
    private String sessionId;
    private NodeInfo startNode;
    private NodeInfo eventNode; // MissionNodeType
    private Throwable exception;
    private long taskId;

	@SuppressWarnings("rawtypes")
    private static final Map<Class, Object> primitiveDefaultValues = new HashMap<Class, Object>();
    private static final Map<String, Object> primitiveDefaultValues2 = new HashMap<String, Object>();
    static {
        primitiveDefaultValues.put(int.class, 0);
        primitiveDefaultValues.put(byte.class, 0);
        primitiveDefaultValues.put(char.class, 0);
        primitiveDefaultValues.put(short.class, 0);
        primitiveDefaultValues.put(long.class, 0);
        primitiveDefaultValues.put(float.class, 0);
        primitiveDefaultValues.put(double.class, 0);
        primitiveDefaultValues.put(boolean.class, false);

        primitiveDefaultValues2.put(int.class.getName(), 0);
        primitiveDefaultValues2.put(byte.class.getName(), 0);
        primitiveDefaultValues2.put(char.class.getName(), 0);
        primitiveDefaultValues2.put(short.class.getName(), 0);
        primitiveDefaultValues2.put(long.class.getName(), 0);
        primitiveDefaultValues2.put(float.class.getName(), 0);
        primitiveDefaultValues2.put(double.class.getName(), 0);
        primitiveDefaultValues2.put(boolean.class.getName(), false);
    }

    public FlowRuntimeContext(Event event, FlowEngine engine) {
        this.engine = engine;
        this.event = event;
        
        this.requestEvent = event;
        event.setAttribute(BuiltInAttributeConstant.KEY_VARIABLECONTEXT, this);
        flowContextInfo = new FlowContextImpl(this);
        event.setAttribute(BuiltInAttributeConstant.KEY_FLOWCONTEXT, flowContextInfo);
        
        try {
			if (this.engine.getServices() != null) {
				Set<Map.Entry<String, IServiceProvider>> set = this.engine.getServices().entrySet();
		        for (Map.Entry<String, IServiceProvider> spi: set) {
		        	globalVariables.setVariableValue(spi.getKey(), spi.getValue());
		        }
			}
			globalVariables.setVariableValue("flowContext", this);
			globalVariables.setVariableValue(FlowEngine.EVENT_VAR_NAME, event);
			
			Set<Map.Entry<String, Object>> set = this.engine.getDefaultGlobalVariables().entrySet();
			for (Map.Entry<String, Object> spi: set) {
	        	globalVariables.setVariableValue(spi.getKey(), spi.getValue());
	        }
			Collection<Entry<String, Serializable>> attributes = event.getAllAttributes();
			if (attributes != null && !attributes.isEmpty()) {
				for (Entry<String, Serializable> attribute: attributes) {
					localVariables.setVariableValue(attribute.getKey(), attribute.getValue());
				}
			}
			
		} catch (EvaluationException e) {
			throw new IllegalStateException("Failed to initialize the flow context: " + e.getMessage(), e);
		}
        this.setEvaluationContextObject("@", globalVariables);
        this.setEvaluationContextObject("$", localVariables);
    }
    
    public FlowEngine getEngine() {
    	return this.engine;
    }
    
    public boolean match(FlowEngine engine) {
        return this.engine == engine;
    }

    public boolean flowContextMatched(FlowContextImpl inputContext) {
        if (isClosed) {
            if (logger.isTraceEnabled()) {
                logger.trace("Context for {} is closed", this.session.getID());
            }
            return false;
        }
        if (inputContext.getWaitingNode() != currentNode) {
        	logger.info("Current waiting node is " + inputContext.getWaitingNode() 
        			+ ", input node is " + currentNode);
        }
    	return inputContext.getWaitingNode() == currentNode;
    }

    public void startNewFlow(boolean isTimedOut) {
        this.flowContextInfo = new FlowContextImpl(this);
        this.event.setAttribute(BuiltInAttributeConstant.KEY_FLOWCONTEXT, flowContextInfo);
        this.waitResponse = false;
        this.responseBack = true;
    }
    
    /**
     * the true value only return once.
     * 
     * @return
     */
    public boolean hasResponse() {
    	boolean tem = responseBack;
    	this.responseBack = false;
    	return tem;
    }

    /**
     * This method will be invoked in below situations:
     * 1. when a flow processed to the terminal/exit node.
     * 2. when a flow processed to an event waiting node.
     * 
     * *: This method MUST invoked before unlock method!
     */
    public void recordState() {
        flowContextInfo.setWaitingNode(currentNode);
    }
    
    public static String marshall(FlowRuntimeContext context) throws Exception {
	    	FlowState state = new FlowState(context.currentNode, context.globalVarNames,
	    			 context.globalVarNamesSet, null);
	    	state.sessionId = context.sessionId;
	    	state.eventConsumer = context.event.getEventConsumer();
	    	state.eventId = context.event.getId();
	    	state.engineId = context.engine.getEngineName();
	    	state.startNode = context.startNode;
	    	state.eventNode = context.eventNode;
	    	state.waitResponse = context.waitResponse;
	    	state.responseBack = context.responseBack;
	    	state.recoverable = context.recoverable;
	    	state.taskId = context.taskId;
	    	state.ready();
	    	return new JSONObject(state).toString(); 
    }
    
//    private static void convert() throws Exception {
//    		TaskImpl condition = new TaskImpl();
//    		List<ITask> result = CoordinatorModel.INSTANCE.searchTasks(condition, null, 0, -1);
//    		for (ITask task : result) {
//    			try {
//		    		FlowState state = SerializeUtil.readData(task.getFlowState1(), FlowState.class);
//		    		state.recover0();
//		    		task.setFlowState((new JSONObject(state)).toString());
//		    		CoordinatorModel.INSTANCE.update(task);
//    			} catch (Exception e) {
//    				
//    			}
//    		}
//    		HibernateUtil.releaseSession(HibernateUtil.getSession(), true);
//    }
    
    public static FlowRuntimeContext unmarshall(String json) throws Exception {
		FlowState state = new FlowState();
		state.recover(new JSONObject(json));
		
		WorkflowLifecycleServiceImpl wfservice = (WorkflowLifecycleServiceImpl)AppContext.get().getService(IWorkflowService.class);
		FlowEngine flowEngine = wfservice.getFlowContainer().getFlowEngine(state.engineId);
		FlowEvent event = new FlowEvent(state.eventConsumer);
		event.setId(state.eventId);
		FlowRuntimeContext context = new FlowRuntimeContext(event, flowEngine);
		context.currentNode = state.currentNode; 
		context.globalVariables.getVariableObjects().put(BuiltInAttributeConstant.KEY_VARIABLECONTEXT, context);
		context.globalVariables.getVariableObjects().put(BuiltInAttributeConstant.KEY_RUNTIME, context);
		context.globalVariables.getVariableObjects().put(BuiltInAttributeConstant.KEY_FLOWCONTEXT, context.getFlowContextInfo());
		
		context.globalVarNames = state.globalVarNames;
		context.globalVarNamesSet = state.globalVarNamesSet; 
		
		context.session = new DefaultWorkflowSession(state.sessionId);
		context.sessionId = state.sessionId;
		context.startNode = state.startNode;
		context.eventNode = state.eventNode;
		context.waitResponse = state.waitResponse;
		context.responseBack = state.responseBack;
		context.recoverable = state.recoverable;
		context.taskId = state.taskId;
    		return context;
    }
    
    /**
     * Prepare for invoking child flow. the variable context of invoker 
     * will be pushed into a stack. When child flow finished, it will be
     * popped again.
     * 
     * @param nodeInfo
     * 			current node
     * @param childInfo
     * 			child node
     * @param childGlobalVarNames
     * 			child global var names
     * @param childGlobalVarNamesSet
     * 			child global var name set
     */
    public void push(NodeInfo nodeInfo, NodeInfo childInfo, List<String> childGlobalVarNames,
            Set<String> childGlobalVarNamesSet) {
        if (stacks == null) {
            stacks = new LinkedList<FlowState>();
        }
        // Child flow input rule : Child flow first node can only access the previous node output
        // of the child flow node. 
        stacks.push(new FlowState(nodeInfo, globalVarNames, globalVarNamesSet, globalVariables));
        globalVarNames = childGlobalVarNames;
        globalVarNamesSet = childGlobalVarNamesSet;
        localVariables.getVariableObjects().clear();
        //TODO: change the local var context as the global var context.
        if (logger.isTraceEnabled()) {
            logger.trace("Sub flow global variables is {}", globalVariables);
        }
    }

    public NodeInfo pop() {
        if (stacks == null) {
            return null;
        } else {
            FlowState state = stacks.poll();
            if (state != null) {
                localVariables.getVariableObjects().clear();
                for (int i = 0, t = globalVarNames.size(); i < t; i++) {
                    String gVarName = globalVarNames.get(i);
                    
                    //if (!localVariables.copyValue(gVarName, outputVariables)) {
                      //  localVariables.copyValue(gVarName, globalVariables);
                    //}
                }
                globalVarNames = state.globalVarNames;
                globalVarNamesSet = state.globalVarNamesSet;
                globalVariables.getVariableObjects().clear();
                // globalVariables.copyAllValue(state.globalVariables);
                if (logger.isTraceEnabled()) {
                    logger.trace("Subflow output variables is {}", localVariables);
                }
                return state.currentNode;
            } else {
                return null;
            }
        }
    }

    public void clearSubFlowInfo() {
        if (stacks != null) {
            stacks.clear();
        }
    }

    @Override
    public Event getEvent() {
        return this.event;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getEvent(Class<T> eventClass) {
        return (T) this.event;
    }

    public void setEvent(Event evt) {
        this.event = evt;
        evt.setAttribute(BuiltInAttributeConstant.KEY_VARIABLECONTEXT, this);
        evt.setAttribute(BuiltInAttributeConstant.KEY_FLOWCONTEXT, flowContextInfo);
        
        Collection<String> keys = evt.getAttributeKeys();
        for(String key: keys) {
        	this.localVariables.getVariableObjects().put(key, evt.getAttribute(key));
        }
    }

    @Override
    public Event getRequestEvent() {
        return this.requestEvent;
    }

    public FlowContextImpl getFlowContextInfo() {
        return flowContextInfo;
    }

    public void changeEvent(Event event) {
        flowContextInfo.registerOutboundEvent(event);
        setEvent(event);
    }

    public void clearEventAttributes() {
    	this.event.removeAttribute(BuiltInAttributeConstant.KEY_VARIABLECONTEXT);
    	this.event.removeAttribute(BuiltInAttributeConstant.KEY_FLOWCONTEXT);
    }

    public <T> T getInput(String name, Class<T> clazz) {
		try {
			Object v = localVariables.getVariableValue(name);
	        if (v != null) {
	            return (T) v;
	        }
	        v = globalVariables.getVariableValue(name);
	        if (v != null) {
	            return (T) v;
	        }
	        if (clazz.isPrimitive()) {
	            return (T) primitiveDefaultValues.get(clazz);
	        }
		} catch (EvaluationException e) {
		}
        if (clazz.isPrimitive()) {
            return (T) primitiveDefaultValues.get(clazz);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public void initInputVariables(FlowRuntimeContext context) {
        this.localVariables.getVariableObjects().clear();
        this.localVariables.getVariableObjects().putAll(
        		context.localVariables.getVariableObjects());
    }

    @Override
    public void setOutput(String name, Object value) {
        if (globalVarNamesSet.contains(name)) {
            try {
				globalVariables.setVariableValue(name, value);
			} catch (EvaluationException e) {
				logger.info(e.getMessage(), e);
			}
        }
    }

    public void setGlobalVarNames(List<String> globalVarNames, Set<String> globalVarNamesSet) {
        this.globalVarNames = globalVarNames;
        this.globalVarNamesSet = globalVarNamesSet;
    }

    public void mapGlobalVariables() {
        for (int i = 0, t = globalVarNames.size(); i < t; i++) {
            String globalVarName = globalVarNames.get(i);
            try {
				globalVariables.setVariableValue(globalVarName, localVariables.getVariableValue(globalVarName));
			} catch (EvaluationException e) {
				logger.info(e.getMessage(), e);
			}
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getGlobalVarible(String name, Class<T> clazz) {
    	try {
			Object v = globalVariables.getVariableValue(name);
	        if (v != null) {
	            return (T) v;
	        }
		} catch (EvaluationException e) {
		}
        if (clazz.isPrimitive()) {
            return (T) primitiveDefaultValues.get(clazz);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getGlobalVarible(String name) {
    	try {
			Object v = globalVariables.getVariableValue(name);
	        if (v != null) {
	            return (T) v;
	        }
		} catch (EvaluationException e) {
		}
        return null;
    }

    @SuppressWarnings("unchecked")
	public void setLocallVariables(Map<String, Object> initVariables) {
        if (initVariables != null) {
        	Set<String> keys = initVariables.keySet();
        	for (String key: keys) {
        		if (!localVariables.getVariableObjects().containsKey(key)) {
        			localVariables.getVariableObjects().put(key, initVariables.get(key));
        		}
        	}
        }
    }

    public void mapVariables(List<NameExpressionType> mappings) {
        if (mappings != null) {
        	localVariables.getVariableObjects().clear();
            for (int i = 0, t = mappings.size(); i < t; i++) {
            	NameExpressionType m = mappings.get(i);
                String targetName = m.getExpression().getExpressionString();
                String srcName = m.getName();
                try {
					localVariables.setVariableValue(srcName, globalVariables.getVariableValue(targetName));
				} catch (EvaluationException e) {
					logger.info(e.getMessage(), e);
				}
            }
            resetLocalVariables();
        }
    }

    public void resetLocalVariables() {
        if (logger.isTraceEnabled()) {
            logger.trace("Current local variables is {}, global variables is {}", 
            		localVariables, globalVariables);
        }
    }

    @SuppressWarnings("unchecked")
	public Map<String, Object> getSnapshot() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.putAll(globalVariables.getVariableObjects());
        m.putAll(localVariables.getVariableObjects());
        return m;
    }

    @Override
    public WorkflowSession getSession() {
        return session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSession(Class<T> sessionClass) {
        return (T) session;
    }

    public void setSession(WorkflowSession session) {
        this.session = session;
        try {
			globalVariables.setVariableValue(FlowEngine.SESSION_VAR_NAME, session);
		} catch (EvaluationException e) {
		}
    }

    public boolean isSessionDestroyed() {
        return sessionDestroyed;
    }

    public void setSessionDestroyed(boolean sessionDestroyed) {
        this.sessionDestroyed = sessionDestroyed;
    }

    public void saveState(Object transactionState) {
        this.transactionState = transactionState;
        this.recoverable = true;
    }

    public Object getTransactionState() {
        return transactionState;
    }

    public void clearState() {
        transactionState = null;
        recoverable = false;
    }

    public boolean isRecoverable() {
        return recoverable;
    }

    public NodeInfo getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(NodeInfo currentNode) {
        this.currentNode = currentNode;
    }

    public boolean isWaitResponse() {
        return this.waitResponse;
    }

    public void markWaitResponse() {
        this.waitResponse = true;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
    
    public NodeInfo getStartNode() {
        return startNode;
    }

    public void setStartNode(NodeInfo startNode) {
        this.startNode = startNode;
    }

    //IntermediateNodeInfo
    public NodeInfo getEventNode() {
        return eventNode;
    }

    public void setEventNode(NodeInfo eventNode) {
        this.eventNode = eventNode;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable e) {
        this.exception = e;
    }

    public void bindSession(final ITaskEntity entity) {
	    	if (entity.getId() == 0) {
	    		//CoordinatorModel.INSTANCE.create(entity);
	    		throw new WorkflowVarException("Workflow task must be a persistent entity! " + entity.toString());
	    	}
	    	
	    	String sessionId = this.getSession().getID();
	    	if (entity.getSessionId() != null && entity.getSessionId().length() > 0) {
			if (!entity.getSessionId().equals(sessionId)) {
				logger.debug("Session id {} has already set!", entity.getSessionId());
			}
			return;
		} 
	    	entity.setSessionId(sessionId);
	    	if (entity.getId() > 0) {
	    		CoordinatorModel.INSTANCE.update(entity);
	    	} else {
	    		CoordinatorModel.INSTANCE.create(entity);
	    	}
    }
    
    @Override
    public String toString() {
        return "FlowRuntimeContext [Node=" + currentNode + "]";
    }
    
    public String currentNodeToString() {
    	return currentNode.toString();
    }

}
