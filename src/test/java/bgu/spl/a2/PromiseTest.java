package bgu.spl.a2;


import junit.framework.TestCase;
import org.junit.Assert;

public class PromiseTest extends TestCase {


    public void testGet() {
        try {
            Promise<Integer> promise = new Promise<>();
            try {//case: call get before the promise is resolved exp: throw IllegalStateException
                promise.get();
                Assert.fail();
            }catch (IllegalStateException ex){}
            //case: call get after the promise is resolved and check if get returns the resolve value
            promise.resolve(5);
            int ans = promise.get();
            assertEquals(5, ans);
        }catch (Exception ex){
            Assert.fail();
        }
    }

    public void testISResolved(){
        try{
            //case: calling isResolved before the promise is resolved exp:false
            Promise<Integer> promise = new Promise<>();
            assertEquals(false,promise.isResolved());

            //case: calling isResolved after the promise is resolved exp: true
            promise.resolve(5);
            assertEquals(true, promise.isResolved());
        }catch (Exception ex){
            Assert.fail();
        }
    }

    public void testResolve(){
        try{
            Promise<Integer> promise = new Promise<>();
            promise.resolve(5);
            try{//case: try to resolve after the promise is already resolved  exp:throw IllegalStateException exception
                promise.resolve(6);
                Assert.fail();
            }
            catch(IllegalStateException ex ) {
                //case: the promise resolved with x value, and then occurred an unsuccessful attempt to resolve again, checking if the value x changed.
                int x = promise.get();
                assertEquals(5, x);
            }
            catch (Exception ex) {
                Assert.fail();
            }
        }
        catch (Exception ex){
            Assert.fail();
        }
    }

        public void testSubscribe(){
            try{
                Promise<Integer> promise = new Promise<>();
                try{//case: try to subscribe a callback when promise is not resolved yet exp:the callback is add to the promise list of callbacks
                    promise.subscribe(()->{throw new IllegalStateException() ;});
                }
                catch (IllegalStateException ex){
                    Assert.fail();
                }
                try{//case: the promise is resolved while having callbacks waiting to be called exp: the callbacks are called
                    promise.resolve(5);
                    Assert.fail();
                }catch (IllegalStateException ex){}
            }
            catch (Exception ex){
                Assert.fail();
            }
        }
}

