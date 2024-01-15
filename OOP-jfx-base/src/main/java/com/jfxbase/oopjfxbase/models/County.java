// com.jfxbase.oopjfxbase.models.County

package com.jfxbase.oopjfxbase.models;

public class County {
    private String name;
    private int totalPopulation;
    private int[] ethnicityPopulation;

    // Constructors, getters, and setters...

    // constructor
    public County(String name, int totalPopulation, int[] ethnicityPopulation) {
        this.name = name;
        this.totalPopulation = totalPopulation;
        this.ethnicityPopulation = ethnicityPopulation;
    }

    //  getters
    public String getName() { return name; }

    public int getTotalPopulation() {
        return totalPopulation;
    }

    public int[] getEthnicityPopulation() {
        return ethnicityPopulation;
    }
}
