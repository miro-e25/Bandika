/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.lucene;

import java.util.ArrayList;

public class SearchResultData {

  protected String pattern = "";
  protected int maxSearchResults = 100;
  protected String[] fieldNames = {"name", "content"};
  protected ArrayList<SearchData> results = new ArrayList<SearchData>();

  public SearchResultData() {
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public int getMaxSearchResults() {
    return maxSearchResults;
  }

  public void setMaxSearchResults(int maxSearchResults) {
    this.maxSearchResults = maxSearchResults;
  }

  public String[] getFieldNames() {
    return fieldNames;
  }

  public void setFieldNames(String[] fieldNames) {
    this.fieldNames = fieldNames;
  }

  public ArrayList<SearchData> getResults() {
    return results;
  }
}