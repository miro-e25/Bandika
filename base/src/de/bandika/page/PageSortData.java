/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika._base.BaseIdData;

import java.util.ArrayList;

/**
 * Class SortData is the data class for sorting child pages. <br>
 * Usage:
 */
public class PageSortData extends BaseIdData implements Comparable<PageSortData> {

  public final static String DATAKEY = "data|pagesort";

  protected int ranking = 0;
  protected String name = "";
  protected ArrayList<PageSortData> children = new ArrayList<PageSortData>();

  public PageSortData() {
  }

  public int compareTo(PageSortData node) {
    return ranking - node.ranking;
  }

  public int getRanking() {
    return ranking;
  }

  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<PageSortData> getChildren() {
    return children;
  }

}