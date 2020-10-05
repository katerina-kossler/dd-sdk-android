# SDK Performance

## SDK Metrics

1. **The SDK size (723 KB)**. Was measured as the **.aar** package size 
without the transitive dependencies.For now the SDK uses the following transitive dependencies: 
     -   androidx.core:core
     -   androidx.navigation:navigation-fragment-ktx
     -   androidx.navigation:navigation-ui-ktx
     -   androidx.navigation:navigation-runtime-ktx
     -   androidx.recyclerview:recyclerview
     -   androidx.work:work-runtime
     -   com.google.code.gson:gson
     -   com.lyft.kronos:kronos-android
     -   com.squareup.okhttp3:okhttp
     -   org.jetbrains.kotlin:kotlin-stdlib

## FAQ

1. Q: **What happens when my application goes in background ?**   
A: When the application goes in background:
    -   the auto - instrumentation stops and
detaches itself from any UI callbacks (lifecycle events, gesture detection). No RUM event will be automatically tracked
but you can still manually send them with the `RumMonitor`.
   -    will keep accepting logs and traces as usual
   -    will keep collecting and sending new batches of data.

2. Q: **How are the batches created and sent ?**   
A: When a new event is ready to be serialised and batched the persistence layer will ask for 
the last known batch(File) to store the serialised event. A batch(File) is considered 
valid for appending new data when  all the following conditions are met:
   
   -   last time the file was accessed was less than 5 seconds ago 
   -   the file size is less or equal than to 4 MB
   -   the number of events in file is less or equal to 500   
    
   Once one of those criterias is not met the batch will be marked as `full` and will be sent to the
   endpoint in one of the next upload cycles.The frequency at which the batches are sent starts at 5 seconds and goes up 
   linearly with every batch sent. The max frequency is 1 batch/second. If there's no batch or network available or the 
   battery level is too low the upload frequency will be linearly decreased to the minimum default value (20 seconds).

3. Q: **How about the battery consumption ?**  
A: The SDK will not perform any Network activity if your battery level is less than 10% or if your 
device is in power - save mode.

4. Q: **Do you have any TTLs on the persisted data?**  
A: The SDK will store persist all the data in batches and will try to send those whenever the network is 
available. A batch will not be stored more than 18 hours in an application. Every time the SDK will try
to read a new batch for sending it, will first remove the batches that are older than 18 hours.

5. Q: **How the SDK deals with low storage memory?**  
A: The SDK checks the storage space used every time it creates a new batch. If this value is bigger than
512 MB (the maximum amount of storage space that the SDK will use) it will first try to make more space available 
by removing the older files. 