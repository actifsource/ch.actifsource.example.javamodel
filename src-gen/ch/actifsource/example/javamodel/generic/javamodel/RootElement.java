package ch.actifsource.example.javamodel.generic.javamodel;

import ch.actifsource.util.collection.IMultiMapOrdered;
import ch.actifsource.core.dynamic.*;

@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
public class RootElement extends DynamicResource implements IRootElement {

  public static final ch.actifsource.core.dynamic.IDynamicResource.IFactory<IRootElement> FACTORY = new ch.actifsource.core.dynamic.IDynamicResource.IFactory<IRootElement>() {
    
    @Override
    public IRootElement create() {
      return new RootElement();
    }
    
    @Override
    public IRootElement create(IDynamicResourceRepository resourceRepository, ch.actifsource.core.Resource resource) {
      return new RootElement(resourceRepository, resource);
    }
  
  };

  public RootElement() {
    super(IRootElement.TYPE_ID);
  }
  
  public RootElement(IDynamicResourceRepository resourceRepository, ch.actifsource.core.Resource resource) {
    super(resourceRepository, resource, IRootElement.TYPE_ID);
  }

  // attributes
  
  @Override
  public java.lang.Integer selectId() {
    return _getSingleAttribute(java.lang.Integer.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_id);
  }
    
  public void setId(java.lang.Integer id) {
     _setSingleAttribute(ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_id, id);
  }

  @Override
  public java.lang.String selectName() {
    return _getSingleAttribute(java.lang.String.class, ch.actifsource.core.CorePackage.NamedResource_name);
  }
    
  public void setName(java.lang.String name) {
     _setSingleAttribute(ch.actifsource.core.CorePackage.NamedResource_name, name);
  }

  // relations
  
  @Override
  public IMultiMapOrdered<ch.actifsource.core.Resource, ? extends ch.actifsource.example.javamodel.generic.javamodel.IDecoElement> selectDecoElement() {
    return _getMultiMap(ch.actifsource.example.javamodel.generic.javamodel.IDecoElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_decoElement);
  }

  public RootElement setDecoElement(IMultiMapOrdered<ch.actifsource.core.Resource, ? extends ch.actifsource.example.javamodel.generic.javamodel.IDecoElement> decoElement) {
    _setMultiMap(ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_decoElement, decoElement);
    return this;
  }
    
  @Override
  public ch.actifsource.example.javamodel.generic.javamodel.IRootElement selectPeerRootElement() {
    return _getSingle(ch.actifsource.example.javamodel.generic.javamodel.IRootElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_peerRootElement);
  }

  public RootElement setPeerRootElement(ch.actifsource.example.javamodel.generic.javamodel.IRootElement peerRootElement) {
    _setSingle(ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_peerRootElement, peerRootElement);
    return this;
  }
    
  @Override
  public java.util.List<? extends ch.actifsource.example.javamodel.generic.javamodel.ISubElement> selectSubElement() {
    return _getList(ch.actifsource.example.javamodel.generic.javamodel.ISubElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_subElement);
  }

  public RootElement setSubElement(java.util.List<? extends ch.actifsource.example.javamodel.generic.javamodel.ISubElement> subElement) {
    _setList(ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_subElement, subElement);
    return this;
  }
    
  @Override
  public ch.actifsource.core.javamodel.IClass selectTypeOf() {
    return _getSingle(ch.actifsource.core.javamodel.IClass.class, ch.actifsource.core.CorePackage.Resource_typeOf);
  }

  public RootElement setTypeOf(ch.actifsource.core.javamodel.IClass typeOf) {
    _setSingle(ch.actifsource.core.CorePackage.Resource_typeOf, typeOf);
    return this;
  }
    
  // accept property value visitor
  @Override
  public void accept(IPropertyValueVisitor visitor) {
    // attributes
    _acceptSingleAttribute(java.lang.Integer.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_id, visitor);
    _acceptSingleAttribute(java.lang.String.class, ch.actifsource.core.CorePackage.NamedResource_name, visitor);
    // relations
    _acceptMultiMap(ch.actifsource.example.javamodel.generic.javamodel.IDecoElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_decoElement, visitor);
    _acceptSingle(ch.actifsource.example.javamodel.generic.javamodel.IRootElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_peerRootElement, visitor);
    _acceptList(ch.actifsource.example.javamodel.generic.javamodel.ISubElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_subElement, visitor);
    _acceptSingle(ch.actifsource.core.javamodel.IClass.class, ch.actifsource.core.CorePackage.Resource_typeOf, visitor);
  }

  public static java.util.List<ch.actifsource.example.javamodel.generic.javamodel.IRootElement> selectToMePeerRootElement(ch.actifsource.example.javamodel.generic.javamodel.IRootElement object) {
    return _getToMeList(object.getRepository(), ch.actifsource.example.javamodel.generic.javamodel.IRootElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_peerRootElement, object.getResource());
  }
  
  public static ch.actifsource.example.javamodel.generic.javamodel.IRootElement selectToMeSubElement(ch.actifsource.example.javamodel.generic.javamodel.ISubElement object) {
    return _getToMeSingle(object.getRepository(), ch.actifsource.example.javamodel.generic.javamodel.IRootElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_subElement, object.getResource());
  }
  
  public static ch.actifsource.example.javamodel.generic.javamodel.IRootElement selectToMeDecoElement(ch.actifsource.example.javamodel.generic.javamodel.IDecoElement object) {
    return _getToMeSingle(object.getRepository(), ch.actifsource.example.javamodel.generic.javamodel.IRootElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.RootElement_decoElement, object.getResource());
  }
  
}
/* Actifsource ID=[4d723cb5-db37-11de-82b8-17be2e034a3b,01144e62-8cf8-11ee-8026-f131a348395a,tIRNXXDRljadzKcCW9Cy9vBrY+M=] */
