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
package org.omidbiz.handler;

import javax.faces.event.ActionEvent;

import org.ajax4jsf.webapp.taglib.AjaxComponentHandler;

import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

/**
 * 
 * @author Omid Pourhadi
 * @version $Revision: 1.0
 * 
 */
public class WindowPopupTagHandler extends ComponentHandler
{

	public WindowPopupTagHandler(ComponentConfig config)
	{
		super(config);
	}

	@Override
	protected MetaRuleset createMetaRuleset(Class type)
	{

		return super.createMetaRuleset(type).addRule(
				new MethodRule("atMin", Void.class, new Class<?>[] { ActionEvent.class }));
	}

}
