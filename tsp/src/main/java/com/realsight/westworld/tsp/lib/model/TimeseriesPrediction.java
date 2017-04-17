package com.realsight.westworld.tsp.lib.model;

import java.io.Serializable;

import com.realsight.westworld.tsp.lib.model.htm.PredictHierarchy;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;

import Jama.Matrix;

public abstract class TimeseriesPrediction implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3804428275026575934L;
	
	protected PredictHierarchy HTM = null;
	
	public TimeseriesPrediction() {}
	
	public abstract Entry<Double> todayValue(Matrix value, Long timestamp);
	public abstract Matrix prediction();
}
