package com.okr.utils;


//{"first":0,"rows":2,"sortField":"isUsed","sortOrder":1,"filters":{},"globalFilter":null}
public class SearchParams {
    private Integer first;
    private Integer rows;
    private String  sortField;
    private Integer sortOrder;
    private String  globalFilter;
    /**
     * @return the first
     */
    public Integer getFirst() {
        return first;
    }
    
    /**
     * @param first the first to set
     */
    public void setFirst(Integer first) {
        this.first = first;
    }
    
    /**
     * @return the rows
     */
    public Integer getRows() {
        return rows;
    }
    
    /**
     * @param rows the rows to set
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }
    
    /**
     * @return the sortField
     */
    public String getSortField() {
        return sortField;
    }
    
    /**
     * @param sortField the sortField to set
     */
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
    
    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    /**
     * @param sortOrder the sortOrder to set
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    /**
     * @return the globalFilter
     */
    public String getGlobalFilter() {
        return globalFilter;
    }
    
    /**
     * @param globalFilter the globalFilter to set
     */
    public void setGlobalFilter(String globalFilter) {
        this.globalFilter = globalFilter;
    }
    /**
     * Gets the order by clause.
     *
     * @return the order by clause
     */
    public String getOrderByClause() {

        StringBuilder sbsb = null;

        if (!Mixin.isNullOrEmpty(this.sortField)) {
            String sortString = (1 == this.sortOrder) ? SORT_ASC : SORT_DESC;
            sbsb = new StringBuilder();
            sbsb.append(ORDER_BY).append(this.sortField).append(SPACE).append(sortString);
        }

        return (null == sbsb) ? BLANK : sbsb.toString();
    }
    /** The Constant ORDER_BY. */
    private static final String ORDER_BY = " ORDER BY ";
    /** The Constant SPACE. */
    private static final String SPACE = " ";
    /** The Constant SORT. */
    private static final String SORT_ASC = "ASC";
    private static final String SORT_DESC = "DESC";
    /** The Constant BLANK. */
    private static final String BLANK = "";
}