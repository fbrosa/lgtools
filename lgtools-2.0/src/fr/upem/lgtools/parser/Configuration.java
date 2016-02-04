package fr.upem.lgtools.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.UnitFactory;

public class Configuration<T extends Analysis> {
	private Unit[] allUnits;
	private int unitCount;
	private final Buffer[] buffers;
    private final Deque<Unit>[] stacks;
    private final T analyses;
    private final List<String> history = new ArrayList<String>();
    
    
    //units is a list of non-root units
    @SuppressWarnings("unchecked")
	public Configuration(List<Unit> units,T analyses, int nBuffers, int nStacks){
    	if(units == null){
    		throw new IllegalArgumentException("List of units cannot be null");
    	}
    	if(units.isEmpty()){
    		throw new IllegalArgumentException("List of units cannot be empty");
    	}
    	if(nBuffers < 1){
    		throw new IllegalArgumentException("There should be at least one buffer (instead of "+nBuffers + ")");
    	}
    	if(nStacks < 1){
    		throw new IllegalArgumentException("There should be at least one stack (instead of "+nStacks + ")");
    	}
    	
    	Unit root = UnitFactory.createRootUnit();
    	buffers = (SimpleBuffer[])new SimpleBuffer[nBuffers];
    	for(int i = 0 ; i < nBuffers ; i++){
    		buffers[i] = new SimpleBuffer(units);    		
    	}
    	stacks =  (Deque<Unit>[])new Deque<?>[nStacks];
    	for(int i = 0 ; i < nStacks ; i++){
    		  stacks[i] = new ArrayDeque<Unit>();
    		  stacks[i].push(root);
    		  
    	}
    	this.allUnits = new Unit[units.size() + 1];
    	for(Unit u:units){
    		this.allUnits[u.getId()] = u;
    	}
    	this.unitCount = allUnits.length;
    	this.analyses = analyses;
    }
    
   @SuppressWarnings("unchecked")
   public Configuration(Configuration<T> configuration){

	   buffers = (SimpleBuffer[])new SimpleBuffer[configuration.buffers.length];
	   for(int i = 0 ; i < configuration.buffers.length ; i++){
		   buffers[i] = new SimpleBuffer((SimpleBuffer)configuration.buffers[i]);    		
	   }
	   stacks =  (Deque<Unit>[])new Deque<?>[configuration.stacks.length];
	   for(int i = 0 ; i < configuration.stacks.length ; i++){
		   stacks[i] = new ArrayDeque<Unit>(configuration.stacks[i]);   		  

	   }
	   this.allUnits = new Unit[configuration.allUnits.length];
	   for(int i = 0 ; i < configuration.allUnits.length ; i++){
		   this.allUnits[i] = configuration.allUnits[i];
	   }
	   this.unitCount = allUnits.length;

	   this.analyses = (T)configuration.analyses.copy();

   }

    
   public void addUnit(Unit u){
	   if(unitCount >= allUnits.length){
		   allUnits = Arrays.copyOf(allUnits, unitCount*2);
	   }
	   allUnits[unitCount] = u;
	   unitCount++;
   }
   
   
    public Unit getUnit(int id){
    	return this.allUnits[id];
    }
    
    public Deque<Unit> getStack(int index){
    	return stacks[index];
    }
    
    
    public Deque<Unit> getFirstStack(){
    	return stacks[0];
    }

    public Deque<Unit> getSecondStack(){
    	if(stacks.length < 2){
    		throw new IllegalArgumentException("To get the second stack, there should be at leat two stacks");
    	}
    	return stacks[1];
    }
    

	public Buffer getFirstBuffer() {
		return buffers[0];
	}


	public T getAnalyses() {
		return analyses;
	}


	public List<String> getHistory() {
		return history;
	}
    
	public int stackCount(){
		return stacks.length;
	}
    
	public int bufferCount(){
		return buffers.length;
	}

	public boolean isTerminal(){
		//buffers must be empty
		for(Buffer buffer:buffers){
			if(buffer.size() != 0){
				return false;
			}
		}
		
		for(Deque<Unit> stack:stacks){
			if(stack.size() > 1){
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return getFirstStack().toString()+""+getFirstBuffer().toString()+"\n";//+analyses.toString();
	}
	
	public List<Unit> getUnits(){
		return Arrays.asList(allUnits);
	}
	
}
