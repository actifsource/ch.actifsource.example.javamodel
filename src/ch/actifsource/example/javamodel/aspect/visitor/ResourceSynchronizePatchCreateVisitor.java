package ch.actifsource.example.javamodel.aspect.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;
import ch.actifsource.core.CorePackage;
import ch.actifsource.core.INode;
import ch.actifsource.core.Literal;
import ch.actifsource.core.Package;
import ch.actifsource.core.PackagedResource;
import ch.actifsource.core.Relationship;
import ch.actifsource.core.Resource;
import ch.actifsource.core.Statement;
import ch.actifsource.core.dynamic.DynamicResource.IPropertyValueVisitor;
import ch.actifsource.core.dynamic.IDynamicResource;
import ch.actifsource.core.dynamic.IResourceRef;
import ch.actifsource.core.job.IReadJobExecutor;
import ch.actifsource.core.job.Select;
import ch.actifsource.core.model.JavaTypeUtil;
import ch.actifsource.core.model.aspects.impl.IGenericLiteralAspect;
import ch.actifsource.core.patch.CompositePatch;
import ch.actifsource.core.patch.IPatch;
import ch.actifsource.core.patch.IStatementPosition;
import ch.actifsource.core.set.INodeList;
import ch.actifsource.core.set.INodeSet;
import ch.actifsource.core.set.IStatementSet;
import ch.actifsource.core.set.NodeList;
import ch.actifsource.core.set.NodeSet;
import ch.actifsource.core.set.StatementSet;
import ch.actifsource.core.util.NodeUtil;
import ch.actifsource.util.Assert;
import ch.actifsource.util.collection.CollectionUtil;
import ch.actifsource.util.collection.HashMap;
import ch.actifsource.util.collection.IMap;
import ch.actifsource.util.collection.IMultiMapOrdered;

/**
 * Define the patch extractor visitor.
 * The patch contains the steps to transform the existing resource to the toResource (similar typeOfs are equals but different guid).
 */
public class ResourceSynchronizePatchCreateVisitor implements IPropertyValueVisitor {

  /**
   * Returns the patch contains the steps to transform the existing resource to the toResource (similar).
   */
  public static IPatch createSynchronizePatch(IReadJobExecutor readJobExecutor, PackagedResource fromResource, IDynamicResource toResource) {
    return createSynchronizePatch(readJobExecutor, fromResource, toResource, new NodeSet(CorePackage.Resource_typeOf));
  }
  
  /**
   * Returns the patch contains the steps to transform the existing resource to the toResource (similar).
   */
  public static IPatch createSynchronizePatch(IReadJobExecutor readJobExecutor, PackagedResource fromResource, IDynamicResource toResource, INodeSet ignoreProperties) {
    return createSynchronizePatch(readJobExecutor, fromResource, toResource, ignoreProperties, false);
  }
  
  /**
   * Returns the patch contains the steps to transform the existing resource to the toResource (similar).
   */
  public static IPatch createSynchronizePatch(IReadJobExecutor readJobExecutor, PackagedResource fromResource, IDynamicResource toResource, INodeSet ignoreProperties, boolean syncExistingResources) {
    PatchBuilder patchBuilder = new PatchBuilder(readJobExecutor);
    toResource.accept(new ResourceSynchronizePatchCreateVisitor(readJobExecutor, fromResource.getPackage(), fromResource.getResource(), ignoreProperties, patchBuilder, syncExistingResources));
    return patchBuilder.buildPatch();
  }
 
  /**
   * Define the interface from the lazy patch creator.
   */
  public static interface ILazyPatchCreator {
    
    /**
     * Create lazy patch.
     */
    public void create(IPatchCreateFactory factory);
  }
  
  /**
   * Define the patch create factory interface.
   */
  public static interface IPatchCreateFactory {
    
    /**
     * Create resource patch.
     */
    public INode createResource(Package package_, INode typeOf);
    
    /**
     * Create add statement patch.
     */
    public void addStatement(Statement statement, IStatementPosition position);

    /**
     * Create dispose statement patch.
     */
    public void disposeStatement(Statement statement, @CheckForNull Statement next);
    
    /**
     * Create remove statement patch.
     */
    public void removeStatement(Statement statement, @CheckForNull Statement next);
    
    /**
     * Add lazy loaded patch.
     */
    public void addLazyPatchCreator(ILazyPatchCreator lazyPatchCreator);
    
    /**
     * Returns true if the patch factory is canceled.
     */
    public boolean isCanceled();
  }
  
  /**
   * Defines the patch factory builder.
   */
  public static class PatchBuilder implements IPatchCreateFactory {

    private final CompositePatch                    fCompositePatch = new CompositePatch();
    
    private final IReadJobExecutor                  fReadJobExecutor;
    
    private final List<ILazyPatchCreator>           fLazyPatchCreator = new ArrayList<>();
   
    
    public PatchBuilder(IReadJobExecutor  readJobExecutor) {
      fReadJobExecutor = readJobExecutor;
    }
    
    /**
     * Build and returns a composition patch.
     */
    public CompositePatch buildPatch() {
      ILazyPatchCreator creator = null;
      while((creator = CollectionUtil.removeFirstOrNull(fLazyPatchCreator)) != null) {
        creator.create(this);
      }
      return fCompositePatch;
    }
    
    @Override
    public boolean isCanceled() {
      return false;
    }
    
    @Override
    public void addLazyPatchCreator(ILazyPatchCreator lazyPatchCreator) {
      fLazyPatchCreator.add(lazyPatchCreator);
    }
    
    @Override
    public INode createResource(Package package_, INode typeOf) {
      Resource newResource = new Resource();
      fCompositePatch.create(package_, newResource);
      fCompositePatch.add(new Statement(package_, newResource, CorePackage.Resource_typeOf, typeOf), IStatementPosition.AT_END);
      return newResource;
    }

    @Override
    public void addStatement(Statement statement, IStatementPosition position) {
      fCompositePatch.add(statement, position);
    }

    @Override
    public void disposeStatement(Statement statement, Statement next) {
      fCompositePatch.remove(statement, next);
      if (!statement.isOwn()) return;
      
      IStatementSet statementsToDispose = Select.statements(fReadJobExecutor, statement.object());
      for (int index = 0; index < statementsToDispose.size(); index++) {
        Statement statementToDispose = statementsToDispose.get(index);
        disposeStatement(statementToDispose, getNext(statementsToDispose, index));
      }
      fCompositePatch.dispose(statement.getPackage(), statement.object());
    }

    @Override
    public void removeStatement(Statement statement, Statement next) {
      fCompositePatch.remove(statement, next);
    }
  }
  
  /**
   * Defines the equals patch builder.
   * as soon as a patch is created, the isEquals function returns false.
   */
  public static class EqualsPatch implements IPatchCreateFactory {

    boolean isEquals = true;
    
    /**
     * Returns false if any patch is created. 
     */
    @Override
    public boolean isCanceled() {
      return !isEquals;
    }
    
    /**
     * Returns true if no patch is created.
     */
    public boolean isEquals() {
      return isEquals;
    }
    
    @Override
    public void addLazyPatchCreator(ILazyPatchCreator lazyPatchCreator) {
      isEquals = false;
    }
    
    @Override
    public INode createResource(Package package_, INode typeOf) {
      isEquals = false;
      return new Resource();
    }

    @Override
    public void addStatement(Statement statement, IStatementPosition position) {
      isEquals = false;
    }

    @Override
    public void disposeStatement(Statement statement, Statement next) {
      isEquals = false;
    }

    @Override
    public void removeStatement(Statement statement, Statement next) {
      isEquals = false;
    }
  }
  
  /**
   * Define the cache key.
   */
  public static class CacheKey {
    
    private INode             fFromObject;
    
    private IDynamicResource  fToObject;
    
    public CacheKey(INode fromObject, IDynamicResource toObject) {
      fFromObject = fromObject;
      fToObject = toObject;
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = getClass().hashCode();
      result = prime * result + fFromObject.hashCode();
      result = prime * result + fToObject.hashCode();
      return result;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      CacheKey other = (CacheKey)obj;
      if (!fFromObject.equals(other.fFromObject)) return false;
      if (!fToObject.equals(other.fToObject)) return false;
      return true;
    }
  }
  
  private final INodeSet                          fIgnoreProperties;

  private final boolean                           fSyncExistingResources;
  
  private final IReadJobExecutor                  fReadJobExecutor;
  
  private final INode                             fFromResource;
  
  private final Package                           fPackage;
  
  private final IPatchCreateFactory               fPatchCreateFactory;
  
  private final IMap<CacheKey, Boolean>           fCheckDeepEqualsCache;

  private final HashMap<IDynamicResource, INode>  fResourceToNodeMap;
  
  private final Set<IDynamicResource>             fCreatedResources;
  
  
  public ResourceSynchronizePatchCreateVisitor(IReadJobExecutor readJobExecutor, Package pkg, INode fromResource, INodeSet ignoreProperties, IPatchCreateFactory patchCreateFactory, 
      boolean syncExistingResources) {
    this(readJobExecutor, pkg, fromResource, ignoreProperties, patchCreateFactory, syncExistingResources, new HashMap<CacheKey, Boolean>());
  }
  
  public ResourceSynchronizePatchCreateVisitor(IReadJobExecutor readJobExecutor, Package pkg, INode fromResource, INodeSet ignoreProperties, IPatchCreateFactory patchCreateFactory, 
      boolean syncExistingResources, IMap<CacheKey, Boolean> checkDeepEqualsCache) {
    this(readJobExecutor, pkg, fromResource, ignoreProperties, patchCreateFactory, syncExistingResources, checkDeepEqualsCache, new HashMap<IDynamicResource, INode>(), new HashSet<IDynamicResource>());
  }
  
  public ResourceSynchronizePatchCreateVisitor(IReadJobExecutor readJobExecutor, Package pkg, INode fromResource, INodeSet ignoreProperties, IPatchCreateFactory patchCreateFactory, 
      boolean syncExistingResources, IMap<CacheKey, Boolean> checkDeepEqualsCache, HashMap<IDynamicResource, INode> resourceToNodeMap, Set<IDynamicResource> createdResources) {
    fReadJobExecutor = readJobExecutor;
    fPackage = pkg;
    fFromResource = fromResource;
    fPatchCreateFactory = patchCreateFactory;
    fIgnoreProperties = ignoreProperties;
    fSyncExistingResources = syncExistingResources;
    fCheckDeepEqualsCache = checkDeepEqualsCache;
    fResourceToNodeMap = resourceToNodeMap;
    fCreatedResources = createdResources;
  }
  
  /**
   * Returns the literal aspect from the attribute.
   */
  @SuppressWarnings("unchecked")
  @CheckForNull
  private <T> IGenericLiteralAspect<T> getLiteralAspect(java.lang.Class<T> type, INode attribute) {
    IGenericLiteralAspect<?> aspect = JavaTypeUtil.getGenericLiteralAspectForAttributeOrNull(fReadJobExecutor, attribute);
    if (aspect == null) return null;
    Assert.assertTrue("value is not assignable to attribute", aspect.getValueType().isAssignableFrom(type));
    return ((IGenericLiteralAspect<T>)aspect);
  }
  
  /**
   * Returns the literal from the attribute value by aspect.
   */
  @CheckForNull
  private <T> Literal asLiteralValue(Class<T> type, INode attribute, T value) {
    if (value == null) return null;
    IGenericLiteralAspect<T> literalAspect = getLiteralAspect(type, attribute);
    if (literalAspect == null) return null;
    return literalAspect.create(value);
  }
  
  /**
   * Returns all literals from the attribute values by aspect.
   */
  @CheckForNull
  private <T> INodeList asLiteralValues(Class<T> type, INode attribute, List<T> values) {
    if (values.isEmpty()) return NodeList.EMPTY_LIST;
    IGenericLiteralAspect<T> literalAspect = getLiteralAspect(type, attribute);
    if (literalAspect == null) return NodeList.EMPTY_LIST;
    NodeList literalValues = new NodeList(values.size());
    for (T value: values) {
      Literal literalValue = literalAspect.create(value);
      literalValues.add(literalValue);
    }
    return literalValues;
  }
  
  /**
   * Returns the next (index + 1) statement from the set.
   */
  private static Statement getNext(IStatementSet statements, int index) {
    if (index + 1 < statements.size()) return statements.get(index + 1);
    return null;
  }
  
  /**
   * Returns true if the type is equals.
   */
  private boolean isTypeOfEquals(INode fromObject, IDynamicResource toObject) {
    if (Select.shallowType(fReadJobExecutor, fromObject).equals(toObject.getTypeId())) {
      return true;
    }
    return false;
  }
  
  /**
   * Returns all statement for relation.
   */
  private IStatementSet statementsForRelation(IReadJobExecutor executor, INode relation, INode subject) {
    StatementSet result = new StatementSet();
    for (Statement statement :Select.statementsForRelation(fReadJobExecutor, relation, fFromResource)) {
      if (!statement.predicate().equals(relation)) continue;
      result.add(statement);
    }
    return result;
  }
  
  /**
   * Returns all statement for attribute.
   */
  private IStatementSet statementsForAttribute(IReadJobExecutor executor, INode attribute, INode subject) {
    StatementSet result = new StatementSet();
    for (Statement statement :Select.statementsForAttribute(fReadJobExecutor, attribute, fFromResource)) {
      if (!statement.predicate().equals(attribute)) continue;
      result.add(statement);
    }
    return result;
  }
  
  /**
   * Returns true if the two resources (fromObject and toObject) are deeply  equals.
   */
  private boolean isDeepEquals(INode fromObject, IDynamicResource toObject) {
    if (!fSyncExistingResources && !toObject.isVirtual()) return fromObject.equals(toObject.getResource());
    if (!isTypeOfEquals(fromObject, toObject)) return false;

    /** Check whether both objects have already been checked and are identical **/ 
    CacheKey cacheKey = new CacheKey(fromObject, toObject);
    Boolean equals = fCheckDeepEqualsCache.get(cacheKey);
    if (equals != null) return equals;
    
    /** Check cycle detection. If the same objects are compared within a detection, it is assumed that they are similar; if they are not similar, other properties must be dissimilar. **/ 
    if (fCheckDeepEqualsCache.containsKey(cacheKey)) {
      return true;
    }
    fCheckDeepEqualsCache.put(cacheKey, null);

    /** check whether both objects are identical **/
    EqualsPatch equalsPatch = new EqualsPatch();
    toObject.accept(new ResourceSynchronizePatchCreateVisitor(fReadJobExecutor, fPackage, fromObject, fIgnoreProperties, equalsPatch, fSyncExistingResources, fCheckDeepEqualsCache));
    boolean isEquals = equalsPatch.isEquals();
    fCheckDeepEqualsCache.put(cacheKey, isEquals);
    return isEquals;
  }
  
  /**********************
   * Create Patch
   *********************/
  
  /**
   * Create Patch:
   * Create remove all statement patch.
   */
  private void removeAllStatements(IStatementSet existingStatements, IPatchCreateFactory factory) {
    for (int index = 0; index < existingStatements.size(); index++) {
      if (factory.isCanceled()) return;
      factory.disposeStatement(existingStatements.get(index), getNext(existingStatements, index));
    }
  }
  
  /**
   * Create Patch:
   * Create new or existing resource.
   */
  private INode getOrCreateNewResource(IDynamicResource resource, IPatchCreateFactory factory) {
    if (!resource.isVirtual()) return resource.getResource();
    INode newResource = fResourceToNodeMap.get(resource);
    if (newResource != null) return newResource;
    newResource = factory.createResource(fPackage, resource.getTypeId());
    fResourceToNodeMap.put(resource, newResource);
    return newResource;
  }
  
  /**
   * Create Patch:
   * Create the deep synchronization patch for the objects map.
   * @param dynamicResourceToObjectMap = contains the mapping fromResource to toResource.
   * @param factory
   */
  private void createDeepSynchronizeDynamicResourcePatch(IMap<IDynamicResource, INode> dynamicResourceToNodeMap, IPatchCreateFactory factory) {
    for (IDynamicResource toResource :dynamicResourceToNodeMap.keySet()) {
      if (factory.isCanceled()) return;
         
      /** Synchronize all objects within the map. **/
      if ((fSyncExistingResources || toResource.isVirtual()) && !fCreatedResources.contains(toResource)) {
        Package pkg = (toResource.isVirtual())? fPackage: Select.mainPackage(fReadJobExecutor, toResource.getResource());
        if (pkg.isModifiable()) {
          fCreatedResources.add(toResource); 
          INode fromResource =  dynamicResourceToNodeMap.get(toResource);        
          toResource.accept(new ResourceSynchronizePatchCreateVisitor(fReadJobExecutor, pkg, fromResource, fIgnoreProperties, fPatchCreateFactory, fSyncExistingResources, fCheckDeepEqualsCache, fResourceToNodeMap, fCreatedResources));
        }
      }
    }
  }
  
  /**
   * Create Patch:
   * Create the synchronization patch for the existing objects in the relationship.
   */
  private void createSynchronizeToExistingNodePath(IStatementSet fromStatements, INode predicate, @CheckForNull INode toObject, IPatchCreateFactory factory) {
    createSynchronizeToExistingNodePath(fromStatements, predicate, NodeUtil.asNodeList(toObject), factory);
  }

  /**
   * Create Patch:
   * Create the synchronization patch for the existing objects in the relationship.
   */
  private void createSynchronizeToExistingNodePath(IStatementSet fromStatements, INode predicate, INodeList toObjects, IPatchCreateFactory factory) {
    if (factory.isCanceled()) return;
    
    /** Remove all statement if the target has no statements. **/
    if (toObjects.isEmpty()) {
      removeAllStatements(fromStatements, factory);
      return;
    }
    
    /** Define the next statement index to check. **/
    int nextStatementIndex = 0;
    
    for (int toIndex = 0; toIndex < toObjects.size(); toIndex++) {
      if (factory.isCanceled()) return;
      
      INode toObject = toObjects.get(toIndex);
      boolean foundMatchingObject = false;
      while (nextStatementIndex < fromStatements.size()) {
        Statement fromStatement = fromStatements.get(nextStatementIndex);
        nextStatementIndex++;
        if (toObject.equals(fromStatement.object())) {
          foundMatchingObject = true;
          break;
        }
        factory.removeStatement(fromStatement, getNext(fromStatements, nextStatementIndex - 1));
      }
      
      if (!foundMatchingObject) {
        factory.addStatement(new Statement(fPackage, fFromResource, predicate, toObject), IStatementPosition.AT_END);
      }
    }
    
    /** Remove all dispensable statements **/
    for (int fromIndex = nextStatementIndex; fromIndex < fromStatements.size(); fromIndex++) {
      Statement fromStatement = fromStatements.get(fromIndex);
      factory.removeStatement(fromStatement, getNext(fromStatements, fromIndex));
    }
  }

  /**
   * Create Patch:
   * Create the synchronization patch for the objects of the relation.
   * The unsynchronized objects are returned as a map from dynamicresource to node.
   */
  @CheckForNull
  private IMap<IDynamicResource, INode> createSynchronizeToDynamicResourcePath(IStatementSet fromStatements, INode predicate, @CheckForNull IDynamicResource toObject, IPatchCreateFactory factory) {
    return createSynchronizeToDynamicResourcePath(fromStatements, predicate, CollectionUtil.asList(toObject), factory);
  }
  
  /**
   * Create Patch:
   * Create the synchronization patch for the objects of the relation.
   * The unsynchronized objects are returned as a map from dynamicresource to node.
   */
  private <T extends IDynamicResource> IMap<IDynamicResource, INode> createSynchronizeToDynamicResourcePath(IStatementSet fromStatements, final INode predicate, List<T> toObjects, IPatchCreateFactory factory) {
    if (factory.isCanceled()) return CollectionUtil.emptyMap();
   
    /** Remove all statement if the target has no statements. **/
    if (toObjects.isEmpty()) {
      removeAllStatements(fromStatements, factory);
      return CollectionUtil.emptyMap();
    }
    
    /** Find equals topEqualsIndex **/
    int topFromAndToEqualsIndex = -1;
    
    for (int index = 0; index < toObjects.size(); index++) {
      if (factory.isCanceled()) return CollectionUtil.emptyMap();
      
      IDynamicResource toObject = toObjects.get(index);
      if (index < fromStatements.size()) {
        Statement fromStatement = fromStatements.get(index);
        if (isDeepEquals(fromStatement.object(), toObject)) {
          topFromAndToEqualsIndex = index;
          fResourceToNodeMap.put(toObject, fromStatement.object());
          continue;
        }
      }
      break;
    }
    
    /** Check is deep equals **/
    if (topFromAndToEqualsIndex == (fromStatements.size() - 1) && topFromAndToEqualsIndex == (toObjects.size() - 1)) {
      return CollectionUtil.emptyMap();
    }
    
    /** Find equals bottomEqualsIndex **/
    int bottomToEqualsIndex = toObjects.size();
    int bottomFromEqualsIndex = fromStatements.size();
    
    for (int index = 0; index < (toObjects.size() - (topFromAndToEqualsIndex + 1)); index++) {
      if (factory.isCanceled()) return CollectionUtil.emptyMap();
      
      int tempToIndex = toObjects.size() - 1 - index;
      int tempFromIndex =  fromStatements.size() - 1 - index;
      IDynamicResource toObject = toObjects.get(tempToIndex);
      if (0 <= tempFromIndex && topFromAndToEqualsIndex < tempFromIndex ) {
        Statement fromStatement = fromStatements.get(tempFromIndex);
        if (isDeepEquals(fromStatement.object(), toObject)) {
          bottomToEqualsIndex = tempToIndex;
          bottomFromEqualsIndex = tempFromIndex;
          continue;
        }
      }
      break;
    }
    final Statement bottomFromEqualsStatement = (bottomFromEqualsIndex < fromStatements.size()) ? fromStatements.get(bottomFromEqualsIndex): null;
    
    /** Remove obsolete statements **/
    List<Statement> fromTopToBottomStatements = new ArrayList<>();
    int lastMatchToIndex = topFromAndToEqualsIndex + 1;
    
    for (int index = topFromAndToEqualsIndex + 1; index < bottomFromEqualsIndex; index++) {
      if (factory.isCanceled()) return CollectionUtil.emptyMap();
      
      Statement fromStatement = fromStatements.get(index);
      if (lastMatchToIndex < bottomToEqualsIndex) {
        IDynamicResource toObject = toObjects.get(lastMatchToIndex);
        if (isTypeOfEquals(fromStatement.object(), toObject)) {
          fromTopToBottomStatements.add(fromStatement);
          lastMatchToIndex++;
          continue;
        }
      }
      /** Delete obsolete statement **/
      factory.disposeStatement(fromStatement, getNext(fromStatements, index));
    }
    
    /** Create missing statements. **/
    HashMap<IDynamicResource, INode> dynamicResourceToNodeMap = new HashMap<>(bottomToEqualsIndex - (topFromAndToEqualsIndex+1));
    final Relationship predicateRelationship = Select.relationship(fReadJobExecutor, predicate);
    
    for (int index = topFromAndToEqualsIndex + 1; index < bottomToEqualsIndex; index++) {
      if (factory.isCanceled()) return CollectionUtil.emptyMap();
      
      final IDynamicResource toObject = toObjects.get(index);
      
      if (index - (topFromAndToEqualsIndex + 1) < fromTopToBottomStatements.size()) {
        Statement fromStatement = fromTopToBottomStatements.get(index - (topFromAndToEqualsIndex + 1));        
        fResourceToNodeMap.put(toObject, fromStatement.object());
        dynamicResourceToNodeMap.put(toObject, fromStatement.object());
        continue;
      }
      
      /** Handle associations */
      if (predicateRelationship == Relationship.ASSOCIATION) {
        INode objectNode = (toObject.isVirtual()) ? fResourceToNodeMap.get(toObject) : toObject.getResource();
        if (objectNode != null) {
          fResourceToNodeMap.put(toObject, objectNode);
          factory.addStatement(new Statement(fPackage, fFromResource, predicate, objectNode, predicateRelationship), 
              (bottomFromEqualsStatement != null) ? new IStatementPosition.Before(bottomFromEqualsStatement) : IStatementPosition.AT_END);
          dynamicResourceToNodeMap.put(toObject, objectNode);
          continue;
        }
         
        /** Add lazy patch to create unresolved relation object */
        factory.addLazyPatchCreator(new ILazyPatchCreator() {

          @Override
          public void create(IPatchCreateFactory factory) {
            INode newResource = getOrCreateNewResource(toObject, factory);
            fResourceToNodeMap.put(toObject, newResource);
            factory.addStatement(new Statement(fPackage, fFromResource, predicate, newResource, predicateRelationship), 
                (bottomFromEqualsStatement != null) ? new IStatementPosition.Before(bottomFromEqualsStatement) : IStatementPosition.AT_END);
            
            HashMap<IDynamicResource, INode> dynamicResourceToNodeMap = new HashMap<>();
            dynamicResourceToNodeMap.put(toObject, newResource);
            createDeepSynchronizeDynamicResourcePatch(dynamicResourceToNodeMap, factory);
          }
        });
        continue;
      }
      
      /** Handle composition/aggregation */
      INode newResource = getOrCreateNewResource(toObject, factory);
      fResourceToNodeMap.put(toObject, newResource);
      factory.addStatement(new Statement(fPackage, fFromResource, predicate, newResource, predicateRelationship), 
          (bottomFromEqualsStatement != null) ? new IStatementPosition.Before(bottomFromEqualsStatement) : IStatementPosition.AT_END);
      
      dynamicResourceToNodeMap.put(toObject, newResource);
    }
    return dynamicResourceToNodeMap;
  }
  
  /********************
   * Visitor interface
   *******************/
  
  /**
   * Visit Attribute
   */
  @Override
  public <T> void visitAttribute(Class<T> type, INode attribute, T value) {
    if (fIgnoreProperties.contains(attribute)) return;
    Literal literalValue = asLiteralValue(type, attribute, value);
    createSynchronizeToExistingNodePath(statementsForAttribute(fReadJobExecutor, attribute, fFromResource), attribute, literalValue, fPatchCreateFactory);
  }

  @Override
  public <T> void visitAttribute(Class<T> type, INode attribute, List<T> values) {
    if (fIgnoreProperties.contains(attribute)) return;
    INodeList literalValues = asLiteralValues(type, attribute, values);
    createSynchronizeToExistingNodePath(statementsForAttribute(fReadJobExecutor, attribute, fFromResource), attribute, literalValues, fPatchCreateFactory);
  }

  /**
   * Visit Relations
   */
  @Override
  public <T extends IDynamicResource> void visitRelation(Class<T> type, INode relation, T value) {
    if (fIgnoreProperties.contains(relation)) return;
    IMap<IDynamicResource, INode> dynamicResourceToObjectMap = createSynchronizeToDynamicResourcePath(statementsForRelation(fReadJobExecutor, relation, fFromResource), relation, value, fPatchCreateFactory);
    createDeepSynchronizeDynamicResourcePatch(dynamicResourceToObjectMap, fPatchCreateFactory);
  }

  @Override
  public <T extends IDynamicResource> void visitRelation(Class<T> type, INode relation, List<T> values) {
    if (fIgnoreProperties.contains(relation)) return;
    IMap<IDynamicResource, INode> dynamicResourceToObjectMap = createSynchronizeToDynamicResourcePath(statementsForRelation(fReadJobExecutor, relation, fFromResource), relation, values, fPatchCreateFactory);
    createDeepSynchronizeDynamicResourcePatch(dynamicResourceToObjectMap, fPatchCreateFactory);
  }

  @Override
  public <T extends IResourceRef> void visitResourceRefRelation(Class<T> type, INode relation, INode value) {
    if (fIgnoreProperties.contains(relation) ) return;
    createSynchronizeToExistingNodePath(statementsForRelation(fReadJobExecutor, relation, fFromResource), relation, value, fPatchCreateFactory);
  }

  @Override
  public <T extends IResourceRef> void visitResourceRefRelation(Class<T> type, INode relation, INodeList values) {
    if (fIgnoreProperties.contains(relation)) return;
    createSynchronizeToExistingNodePath(statementsForRelation(fReadJobExecutor, relation, fFromResource), relation, values, fPatchCreateFactory);
  } 
  
  /**
   * Visit Decorations
   */
  @Override
  public <T extends IDynamicResource> void visitRelation(Class<T> type, INode relation, Map<Resource, T> values) {
    if (fIgnoreProperties.contains(relation)) return;
    List<T> toObjects = new ArrayList<>(values.values());
    IMap<IDynamicResource, INode> dynamicResourceToObjectMap = createSynchronizeToDynamicResourcePath(statementsForRelation(fReadJobExecutor, relation, fFromResource), relation, toObjects, fPatchCreateFactory);
    createDeepSynchronizeDynamicResourcePatch(dynamicResourceToObjectMap, fPatchCreateFactory);
  }

  @Override
  public <T extends IDynamicResource> void visitRelation(Class<T> type, INode relation, IMultiMapOrdered<Resource, T> values) {
    if (fIgnoreProperties.contains(relation)) return;
    List<T> toObjects = new ArrayList<>();
    for (Resource key: values.keyIterable()) {
      for (T value: values.get(key)) {
        toObjects.add(value);
      }
    }
    IMap<IDynamicResource, INode> dynamicResourceToObjectMap = createSynchronizeToDynamicResourcePath(statementsForRelation(fReadJobExecutor, relation, fFromResource), relation, toObjects, fPatchCreateFactory);
    createDeepSynchronizeDynamicResourcePatch(dynamicResourceToObjectMap, fPatchCreateFactory);
  }

}
