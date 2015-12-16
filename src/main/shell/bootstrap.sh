#!/bin/bash

#后端程序通用脚本
# cd shell path
cd "$(dirname "$0")"
# server home path
HOME="$(dirname $(pwd))"

#bin  lib  conf  path properties
BIN_DIR="$HOME/bin"
LIB_DIR="$HOME/lib"
CONF_DIR="$HOME/config"
AGENT_DIR="$HOME/agent"

#server startup class
MAIN_CLASS=com.jerry.thriftnameserver.app.ThriftNameServer

# set log config
LOG_FILE="$CONF_DIR/log4j.properties"

#jvm config
JAVA_OPTS="-javaagent:$AGENT_DIR/propertiesloadagent-0.0.1.jar=$CONF_DIR/core.properties -Xms1024M -Xmx1024M -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseParallelOldGC -XX:+UseAdaptiveSizePolicy -DHOSTNAME=$HOSTNAME -Dlog4j.configuration=file:$LOG_FILE"


#java pid
psid=0

#clsaaesPath
CLASSPATH=${base_dir}
for i in "$LIB_DIR"/*.jar; do
   CLASSPATH="$CLASSPATH":"$i"
done


start(){
    getPid
    if [ ${psid} -ne 0 ];then
        echo "[WRING]============$MAIN_CLASS Already Started(PID=$psid)============================="
    else
        echo "[INFO]====================start $MAIN_CLASS[Begin]================================="
        java -server ${JAVA_OPTS}  -cp "${CLASSPATH}" ${MAIN_CLASS} >>$BIN_DIR/std.out 2>&1 &
        echo "[INFO]====================start $MAIN_CLASS[Success]==============================="
    fi
}

#get main class pid
getPid(){
    javaps=`jps -l | grep -e $MAIN_CLASS`
    if [ -n "$javaps" ]; then
        psid=`echo $javaps | awk '{print $1}'`
    else
        psid=0
    fi
}

stop(){
    getPid
    if [ ${psid} -ne 0 ];then
        echo "[INFO]================stop $MAIN_CLASS (PID=$psid) [Begin]========================"
        kill -15 ${psid}
        sleep 1
        getPid
        if [ ${psid} -ne 0 ];then
            echo "[INFO]====================stop $MAIN_CLASS[Failed]==============================="
        else
            echo "[INFO]====================stop $MAIN_CLASS[Success]=============================="
        fi
    else
         echo "[WRING]============$MAIN_CLASS not Running================================"
    fi
}


case "$1" in
   'start')
      start
      ;;
   'stop')
     stop
     ;;
   'restart')
     stop
     start
     ;;
  *)
     echo "Usage: $0 {start|stop|restart|info}"
     echo " using options descript "
     echo "         start:Start Server"
     echo "         stop: Stop Server"
     echo "         restart:Restart Server"
     exit 1
esac
exit 0





