/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

abstract public class MultiChoice extends Choice implements Serializable
{
    private static final long serialVersionUID = 3779890420561677855L;

    public MultiChoice(String id, Layout layout)
    {
        super(id, layout);
    }

    public void addAttribute(String name, Object value, boolean update)
    {
        if ( name.equals("values") )
        {
            if ( value instanceof List )
            {
                super.addAttribute(name, value, update);
            }
            else if ( value instanceof String )
            {
                List<String> values = getValues();
                values.clear();
                if( ((String) value).length() > 0)
                {
                    String[] valueArray = ((String) value).split(",");
                    for(int i=0; i<valueArray.length; i++)
                    {
                        values.add(valueArray[i]);
                    }
                }
                setValues(values);
            }
        }
        else
        {
            super.addAttribute(name, value, update);
        }
    }

    public void generateAttribute(String name, Object value, StringBuilder sb)
    {
        if ( !name.equals("values") )
        {
            super.generateAttribute(name, value, sb);
        }
    }
    
    public void addValue(String value)
    {
        if(this._isReadOnly())
        {
            return;
        }
        
        List<String> values = this.getValues();
        if(this.optionValues.contains(value) && !values.contains(value))
        {
            values.add(value);
            this.setValues(values);
        }
    }
    
    public void clearValues()
    {
        addAttribute("values", new ArrayList<String>());
    }
    
    public void removeValue(String value)
    {
        if(this._isReadOnly())
        {
            return;
        }
        
        List<String> values = this.getValues();
        if(values.contains(value))
        {
            values.remove(value);
            this.setValues(values);
        }
    }

    public void setValues(List<String> values)
    {
        if(this._isReadOnly())
        {
            return;
        }
        
        if(values == null || this.optionValues == null)
        {
            return;
        }
        //if values.length == 0, that means clear all values.
        for(int i = 0; i < values.size(); i++)//filter illegal item.
        {
        	boolean found = false;
        	for(int j = 0; j < optionValues.size(); j++)//filter illegal item.
            {//optionValues could be integer!
        		if( String.valueOf(this.optionValues.get(j)).equals(values.get(i)) )
        		{
        			found = true;
        			break;
        		}
            }
        	if (!found) {
	        	values.remove(i);
	        	i=0;//to prevent out of array index exception.
        	}
        }

        super.addAttribute("values", values);
    }

    public void checkConstraint() {
    	Object value = getAttribute("values");
    	if (value == null) {
			throw new IllegalStateException("UI Constraint fails in: " 
					+ this.getConstraint("selectedValuesConstraintText")
					+ ",UIID: " + this.getId());
    	}
    	List<String> values = (List<String>)value;
    	if(this.hasConstraint("selectedValuesConstraint")) {
    		String[] selectedValues = (String[])
    				this.getConstraint("selectedValuesConstraintReal");
    		for (String v: selectedValues) {
    			if (!values.contains(v)) {
    				throw new IllegalStateException("UI Constraint fails in: " 
    						+ this.getConstraint("selectedValuesConstraintText")
    						+ ",UIID: " + this.getId());
    			}
    		}
    	}
	}
    
    public List<String> getSelectedDisplayValues() {
    	List<String> svalues = new ArrayList<String>();
    	List<String> values = getValues();
    	for (String v : values) {
    		svalues.add(getOptionDisplayValues().get(Integer.valueOf(v)-1));
    	}
    	return svalues;
    }
    
    public List<String> getValues()
    {
    	checkConstraint();
    	
        List<String> values = (List<String>)getAttribute("values");
        return values == null? new ArrayList(): values;
    }

    public String getValue()
    {
    	checkConstraint();
    	
        List<String> values = (List<String>)getAttribute("values");
        return values == null || values.size() == 0? "": values.get(0);
    }
    
    protected boolean equal(String str)
    {
        List<String> values = getValues();
        if ( values == null )
        {
            return false;
        }
        for ( int i = 0; i < values.size(); i++ )
        {
            if ( values.get(i).toString().equalsIgnoreCase(str) )
            {
                return true;
            }
        }
        return false;
    }
}
