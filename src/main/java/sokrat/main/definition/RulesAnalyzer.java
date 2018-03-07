package sokrat.main.definition;

import com.google.common.math.Stats;
import sokrat.main.model.Ride;

import java.util.HashMap;
import java.util.Map;

public class RulesAnalyzer {
    Stats lengthStats;
    Stats earlyStartStats;
    Stats lateStartStats;
    int[] buckets;

    String bucketNames[];
    Stats bucketStats[];



    RulesAnalyzer(Rules rules, int[] buckets){
        this.buckets = buckets;
        bucketNames=new String[buckets.length+1];
        bucketStats=new Stats[buckets.length+1];

        lengthStats=Stats.of(rules.getRides().stream().map(ride->ride.getLength()).iterator());
        earlyStartStats=Stats.of(rules.getRides().stream().map(ride->ride.getEarliestStart()).iterator());
        lateStartStats=Stats.of(rules.getRides().stream().map(ride->ride.getLatestStart()).iterator());
        int prevSize = 0;
        int i=0;
        for( int size : buckets){
            final int lowerBound = prevSize;
            final int upperBound = size;
            bucketNames[i] = "Bucket " + size + " > length > " + prevSize;
            bucketStats[i] = Stats.of(rules.getRides().stream().map(ride->ride.getLength()).filter((l)->l>lowerBound&&l<upperBound).iterator());
            prevSize = size;
            i++;
        }
        final int lowerBound = prevSize;
        bucketNames[i] = "Bucket  length > " + prevSize;
        bucketStats[i] = Stats.of(rules.getRides().stream().map(ride->ride.getLength()).filter((l)->l>lowerBound).iterator());



    }

    public String toString(){

        StringBuilder sb = new StringBuilder();

        dumpStats("length",lengthStats, sb);
        dumpStats("earliest start",earlyStartStats, sb);
        dumpStats("latest start",lateStartStats, sb);
        for(int i = 0; i < buckets.length+1; i++){
            dumpStats(bucketNames[i],bucketStats[i], sb);
        }
        return sb.toString();
    }

    private void dumpStats(String name, Stats stats, StringBuilder sb) {
        sb.append("\n---- " + name +"\n");
        sb.append("count: " + stats.count() +"\n");
        sb.append("min: " + stats.min() +"\n");
        sb.append("max: " + stats.max() +"\n");
        sb.append("avg: " + stats.mean() +"\n");
        sb.append("stddev: " + stats.sampleStandardDeviation() +"\n");
        sb.append("var: " + stats.sampleVariance() +"\n");
        sb.append("sum: " + stats.sum() +"\n");

    }
}
