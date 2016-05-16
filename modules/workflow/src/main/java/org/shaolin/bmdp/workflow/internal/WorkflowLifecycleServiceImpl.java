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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.ILifeCycleProvider;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.bmdp.workflow.be.FlowEntityImpl;
import org.shaolin.bmdp.workflow.be.IFlowEntity;
import org.shaolin.bmdp.workflow.dao.WorkflowModel;
import org.shaolin.bmdp.workflow.internal.cache.FlowObject;
import org.shaolin.bmdp.workflow.spi.IWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowLifecycleServiceImpl implements ILifeCycleProvider, IServiceProvider, IWorkflowService {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkflowLifecycleServiceImpl.class);
	
	private final FlowContainer flowContainer;

	public WorkflowLifecycleServiceImpl() {
		this.flowContainer = new FlowContainer(IServerServiceManager.INSTANCE.getMasterNodeName());
	}
	
	@Override
	public void startService() {
		IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();

		FlowEntityImpl searchCriteria = new FlowEntityImpl();
		List<IFlowEntity> allFlowEntities = WorkflowModel.INSTANCE.searchFlowEntities(searchCriteria, null, 0, -1);
		for (IFlowEntity wf : allFlowEntities) {
			try {
				Workflow workflow = EntityUtil.unmarshaller(Workflow.class, new StringReader(wf.getContent()));
				// add the customized workflow to current application entity manager.
				entityManager.appendEntity(workflow);
			} catch (JAXBException e) {
				e.printStackTrace();
				continue;
			}
		}
		
		final List<Workflow> allFlows = new ArrayList<Workflow>();
		entityManager.executeListener(new IEntityEventListener<Workflow, DiagramType>() {
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}

			@Override
			public void notify(EntityAddedEvent<Workflow, DiagramType> event) {
				allFlows.add(event.getEntity());
			}

			@Override
			public void notify(EntityUpdatedEvent<Workflow, DiagramType> event) {
			}

			@Override
			public void notifyLoadFinish(DiagramType diagram) {
			}

			@Override
			public void notifyAllLoadFinish() {
			}

			@Override
			public Class<Workflow> getEventType() {
				return Workflow.class;
			}
		});
		
		AppContext.get().register(this);
		
		flowContainer.startService(allFlows);
	}

	@Override
	public void reload() {
		
	}
	
	@Override
	public boolean readyToStop() {
		return true;
	}

	@Override
	public void stopService() {
		this.flowContainer.stopService();
	}

	@Override
	public int getRunLevel() {
		return 20;
	}
	
	@Override
	public Workflow getWorkflowEntity(String name) {
		IEntityManager entityManager = AppContext.get().getEntityManager();
		return entityManager.getEntity(name, Workflow.class);
	}
	
	@Override
	public void refreshWorkflow(String name) {
		flowContainer.restartService(getWorkflowEntity(name));
	}
	
	/**
	 * Import data from data base, all constant entities can be overrided.
	 * 
	 * @param constants
	 */
	public void importData(Workflow[] constants) {
		for (Workflow ce: constants) {
			logger.info("Add constant entity: " + ce.getEntityName() + " from DB data.");
		}
	}

	@Override
	public Class getServiceInterface() {
		return IWorkflowService.class;
	}

	FlowContainer getFlowContainer() {
        return flowContainer;
    }
	
	@Override
	public FlowObject getFlowObject(String flowName) {
		return flowContainer.getFlowObject(flowName);
	}
}
