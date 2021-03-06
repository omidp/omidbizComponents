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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.ajax4jsf.renderkit.HeaderResourcesRendererBase;
import org.omidbiz.component.UIInputListOfValues;
import org.omidbiz.component.UIInputTooltip;

/**
 * 
 * @author Omid Pourhadi
 * @version $Revision: 1.0
 * 
 */
public class InputTooltipRenderBase extends HeaderResourcesRendererBase
{

	public void initializeMask(FacesContext context, UIInputTooltip component) throws IOException
	{

		

		UIComponent parentComponent = component.getParent();
		ResponseWriter writer = context.getResponseWriter();

		if (parentComponent != null)
		{
		    String tipsyClientId = parentComponent.getClientId(context);
		    if(parentComponent instanceof UIInputListOfValues)
		    {
		        UIInputListOfValues lovParent = (UIInputListOfValues)parentComponent;
		        tipsyClientId = lovParent.getObjectName() + "Name";
		    }
			StringBuilder sb = new StringBuilder("jQuery(document).ready(function(){");
			String openMode = (String)component.getAttributes().get("openMode");
            String direction = (String)component.getAttributes().get("direction");
            Boolean escape = (Boolean)component.getAttributes().get("escape");
            String ultraRight ="right";
            if ((Boolean)component.getAttributes().get("ultraRight"))
                ultraRight = "left";
                
			//jQuery("#"+id)
			String jQueryId = String.format("jQuery(\"#%s\")", tipsyClientId.replace(":", "\\\\:"));
			sb.append("var titleValue="+jQueryId+".attr('title');");
			if(openMode != null && openMode.equals("mouseenter"))
			{
			    if(escape !=null && escape)
			        sb.append("("+jQueryId+".attr('title')!=''&&"+jQueryId+".attr('original-title')!=''&&"+jQueryId+".attr('oldtitle')!='')&&("+jQueryId+".qtip({content:{text: escapeHtml(titleValue)}, style: { classes: 'qtip-youtube qtip-shadow custome-qtip-style' },show:{event:'"+openMode+"',delay:1000},position:{viewport:jQuery(window),my:'top "+ultraRight+"',at: 'bottom "+direction+"',target:"+jQueryId+" }}))");
			    else
			        sb.append("("+jQueryId+".attr('title')!=''&&"+jQueryId+".attr('original-title')!=''&&"+jQueryId+".attr('oldtitle')!='')&&("+jQueryId+".qtip({content:{text: titleValue}, style: { classes: 'qtip-youtube qtip-shadow custome-qtip-style' },show:{event:'"+openMode+"',delay:1000},position:{viewport:jQuery(window),my:'top "+ultraRight+"',at: 'bottom "+direction+"',target:"+jQueryId+" }}))");
			}
			if(openMode != null && openMode.equals("click"))
			{
			    if(escape !=null && escape)
			        sb.append("("+jQueryId+".attr('title')!=''&&"+jQueryId+".attr('original-title')!=''&&"+jQueryId+".attr('oldtitle')!='')&&("+jQueryId+".qtip({content:{text: escapeHtml(titleValue)},style: { classes: 'qtip-youtube qtip-shadow   custome-qtip-style' },show:{event:'"+openMode+"'},position:{viewport:jQuery(window),my:'top "+ultraRight+"',at: 'bottom "+direction+"',target:"+jQueryId+" }}))");
			    else
			        sb.append("("+jQueryId+".attr('title')!=''&&"+jQueryId+".attr('original-title')!=''&&"+jQueryId+".attr('oldtitle')!='')&&("+jQueryId+".qtip({content:{text: titleValue},style: { classes: 'qtip-youtube qtip-shadow  custome-qtip-style' },show:{event:'"+openMode+"'},position:{viewport:jQuery(window),my:'top "+ultraRight+"',at: 'bottom "+direction+"',target:"+jQueryId+" }}))");
			}
			
			sb.append("});");
			getUtils().writeScript(context, component, sb.toString());
		}
	}

	@Override
	protected Class getComponentClass()
	{
		return UIInputTooltip.class;
	}

}
