package com.okr.utils;

import com.google.gson.Gson;

import java.util.List;

public class DataTableResults<T> {
    /**
     * The draw.
     */
    private String draw;
    private String first;

    /**
     * The records filtered.
     */
    private String recordsFiltered;

    /**
     * The records total.
     */
    private String recordsTotal;

    /**
     * The list of data objects.
     */
    List<T> data;

    public String getJson() {
        return new Gson().toJson(this);
    }

    /**
     * Gets the draw.
     *
     * @return the draw
     */
    public String getDraw() {
        return draw;
    }

    /**
     * Sets the draw.
     *
     * @param draw the draw to set
     */
    public void setDraw(String draw) {
        this.draw = draw;
    }

    /**
     * Gets the records filtered.
     *
     * @return the recordsFiltered
     */
    public String getRecordsFiltered() {
        return recordsFiltered;
    }

    /**
     * Sets the records filtered.
     *
     * @param recordsFiltered the recordsFiltered to set
     */
    public void setRecordsFiltered(String recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    /**
     * Gets the records total.
     *
     * @return the recordsTotal
     */
    public String getRecordsTotal() {
        return recordsTotal;
    }

    /**
     * Sets the records total.
     *
     * @param recordsTotal the recordsTotal to set
     */
    public void setRecordsTotal(String recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    /**
     * Get list data
     *
     * @return
     */
    public List<T> getData() {
        return data;
    }

    /**
     * Set list data
     *
     * @param data
     */
    public void setData(List<T> data) {
        this.data = data;
    }


    /**
     * @return the first
     */
    public String getFirst() {
        return first;
    }


    /**
     * @param first the first to set
     */
    public void setFirst(String first) {
        this.first = first;
    }

}