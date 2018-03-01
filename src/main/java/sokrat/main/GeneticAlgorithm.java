package sokrat.main;

import java.util.*;

public class GeneticAlgorithm {

    public int bestGrade;

    public List<Solution> solutions;

    public Simulator simulator;

    public GeneticAlgorithm(Simulator simulator){
        this.bestGrade = 0;
        solutions = new ArrayList<>();
        this.simulator = simulator;
    }

    public Solution solve(){
        for(int index = 0 ; index < 5 ; ++index){
            solutions.addAll(generateNSolutions(1000));
            //gradeAll();
            solutions.sort(new Comparator<Solution>() {
                @Override
                public int compare(Solution o1, Solution o2) {
                    return o1.gain() - o2.gain();
                }
            });
            removeNWeakest(980);
            iterate();
        }
        if(solutions.size() > 0){
            return solutions.get(0);
        }
        return null;
    }

    public List<Solution> generateNSolutions(int poolsize){
        return null;
    }

    public Solution getMutation(Solution parent){
        return null;
    }

    public Solution getCrossover(Solution mother, Solution motherfucker){
        return null;
    }

    public void iterate(){
        for(int index = 0 ; index < 750 ; index ++){
            int iParent = (int)(solutions.size()*Math.random());
            Solution parent = solutions.get(iParent);
            solutions.add(getMutation(parent));
        }
        for(int index = 0 ; index < 230; index ++){
            int iParent1 = (int)(solutions.size()*Math.random());
            int iParent2 = (int)(solutions.size()*Math.random());
            if(iParent1 == iParent2){
                iParent2++;
                if(iParent2 >= solutions.size()){
                    iParent2 = 0;
                }
            }
            Solution parent1 = solutions.get(iParent1);
            Solution parent2 = solutions.get(iParent2);
            solutions.add(getCrossover(parent1, parent2));
        }
    }


    public void gradeAll(){

    }

    public void removeNWeakest(int tailsize){
        List<Solution> remaining = new ArrayList<>();
        for(int index = 0 ; index < solutions.size() - tailsize; ++index){
            remaining.add(solutions.get(index));
        }
        solutions = remaining;
    }


}
