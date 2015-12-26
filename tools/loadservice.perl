#!/usr/bin/perl
# This Perl script open connection and call domains
# $jar stands for path of jmxterm jar file
use strict;

my $file = "../config/service.cfg";

open (my $fh, "<", $file) or die "Can't open the file $file: ";
close $file;
my $pid=`ps -ef | grep -e ThriftNameServer | grep -v grep | awk '{print \$2}'`;
if($pid <= 0){
print "Can't open the pid [thriftnameserver]\n";
exit;
}
open JMX, "| java -jar jmxterm-1.0-alpha-4-uber.jar -n";
print JMX "open $pid\n";
print JMX "bean -d SNodeManagerMBean name=sNodeManagerMBean\n";
while (my $line =<$fh>)
{
    chomp ($line);
    my($key1, $key2, $key3, $key4) = split(" ", $line);
    print JMX "run onLine $key1 $key2 $key3 $key4\n";
}
print JMX "close\n";
print JMX "bye\n";
close JMX;