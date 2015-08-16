# Blogger to Jbake

從 [Blogger](https://www.blogger.com) 搬家到 [Jbake](http://www.jbake.org/) 的搬遷工具。
可以把放在 Blogger 裡的個人文章、圖片全部匯出至本地端，並且轉成 Jbake 可以匯入的檔案格式。

## 如何使用

1. 修改 `src/main/resources/application.properties` 設定

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

2. 在 console 執行 `gradlew run`，並確認執行成功

3. 執行結果目錄會產生 Jbake 可以匯入的目錄與檔案

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

## 在 eclispe 執行或開發

如果要讓程式可以在 eclipse 上執行或開發，請執行下述步驟

1. 在 console 執行 `gradlew eclipse`

2. 在 eclipse 裡把 `blogger-to-jbake` 專案匯入

## 調整執行時的 log 明細

log 設定檔放在 `src/main/resources/log4j.properties`，可視需求調整 log level

## 如何客製化

如果你需要把 Blogger 匯出的資料轉成其它格式檔案(e.g. [jekyll](http://jekyllrb.com/))，
可以參考  [JbakeConverter.java](https://github.com/cloudtu/blogger-to-jbake/blob/master/src/main/java/cloudtu/blog/JbakeConverter.java) 的寫法自行客製
