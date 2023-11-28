package ch.actifsource.example.javamodel.aspect;

import ch.actifsource.core.CorePackage;
import ch.actifsource.core.PackagedResource;
import ch.actifsource.core.Resource;
import ch.actifsource.core.update.ApplyPatchJob;
import ch.actifsource.core.update.IModifiable;
import ch.actifsource.example.javamodel.aspect.visitor.ResourceSynchronizePatchCreateVisitor;
import ch.actifsource.example.javamodel.generic.javamodel.DecoElement;
import ch.actifsource.example.javamodel.generic.javamodel.IDecoElement;
import ch.actifsource.example.javamodel.generic.javamodel.RootElement;
import ch.actifsource.example.javamodel.generic.javamodel.SubElement;
import ch.actifsource.core.patch.IPatch;
import ch.actifsource.core.selector.typesystem.impl.TypeSystem;
import ch.actifsource.core.set.INodeSet;
import ch.actifsource.core.set.NodeSet;
import ch.actifsource.util.collection.CollectionUtil;
import ch.actifsource.util.collection.MultiMapOrdered;

public class ModifyByJavaModelActionAspect implements ch.actifsource.environment.modelmenu.aspect.IMenuItemActionAspect {

	@Override
	public void run(IModifiable modifiable, PackagedResource selectedRootElement) {
		if (!selectedRootElement.getPackage().isModifiable()) return;
		
		RootElement rootElement = new RootElement(TypeSystem.getCompatibleDynamicResourceRepository(modifiable), (Resource)selectedRootElement.getResource());
		rootElement.setName("RootElement");
		rootElement.setId(55);
		RootElement peerRootElement = new RootElement(TypeSystem.getCompatibleDynamicResourceRepository(modifiable), new Resource("bd30d030-8d35-11ee-a7ca-3ba7c26ad18f"));
		peerRootElement.setName("peerRootElement");
		rootElement.setPeerRootElement(peerRootElement);
		
		SubElement subElement1 = new SubElement();
		subElement1.setName("SubElement1");
		SubElement subElement2 = new SubElement();
		subElement2.setName("SubElement2");
		subElement2.setSubElementRef(CollectionUtil.asList(subElement2, subElement1));
		rootElement.setSubElement(CollectionUtil.asList(subElement1, subElement2));
		
		DecoElement decoElement = new DecoElement();
		decoElement.setTarget(subElement1);
		MultiMapOrdered<Resource, IDecoElement> decoMap = new MultiMapOrdered<>();
		decoMap.put(new Resource(), decoElement);
		rootElement.setDecoElement(decoMap);
		
		// Apply model to selected model.
		INodeSet ignoreProperties = new NodeSet(CorePackage.Resource_typeOf);
		boolean syncExistingResources =  true;
    IPatch diffPatch = ResourceSynchronizePatchCreateVisitor.createSynchronizePatch(modifiable, selectedRootElement, rootElement, ignoreProperties, syncExistingResources);
    if (!diffPatch.isEmpty()) {
      modifiable.execute(new ApplyPatchJob(diffPatch));
    }
	}	
}

