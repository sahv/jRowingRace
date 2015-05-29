package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Discipline
        implements Serializable {

    private static final long serialVersionUID = -5402683624069680556L;
    private Long id;
    private String name;
    private List<Interval> intervals;

    
    public Discipline(){}
    
    public Discipline(Discipline d){
    	if (d.getId()!=null) this.id = d.getId();
    	if (d.getName()!=null) this.name = d.getName();
    	if (d.getIntervals()!=null){
    		this.intervals = new ArrayList<Interval>();
    		for (Interval i:d.getIntervals()){
    			this.intervals.add(new Interval(i));
    		}
    		
    	}
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

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }
    public String toString(){
    	return this.name;
    }
}
