/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.taglib;

import de.bandika._base.FormatHelper;
import de.bandika._base.Logger;
import de.bandika._base.RequestData;
import de.bandika.cms.BaseField;
import de.bandika.cms.TextLineField;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class TextLineTag extends CmsBaseTag {

  TextLineField field;

  private static final String editTag = "<input type=\"text\" class=\"editField\" name=\"%s\" value=\"%s\" style=\"width:100%%\"/>";

  @Override
  protected void doEditTag(RequestData rdata) throws JspException {
    field = (TextLineField) pdata.ensureField(name, BaseField.FIELDTYPE_TEXTLINE);
    try {
      JspWriter writer = getWriter();
      writer.print(String.format(editTag,
        field.getIdentifier(),
        FormatHelper.toHtml(field.getText())));
    } catch (Exception e) {
      Logger.error(null, "textline tag error", e);
    }
  }

  @Override
  protected void doRuntimeTag(RequestData rdata) throws JspException {
    field = (TextLineField) pdata.ensureField(name, BaseField.FIELDTYPE_TEXTLINE);
    try {
      JspWriter writer = getWriter();
      if (field.getText().length() == 0)
        writer.print("&nbsp;");
      else
        writer.print(FormatHelper.toHtml(field.getText()));
    } catch (Exception ignored) {
    }
  }

}