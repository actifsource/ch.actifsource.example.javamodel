package ch.actifsource.example.javamodel.generic.javamodel;

import ch.actifsource.util.collection.IMultiMapOrdered;
import ch.actifsource.core.dynamic.*;

@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
public class DecoElement extends DynamicResource implements IDecoElement {

  public static final ch.actifsource.core.dynamic.IDynamicResource.IFactory<IDecoElement> FACTORY = new ch.actifsource.core.dynamic.IDynamicResource.IFactory<IDecoElement>() {
    
    @Override
    public IDecoElement create() {
      return new DecoElement();
    }
    
    @Override
    public IDecoElement create(IDynamicResourceRepository resourceRepository, ch.actifsource.core.Resource resource) {
      return new DecoElement(resourceRepository, resource);
    }
  
  };

  public DecoElement() {
    super(IDecoElement.TYPE_ID);
  }
  
  public DecoElement(IDynamicResourceRepository resourceRepository, ch.actifsource.core.Resource resource) {
    super(resourceRepository, resource, IDecoElement.TYPE_ID);
  }

  // relations
  
  @Override
  public ch.actifsource.core.javamodel.IResource selectTarget() {
    return _getSingle(ch.actifsource.core.javamodel.IResource.class, ch.actifsource.core.CorePackage.Decorator_target);
  }

  public DecoElement setTarget(ch.actifsource.core.javamodel.IResource target) {
    _setSingle(ch.actifsource.core.CorePackage.Decorator_target, target);
    return this;
  }
    
  @Override
  public ch.actifsource.core.javamodel.IClass selectTypeOf() {
    return _getSingle(ch.actifsource.core.javamodel.IClass.class, ch.actifsource.core.CorePackage.Resource_typeOf);
  }

  public DecoElement setTypeOf(ch.actifsource.core.javamodel.IClass typeOf) {
    _setSingle(ch.actifsource.core.CorePackage.Resource_typeOf, typeOf);
    return this;
  }
    
  // accept property value visitor
  @Override
  public void accept(IPropertyValueVisitor visitor) {
    // relations
    _acceptSingle(ch.actifsource.core.javamodel.IResource.class, ch.actifsource.core.CorePackage.Decorator_target, visitor);
    _acceptSingle(ch.actifsource.core.javamodel.IClass.class, ch.actifsource.core.CorePackage.Resource_typeOf, visitor);
  }

}
/* Actifsource ID=[4d723cb5-db37-11de-82b8-17be2e034a3b,cfe68b82-8d1b-11ee-b60e-35f2f332bbb6,zDhn7tlaQRsvlU9vYvMViJEM4kk=] */
