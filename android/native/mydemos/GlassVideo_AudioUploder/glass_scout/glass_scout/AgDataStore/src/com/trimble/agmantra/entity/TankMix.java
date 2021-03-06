package com.trimble.agmantra.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table TANK_MIX.
 */
public class TankMix {

    private Long id;
    private String name;
    private Integer format;
    private String totalQty;
    private Integer status;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public TankMix() {
    }

    public TankMix(Long id) {
        this.id = id;
    }

    public TankMix(Long id, String name, Integer format, String totalQty, Integer status) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.totalQty = totalQty;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFormat() {
        return format;
    }

    public void setFormat(Integer format) {
        this.format = format;
    }

    public String getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(String totalQty) {
        this.totalQty = totalQty;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
