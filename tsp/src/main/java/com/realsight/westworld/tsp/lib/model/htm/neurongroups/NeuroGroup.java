package com.realsight.westworld.tsp.lib.model.htm.neurongroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.realsight.westworld.tsp.lib.util.Pair;

public class NeuroGroup implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7024856854549187135L;
	private int maxActiveNeuronsNum;
	private int maxRemeberNeuronsNum;
	private List<Integer> leftFactsGroup;
	private List<Integer> activeNeuros;
	private NeuroGroupOperator neuroGroupOperator;
	private List<Pair<List<Integer>, List<Integer>>> potentialNewContextList;
	private double activePrecision = 0.0;
	
	public NeuroGroup(int maxActiveNeuronsNum, int maxLeftSemiContextsLenght, int maxRemeberNeuronsNum) {
		this.maxActiveNeuronsNum = maxActiveNeuronsNum;
		this.leftFactsGroup = new ArrayList<Integer>();
		this.neuroGroupOperator = new NeuroGroupOperator(maxLeftSemiContextsLenght);
		this.potentialNewContextList = new ArrayList<Pair<List<Integer>, List<Integer>>>();
		this.maxRemeberNeuronsNum = maxRemeberNeuronsNum;
	}

	public List<Integer> getLeftFactsGroup() {
		return leftFactsGroup;
	}

	public NeuroGroupOperator getNeuroGroupOperator() {
		return neuroGroupOperator;
	}

	public List<Pair<List<Integer>, List<Integer>>> getPotentialNewContextList() {
		return potentialNewContextList;
	}

	public void setLeftFactsGroup(List<Integer> leftFactsGroup) {
		this.leftFactsGroup = leftFactsGroup;
	}

	public void setActiveNeuros(List<Integer> activeNeuros) {
		this.activeNeuros = activeNeuros;
	}

	public void setNeuroGroupOperator(NeuroGroupOperator neuroGroupOperator) {
		this.neuroGroupOperator = neuroGroupOperator;
	}

	public void setPotentialNewContextList(List<Pair<List<Integer>, List<Integer>>> potentialNewContextList) {
		this.potentialNewContextList = potentialNewContextList;
	}
	
	public void sleep() {
		this.leftFactsGroup.clear();
		this.potentialNewContextList.clear();
		this.activeNeuros = new ArrayList<Integer>();
	}
	
	private double activate(List<Integer> currSensFacts) {
		
//		for(int i = 0; i < this.leftFactsGroup.size(); i++)
//			System.out.print(this.leftFactsGroup.get(i) + " ");
//		System.out.print("-> ");
//		for(int i = 0; i < currSensFacts.size(); i++)
//			System.out.print(currSensFacts.get(i) + " ");
//		System.out.print("\n");
		currSensFacts = new ArrayList<Integer>(new HashSet<Integer>(currSensFacts));
		Collections.sort(currSensFacts);
		List<Pair<List<Integer>, List<Integer>>> potNewZeroLevelContext = 
				new ArrayList<Pair<List<Integer>, List<Integer>>>();
		int newContextFlag = -1;
		if ( (this.leftFactsGroup.size()>0) && (currSensFacts.size()>0) ) {
			potNewZeroLevelContext.add(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts));
            newContextFlag = this.neuroGroupOperator.getContextByFacts(potNewZeroLevelContext, 1);
		}
		this.neuroGroupOperator.contextCrosser(1, currSensFacts,(newContextFlag>=0), true,  new ArrayList<Pair<List<Integer>, List<Integer>>>());
		double percentSelectedContextActive = 0.0;
		
		if (this.neuroGroupOperator.getNumSelectedContext() > 0) {
			percentSelectedContextActive = this.neuroGroupOperator.getActiveContexts().size();
			percentSelectedContextActive /= this.neuroGroupOperator.getNumSelectedContext();
		}
		
		Set<Pair<List<Integer>, List<Integer>>> activeContext = new HashSet<Pair<List<Integer>, List<Integer>>>();
		activeContext.addAll(this.neuroGroupOperator.getPotentialNewContextList());
		
		if ( (this.leftFactsGroup.size()>0) && (currSensFacts.size()>0) ) {
//			System.out.println(activeContext.contains(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts)));
			activeContext.add(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts));
//			System.out.println(activeContext.contains(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts)));
		}
		
		Collections.sort(this.neuroGroupOperator.getActiveContexts());
		this.leftFactsGroup = new ArrayList<Integer>();
		for ( int i = 0; i<this.neuroGroupOperator.getActiveContexts().size() && i<this.maxActiveNeuronsNum; i++){
			if (this.neuroGroupOperator.getActiveContexts().get(i).getNumActivate() <= activePrecision) continue;
			this.leftFactsGroup.add(this.neuroGroupOperator.getActiveContexts().get(i).getContextID()+(1<<30));
		}
		for ( int i = 0; i < currSensFacts.size(); i++ ) {
			this.leftFactsGroup.add(currSensFacts.get(i));
		}
		Collections.sort(this.leftFactsGroup);
		this.activeNeuros = new ArrayList<Integer>();
		for ( int i = 0; i < this.neuroGroupOperator.getActiveContexts().size(); i++ ) {
			this.activeNeuros.add(this.neuroGroupOperator.getActiveContexts().get(i).getContextID());
		}
		
		this.potentialNewContextList = this.neuroGroupOperator.getPotentialNewContextList();
		
//		for(int s = 0; s < this.potentialNewContextList.size(); s++) {
//			Pair<List<Integer>, List<Integer>> t = this.potentialNewContextList.get(s);
//			System.out.print("(");
//			for(int i = 0; i < t.getA().size(); i++)
//				System.out.print(t.getA().get(i) + ",");
//			System.out.print(");(");
//			for(int i = 0; i < t.getB().size(); i++)
//				System.out.print(t.getB().get(i) + ",");
//			System.out.print(")\n");
//		}
		this.neuroGroupOperator.contextCrosser(0, this.leftFactsGroup, false, 
				this.neuroGroupOperator.getActiveContexts().size()<this.maxRemeberNeuronsNum, this.potentialNewContextList);
		double percentaddContextActive = 0.0;
		
//		this.neuroGroupOperator.contextCrosser(0, this.leftFactsGroup, false, 
//				true, this.potentialNewContextList);
//		double percentaddContextActive = 0.0;
		
		if ( activeContext.size() > 0 ) {
			double newContextNum = this.neuroGroupOperator.getNumAddedContexts();
			if ( newContextFlag >= 0 )
				newContextNum += 1.0;
			percentaddContextActive = newContextNum / activeContext.size();
//			System.out.println(percentSelectedContextActive + "," + newContextNum + "," + activeContext.size()+","+newContextFlag);
//			System.out.println((1.0 - percentSelectedContextActive + percentaddContextActive) / 2.0);
		}
		
		return (1.0 - percentSelectedContextActive)*0.50 + (percentaddContextActive)*0.50; 
	}
	
	public double learn(List<Integer> currSensFacts) {
		return activate(currSensFacts);
	}
	
	public double predict_learn(List<Integer> currSensFacts) {
		currSensFacts = new ArrayList<Integer>(new HashSet<Integer>(currSensFacts));
		Collections.sort(currSensFacts);
		this.neuroGroupOperator.contextCrosser(1, currSensFacts, false, false,  new ArrayList<Pair<List<Integer>, List<Integer>>>());
		double percentSelectedContextActive = 0.0;
		
		if (this.neuroGroupOperator.getNumSelectedContext() > 0) {
			percentSelectedContextActive = this.neuroGroupOperator.getActiveContexts().size();
			percentSelectedContextActive /= this.neuroGroupOperator.getNumSelectedContext();
		}
		
		Set<Pair<List<Integer>, List<Integer>>> activeContext = new HashSet<Pair<List<Integer>, List<Integer>>>();
		activeContext.addAll(this.neuroGroupOperator.getPotentialNewContextList());
		
		if ( (this.leftFactsGroup.size()>0) && (currSensFacts.size()>0) ) {
			activeContext.add(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts));
		}
		
		Collections.sort(this.neuroGroupOperator.getActiveContexts());
		this.leftFactsGroup = new ArrayList<Integer>();
		for ( int i = 0; i<this.neuroGroupOperator.getActiveContexts().size() && i<this.maxActiveNeuronsNum; i++){
			if (this.neuroGroupOperator.getActiveContexts().get(i).getNumActivate() <= activePrecision) continue;
			this.leftFactsGroup.add(this.neuroGroupOperator.getActiveContexts().get(i).getContextID()+(1<<30));
		}
		for ( int i = 0; i < currSensFacts.size(); i++ ) {
			this.leftFactsGroup.add(currSensFacts.get(i));
		}
		Collections.sort(this.leftFactsGroup);
		this.activeNeuros = new ArrayList<Integer>();
		for ( int i = 0; i < this.neuroGroupOperator.getActiveContexts().size(); i++ ) {
			this.activeNeuros.add(this.neuroGroupOperator.getActiveContexts().get(i).getContextID());
		}
		
		this.potentialNewContextList = this.neuroGroupOperator.getPotentialNewContextList();
		
		this.neuroGroupOperator.contextCrosser(0, this.leftFactsGroup, false, false, this.potentialNewContextList);
		double percentaddContextActive = 0.0;
		if ( activeContext.size() > 0 ) {
			double newContextNum = this.neuroGroupOperator.getNumAddedContexts();
			percentaddContextActive = newContextNum / activeContext.size();
		}
		
		return (1.0 - percentSelectedContextActive)*0.50 + (percentaddContextActive)*0.50; 
	}
	
	public double predict(List<Integer> currSensFacts) {
		
		currSensFacts = new ArrayList<Integer>(new HashSet<Integer>(currSensFacts));
		Collections.sort(currSensFacts);
		this.neuroGroupOperator.contextCrosser(1, currSensFacts, false, false, new ArrayList<Pair<List<Integer>, List<Integer>>>());
		double percentSelectedContextActive = 0.0;
		if (this.neuroGroupOperator.getNumSelectedContext() > 0) {
			percentSelectedContextActive = this.neuroGroupOperator.getSumActiveContextsValue();
		}
		return percentSelectedContextActive;
	}
	
	public List<Integer> getActiveNeuros() {
		return activeNeuros;
	}
}

