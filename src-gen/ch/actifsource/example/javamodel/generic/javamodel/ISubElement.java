package ch.actifsource.example.javamodel.generic.javamodel;

import ch.actifsource.util.collection.IMultiMapOrdered;

public interface ISubElement extends ch.actifsource.core.javamodel.INamedResource {

  public static final ch.actifsource.core.INode TYPE_ID = new ch.actifsource.core.Resource("2e78ca50-8cf8-11ee-8026-f131a348395a");
  
  // relations
  
  public java.util.List<? extends ch.actifsource.example.javamodel.generic.javamodel.ISubElement> selectSubElement();
  
  public java.util.List<? extends ch.actifsource.example.javamodel.generic.javamodel.ISubElement> selectSubElementRef();
  
}

/* Actifsource ID=[3ca9f967-db37-11de-82b8-17be2e034a3b,2e78ca50-8cf8-11ee-8026-f131a348395a,snwCvQL4/xqRYNn+odr9TCEYoCs=] */
