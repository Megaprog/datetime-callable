DateTime Callable
=============

На вход поступают пары (DateTime, Callable).   
Нужно реализовать систему, которая будет выполнять  
Callable для каждого пришедшего события в указанный DateTime,  
либо как можно скорее в случае если система перегружена  
и не успевает все выполнять (имеет беклог).  
Задачи должны выполняться в порядке согласно значению  
DateTime либо в порядке прихода события для равных DateTime.  
События могут приходить в произвольном порядке и добавление   
новых пар (DateTime, Callable) может вызываться из разных потоков.  
     
- - -

### Tips

* To run tests use a command: *mvn test*
* To start processing the DateTimeCallableConsumer.start() method should be invoked
* To stop processing the DateTimeCallableConsumer.interrupt() method should be invoked
* The DateTimeCallableConsumer.accept(Instant, Callable) method schedule **Callable** execution at **Instant** time

