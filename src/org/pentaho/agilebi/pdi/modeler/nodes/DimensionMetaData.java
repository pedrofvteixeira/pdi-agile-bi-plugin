/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.agilebi.pdi.modeler.nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.pentaho.agilebi.pdi.modeler.ModelerWorkspace;
import org.pentaho.agilebi.pdi.modeler.propforms.DimensionPropertiesForm;
import org.pentaho.di.i18n.BaseMessages;

/**
 * Event aware node class that also listens to it's children's events and propagates them up.
 */
public class DimensionMetaData extends AbstractMetaDataModelNode<HierarchyMetaData> implements Serializable {
  
  private static final long serialVersionUID = -891901735974255178L;
  
  String name;
  
  public DimensionMetaData(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public String getDisplayName(){
    return getName();
  }
  
  public void setName(String name) {
    if (!StringUtils.equals(name, this.name)) {
      String oldName = this.name;
      this.name = name;
      this.firePropertyChange("name", oldName, name); //$NON-NLS-1$
      this.firePropertyChange("displayName", oldName, name); //$NON-NLS-1$
      validateNode();
    }
  }
  
  public String toString() {
    return "Dimension Name: " + name;
  }
  
  public String getValidImage() {
    return "images/sm_dim_icon.png"; //$NON-NLS-1$
  }
  
  public void validate() {
    validationMessages.clear();
    valid = true;
    if (StringUtils.isEmpty(name)) {
      validationMessages.add("Name is empty");
      valid = false;
    }
    if (size() == 0) {
      validationMessages.add("Dimension must have at least one hierarchy.");
      valid = false;
    }
    List<String> usedNames = new ArrayList<String>();
    for(HierarchyMetaData hier: children){
      valid &= hier.isValid();
      validationMessages.addAll(hier.getValidationMessages());
      if(usedNames.contains(hier.getName())){
        valid = false;
        validationMessages.add(BaseMessages.getString(ModelerWorkspace.class, "duplicate_hier_names"));
      }
      usedNames.add(hier.getName());
    }
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof DimensionMetaData) {
      DimensionMetaData dim = (DimensionMetaData)obj;
      return name != null && name.equals(dim.name);
    } else {
      return false;
    }
  }

  public boolean isTime() {
    // TODO: make time dimension real
    return false;
  }
  
  public boolean isUiExpanded() {
    return true;
  }

  public boolean isEditingDisabled(){
    return false;
  }

  public Class getPropertiesForm() {
    return DimensionPropertiesForm.class;
  }
  
  public void onAdd(HierarchyMetaData child) {
    child.addPropertyChangeListener("name", nameListener);
    child.addPropertyChangeListener("valid", validListener);
    child.addPropertyChangeListener("children", childrenListener);
    validateNode();
  }

  public void onRemove(HierarchyMetaData child) {
    child.removePropertyChangeListener(validListener);
    child.removePropertyChangeListener(nameListener);
    child.removePropertyChangeListener(childrenListener);
    validateNode();
  }
  
  
}