/*******************************************************************************
 * Copyright 2012 Omid Pourhadi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omidbiz.component;

import java.util.Map;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 * JSF component class
 * 
 */
public class UIClickableButton extends UIComponentBase
{

    public static final String COMPONENT_TYPE = "org.omidbiz.UIClickableButton";

    public static final String COMPONENT_FAMILY = "org.omidbiz.UIClickableButton";

    @Override
    public String getFamily()
    {
        return COMPONENT_FAMILY;
    }

    private String forClientId;

    public String getForClientId()
    {
        return forClientId;
    }

    public void setForClientId(String forClientId)
    {
        this.forClientId = forClientId;
    }

    @Override
    public Object saveState(FacesContext context)
    {
        Object values[] = new Object[2];
        values[0] = super.saveState(context);
        values[1] = forClientId;
        return values;
    }

    @Override
    public void restoreState(FacesContext context, Object state)
    {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        forClientId = (String) values[1];
    }
    
}
