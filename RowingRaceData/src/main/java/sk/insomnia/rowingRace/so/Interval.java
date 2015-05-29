package sk.insomnia.rowingRace.so;

import java.io.Serializable;

public class Interval implements Serializable {

    private static final long serialVersionUID = 1718118115414993067L;

    private Long id;
    private EnumEntity dimension;
    private Integer workoutValue;
    private Integer restValue;
    private String intervalNumber;
    private Integer relaySplitValue;

    public Interval(){
    	
    }

    public Interval(Interval i){
    	if (i.getId()!=null) this.id = i.getId();
    	if (i.getDimension()!=null) this.dimension = i.getDimension();
    	if (i.getWorkoutValue()!=null) this.workoutValue = i.getWorkoutValue();
    	if (i.getRestValue()!=null) this.restValue = i.getRestValue();
    	if (i.getIntervalNumber()!=null) this.intervalNumber = i.getIntervalNumber();
    	if (i.getRelaySplitValue()!=null) this.relaySplitValue = i.getRelaySplitValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntervalNumber() {
        return intervalNumber;
    }

    public void setIntervalNumber(String intervalNumber) {
        this.intervalNumber = intervalNumber;
    }

    public Integer getWorkoutValue() {
        return workoutValue;
    }

    public void setWorkoutValue(Integer workoutValue) {
        this.workoutValue = workoutValue;
    }

    public Integer getRestValue() {
        return restValue;
    }

    public void setRestValue(Integer restValue) {
        this.restValue = restValue;
    }

    public EnumEntity getDimension() {
        return dimension;
    }

    public void setDimension(EnumEntity dimension) {
        this.dimension = dimension;
    }

	public Integer getRelaySplitValue() {
		return relaySplitValue;
	}

	public void setRelaySplitValue(Integer relaySplitValue) {
		this.relaySplitValue = relaySplitValue;
	}
    
    
}
