# Blogger to Jbake

Relocation Tool to move your contents from [Blogger](https://www.blogger.com) to [Jbake](http://www.jbake.org/). It can retrieve Blogger's personal articles, pictures are all the content is stored to the local end, and converted into a format that Jbake can import.

## how to use

1. Change the settings file `src/main/resources/application.properties`

  ```
  # blogger's atom file path
  # bogger 匯出的網誌備份檔案(atom 格式檔案)位置
  blogAtomFilePath=z:/blog_in/atom.xml

  # output path of result file
  # 執行結果要產生在哪個目錄
  outputFolderPath=z:/blog_out

  # my blogger site url
  # 我的 blogger 網址
  myBloggerUrl=http://cloudtu.blogspot.com
  ```

2. Enter in the console `gradlew run`， and confirm that it executes correctly

3. The results of the execution will produce directories and files that Jbake can import

  ```
  Z:.
  ├─blog
  │  └─2015
  │      └─02
  │              github-project-branch-bitbucket.html
  │              https-github-push-pull.html
  │
  └─img
      └─2015
          └─02
                  201502261434_1.png
                  201502261434_2.png
                  201502261434_3.png
                  201502261434_4.png
                  201502261434_5.png
                  201502261657_1.png
                  201502261657_2.png
  ```

## Execution or development with eclispe

If you want the program to be executed or developed with Eclipse, follow the steps below

1. Enter in the console `gradlew eclipse`

2. Inside Eclipse `blogger-to-jbake` execute project import

## adjust the implementation of the log details

change the log profile in `src/main/resources/log4j.properties`， and adjust the log level as needed

## How to customize

If you need to convert the data exported by Blogger into other format files (e.g. [jekyll](http://jekyllrb.com/)),
You can refer to [JbakeConverter.java](https://github.com/cloudtu/blogger-to-jbake/blob/master/src/main/java/cloudtu/blog/JbakeConverter.java)
