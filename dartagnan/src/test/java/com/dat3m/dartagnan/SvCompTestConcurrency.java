package com.dat3m.dartagnan;

import com.dat3m.dartagnan.parsers.cat.ParserCat;
import com.dat3m.dartagnan.utils.ResourceHelper;
import com.dat3m.dartagnan.wmm.Wmm;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.dat3m.dartagnan.utils.ResourceHelper.TEST_RESOURCE_PATH;

@RunWith(Parameterized.class)
public class SvCompTestConcurrency extends AbstractSvCompTest {

	@Parameterized.Parameters(name = "{index}: {0} bound={2}")
    public static Iterable<Object[]> data() throws IOException {
        Wmm wmm = new ParserCat().parse(new File(ResourceHelper.CAT_RESOURCE_PATH + "cat/svcomp.cat"));

        List<Object[]> data = new ArrayList<>();

        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/fib_bench-1.bpl", wmm, 6});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/fib_bench-2.bpl", wmm, 6});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/fib_bench_longer-1.bpl", wmm, 7});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/fib_bench_longer-2.bpl", wmm, 7});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/lazy01.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/stack-1.bpl", wmm, 3});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/stack-2.bpl", wmm,3});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/stateful01-1.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/stateful01-2.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/triangular-1.bpl", wmm, 6});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/triangular-2.bpl", wmm, 6});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/triangular-longer-1.bpl", wmm, 11});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/triangular-longer-2.bpl", wmm, 11});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/gcd-2.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/qrcu-2.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/read_write_lock-1.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/read_write_lock-2.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/scull.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/time_var_mutex.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/18_read_write_lock.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/19_time_var_mutex.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe000_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe000_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe001_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe002_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe002_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe003_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe003_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe004_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe005_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe005_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe006_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe006_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe006_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe006_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe006_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe007_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe007_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe008_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe009_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe009_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe009_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe009_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe010_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe010_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe011_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe012_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe013_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe013_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe014_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe014_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe014_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe015_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe015_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe016_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe016_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe016_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe017_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe018_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe018_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe018_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe019_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe019_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe019_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe020_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe020_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe020_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe020_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe021_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe022_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe022_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe023_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe024_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe025_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe025_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe025_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe026_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe026_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe027_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe027_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe027_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe028_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe028_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe029_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe029_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe030_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe031_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe031_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe033_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe033_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe034_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe034_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe035_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe035_rmo.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe035_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe035_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe036_power.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe036_power.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe036_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe036_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe036_rmo.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe036_rmo.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe036_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe036_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe037_power.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe037_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe037_rmo.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe037_rmo.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe037_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe037_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin000_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin000_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin000_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin000_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin001_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin001_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin001_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin002_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin002_pso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin002_tso.opt.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/qw2004-2.bpl", wmm, 2});    
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/race-1_1-join.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/race-1_2-join.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/race-1_3-join.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/race-3_2-container_of-global.bpl", wmm, 1});
        
        return data;
    }

	public SvCompTestConcurrency(String path, Wmm wmm, int bound) {
		super(path, wmm, bound);
	}
}