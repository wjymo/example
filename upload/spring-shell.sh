#!/bin/bash
#Desc spring-shell 项目
#Auth zongf
#Date 2019-01-25

# 打包
function package(){

    # 切换目录
    script_path=`cd $(dirname $0); pwd`
    cd `dirname $script_path`

    # 清理
    mvn -s $settings clean

    # 打包
    mvn -s $settings -DskipTests package
}

# 运行程序
function run(){
    # 切换目录
    script_path=`cd $(dirname $0); pwd`
    cd `dirname $script_path`/target

    # 运行程序
    java -jar spring-shell-0.0.1-SNAPSHOT.jar

}
######################################## main ########################################

#　配置位置
settings="/opt/app/maven/apache-maven-2.2.1/conf/settings.xml"

if [ "$1" == "package" ]; then
    package
else
    run
fi
