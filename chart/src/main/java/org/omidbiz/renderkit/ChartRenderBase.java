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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.ajax4jsf.context.AjaxContext;
import org.ajax4jsf.renderkit.HeaderResourcesRendererBase;
import org.omidbiz.component.UIChart;

/**
 * 
 * @author Shahrad Kashani
 * @version $Revision: 1.0
 * 
 */
public class ChartRenderBase extends HeaderResourcesRendererBase
{

    public void initializeMask(FacesContext context, UIChart component) throws IOException
    { 
        String type = String.valueOf(component.getAttributes().get("type"));
        String renderOption = String.valueOf(component.getAttributes().get("renderOption")).isEmpty()?"":","+String.valueOf(component.getAttributes().get("renderOption"));
        Double tickAngle = Double.parseDouble((String) component.getAttributes().get("tickAngle"));
        String xAxisLabel = String.valueOf(component.getAttributes().get("xAxisLable"));
        String yAxisLabel = String.valueOf(component.getAttributes().get("yAxisLable"));

        String title = String.valueOf(component.getAttributes().get("title"));
        String dataSets = String.valueOf(component.getAttributes().get("value"));
        ArrayList<String> ticks = (ArrayList<String>) component.getAttributes().get("ticks");
        ArrayList<String> series =  (ArrayList<String>) component.getAttributes().get("series");
        StringBuilder sb = new StringBuilder("jQuery(document).ready(function(){");
        sb.append("var id ='"+getJQueryId(context,component)+"'; ");
        sb.append("var dataSets ="+dataSets+"; ");
        sb.append("var ticks =[");
        int ticksCounter = 0;
        for (String tick : ticks)
        {
            ticksCounter ++;
            if(ticksCounter == ticks.size())
                sb.append("'"+tick+"'");
            else
                sb.append("'"+tick+"',");
        }
        sb.append("];");
        sb.append("var series =[");
        int seriesCounter = 0;
        for (String serie : series)
        {
            seriesCounter ++;
            if(seriesCounter == series.size())
                sb.append("{label:'"+serie+"'}");
            else
                sb.append("{label:'"+serie+"'},");
        }
        sb.append("];"); 
        sb.append("var opt ={title: '"+title+"',"
                + "series:series,"
                +"rendererOptions: { padding: 8, showDataLabels: true"+renderOption+" },"
                + "seriesDefaults:{");
                if(type.equals("bar"))
                    sb.append("renderer:jQuery.jqplot.BarRenderer,");
                else if(type.equals("pie"))
                    sb.append("renderer:jQuery.jqplot.PieRenderer,");
                if(type.equals("bubble"))
                    sb.append("renderer: jQuery.jqplot.BubbleRenderer,"
                        +"shadow: true,shadowAlpha: 0.05},"
                        + "highlighter: {show: true,showLabel: true,tooltipAxes: 'y'},"
                        + "cursor:{show: true,zoom: true},"
                        + "axes: {xaxis:{renderer: jQuery.jqplot.CategoryAxisRenderer,"
                        + "label: '"+xAxisLabel+"',"
                        + "labelRenderer: jQuery.jqplot.CanvasAxisLabelRenderer},"
                        + "yaxis: {"
                        + "autoscale:true,"
                        + "label: '"+yAxisLabel+"',"
                        + "labelRenderer: jQuery.jqplot.CanvasAxisLabelRenderer}");
                else
                {
                    sb.append("shadow: true,shadowAlpha: 0.05},"
                            + "highlighter: {show: true,showLabel: true,tooltipAxes: 'y'},"
                            + "legend: {show: true,location: 'e',placement: 'outside'},cursor:{show: true,zoom: true},"
                            + "axes: {xaxis:{renderer: jQuery.jqplot.CategoryAxisRenderer,"
                            + "label: '"+xAxisLabel+"',"
                            + "labelRenderer: jQuery.jqplot.CanvasAxisLabelRenderer,"
                            + "tickRenderer: jQuery.jqplot.CanvasAxisTickRenderer,"
                            + "ticks: ticks,"
                            + "tickOptions: {angle:"+tickAngle+"}},"
                            + "yaxis: {"
                            + "autoscale:true,"
                            + "label: '"+yAxisLabel+"',"
                            + "labelRenderer: jQuery.jqplot.CanvasAxisLabelRenderer,"
                            + "tickRenderer: jQuery.jqplot.CanvasAxisTickRenderer,"
                            + "tickOptions: {}}");
                }
                    sb.append("}}; plot1b = jQuery.jqplot(id,dataSets,opt);});");
        getUtils().writeScript(context, component, sb.toString());

    }

    @Override
    protected void doEncodeEnd(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
    {
        
    }

    @Override
    protected void doDecode(FacesContext context, UIComponent component)
    {
        
    }
    
    public String getFinalId(FacesContext context, UIChart component)
    {
        String clientId = component.getAttributes().get("forceId")!=null?String.valueOf(component.getAttributes().get("forceId")) : component.getClientId(context);
        return clientId;
    }
    public Object getJQueryId(FacesContext context, UIChart component)
    {
        return (Object) getFinalId(context,component).replace(":", "\\\\:");
    }

    private static String capitalize (String str){
        
        String firstChar = str.substring(0, 1);
        return firstChar.toUpperCase()+str.substring(1, str.length());
        
    }
    @Override
    protected Class getComponentClass()
    {
        return UIChart.class;
    }

}
