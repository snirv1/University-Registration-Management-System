package bgu.spl.a2;

import junit.framework.TestCase;
import org.junit.Assert;


public class VersionMonitorTest extends TestCase {
    public void testGetVersion(){
        try{
            VersionMonitor monitor = new VersionMonitor();
            if(monitor.getVersion()  != 0) {
                Assert.fail();
            }
        }catch (Exception ex){
            Assert.fail();
        }

    }


    public void testAwait(){
        try {
            VersionMonitor monitor = new VersionMonitor();
            int currentVer = monitor.getVersion();
            try{//case:call await(N) when current version is not N. expected: return immediately with no exception
                monitor.await(currentVer+1);
            }catch (Exception ex){
                Assert.fail();
            }
            try{//case:call await(N) when current version is N and then increment the monitor version  expected: thread is waiting and then going to runnable
                Thread t1 = new Thread(()->{
                    try {
                        monitor.await(currentVer);
                    }catch (InterruptedException ex){}
                });
                t1.start();
                Thread.sleep(100);
                assertEquals(t1.getState() , Thread.State.valueOf("WAITING"));
                monitor.inc();
                Thread.sleep(100);
                if(t1.isAlive()){
                    Assert.fail();
                }
            }catch (Exception ex){
                Assert.fail();
            }

        }catch (Exception ex){
            Assert.fail();
        }
    }



    public void testInc() {
        try {
            VersionMonitor monitor = new VersionMonitor();
            int oldVersion = monitor.getVersion();
            monitor.inc();
            assertEquals(oldVersion + 1, monitor.getVersion());
        } catch (Exception ex){
            Assert.fail();
        }
    }
}
