package sokrat.main.algorithms.naive;

import com.google.common.collect.ComparisonChain;
import sokrat.main.model.Ride;

import java.util.Comparator;

public interface RidesOrderingStrategy extends Comparator<Ride> {



    public static final RidesOrderingStrategy EARLIEST_START_FIRST= (Ride r1, Ride r2) -> {
        return ComparisonChain.start()
                .compare(r1.getEarliestStart(),r2.getEarliestStart())
                .compare(r2.getLength(),r1.getLength())
                .result();
    };

    public static final RidesOrderingStrategy LATEST_START_LAST = (Ride r1, Ride r2) -> {
        return ComparisonChain.start()
                .compare(r2.getLatestStart(),r1.getLatestStart())
                .compare(r1.getLength(),r2.getLength())
                .result();
    };
    public static final RidesOrderingStrategy LATEST_START_FIRST= (Ride r1, Ride r2) -> {
        return ComparisonChain.start()
                .compare(r1.getLatestStart(),r2.getLatestStart())
                .compare(r2.getLength(),r1.getLength())
                .result();
    };
    public static final RidesOrderingStrategy DEFAULT=(r1, r2) -> Integer.compare(r1.getIndex(),r2.getIndex());


}
