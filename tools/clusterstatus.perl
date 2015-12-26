#!/usr/bin/perl
# This Perl script open connection and call domains
# $jar stands for path of jmxterm jar file

my $pid=`ps -ef | grep -e ThriftNameServer | grep -v grep | awk '{print \$2}'`;
if($pid <= 0){
print "Can't open the pid [thriftnameserver]\n";
exit;
}
open JMX, "| java -jar jmxterm-1.0-alpha-4-uber.jar -n";
print JMX "open $pid\n";
print JMX "bean -d CNodeManagerMBean name=cNodeManagerMBean\n";
print JMX "run clusterStatus\n";
print JMX "close\n";
print JMX "bye\n";
close JMX;