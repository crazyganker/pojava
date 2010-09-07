package org.pojava.persistence.norm;

public class NormField {

    String name;
    String comment;
    String normType;
    String defaultValue;
    int displaySize;
    int precision;
    int scale;
    Boolean nullable;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getNormType() {
        return normType;
    }
    public void setNormType(String normType) {
        this.normType = normType;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public int getDisplaySize() {
        return displaySize;
    }
    public void setDisplaySize(int displaySize) {
        this.displaySize = displaySize;
    }
    public int getPrecision() {
        return precision;
    }
    public void setPrecision(int precision) {
        this.precision = precision;
    }
    public int getScale() {
        return scale;
    }
    public void setScale(int scale) {
        this.scale = scale;
    }
    public Boolean getNullable() {
        return nullable;
    }
    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }
    
    
}
