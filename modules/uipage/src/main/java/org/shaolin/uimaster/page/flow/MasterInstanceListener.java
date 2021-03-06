package org.shaolin.uimaster.page.flow;

import java.util.ArrayList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.datamodel.page.WebService;
import org.shaolin.bmdp.datamodel.pagediagram.WebChunk;
import org.shaolin.bmdp.persistence.HibernateUtil;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.ce.ConstantServiceImpl;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.bmdp.runtime.spi.IAppServiceManager;
import org.shaolin.bmdp.runtime.spi.IAppServiceManager.State;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.ILifeCycleProvider;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.WebConfigSpringInstance;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFlowCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The master instance is responsible for reading all entities from the whole
 * system.
 * 
 * @author wushaol
 * 
 */
@Service
public class MasterInstanceListener implements ServletContextListener, ILifeCycleProvider {

	private static final Logger logger = LoggerFactory.getLogger(MasterInstanceListener.class);

	private boolean initialized = false;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		if (initialized) {
			return;
		}
		if (!sce.getServletContext().getContextPath().equals("/uimaster")) {
			return;
		}
		IServerServiceManager serverManager = IServerServiceManager.INSTANCE;
		if (serverManager.getState() != State.NONE) {
			return;
		}
		// only one time initialization will be ok.
		initialized = true;
		logger.info("Initializing application instance " + sce.getServletContext().getContextPath() + "...");
		try {
			AppContext.register(serverManager);
			// add application to the server manager.
			serverManager.setAppClassLoader(this.getClass().getClassLoader());
			// bind the app context with the servlet context.
			sce.getServletContext().setAttribute(IAppServiceManager.class.getCanonicalName(), IServerServiceManager.INSTANCE);
			
			// configure all life-cycled services.
			serverManager.configureLifeCycleProviders();
	    	
			// load all entities from applications. only load once if there were many web instances.
			IEntityManager entityManager = serverManager.getEntityManager();
			((EntityManager)entityManager).initRuntime();
			
//	    	// @Deprecated 
	    	// wire all services by spring automatically.
//	    	OOEEContext context = OOEEContextFactory.createOOEEContext();
//	    	List<String> serviceNodes = Registry.getInstance().getNodeChildren("/System/services");
//        	for (String path: serviceNodes) {
//        		String expression = Registry.getInstance().getExpression("/System/services/" + path);
//        		logger.debug("Evaluate module initial expression: " + expression);
//        		CompilationUnit compliedUnit = StatementParser.parse(expression, context);
//        		compliedUnit.execute(context);
//        	}
        	serverManager.startLifeCycleProviders();
        	serverManager.setState(State.ACTIVE);
        	entityManager.offUselessCaches();
	    	
        	logger.info("\n=============={} is initialized successfully.==============", sce.getServletContext().getContextPath());
		} catch (Throwable e) {
			logger.error("Fails to start Config server start! Error: " + e.getMessage(), e);
			serverManager.setState(State.FAILURE);
		} finally {
			HibernateUtil.releaseSession(true);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (IServerServiceManager.INSTANCE != null) {
			try {
				IServerServiceManager.INSTANCE.stopLifeCycleProviders();
			} catch (Exception e) {
				//meaningless.
			}
		}
	}

	@Override
	public void configService() {
		IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();
		WebConfigSpringInstance instance = IServerServiceManager.INSTANCE.getSpringContext().getBean(WebConfigSpringInstance.class);
    	logger.info("UIMaster contextRoot: " + instance.getContextRoot());
		WebConfig.setSpringInstance(instance);
		//WebConfig.setResourcePath(servletContext.getRealPath("/"));
		//load all customized constant items from DB table.
		entityManager.addEventListener((ConstantServiceImpl)IServerServiceManager.INSTANCE.getConstantService());
		entityManager.addEventListener(new IEntityEventListener<WebChunk, DiagramType>() {
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}

			@Override
			public void notify(
					EntityAddedEvent<WebChunk, DiagramType> event) {
				try {
					UIFlowCacheManager.getInstance().addChunk(
							event.getEntity());
				} catch (ParsingException e) {
					logger.error(
							"Parse ui flow error: " + e.getMessage(), e);
				}
			}

			@Override
			public void notify(
					EntityUpdatedEvent<WebChunk, DiagramType> event) {
				try {
					UIFlowCacheManager.getInstance().addChunk(
							event.getNewEntity());
				} catch (ParsingException e) {
					logger.error(
							"Parse ui flow error: " + e.getMessage(), e);
				}
			}

			@Override
			public void notifyLoadFinish(DiagramType diagram) {
			}

			@Override
			public void notifyAllLoadFinish() {
			}

			@Override
			public Class<WebChunk> getEventType() {
				return WebChunk.class;
			}

		});
		entityManager.addEventListener(new IEntityEventListener<UIPage, DiagramType>() {
			ArrayList<String> uipages = new ArrayList<String>();
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}

			@Override
			public void notify(
					EntityAddedEvent<UIPage, DiagramType> event) {
				uipages.add(event.getEntity().getEntityName());
			}

			@Override
			public void notify(
					EntityUpdatedEvent<UIPage, DiagramType> event) {
				uipages.add(event.getNewEntity().getEntityName());
			}

			@Override
			public void notifyLoadFinish(DiagramType diagram) {
			}

			@Override
			public void notifyAllLoadFinish() {
				for (String uipage: uipages) {
					try {
						PageCacheManager.getODPageEntityObject(uipage);
						PageCacheManager.getUIPageObject(uipage);
					} catch (Exception e) {
						logger.error(
								"Parse ui page error: " + e.getMessage(), e);
					}
				}
				uipages.clear();
			}

			@Override
			public Class<UIPage> getEventType() {
				return UIPage.class;
			}
		});
		entityManager.addEventListener(new IEntityEventListener<UIEntity, DiagramType>() {
			ArrayList<String> uiforms = new ArrayList<String>();
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}

			@Override
			public void notify(
					EntityAddedEvent<UIEntity, DiagramType> event) {
				//uiforms.add(event.getEntity().getEntityName());
			}

			@Override
			public void notify(
					EntityUpdatedEvent<UIEntity, DiagramType> event) {
				//uiforms.add(event.getNewEntity().getEntityName());
			}

			@Override
			public void notifyLoadFinish(DiagramType diagram) {
			}

			@Override
			public void notifyAllLoadFinish() {
				// lazy loading all ui-forms.
//				for (String uiform: uiforms) {
//					try {
//						PageCacheManager.getODFormObject(uiform);
//						PageCacheManager.getUIFormObject(uiform);
//					} catch (Exception e) {
//						logger.error(
//								"Parse ui page error: " + e.getMessage(), e);
//					}
//				}
				uiforms.clear();
			}

			@Override
			public Class<UIEntity> getEventType() {
				return UIEntity.class;
			}
		});
		entityManager.addEventListener(new IEntityEventListener<WebService, DiagramType>() {
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}

			@Override
			public void notify(
					EntityAddedEvent<WebService, DiagramType> event) {
				try {
					PageCacheManager.addWebService(event.getEntity());
				} catch (ParsingException e) {
					logger.error(
							"Parse web service error: " + e.getMessage(), e);
				}
			}

			@Override
			public void notify(
					EntityUpdatedEvent<WebService, DiagramType> event) {
				try {
					PageCacheManager.addWebService(event.getNewEntity());
				} catch (ParsingException e) {
					logger.error(
							"Parse web service error: " + e.getMessage(), e);
				}
			}

			@Override
			public void notifyLoadFinish(DiagramType diagram) {
			}

			@Override
			public void notifyAllLoadFinish() {
			}

			@Override
			public Class<WebService> getEventType() {
				return WebService.class;
			}
		});
	}

	@Override
	public void startService() {
		
	}
	
	@Override
	public boolean readyToStop() {
		return true;
	}

	@Override
	public void stopService() {
		
	}

	@Override
	public void reload() {
		
	}

	@Override
	public int getRunLevel() {
		return 0;
	}

}
