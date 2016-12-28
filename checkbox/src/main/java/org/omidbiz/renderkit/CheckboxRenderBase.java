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
package org.omidbiz.renderkit;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.ajax4jsf.renderkit.HeaderResourcesRendererBase;
import org.ajax4jsf.util.InputUtils;
import org.omidbiz.component.UICheckbox;

/**
 * 
 * @author Omid Pourhadi
 * @version $Revision: 1.0
 * 
 */
public class CheckboxRenderBase extends HeaderResourcesRendererBase
{

    @Override
    public void decode(FacesContext context, UIComponent component)
    {
        super.decode(context, component);
        ExternalContext external = context.getExternalContext();
        Map requestParams = external.getRequestParameterMap();
        UICheckbox checkBox = (UICheckbox) component;
        String clientId = checkBox.getClientId(context);
        String submittedValue = (String) requestParams.get(clientId + "-chk");        
        checkBox.setSubmittedValue(submittedValue);
    }
    
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException
    {
        return InputUtils.getConvertedStringValue(context, component, submittedValue);
    }

    @Override
    public boolean getRendersChildren()
    {
        return true;
    }

    @Override
    protected Class getComponentClass()
    {
        return UICheckbox.class;
    }

}
