#### сборка проекта

требуются
```
javac 1.8.0_201
openjdk version "1.8.0_201"
Apache Maven 3.5
```

`$ mvn package`

```
... 
[INFO] Building jar: ~/DEV/fs/FS6926/target/filesorter-jar-with-dependencies.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
...
```

`target/filesorter-jar-with-dependencies.jar` используется в запускающем скрипте `sort.sh`

#### параметры запуска

```
$ ./sort.sh 

usage: java -jar filesorter.jar [OPTIONS] output.file input.files...
output.file  Обязательное имя файла с результатом сортировки.
input.files  Один, или больше входных файлов.
 -a          Сортировка по возрастанию. Применяется по умолчанию при
             отсутствии -a или -d.
 -d          Сортировка по убыванию. Опция не обязательна как и -a.
 -h,--help   Отобразить справку.
 -i          Файлы содержат целые числа. Обязательна, взаимоисключительна
             с -s.
 -s          Файлы содержат строки. Обязательна, взаимоисключительна с -i.
 -w          Файлы ожидаются в кодировке CP1251. Опция не обязательна. По
             умолчанию используется UTF8 кодировка файлов.
```
#### примеры запуска

неправильные параметры

```
$ ./sort.sh -a -d ; ./sort.sh -s -i ; ./sort.sh out.txt ;  ./sort.sh -s out.txt none

23:46:04.931 [main] WARN  ru.sua.fs6926.ParseCommandLine - Отсутствует обязательная опция -s или -i
usage: java -jar filesorter.jar [OPTIONS] output.file input.files...
output.file  Обязательное имя файла с результатом сортировки.
input.files  Один, или больше входных файлов.
 -a          Сортировка по возрастанию. Применяется по умолчанию при
             отсутствии -a или -d.
 -d          Сортировка по убыванию. Опция не обязательна как и -a.
 -h,--help   Отобразить справку.
 -i          Файлы содержат целые числа. Обязательна, взаимоисключительна
             с -s.
 -s          Файлы содержат строки. Обязательна, взаимоисключительна с -i.
 -w          Файлы ожидаются в кодировке CP1251. Опция не обязательна. По
             умолчанию используется UTF8 кодировка файлов.

23:46:05.269 [main] WARN  ru.sua.fs6926.ParseCommandLine - Должна быть только одна опция или -s или -i
usage: java -jar filesorter.jar [OPTIONS] output.file input.files...
output.file  Обязательное имя файла с результатом сортировки.
input.files  Один, или больше входных файлов.
 -a          Сортировка по возрастанию. Применяется по умолчанию при
             отсутствии -a или -d.
 -d          Сортировка по убыванию. Опция не обязательна как и -a.
 -h,--help   Отобразить справку.
 -i          Файлы содержат целые числа. Обязательна, взаимоисключительна
             с -s.
 -s          Файлы содержат строки. Обязательна, взаимоисключительна с -i.
 -w          Файлы ожидаются в кодировке CP1251. Опция не обязательна. По
             умолчанию используется UTF8 кодировка файлов.

23:46:05.601 [main] WARN  ru.sua.fs6926.ParseCommandLine - Отсутствует обязательная опция -s или -i
usage: java -jar filesorter.jar [OPTIONS] output.file input.files...
output.file  Обязательное имя файла с результатом сортировки.
input.files  Один, или больше входных файлов.
 -a          Сортировка по возрастанию. Применяется по умолчанию при
             отсутствии -a или -d.
 -d          Сортировка по убыванию. Опция не обязательна как и -a.
 -h,--help   Отобразить справку.
 -i          Файлы содержат целые числа. Обязательна, взаимоисключительна
             с -s.
 -s          Файлы содержат строки. Обязательна, взаимоисключительна с -i.
 -w          Файлы ожидаются в кодировке CP1251. Опция не обязательна. По
             умолчанию используется UTF8 кодировка файлов.

23:46:05.939 [main] ERROR ru.sua.fs6926.ReadFileLineByLine - Входной файл 'none' не открыт по причине 'none (Нет такого файла или каталога)'. В сортировке не участвует.
23:46:05.943 [main] ERROR ru.sua.fs6926.WorkersHolder - Нет доступных для обработки входных файлов.


$ touch out ; chmod a-w out ; ./sort.sh -s out in
23:47:57.060 [main] ERROR ru.sua.fs6926.SorterImpl - Проблема с созданием выходного файла 'out' по причине 'out (Отказано в доступе)'

```

входные файлы integer ascending
``` 
$ ./sort.sh -a -i out.txt ExampleData/*asc*.txt ; cat out.txt
23:50:53.819 [main] ERROR ru.sua.fs6926.SorterImpl - Нарушение формата чисел в одном из входных файлов. Файл исключен из обработки. В строке 'ASD'. Причина 'For input string: "ASD"'
23:50:53.824 [main] ERROR ru.sua.fs6926.SorterImpl - Нарушение формата чисел в одном из входных файлов. Файл исключен из обработки. В строке ''. Причина 'For input string: ""'
23:50:53.826 [main] ERROR ru.sua.fs6926.SorterImpl - Нарушение сортировки в одном из входных файлов. Файл исключен из обработки.
23:50:53.827 [pool-1-thread-1] WARN  ru.sua.fs6926.ReadFileLineByLine - Прервано чтение файла 'ExampleData/in1ascErrSort.txt'
23:50:53.827 [pool-3-thread-1] WARN  ru.sua.fs6926.ReadFileLineByLine - Прервано чтение файла 'ExampleData/in2ascErrEmptyLine.txt'
23:50:53.827 [pool-5-thread-1] WARN  ru.sua.fs6926.ReadFileLineByLine - Прервано чтение файла 'ExampleData/in3ascErrType.txt'
-1
-1
0
1
1
1
1
1
2
3
4
4
5
6
7
8
9
16
27
64

```

входные файлы integer descending
``` 
$ ./sort.sh -d -i out.txt ExampleData/*desc*.txt ; cat out.txt
23:52:00.173 [main] ERROR ru.sua.fs6926.SorterImpl - Нарушение формата чисел в одном из входных файлов. Файл исключен из обработки. В строке '5.5'. Причина 'For input string: "5.5"'
23:52:00.176 [main] ERROR ru.sua.fs6926.SorterImpl - Нарушение сортировки в одном из входных файлов. Файл исключен из обработки.
23:52:00.179 [pool-4-thread-1] WARN  ru.sua.fs6926.ReadFileLineByLine - Прервано чтение файла 'ExampleData/in3descErrTypeFloat.txt'
23:52:00.179 [pool-3-thread-1] WARN  ru.sua.fs6926.ReadFileLineByLine - Прервано чтение файла 'ExampleData/in3descErrSort.txt'
65
28
16
9
9
7
7
7
6
6
5
5
4
4
3
2
1
1
1
0
-1

```

входные файлы string ascending
```
$ ./sort.sh -a -s out.txt ExampleData/*strAsc*.txt ; cat out.txt
23:53:56.274 [main] ERROR ru.sua.fs6926.SorterImpl - Нарушение сортировки в одном из входных файлов. Файл исключен из обработки.
23:53:56.280 [pool-2-thread-1] WARN  ru.sua.fs6926.ReadFileLineByLine - Прервано чтение файла 'ExampleData/in5strAscErrSort.txt'
1
2
2
2
3
3
3
a
a
aa
aa
aaa
aaa
b
b
bb
bb
bbb
bbb
bbbb
bbbb

```


входные файлы integer descending
``` 
$ ./sort.sh -d -s out.txt ExampleData/*strDesc*.txt ; cat out.txt
23:54:45.628 [main] ERROR ru.sua.fs6926.SorterImpl - Нарушение сортировки в одном из входных файлов. Файл исключен из обработки.
23:54:45.632 [pool-2-thread-1] WARN  ru.sua.fs6926.ReadFileLineByLine - Прервано чтение файла 'ExampleData/in6strDescErrSort.txt'
а
Ц
Х
Ф
ZZZZ
ZZZZ
Z
Z
XXX
XXX
A
444
100A

```