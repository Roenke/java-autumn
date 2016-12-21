### Benchmark info

клиент - `JDK8, Ubuntu 16 x64 , i5 4590 3.3 GHz, 16 gb` 

сервер - `JDK8, Windows 10 x64, i7 2370M 2.4 GHz, 8 gb`

чтобы снизить влияние GC, JIT и других рандомных факторов каждый тест запускался 5 раз, среди получившихся результатов 
выбирался лучший (т.е. для установленных значений `M, N, X, Delta` делали 5 последовательных запусков). В результате 
полученные цифры оказались воспроизводимы.

##N - array size

Изменяемая величина: `N` - размер массива. Пределы: `1-12001` шаг `500`

Значения остальных параметров:
`M = 30, X = 3, Delta = 0`

![n-lifetime](https://raw.githubusercontent.com/Roenke/java-autumn/bench/server-benchmark/img/array-size/n-lifetime.png)
![n-server-client](https://raw.githubusercontent.com/Roenke/java-autumn/bench/server-benchmark/img/array-size/n-server-per-client.png)
![n-server-request](https://raw.githubusercontent.com/Roenke/java-autumn/bench/server-benchmark/img/array-size/n-server-per-request.png)


## M - clients count
Изменяемая величина: `M` - количество клиентов. Пределы: `1-1001` шаг `50`

Значения остальных параметров:
`N = 300, X = 2, Delta = 0`
 
 ![m-lifetime](https://raw.githubusercontent.com/Roenke/java-autumn/bench/server-benchmark/img/client-count/m-lifetime.png)
 ![m-server-client](https://raw.githubusercontent.com/Roenke/java-autumn/bench/server-benchmark/img/client-count/m-server-per-client.png)
 ![m-server-request](https://raw.githubusercontent.com/Roenke/java-autumn/bench/server-benchmark/img/client-count/m-server-per-request.png)

 
## Delta - client delay
Изменяемая величина: `Delta` - количество клиентов. Пределы: `0-100` шаг `5`

Значения остальных параметров:
`M = 1000, N = 1, X = 10`

![delta-lifetime](https://raw.githubusercontent.com/Roenke/java-autumn/bench/server-benchmark/img/client-delay/delta-lifetime.png)
![delta-server-client](https://raw.githubusercontent.com/Roenke/java-autumn/bench/server-benchmark/img/client-delay/delta-server-per-client.png)
![delta-server-request](https://raw.githubusercontent.com/Roenke/java-autumn/bench/server-benchmark/img/client-delay/delta-server-per-request.png)
