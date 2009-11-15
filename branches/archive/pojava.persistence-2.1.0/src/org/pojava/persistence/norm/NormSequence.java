package org.pojava.persistence.norm;

public class NormSequence {
    String name=null;
    long minValue=0;
    long maxValue=Long.MAX_VALUE;
    long startWith=0;
    long increment=1;
    int cache=1;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getMinValue() {
        return minValue;
    }
    public void setMinValue(long minValue) {
        this.minValue = minValue;
    }
    public long getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }
    public long getStartWith() {
        return startWith;
    }
    public void setStartWith(long startWith) {
        this.startWith = startWith;
    }
    public long getIncrement() {
        return increment;
    }
    public void setIncrement(long increment) {
        this.increment = increment;
    }
    public int getCache() {
        return cache;
    }
    public void setCache(int cache) {
        if (cache<1) {
            this.cache=1;
        }
        this.cache = cache;
    }
    
}
