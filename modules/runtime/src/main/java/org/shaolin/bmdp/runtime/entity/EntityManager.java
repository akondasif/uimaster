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
package org.shaolin.bmdp.runtime.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.JAXBException;

import org.shaolin.bmdp.datamodel.bediagram.BEDiagram;
import org.shaolin.bmdp.datamodel.bediagram.BusinessEntityType;
import org.shaolin.bmdp.datamodel.bediagram.ConstantEntityType;
import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.common.EntityType;
import org.shaolin.bmdp.datamodel.flowdiagram.FlowChunk;
import org.shaolin.bmdp.datamodel.page.ODMappingType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.datamodel.page.WebService;
import org.shaolin.bmdp.datamodel.pagediagram.WebChunk;
import org.shaolin.bmdp.datamodel.rdbdiagram.ClassMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBDiagram;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ViewType;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.runtime.cache.CacheManager;
import org.shaolin.bmdp.runtime.cache.ICache;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.utils.CloseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Here is only one entity manager while running multiple applications.
 * To reduce our cost of memory consuming.
 * 
 * Shaolin Wu
 */
@Service
public final class EntityManager implements IEntityManager {

	private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

	private List<String> extSequence = new ArrayList<String>();
	
	/**
	 * we are able to add some post tasks after finishing the entity initialization.
	 * Especially, this case happens in designtime build.
	 */
	private List<Runnable> postTasks = new ArrayList<Runnable>();
	
	/**
	 * the listeners for all entities.
	 */
	private final List<IEntityEventListener<? extends EntityType, ?>> listeners = 
			new CopyOnWriteArrayList<IEntityEventListener<? extends EntityType, ?>>();
	
	private static final Map<String, Class<?>> entityMapping = 
			new ConcurrentHashMap<String, Class<?>>();
	{
		entityMapping.put("bediagram", BEDiagram.class);
		entityMapping.put("rdbdiagram", RDBDiagram.class);
		entityMapping.put("pageflow", WebChunk.class);
		entityMapping.put("page", UIPage.class);
		entityMapping.put("form", UIEntity.class);
		entityMapping.put("websvis", WebService.class);
		entityMapping.put("flow", FlowChunk.class);
		entityMapping.put("workflow", Workflow.class);
	}
	
	/**
	 * The server cache is only one here.
	 * 
	 * K: entity type
	 * V: entity data
	 */
	private static final ICache<Class, ICache> sysEntityCache;
	
	static {
		sysEntityCache = CacheManager.getInstance().getCache("__sys_entity", Class.class, ICache.class);
		
		sysEntityCache.put(BusinessEntityType.class, CacheManager.getInstance().getCache("__sys_entity_be", String.class, BusinessEntityType.class));
		sysEntityCache.put(ConstantEntityType.class, CacheManager.getInstance().getCache("__sys_entity_ce", String.class, ConstantEntityType.class));
		sysEntityCache.put(ODMappingType.class, CacheManager.getInstance().getCache("__sys_entity_odform", String.class, ODMappingType.class));
		sysEntityCache.put(UIEntity.class, CacheManager.getInstance().getCache("__sys_entity_uiform", String.class, UIEntity.class));
		sysEntityCache.put(UIPage.class, CacheManager.getInstance().getCache("__sys_entity_uipage", String.class, UIPage.class));
		sysEntityCache.put(WebService.class, CacheManager.getInstance().getCache("__sys_entity_websvis", String.class, WebService.class));
		sysEntityCache.put(WebChunk.class, CacheManager.getInstance().getCache("__sys_entity_webflow", String.class, WebChunk.class));
		sysEntityCache.put(TableType.class, CacheManager.getInstance().getCache("__sys_entity_dbtable", String.class, TableType.class));
		sysEntityCache.put(ViewType.class, CacheManager.getInstance().getCache("__sys_entity_dbview", String.class, ViewType.class));
		sysEntityCache.put(ClassMappingType.class, CacheManager.getInstance().getCache("__sys_entity_rbmapping", String.class, ClassMappingType.class));
		sysEntityCache.put(FlowChunk.class, CacheManager.getInstance().getCache("__sys_entity_commonflow", String.class, FlowChunk.class));
		sysEntityCache.put(Workflow.class, CacheManager.getInstance().getCache("__sys_entity_workflow", String.class, Workflow.class));
	}

	/**
	 * Application entity cache.
	 */
	private ICache<Class, ICache> appEntityCache;
	
	private String appName = null;
	
	private final Map<String, LinkedList<JarEntryEntity>> scanningJarEntries = 
			new LinkedHashMap<String, LinkedList<JarEntryEntity>>();
	
	private final Map<String, LinkedList<File>> scanningFiles = 
			new LinkedHashMap<String, LinkedList<File>>();
	
	private final Map<String, File> lazyLoadingScanFiles = 
			new LinkedHashMap<String, File>();
	
	/**
	 * specify only scanning some paths.
	 */
	private String[] entityPathFilters = null;
	
	/**
	 * specify only scanning some types which is useful for performance optimization.
	 */
	private String[] scanTypes = new String[0];
	
	/**
	 * specify the lazy-loading types which purposes on perf-optimization.
	 */
	private String[] lazyLoadingTypes = new String[0];
	
	private boolean initialized = false;
	
	class JarEntryEntity {
		JarFile jar;
		JarEntry entry;
	}
	
	/**
	 * The server entity manager.
	 */
	public EntityManager() {
	}
	
	/**
	 * The application entity manager.
	 * 
	 * @param appName
	 */
	public EntityManager(String appName) {
		this.appName = appName;
		this.appEntityCache = CacheManager.getInstance().getCache(appName + "_entity", Class.class, ICache.class);
	}
	
	/**
	 * 
	 */
	public void initRuntime() {
		//diagrams are useless in runtime & forms are setting into lazy-loading mode.
		if (extSequence.size() > 0) {
			String[] extTypes = new String[extSequence.size()];
			extSequence.toArray(extTypes);
			String[] types = new String[] {"pageflow", "page", "form", "websvis", "workflow", "flow"};
			String[] scanTypes = new String[extTypes.length + types.length];
			int i = 0;
			for (;i<extTypes.length;i++) {
				scanTypes[i] = extTypes[i];
			}
			for (;i<types.length;i++) {
				scanTypes[i] = types[i];
			}
			this.scanTypes = scanTypes;
		} else {
			this.scanTypes = new String[] {"pageflow", "page", "form", "websvis", "workflow", "flow"};
		}
		this.lazyLoadingTypes = new String[] {"form"};
		init(null, null);
		//then we don't need it anymore.
		this.lazyLoadingTypes = new String[0];
	}
	
	/**
	 * Runtime initialization.
	 */
	public void init(List<IEntityEventListener<? extends EntityType, ?>> listeners, String[] filters) {
		if (initialized) {
			return;
		}
		
		try {
			if (listeners != null) {
				for (IEntityEventListener<? extends EntityType, ?> l : listeners) {
					l.setEntityManager(this);
				}
				this.listeners.addAll(listeners);
			}
			this.entityPathFilters = filters;
			
			scan();
			load(false);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Execute post tasks...");
			}
			for(Runnable run: this.postTasks) {
				run.run();
			}
		} finally {
			initialized = true;
		}
	}
	
	/**
	 * Design time with loading specific path.
	 * 
	 * @param listeners
	 * @param filters path or file filters
	 * @param entityPath
	 */
	public void init(List<IEntityEventListener<? extends EntityType, ?>> listeners, 
		String[] filters, File[] entityPath) {
		if (initialized) {
			return;
		}
		
		try {
			for (IEntityEventListener<? extends EntityType, ?> l : listeners) {
				l.setEntityManager(this);
			}
	
			this.listeners.addAll(listeners);
			this.entityPathFilters = filters;
			
			scan();
			try {
				for (File path : entityPath) {
					if (logger.isDebugEnabled()) {
						logger.debug("Loading entity directory: " + path);
					}    
					scanEntityFromDir(path);
				}
			} catch (java.io.IOException e) {
				logger.error("Failed to load path: " + entityPath, e);
			}
			load(false);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Execute post tasks...");
			}
			for(Runnable run: this.postTasks) {
				run.run();
			}
			
		} finally {
			initialized = true;
		}
	}

	private void scan() {
		try {
			Enumeration<URL> urls1 = EntityManager.class.getClassLoader()
					.getResources("entities/");
			while (urls1.hasMoreElements()) {
				URL url = urls1.nextElement();
				String path = url.toString();
				if (logger.isInfoEnabled()) {
					logger.info("Entities directory: " + path);
				}
				if (entityPathFilters != null) {
					boolean letGo = false;
					for (String filter : entityPathFilters) {
						if (path.indexOf(filter) != -1) {
							letGo = true;
							break;
						}
					}
					if (!letGo) {
						if (logger.isInfoEnabled()) {
							logger.info("Skip directory by filter pattern: " + path);
						}
						continue;
					}
				}
				if (path.startsWith("jar:")) {
					/**
					 * jar:file:/E:/projects/uimaster/components1.jar!/entities
					 */
					String file = path.substring("jar:file:".length(),
												 path.indexOf(".jar") + 4);
					scanEntityFromJar(file);
				} else {
					/**
					 * file:/E:/projects/uimaster/main/resources/entities/
					 */
					try {
						scanEntityFromDir(new File(url.toURI()));
					} catch (URISyntaxException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			
		} catch (IOException e1) {
			logger.error("Failed to scan the entity cache. Cause: "
							+ e1.getMessage(), e1);
		}
	}
	
	private void load(boolean reload) {
		try {
			// load the first level entities.
			for (String type : loadSequence) {
				if (logger.isInfoEnabled()) {
					logger.info("Load entity type: {}", type);
				}
				if (scanningJarEntries.containsKey(type)) {
					loadEntityFromJar(scanningJarEntries.get(type), reload);
					scanningJarEntries.remove(type);
				}
				if (scanningFiles.containsKey(type)) {
					LinkedList<File> files = scanningFiles.get(type);
					LinkedList<File> filterFiles = new LinkedList<File>();
					for (File f : files) {
						boolean letgo = false;
						for (String t: this.scanTypes) {
							if (f.getName().endsWith(t)) {
								letgo = true;
								break;
							}
						}
						if (letgo || this.scanTypes.length == 0) {
							filterFiles.add(f);
						} else {
							if (logger.isInfoEnabled()) {
								logger.info("Skip this file {} due to it's not any of scanning types for runtime.", f.getName());
							}
						}
					}
					for (File f : filterFiles) {
						String lltype;
						boolean letgo = true;
						for (String lltype0: this.lazyLoadingTypes) {
							if (f.getName().endsWith(lltype0)) {
								lltype = lltype0;
								letgo = false;
								break;
							}
						}
						if (!letgo) {
							String entityName = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf('.'));
							entityName = entityName.substring(entityName.indexOf("org"));
							entityName = entityName.replace(File.separatorChar, '.');
							lazyLoadingScanFiles.put(entityName, f);
							//TODO: support more type.
							sysEntityCache.get(UIEntity.class).remove(entityName);
							if (logger.isDebugEnabled()) {
								logger.debug("lazy loading this file {}, entityname {}", f.getName(), entityName);
							}
						} else {
							loadEntityFromDir(f, reload);
						}
					}
					scanningFiles.remove(type);
				}
				notifyAllLoadFinish();
			}
			// load the second level entities.
			for (String type: extSequence) {
				if (logger.isInfoEnabled()) {
					logger.info("Load entity type: {}", type);
				}
				if (scanningJarEntries.containsKey(type)) {
					loadEntityFromJar(scanningJarEntries.get(type), reload);
					scanningJarEntries.remove(type);
				}
				if (scanningFiles.containsKey(type)) {
					LinkedList<File> files = scanningFiles.get(type);
					for (File f : files) {
						loadEntityFromDir(f, reload);
					}
					scanningFiles.remove(type);
				}
				notifyAllLoadFinish();
			}
			// scanned jar entities
			if (!scanningJarEntries.isEmpty()) {
				Iterator<Entry<String, LinkedList<JarEntryEntity>>> iterator = 
						scanningJarEntries.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, LinkedList<JarEntryEntity>> entry = iterator.next();
					if (logger.isInfoEnabled()) {
						logger.info("Load entity type: {}", entry.getKey());
					}
					
					loadEntityFromJar(entry.getValue(), reload);
				}
				notifyAllLoadFinish();
				scanningJarEntries.clear();
			}
			// scanned file entities.
			if (!scanningFiles.isEmpty()) {
				Iterator<Entry<String, LinkedList<File>>> iterator = 
						scanningFiles.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, LinkedList<File>> entry = iterator.next();
					for (File f : entry.getValue()) {
						if (logger.isInfoEnabled()) {
							logger.info("Load entity type: {}", entry.getKey());
						}
						
						loadEntityFromDir(f, reload);
					}
					notifyAllLoadFinish();
				}
				scanningFiles.clear();
			}
		} catch (IOException e) {
			logger.error("Failed to load the entity cache. Cause: "
					+ e.getMessage(), e);
		} finally {
			scanningJarEntries.clear();
			scanningFiles.clear();
		}
		// all scanned entries must be removed 
		// after loading process finished.
	}
	
	private void scanEntityFromJar(final String file) throws IOException {
		JarFile jar = new JarFile(new File(file));
		final Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) {
			final JarEntry entry = e.nextElement();
			if (!entry.isDirectory() && entry.getName().startsWith("entities/")) {
				JarEntryEntity entitySource = new JarEntryEntity();
				entitySource.entry = entry;
				entitySource.jar = jar;
				
				String entityType = entry.getName();
				entityType = entityType.substring(entityType.lastIndexOf('.') + 1);
				if (!scanningJarEntries.containsKey(entityType)) {
					scanningJarEntries.put(entityType, new LinkedList<JarEntryEntity>());
				} 
				scanningJarEntries.get(entityType).add(entitySource);
			}
		}
	}
	
	private void scanEntityFromDir(final File path) throws IOException {
		if (path.isDirectory()) {
			final String files[] = path.list();
			for (final String file : files) {
				scanEntityFromDir(new File(path, file));
			}
		} else {
			String entityType = path.getName();
			entityType = entityType.substring(entityType.lastIndexOf('.') + 1);
			if (!scanningFiles.containsKey(entityType)) {
				scanningFiles.put(entityType, new LinkedList<File>());
			} 
			scanningFiles.get(entityType).add(path);
		}
	}
	
	private void loadEntityFromJar(LinkedList<JarEntryEntity> entries, boolean reload) throws IOException {
		for (JarEntryEntity item : entries) {
			if (!item.entry.isDirectory() && item.entry.getName().startsWith("entities/")) {
				InputStream in = null;
				try {
					in = item.jar.getInputStream(item.entry);
	
					String entityType = item.entry.getName();
					entityType = entityType.substring(entityType.lastIndexOf('.') + 1);
					
					addEntity(item.entry.getName(), in, entityType, reload);
				} catch (JAXBException e1) {
					logger.error("Failed to load entity " + item.entry.getName() 
							+ ". Cause: " + e1.getMessage(), e1);
				} finally {
					CloseUtil.close(in);
				}
			}
		}
	}

	private void loadEntityFromDir(final File path, boolean reload) throws IOException {
		if (path.isDirectory()) {
			final String files[] = path.list();
			for (final String file : files) {
				loadEntityFromDir(new File(path, file), reload);
			}
		} else {
			InputStream in = null;
			try {
				in = new FileInputStream(path);

				String entityType = path.getName();
				entityType = entityType
						.substring(entityType.lastIndexOf('.') + 1);
				String entityName = path.getName().substring(0, path.getName().lastIndexOf('.'));
				entityName = entityName.substring(entityName.lastIndexOf('.') + 1);
				addEntity(entityName, in, entityType, reload);
			} catch (JAXBException e1) {
				logger.error("Failed to load entity " + path.getName() 
						+ ". Cause: " + e1.getMessage(), e1);
			} finally {
				CloseUtil.close(in);
			}
		}
	}

	private void addEntity(final String name, InputStream in,
			String entityType, boolean reload) throws JAXBException {
		if (entityMapping.containsKey(entityType)) {
			Class<?> type = entityMapping.get(entityType);
			try {
				Object obj = EntityUtil.unmarshaller(type, in);
				addEntity(name, obj, reload);
			} catch (Exception e) {
				throw new JAXBException("Failed to load entity: " + name, e);
			}
		}
	}
	
	private void addEntity(String name, Object object, boolean reload) throws JAXBException {
		if (object instanceof DiagramType) {
			if (object instanceof BEDiagram) {
				BEDiagram bediagram = (BEDiagram)object;
				bediagram.setName(name);
				// ce has higher priority than be.
				List<ConstantEntityType> celist = bediagram.getCeEntities();
				for (ConstantEntityType ce: celist) {
					ce.setEntityName(bediagram.getCePackage() 
							+ "." + ce.getEntityName());
					addEntity0(bediagram, ce, ConstantEntityType.class, reload);
				}
				
				List<BusinessEntityType> belist = bediagram.getBeEntities();
				for (BusinessEntityType be: belist) {
					be.setEntityName(bediagram.getBePackage() 
							+ "." + be.getEntityName());
					addEntity0(bediagram, be, BusinessEntityType.class, reload);
				}
				
				for (IEntityEventListener<? extends EntityType, ?> listener: listeners) {
					if (listener.getEventType().isAssignableFrom(BusinessEntityType.class)
							|| listener.getEventType().isAssignableFrom(ConstantEntityType.class)) {
						try {
							listener.notifyLoadFinish(bediagram);
						} catch (Throwable e) {
							logger.warn(e.getMessage(), e);
						}
					}
				}
			} else if (object instanceof RDBDiagram) {
				RDBDiagram rdbdiagram = (RDBDiagram)object;
				rdbdiagram.setName(name);
				List<TableType> tableList = rdbdiagram.getTables();
				for (TableType table: tableList) {
					addEntity0(rdbdiagram, table, TableType.class, reload);
					
					if (table.getMapping() != null) {
						table.getMapping().setEntityName(table.getEntityName());
						addEntity0(rdbdiagram, table.getMapping(),
								ClassMappingType.class, reload);
					}
				}
				List<ViewType> viewList = rdbdiagram.getViews();
				for (ViewType view: viewList) {
					view.setEntityName(rdbdiagram.getDaoPackage() 
							+ "." + view.getEntityName());
					addEntity0(rdbdiagram, view, ViewType.class, reload);
				}
				
				for (IEntityEventListener<? extends EntityType, ?> listener: listeners) {
					if (listener.getEventType().isAssignableFrom(TableType.class)
							|| listener.getEventType().isAssignableFrom(ViewType.class)
							|| listener.getEventType().isAssignableFrom(RDBType.class)) {
						try {
							listener.notifyLoadFinish(rdbdiagram);
						} catch (Throwable e) {
							logger.warn(e.getMessage(), e);
						}
					}
				}
			} else {
				logger.warn("Unsupported diagram object: " + object);
			}
		} else if (object instanceof EntityType) {
			EntityType entity = (EntityType)object;
			addEntity0(entity, object.getClass(), reload);
		} else {
			logger.warn("Unsupported object: " + object);
		}
	}

	private void addEntity0(EntityType entity, Class<?> cacheId, boolean reload) {
		addEntity0(null, entity, cacheId, reload);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addEntity0(Object diagram, EntityType entity, Class<?> cacheId, boolean reload) {
		ICache<String, EntityType> cache;
		if (appName != null) {
			// add the customized entity.
			if (!appEntityCache.containsKey(cacheId)) {
				appEntityCache.put(cacheId, CacheManager.getInstance().getCache(appName + "_" + cacheId.getName(), String.class, entity.getClass()));
			}
			
			cache = (ICache<String, EntityType>) appEntityCache.get(cacheId);
		} else {
			if (!sysEntityCache.containsKey(cacheId)) {
				throw new IllegalArgumentException("Please register " 
						+ cacheId + " cache id before using it.");
			}
			
			cache = sysEntityCache.get(cacheId);
		}
		EntityType oldEntity = cache.putIfAbsent(entity.getEntityName(), entity);
		if (oldEntity != null) {
			if (reload) {
				if (logger.isInfoEnabled()) {
					logger.info("Reload this entity: " + entity.getEntityName());
				}
				cache.put(entity.getEntityName(), entity);
			} else {
				logger.warn("Entity already existed: " + entity.getEntityName());
			}
			for (IEntityEventListener<? extends EntityType, ?> listener: listeners) {
				if (listener.getEventType().isAssignableFrom(entity.getClass())) {
					EntityUpdatedEvent event = 
							new EntityUpdatedEvent(diagram, oldEntity, entity);
					try {
						listener.notify(event);
					} catch (Throwable e) {
						logger.warn(e.getMessage(), e);
					}
				}
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Added entity: " + entity.getEntityName());
			}
			for (IEntityEventListener<? extends EntityType, ?> listener: listeners) {
				if (listener.getEventType().isAssignableFrom(entity.getClass())) {
					try {
						listener.notify(new EntityAddedEvent(diagram, entity));
					} catch (Throwable e) {
						logger.warn(e.getMessage(), e);
					}
				}
			}
		}
	}
	
	private void notifyAllLoadFinish() {
		for (IEntityEventListener<? extends EntityType, ?> listener: listeners) {
			try {
				listener.notifyAllLoadFinish();
			} catch (Throwable e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}
	
	public void addLoadSequence(String entityType) {
		this.extSequence.add(entityType);
	}
	
	public void addListeners(List<IEntityEventListener<? extends EntityType, ?>> listeners) {
		this.listeners.addAll(listeners);
	}
	
	public void addListener(IEntityEventListener<? extends EntityType, ?> listener) {
		this.listeners.add(listener);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void executeListener(IEntityEventListener<? extends EntityType, ?> listener) {
		ArrayList<String> customizedList = new ArrayList<String>();
		if (appName != null) {
			// if this is application, try to access whether has a customized entity or not.
			ICache<String, EntityType> cache = appEntityCache.get(listener.getEventType());
			if (cache != null && cache.size() > 0) {
				Set<Map.Entry<String, EntityType>> entries = cache.getCacheData().entrySet();
				for (Map.Entry<String, EntityType> e : entries) {
					customizedList.add(e.getValue().getEntityName());
					EntityAddedEvent event = new EntityAddedEvent(null, e.getValue());
					listener.notify(event);
				}
			}
		}
		ICache<String, EntityType> cache = sysEntityCache.get(listener.getEventType());
		if (cache == null) {
			throw new IllegalArgumentException("Unsupported entity: " + listener.getEventType());
		}
		Set<Map.Entry<String, EntityType>> entries = cache.getCacheData().entrySet();
		for (Map.Entry<String, EntityType> e : entries) {
			if (customizedList.size() > 0 && customizedList.contains(e.getValue().getEntityName())) {
				//reject it.
				continue;
			}
			EntityAddedEvent event = new EntityAddedEvent(null, e.getValue());
			listener.notify(event);
		}
		listener.notifyAllLoadFinish();
	}
	
	public void registerEntityType(String suffix, Class<?> entityType) {
		if (entityType.isAssignableFrom(EntityType.class)) {
			entityMapping.put(suffix, entityType);
			if (appName != null) {
				appEntityCache.put(entityType, CacheManager.getInstance().getCache(appName + "_" + suffix, String.class, entityType.getClass()));
			} 
			sysEntityCache.put(entityType, CacheManager.getInstance().getCache(suffix, String.class, entityType.getClass()));
		}
		throw new IllegalArgumentException("Entity class must extend from EntityType.class");
	}
	
	public void addPostTask(Runnable task) {
		this.postTasks.add(task);
	}
	
	public void appendEntity(String type, File file) {
		if (!this.scanningFiles.containsKey(type)) {
			this.scanningFiles.put(type, new LinkedList<File>());
		}
		this.scanningFiles.get(type).add(file);
	}
	
	public void appendEntity(EntityType entity) {
		try {
			addEntity(entity.getEntityName(), entity, true);
		} catch (JAXBException e) {
			logger.warn("Failed to append entity to cache.", e);
		}
	}
	
	public void appendEntity(DiagramType diagram) {
		try {
			addEntity(diagram.getName(), diagram, true);
		} catch (JAXBException e) {
			logger.warn("Failed to append entity to cache.", e);
		}
	}
	
	public void reloadDir(File path) throws IOException {
		scanningJarEntries.clear();
		scanningFiles.clear();
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Reloading entity directory: " + path);
			}
			// no need filters for file.
			scanEntityFromDir(path);
			load(true);
		} finally {
			scanningJarEntries.clear();
			scanningFiles.clear();
		}
	}
	
	/**
	 * Design time case.
	 * 
	 * @param path
	 * @param fileFilters
	 * @throws IOException
	 */
	public void reloadDir(File path, String[] fileFilters) throws IOException {
		scanningJarEntries.clear();
		scanningFiles.clear();
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Reloading entity directory: " + path);
			}
			// no need filters for file.
			scanEntityFromDir(path);
			
			 Map<String, LinkedList<File>> scanningFiles0 = new LinkedHashMap<String, LinkedList<File>>();
			for(String s: fileFilters) {
				if (scanningFiles.containsKey(s)) {
					scanningFiles0.put(s, scanningFiles.get(s));
				}
			}
			scanningFiles.clear();
			scanningFiles.putAll(scanningFiles0);
			
			load(true);
		} finally {
			scanningJarEntries.clear();
			scanningFiles.clear();
		}
	}
	
	
	public void reloadEntity(EntityType entity) throws IOException {
		Class<?> type = entity.getClass();
		addEntity0(entity, type, true);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEntity(String name, Class<T> type) throws EntityNotFoundException {
		if (sysEntityCache.containsKey(type)) {
			if (sysEntityCache.get(type).containsKey(name)) {
				return (T) sysEntityCache.get(type).get(name);
			} 
		} 
		if (lazyLoadingScanFiles.containsKey(name)) {
			File f = lazyLoadingScanFiles.remove(name);
			try {
				loadEntityFromDir(f, true);
			} catch (IOException e) {
				logger.error("Failed to load this file " +f.getAbsolutePath() +". Cause: "
						+ e.getMessage(), e);
			}
			if (sysEntityCache.containsKey(type)) {
				if (sysEntityCache.get(type).containsKey(name)) {
					return (T) sysEntityCache.get(type).get(name);
				} 
			} 
		}
		throw new EntityNotFoundException(ExceptionConstants.UIMASTER_ODMAPPER_056,
				new Object[]{name});
	}
	
	public void addEventListener(IEntityEventListener<? extends EntityType, ?> listener) {
		listener.setEntityManager(this);
		listeners.add(listener);
	}

	public void removeEventListener(IEntityEventListener<? extends EntityType, ?> listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Release the useless caches after startup.
	 * 
	 */
	public void offUselessCaches() {
		CacheManager.getInstance().getCache("__sys_entity_be", String.class, BusinessEntityType.class).clear();
		CacheManager.getInstance().getCache("__sys_entity_ce", String.class, ConstantEntityType.class).clear();
		CacheManager.getInstance().getCache("__sys_entity_rbmapping", String.class, ClassMappingType.class).clear();
		CacheManager.getInstance().getCache("__sys_entity_commonflow", String.class, FlowChunk.class).clear();
		CacheManager.getInstance().getCache("__sys_entity_workflow", String.class, Workflow.class).clear();
		// these are lazy-loading components.
//		CacheManager.getInstance().getCache("__sys_entity_odform", String.class, ODMappingType.class).clear();
//		CacheManager.getInstance().getCache("__sys_entity_uiform", String.class, UIEntity.class).clear();
		CacheManager.getInstance().getCache("__sys_entity_uipage", String.class, UIPage.class).clear();
		CacheManager.getInstance().getCache("__sys_entity_websvis", String.class, WebService.class).clear();
		CacheManager.getInstance().getCache("__sys_entity_webflow", String.class, WebChunk.class).clear();
//		CacheManager.getInstance().getCache("__sys_entity_dbtable", String.class, TableType.class).clear();
		CacheManager.getInstance().getCache("__sys_entity_dbview", String.class, ViewType.class).clear();
		
		
		sysEntityCache.remove(BusinessEntityType.class);
		sysEntityCache.remove(ConstantEntityType.class);
		sysEntityCache.remove(FlowChunk.class);
		sysEntityCache.remove(ClassMappingType.class);
		sysEntityCache.remove(Workflow.class);
//		sysEntityCache.remove(ODMappingType.class);
//		sysEntityCache.remove(UIEntity.class);
		sysEntityCache.remove(UIPage.class);
		sysEntityCache.remove(WebService.class);
		sysEntityCache.remove(WebChunk.class);
//		sysEntityCache.remove(TableType.class);
		sysEntityCache.remove(ViewType.class);
		
	}

}
