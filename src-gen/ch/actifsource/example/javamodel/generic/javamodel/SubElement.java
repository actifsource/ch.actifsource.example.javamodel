package ch.actifsource.example.javamodel.generic.javamodel;

import ch.actifsource.util.collection.IMultiMapOrdered;
import ch.actifsource.core.dynamic.*;

@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
public class SubElement extends DynamicResource implements ISubElement {

  public static final ch.actifsource.core.dynamic.IDynamicResource.IFactory<ISubElement> FACTORY = new ch.actifsource.core.dynamic.IDynamicResource.IFactory<ISubElement>() {
    
    @Override
    public ISubElement create() {
      return new SubElement();
    }
    
    @Override
    public ISubElement create(IDynamicResourceRepository resourceRepository, ch.actifsource.core.Resource resource) {
      return new SubElement(resourceRepository, resource);
    }
  
  };

  public SubElement() {
    super(ISubElement.TYPE_ID);
  }
  
  public SubElement(IDynamicResourceRepository resourceRepository, ch.actifsource.core.Resource resource) {
    super(resourceRepository, resource, ISubElement.TYPE_ID);
  }

  // attributes
  
  @Override
  public java.lang.String selectName() {
    return _getSingleAttribute(java.lang.String.class, ch.actifsource.core.CorePackage.NamedResource_name);
  }
    
  public void setName(java.lang.String name) {
     _setSingleAttribute(ch.actifsource.core.CorePackage.NamedResource_name, name);
  }

  // relations
  
  @Override
  public java.util.List<? extends ch.actifsource.example.javamodel.generic.javamodel.ISubElement> selectSubElement() {
    return _getList(ch.actifsource.example.javamodel.generic.javamodel.ISubElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.SubElement_subElement);
  }

  public SubElement setSubElement(java.util.List<? extends ch.actifsource.example.javamodel.generic.javamodel.ISubElement> subElement) {
    _setList(ch.actifsource.example.javamodel.generic.GenericPackage.SubElement_subElement, subElement);
    return this;
  }
    
  @Override
  public java.util.List<? extends ch.actifsource.example.javamodel.generic.javamodel.ISubElement> selectSubElementRef() {
    return _getList(ch.actifsource.example.javamodel.generic.javamodel.ISubElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.SubElement_subElementRef);
  }

  public SubElement setSubElementRef(java.util.List<? extends ch.actifsource.example.javamodel.generic.javamodel.ISubElement> subElementRef) {
    _setList(ch.actifsource.example.javamodel.generic.GenericPackage.SubElement_subElementRef, subElementRef);
    return this;
  }
    
  @Override
  public ch.actifsource.core.javamodel.IClass selectTypeOf() {
    return _getSingle(ch.actifsource.core.javamodel.IClass.class, ch.actifsource.core.CorePackage.Resource_typeOf);
  }

  public SubElement setTypeOf(ch.actifsource.core.javamodel.IClass typeOf) {
    _setSingle(ch.actifsource.core.CorePackage.Resource_typeOf, typeOf);
    return this;
  }
    
  // accept property value visitor
  @Override
  public void accept(IPropertyValueVisitor visitor) {
    // attributes
    _acceptSingleAttribute(java.lang.String.class, ch.actifsource.core.CorePackage.NamedResource_name, visitor);
    // relations
    _acceptList(ch.actifsource.example.javamodel.generic.javamodel.ISubElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.SubElement_subElement, visitor);
    _acceptList(ch.actifsource.example.javamodel.generic.javamodel.ISubElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.SubElement_subElementRef, visitor);
    _acceptSingle(ch.actifsource.core.javamodel.IClass.class, ch.actifsource.core.CorePackage.Resource_typeOf, visitor);
  }

  // toMeRelations
  
  public static ch.actifsource.example.javamodel.generic.javamodel.ISubElement selectToMeSubElement(ch.actifsource.example.javamodel.generic.javamodel.ISubElement object) {
    return _getToMeSingle(object.getRepository(), ch.actifsource.example.javamodel.generic.javamodel.ISubElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.SubElement_subElement, object.getResource());
  }
  
  public static java.util.List<ch.actifsource.example.javamodel.generic.javamodel.ISubElement> selectToMeSubElementRef(ch.actifsource.example.javamodel.generic.javamodel.ISubElement object) {
    return _getToMeList(object.getRepository(), ch.actifsource.example.javamodel.generic.javamodel.ISubElement.class, ch.actifsource.example.javamodel.generic.GenericPackage.SubElement_subElementRef, object.getResource());
  }
  
}
/* Actifsource ID=[4d723cb5-db37-11de-82b8-17be2e034a3b,2e78ca50-8cf8-11ee-8026-f131a348395a,P8F9m7NdW5TGaEvxKJuz9I5IcRA=] */
