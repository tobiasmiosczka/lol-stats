package com.github.tobiasmiosczka.lolstats;

public class MeanBucket {

    private int count;
    private double mean;

    public MeanBucket() {

    }

    public void add(double val) {
        double sum = ((this.mean * this.count) + val);
        this.count++;
        this.mean = sum / this.count;
    }

    public double getMean() {
        return mean;
    }

    public int getCount() {
        return count;
    }
}
