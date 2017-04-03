/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page.fields;

import de.bandika.base.RequestError;
import de.bandika.base.XmlHelper;
import de.bandika.data.RequestData;
import de.bandika.data.SessionData;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * Class TextAreaField is the data class for editable Fields used for a text area.  <br>
 * Usage:
 */
public class TextAreaField extends BaseField {

  protected String text = "";

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
	public Element generateXml(Document doc, Element parent){
		Element elem= super.generateXml(doc,parent);
		XmlHelper.createCDATA(doc,elem,text);
		return elem;
	}

	@Override
	public void evaluateXml(Element node){
		super.evaluateXml(node);
		text=XmlHelper.getCData(node);
	}

}