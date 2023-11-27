package ch.actifsource.example.javamodel.generic.javamodel;

import ch.actifsource.util.collection.IMultiMapOrdered;

public interface IRootElement extends ch.actifsource.core.javamodel.INamedResource {

  public static final ch.actifsource.core.INode TYPE_ID = new ch.actifsource.core.Resource("01144e62-8cf8-11ee-8026-f131a348395a");
  
  // attributes
  
  public java.lang.Integer selectId();
  
  public ch.actifsource.example.javamodel.generic.javamodel.IRootElement selectPeerRootElement();
  
  public java.util.List<? extends ch.actifsource.example.javamodel.generic.javamodel.ISubElement> selectSubElement();
  
  public IMultiMapOrdered<ch.actifsource.core.Resource, ? extends ch.actifsource.example.javamodel.generic.javamodel.IDecoElement> selectDecoElement();
  
}

/* Actifsource ID=[3ca9f967-db37-11de-82b8-17be2e034a3b,01144e62-8cf8-11ee-8026-f131a348395a,j8O7Ts5rg5+RpGzkWU8Q36lANNQ=] */
