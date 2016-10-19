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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.ajax4jsf.renderkit.HeaderResourcesRendererBase;
import org.omidbiz.component.UIInputMultiSelectList;

/**
 * 
 * @author Shahrad Kashani
 * @version $Revision: 1.0
 * 
 */
public class InputMultiSelectListRenderBase extends HeaderResourcesRendererBase
{
    private Object id = new Object();
    public void initializeMask(FacesContext context, UIInputMultiSelectList component) throws IOException
    {
        Object componentId = new Object();
        if(component.getAttributes().get("forceId")!=null)
            componentId  = (Object) component.getAttributes().get("forceId");
        else
            componentId = component.getClientId(context);
        
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", null);
            getUtils().writeAttribute(writer, "id", componentId+"wrapper");
            getUtils().writeAttribute(writer, "style",component.getAttributes().get("style").toString());
            getUtils().writeAttribute(writer, "class", "input-mutli-select-list row");
            writer.startElement("div", null);
               getUtils().writeAttribute(writer, "class", "col s9");
               writer.startElement("input", null);
                   getUtils().writeAttribute(writer, "id", componentId);
                   getUtils().writeAttribute(writer, "name", componentId);
                   getUtils().writeAttribute(writer, "class", "entry-select-text");
                   getUtils().writeAttribute(writer, "type", "text");
                   getUtils().writeAttribute(writer, "value", component.getAttributes().get("value"));
               writer.endElement("input");
            writer.endElement("div");
            writer.startElement("div", null);
            getUtils().writeAttribute(writer, "class", "col s3");
            writer.startElement("a", null);
                getUtils().writeAttribute(writer, "id", componentId+"Button");
                getUtils().writeAttribute(writer, "class", "toggle-button");
            writer.endElement("a");
         writer.endElement("div");            
        writer.endElement("div");


        
    }
    @Override
    protected Class<? extends UIComponent> getComponentClass()
    {
        return UIInputMultiSelectList.class;
    }
    @Override
    protected void doEncodeEnd(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
    {
        
    }

    @Override
    protected void doDecode(FacesContext context, UIComponent component)
    {
        ExternalContext external = context.getExternalContext();
        Map requestParams = external.getRequestParameterMap();
        UIInputMultiSelectList inputMultiSelectList = (UIInputMultiSelectList) component;
        String forceId = (String) inputMultiSelectList.getAttributes().get("forceId");
        String clientId = inputMultiSelectList.getClientId(context);
        if (forceId != null && forceId.length() > 0)
            clientId = forceId;
        String submittedValue = (String) requestParams.get(clientId);
        if (submittedValue != null)
        {
            inputMultiSelectList.setSubmittedValue(submittedValue);
        }
    }   
    public String getJQueryId(FacesContext context, UIInputMultiSelectList component)
    {

        return getId(context, component).replace(":", "\\\\:");
    }
    @SuppressWarnings("unchecked")
    public String getTableListStr(FacesContext context, UIInputMultiSelectList component){
        if(component.getAttributes().get("inputSelectMap")==null)
            try
            {
                throw new Exception("Fanpardaz component (inputMutiSelectList)-> inputSelectMap attribute must not be null");
            }
            catch (Exception e)
            {
                System.out.println("Fanpardaz component (inputMutiSelectList)-> inputSelectMap attribute must not be null");
                e.printStackTrace();
            }
        StringBuffer htmlTableStr = new StringBuffer();
        Map<String, String> inputMap = (Map<String, String>) component.getAttributes().get("inputSelectMap");
        if(inputMap != null)
        {
            htmlTableStr.append("<div id='"+getId(context, component)+"Scrolling' class='table-wrapper-scrolling'  style='height:"+(37*Integer.parseInt(component.getAttributes().get("scrollLimit").toString()))+"px'" 
                    + "><table cellpadding='0' cellspacing='0' width='100%' id='"+getId(context,component)+"Table' class='inputMSLTable'>");
            Set<String> inputMapKeySet = inputMap.keySet();
            for (String mapKey : inputMapKeySet)
            {
                htmlTableStr.append("<tr status='notSelected'><td>");
                htmlTableStr.append(mapKey);
                htmlTableStr.append("</td><td>");
                htmlTableStr.append(inputMap.get(mapKey));
                htmlTableStr.append("</td></tr>");
            }
            htmlTableStr.append("</table></div>");
        }
        return htmlTableStr.toString();
    }

    public String getId(FacesContext context, UIInputMultiSelectList component)
    {
        if(component.getAttributes().get("forceId")!=null)
            id  = (Object) component.getAttributes().get("forceId");
        else
            id = component.getClientId(context);
        return id.toString();
    }

}
